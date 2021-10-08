package net.java.nio2.datagramchannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatagramServer {
    public static DatagramChannel startServer() throws IOException {
        InetSocketAddress address = new InetSocketAddress("localhost", 7001);
        DatagramChannel server = DatagramChannelBuilder.bindChannel(address);
        
        log.info("Server started at #" + address);
        
        return server;
    }
    
    public static String receiveMessage(DatagramChannel server) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        SocketAddress remoteAdd = server.receive(buffer);
        String message = extractMessage(buffer);
        
        log.info("Client at #" + remoteAdd + "  sent: " + message);
        
        return message;
    }
    
    private static String extractMessage(ByteBuffer buffer) {
        buffer.flip();

        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        
        String msg = new String(bytes);
        
        return msg;
    }

    public static void main(String[] args) throws IOException {
        DatagramChannel server = startServer();
        receiveMessage(server);
    }    
}
