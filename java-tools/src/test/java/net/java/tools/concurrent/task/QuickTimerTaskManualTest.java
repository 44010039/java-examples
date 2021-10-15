package net.java.tools.concurrent.task;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuickTimerTaskManualTest {
    
    public static void main(String[] args) throws InterruptedException{
        // 有两种方式创建

        // 1 .
        QuickTimerTask.scheduleAtFixedRate(() -> {
            log.info("QuickTimerTask - 1");
        }, 100, 500);

        // 2.
        new DemoTask();

        TimeUnit.SECONDS.sleep(10);

        QuickTimerTask.cancelQuickTask();
    }

    private final static class DemoTask extends QuickTimerTask {

        @Override
        public void run() {    
            log.info("DemoTask - 1");        
        }

        @Override
        protected long getPeriod() {
            return 300;
        }

    }
}
