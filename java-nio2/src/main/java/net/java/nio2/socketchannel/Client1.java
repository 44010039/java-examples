package net.java.nio2.socketchannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import lombok.extern.slf4j.Slf4j;

/**
 * 异步消息发送
 */
@Slf4j
public class Client1 {
    private AsynchronousSocketChannel client;
    private Future<Void> future;
    private static Client1 instance;    

    private Client1() {
        try {
            client = AsynchronousSocketChannel.open();
            InetSocketAddress hostAddress = new InetSocketAddress("localhost", 5454);
            future = client.connect(hostAddress);
            start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Client1 getInstance() {
        if (instance == null)
            instance = new Client1();
        return instance;
    }

    private void start() {
        log.info("Message Client is running...");
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public String sendMessage(String message) {
        byte[] byteMsg = message.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(byteMsg);
        Future<Integer> writeResult = client.write(buffer);

        try {
            writeResult.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        buffer.flip();
        Future<Integer> readResult = client.read(buffer);
        try {
            readResult.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String echo = new String(buffer.array()).trim();
        buffer.clear();
        return echo;
    }

    public void stop() {
        log.info("Message Client stop");
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Client1 client = Client1.getInstance();
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
