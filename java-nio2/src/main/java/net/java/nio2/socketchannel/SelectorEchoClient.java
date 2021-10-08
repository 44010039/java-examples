package net.java.nio2.socketchannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SelectorEchoClient {
    private SocketChannel client;
    private ByteBuffer buffer;
    private static SelectorEchoClient instance;

    public static SelectorEchoClient start() {
        if (instance == null)
            instance = new SelectorEchoClient();

        return instance;
    }

    public void stop() throws IOException {
        client.close();
        buffer = null;
    }

    private SelectorEchoClient() {
        try {
            client = SocketChannel.open(new InetSocketAddress("localhost", 5454));
            buffer = ByteBuffer.allocate(256);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sendMessage(String msg) {
        buffer = ByteBuffer.wrap(msg.getBytes());
        String response = null;
        try {
            client.write(buffer);
            buffer.clear();
            client.read(buffer);
            response = new String(buffer.array()).trim();
            // log.info("response=" + response);
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static void main(String[] args) throws IOException {
        SelectorEchoClient client = SelectorEchoClient.start();
        String resp1 = client.sendMessage("hello");
        String resp2 = client.sendMessage("world");
        log.info("resp1 =" + resp1);
        log.info("resp2 =" + resp2);

        client.stop();
    }
}
