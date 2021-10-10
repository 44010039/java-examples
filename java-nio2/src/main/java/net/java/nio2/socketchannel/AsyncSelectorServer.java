package net.java.nio2.socketchannel;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * The Server With Selector
 */
@Slf4j
public class AsyncSelectorServer {
    private static final String POISON_PILL = "POISON_PILL";

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress("localhost", 5454));
        serverSocket.configureBlocking(false);
        // 注册Selector
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        ByteBuffer buffer = ByteBuffer.allocate(256);

        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();
            while (iter.hasNext()) {

                SelectionKey key = iter.next();

                if (key.isAcceptable()) {
                    register(selector, serverSocket);
                }

                if (key.isReadable()) {
                    answerWithEcho(buffer, key);
                }
                iter.remove();
            }
        }
    }

    private static void answerWithEcho(ByteBuffer buffer, SelectionKey key) throws IOException {
        SocketChannel channel = null;
        try {
            channel = (SocketChannel) key.channel();
            channel.read(buffer);
            if (new String(buffer.array()).trim().equals(POISON_PILL)) {
                channel.close();
                log.info("Not accepting client messages anymore");
            } else {
                buffer.flip();
                log.info("server receive message: " + new String(buffer.array()).trim());

                channel.write(buffer);
                buffer.clear();
            }
        } catch (IOException e) {
            // 一定要捕捉异常，否则客户端断开后，程序会退出
            // 一定要关闭通道，否则会死循环
            if (channel != null) {
                channel.close();
            }
            e.printStackTrace();
        }
    }

    private static void register(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

    public static Process start() throws IOException, InterruptedException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = AsyncSelectorServer.class.getCanonicalName();

        ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, className);

        return builder.start();
    }
}
