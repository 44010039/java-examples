package net.java.nio2.socketchannel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AsyncEchoIntegrationTest {
    private Process server;
    private AsyncEchoClient client;

    @BeforeAll
    public void setup() throws IOException, InterruptedException {
        server = AsyncEchoServer2.start();
        client = AsyncEchoClient.getInstance();
    }

    @Test
    public void givenServerClient_whenServerEchosMessage_thenCorrect() throws Exception {
        String resp1 = client.sendMessage("hello");
        String resp2 = client.sendMessage("world");
        assertEquals("hello", resp1);
        assertEquals("world", resp2);
    }

    @AfterAll
    public void teardown() throws IOException {
        client.stop();
        server.destroy();   
    }
}
