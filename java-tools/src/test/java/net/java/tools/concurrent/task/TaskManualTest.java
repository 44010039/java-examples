package net.java.tools.concurrent.task;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskManualTest {

    public static void main(String[] args) throws InterruptedException {
        TaskExecutor executor = TaskExecutor.builder().withCorePoolSize(3).build();
        executor.execute(new Task("Task1", 100));
        executor.execute(new Task("Task2", 300));
        executor.execute(new Task("Task3", 600));
        executor.execute(new Task("Task4", 800));
        executor.execute(new Task("Task5", 1500)); // 已经超出关闭时间
        
        // 此任务不会停止, 导致main线程无法停止
        // executor.execute(() -> {
        //     while(true) {
                
        //     }
        // });

        // 清理任务
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                executor.shutdownNow();
            }
        }, 7, TimeUnit.SECONDS);
    }

    private static final class Task implements Runnable {
        private final String name;
        private final int sleepTime;

        public Task(String name, int sleepTime) {
            this.name = name;
            this.sleepTime = sleepTime;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 5; i++) {
                    TimeUnit.MILLISECONDS.sleep(sleepTime);

                    log.info("{} - {}", name, i);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("{} is finished", name);
        }

    }
}
