package net.java.nio2.socketchannel;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * The Server With CompletionHandler
 * 
 * 接收客户端发送的消息，并返回消息给客户端
 * 
 * 
 */
@Slf4j
public class AsyncCompletionHandlerServer {
    private AsynchronousServerSocketChannel serverChannel;

    public AsyncCompletionHandlerServer() {
        try {
            serverChannel = AsynchronousServerSocketChannel.open();
            InetSocketAddress hostAddress = new InetSocketAddress("localhost", 5454);
            serverChannel.bind(hostAddress);
            log.info("Message Server is running...");
            while (true) {

                serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {

                    @Override
                    public void completed(AsynchronousSocketChannel channel, Object attachment) {
                        if (serverChannel.isOpen())
                            serverChannel.accept(null, this);
            
                        if ((channel != null) && (channel.isOpen())) {
                            ReadWriteHandler handler = new ReadWriteHandler();
                            ByteBuffer buffer = ByteBuffer.allocate(32);
                            Map<String, Object> readInfo = new HashMap<>();
                            readInfo.put("action", "read");
                            readInfo.put("buffer", buffer);
                            readInfo.put("channel", channel);
                            channel.read(buffer, readInfo, handler);
                        }
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        exc.printStackTrace();
                    }
                });
                try {
                    System.in.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ReadWriteHandler implements CompletionHandler<Integer, Map<String, Object>> {

        @Override
        public void completed(Integer result, Map<String, Object> attachment) {
            Map<String, Object> actionInfo = attachment;
            String action = (String) actionInfo.get("action");
            AsynchronousSocketChannel channel = (AsynchronousSocketChannel) actionInfo.get("channel");
            if ("read".equals(action)) {
                ByteBuffer buffer = (ByteBuffer) actionInfo.get("buffer");
                buffer.flip();
                actionInfo.put("action", "write");
                channel.write(buffer, actionInfo, this);
                buffer.clear();
            } else if ("write".equals(action)) {
                ByteBuffer buffer = ByteBuffer.allocate(32);
                actionInfo.put("action", "read");
                actionInfo.put("buffer", buffer);
                channel.read(buffer, actionInfo, this);
            }

        }

        @Override
        public void failed(Throwable exc, Map<String, Object> attachment) {
            exc.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new AsyncCompletionHandlerServer();
    }

    /**
     * 测试入口
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static Process start() throws IOException, InterruptedException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = AsyncCompletionHandlerServer.class.getCanonicalName();

        ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, className);

        return builder.start();
    }    
}
