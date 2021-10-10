package net.java.nio2.socketchannel;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * The Server With Future 接收客户端发送的消息，并返回消息给客户端
 * 
 */
@Slf4j
public class AsyncFutureServer {
    /**
     * 
     */
    private final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "Session");
            thread.setDaemon(true);
            return thread;
        }
    });
    private AsynchronousServerSocketChannel serverChannel;

    public AsyncFutureServer() {
        try {
            serverChannel = AsynchronousServerSocketChannel.open();
            InetSocketAddress hostAddress = new InetSocketAddress("localhost", 5454);
            serverChannel.bind(hostAddress);
            log.info("Message Server is running...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void runServer() {
        try {
            while (true) {
                Future<AsynchronousSocketChannel> acceptResult = serverChannel.accept();
                AsynchronousSocketChannel channel = acceptResult.get();
                if ((channel != null) && (channel.isOpen())) {
                    SCHEDULED_EXECUTOR_SERVICE.schedule(new Session(channel), 0, TimeUnit.MILLISECONDS);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        AsyncFutureServer server = new AsyncFutureServer();
        server.runServer();
    }

    /**
     * 测试使用
     * 
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static Process start() throws IOException, InterruptedException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = AsyncFutureServer.class.getCanonicalName();

        ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, className);

        return builder.start();
    }

    static class Session implements Runnable {
        private final AsynchronousSocketChannel channel;

        public Session(AsynchronousSocketChannel channel) {
            this.channel = channel;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    ByteBuffer buffer = ByteBuffer.allocate(32);
                    Future<Integer> readResult = channel.read(buffer);

                    // do some computation

                    readResult.get();

                    buffer.flip();
                    String message = new String(buffer.array()).trim();
                    if (message.equals("bye")) {
                        break; // while loop
                    }
                    buffer = ByteBuffer.wrap(new String(message).getBytes());
                    Future<Integer> writeResult = channel.write(buffer);

                    // do some computation
                    writeResult.get();
                    buffer.clear();

                } // while()
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }

    }
}
