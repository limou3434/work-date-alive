package cn.com.edtechhub.workdatealive.manager.ai.memorys;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于文件持久化的对话记忆实现类
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Slf4j
public class InFileMemory implements ChatMemory {

    // TODO: 后期可以考虑升级该类以支持持久化对话到 MySQL 或 Redis 存储中, 最好是定义一个 ConversationStorageStrategy 接口和父类 StrategyBasedMemory, 然后分别派生 InFileMemory、InDatabaseMemory、InedisMemory 等子类

    /**
     * 对话持久文件保存目录
     */
    private final String BASE_DIR;

    /**
     * 创建 Kryo 序列化工具
     */
    private static final Kryo kryo = new Kryo();

    static {
        kryo.setRegistrationRequired(false); // 无需显式注册每个类(只需要包含类名, 类似使用 @JsonTypeInfo, 如果为 true 则需要注册类来通过数字 ID 替代类名, 这样可以节省空间、加快解析, 但是这样就必须不断手动注册类)
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy()); // 设置对象创建策略, 用 Objenesis 库绕过构造函数, 允许 Kryo 在没有无参构造函数的情况下创建对象实例
    }

    /**
     * 构造方法, 初始化指定的持久文件保存目录
     *
     * @param dir 持久文件保存目录
     */
    public InFileMemory(String dir) {
        this.BASE_DIR = dir;
        File baseDir = new File(dir);
        if (!baseDir.exists()) { // 如果目录不存在
            if (!baseDir.mkdirs()) { // 尝试创建目录
                log.debug("创建目录失败 {}", baseDir.getAbsolutePath()); // TODO: 项目的所有日志都设置为 debug, 然后在项目正式上线的时候进行异常抛出或是其他级别的日志
            }
        }
    }

    /**
     * 根据会话 ID, 添加消息
     *
     * @param conversationId 会话标识
     * @param messages       消息列表
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        List<Message> conversationMessages = this.getOrCreateConversationByFile(conversationId);
        conversationMessages.addAll(messages); // 把另一个集合中的所有元素按顺序添加到当前集合的末尾
        this.saveConversationByFile(conversationId, conversationMessages);
    }

    /**
     * 根据会话 ID, 获取消息
     *
     * @param conversationId    会话标识
     * @param lastMessagesCount 指示需要获取的最后 N 条消息
     * @return 消息列表
     */
    @Override
    public List<Message> get(String conversationId, int lastMessagesCount) {
        List<Message> allMessages = this.getOrCreateConversationByFile(conversationId);
        return allMessages
                .stream()
                .skip(Math.max(0, allMessages.size() - lastMessagesCount))
                .toList();
    }

    /**
     * 根据会话 ID, 清除消息
     *
     * @param conversationId 会话标识
     */
    @Override
    public void clear(String conversationId) {
        File file = getConversationFile(conversationId);
        if (file.exists()) { // 如果文件存在
            if (!file.delete()) { // 尝试删除文件
                throw new RuntimeException("Failed to delete file: " + file.getAbsolutePath());
            }
        }
    }

    /**
     * 在持久文件中获取对应 ID 的消息列表或创建对应 ID 的消息列表, 如果不存在则会自动创建
     *
     * @param conversationId 会话标识
     * @return 消息列表
     */
    private List<Message> getOrCreateConversationByFile(String conversationId) {
        // 获取持久文件对象
        File file = this.getConversationFile(conversationId);

        // 解析持久文件对象
        List<Message> messages = new ArrayList<>();
        if (file.exists()) {
            try (Input input = new Input(new FileInputStream(file))) {
                messages = kryo.readObject(input, ArrayList.class); // TODO: 这里有类型警告, 可以未来再来解决
            } catch (IOException e) {
                log.debug("读出现异常错误 {}", e.getMessage());
            }
        }
        return messages;
    }

    /**
     * 保存对应 ID 的消息列表或创建对应 ID 的消息列表到持久文件中
     *
     * @param conversationId 会话标识
     * @param messages       消息列表
     */
    private void saveConversationByFile(String conversationId, List<Message> messages) {
        // 获取持久文件对象
        File file = this.getConversationFile(conversationId);

        // 序列消息列表对象
        try (Output output = new Output(new FileOutputStream(file))) {
            kryo.writeObject(output, messages);
        } catch (IOException e) {
            log.debug("写出现异常错误 {}", e.getMessage());
        }
    }

    /**
     * 获取对应 ID 的持久文件对象
     *
     * @param conversationId 会话标识
     * @return 持久文件对象
     */
    private File getConversationFile(String conversationId) {
        return new File(BASE_DIR, conversationId + ".kryo");
    }

}
