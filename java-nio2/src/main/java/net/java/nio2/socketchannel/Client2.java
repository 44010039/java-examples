package net.java.nio2.socketchannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Client2 {
    private SocketChannel client;
    private ByteBuffer buffer;
    private static Client2 instance;

    public static Client2 start() {
        if (instance == null)
            instance = new Client2();

        return instance;
    }

    public void stop() throws IOException {
        client.close();
        buffer = null;
    }

    private Client2() {
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
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static void main(String[] args) throws IOException {
        Client2 client = Client2.start();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        log.debug("Message to server:");
        while ((line = br.readLine()) != null) {
            String response = client.sendMessage(line);
            log.debug("response from server: " + response);
            log.debug("Message to server:");
        }
    }
}
