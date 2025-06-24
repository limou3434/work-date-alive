package cn.com.edtechhub.workdatealive.manager.ai;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
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
public class InFileMemory implements ChatMemory {

    /**
     * 对话持久文件保存目录
     */
    private final String BASE_DIR;

    /**
     * 创建 Kryo 序列化工具
     */
    private static final Kryo kryo = new Kryo();

    static {
        kryo.setRegistrationRequired(false); // 无需显式注册每个类
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy()); // 设置对象创建策略, 用 Objenesis 库绕过构造函数, 直接创建实例
    }

    /**
     * 构造对象, 可以指定文件保存目录
     *
     * @param dir 持久文件保存目录
     */
    public InFileMemory(String dir) {
        this.BASE_DIR = dir;
        File baseDir = new File(dir);
        if (!baseDir.exists()) { // 如果目录不存在
            if (!baseDir.mkdirs()) { // 尝试创建目录
                throw new RuntimeException("Failed to create directory: " + dir);
            }
        }
    }

    /**
     * 添加消息
     *
     * @param conversationId 会话标识
     * @param messages 消息列表
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        List<Message> conversationMessages = getOrCreateConversation(conversationId);
        conversationMessages.addAll(messages);
        saveConversation(conversationId, conversationMessages);
    }

    /**
     * 获取消息
     *
     * @param conversationId 会话标识
     * @param lastN 最后 N 条消息
     * @return 消息列表
     */
    @Override
    public List<Message> get(String conversationId, int lastN) {
        List<Message> allMessages = getOrCreateConversation(conversationId);
        return allMessages.stream()
                .skip(Math.max(0, allMessages.size() - lastN))
                .toList();
    }

    /**
     * 清除消息
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

    private List<Message> getOrCreateConversation(String conversationId) {
        File file = getConversationFile(conversationId);
        List<Message> messages = new ArrayList<>();
        if (file.exists()) {
            try (Input input = new Input(new FileInputStream(file))) {
                messages = kryo.readObject(input, ArrayList.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return messages;
    }

    private void saveConversation(String conversationId, List<Message> messages) {
        File file = getConversationFile(conversationId);
        try (Output output = new Output(new FileOutputStream(file))) {
            kryo.writeObject(output, messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getConversationFile(String conversationId) {
        return new File(BASE_DIR, conversationId + ".kryo");
    }

}
