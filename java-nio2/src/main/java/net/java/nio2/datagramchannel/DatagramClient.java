package net.java.nio2.datagramchannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatagramClient {
    public static DatagramChannel startClient() throws IOException {
        DatagramChannel client = DatagramChannelBuilder.custom().open().build();
        client.configureBlocking(false);
        return client;
    }
    
    public static void sendMessage(DatagramChannel client, String msg, SocketAddress serverAddress) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
        client.send(buffer, serverAddress);
    }

    public static void main(String[] args) throws IOException {
        DatagramChannel client = startClient();
        InetSocketAddress serverAddress = new InetSocketAddress("localhost", 7001);
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        log.debug("Message to server:");
        while ((line = br.readLine()) != null) {
            sendMessage(client, line, serverAddress);
        }

        
        
    }    
}
