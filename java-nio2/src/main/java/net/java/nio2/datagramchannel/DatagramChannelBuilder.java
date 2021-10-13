package net.java.nio2.datagramchannel;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

public class DatagramChannelBuilder {
    private DatagramChannel channel;

    public static DatagramChannelBuilder custom() {
        return new DatagramChannelBuilder();
    }

    public DatagramChannelBuilder open() throws IOException {
        channel =  DatagramChannel.open();
        return this;
    }
    
    public DatagramChannelBuilder bind(SocketAddress local) throws IOException {
        if (this.channel == null) {
            throw new RuntimeException("请先先调用open方法");
        }

        if (local != null) {
            channel.bind(local);
        } 
        return this;
    }

    public DatagramChannel build() {
        return this.channel;
    }
}
