package net.java.nio2.socketchannel;

import java.io.IOException;
import java.nio.channels.Pipe;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * we use Java NIO's Pipe class to open a channel for testing purposes. 
 * We track code execution steps in a thread-safe list. By analyzing these steps, 
 * we can see how selector.wakeup() releases the thread blocked by selector.select().
 */
public class SelectorManualTest {
    @Test
    public void whenWakeUpCalledOnSelector_thenBlockedThreadReturns() throws IOException, InterruptedException {
        Pipe pipe = Pipe.open();

        Selector selector = Selector.open();
        SelectableChannel channel = pipe.source();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);

        List<String> invocationStepsTracker = Collections.synchronizedList(new ArrayList<>());

        CountDownLatch latch = new CountDownLatch(1);

        Thread thread = new Thread(() -> {
            invocationStepsTracker.add(">> Count down");
            latch.countDown();
            try {
                invocationStepsTracker.add(">> Start select");
                selector.select();
                invocationStepsTracker.add(">> End select");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        invocationStepsTracker.add(">> Start await");
        thread.start();
        latch.await();
        invocationStepsTracker.add(">> End await");

        invocationStepsTracker.add(">> Wakeup thread");

        selector.wakeup();
        channel.close();

        assertThat(invocationStepsTracker)
          .containsExactly(
            ">> Start await",
            ">> Count down",
            ">> Start select",
            ">> End await",
            ">> Wakeup thread",
            ">> End select"
          );
    }
}
