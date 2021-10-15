package net.java.tools.concurrent.task;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 计划任务执行器。
 * 
 */
@Slf4j
public class TaskExecutor {
    private final ScheduledExecutorService scheduledExecutorService;
    private final SchedulerBuilder properties;

    private volatile boolean open;    

    private TaskExecutor(SchedulerBuilder builder) {
        if (log.isDebugEnabled()) {
            log.debug("Scheduler Executor is created: {}", builder.toString());
        }

        this.properties = builder.copy();

        this.scheduledExecutorService = Executors.newScheduledThreadPool(builder.corePoolSize, (r) -> {      
            Thread thread = new Thread(r);
            thread.setName(String.format("Scheduler Executor - %s", builder.name));
            thread.setDaemon(builder.threadDaemon);
            return thread;
        });
        
        this.open = true;
    }

    public synchronized void shutdownNow() {
        if (open) {
            boolean interrupted = false;
            scheduledExecutorService.shutdownNow();
            try {
                boolean shutdown = scheduledExecutorService
                    .awaitTermination(properties.threadTerminationTimeout, TimeUnit.MILLISECONDS);
                if (!shutdown) {
                    log.error("Fail to shutdown scheduled executor service");
                }
            } catch (InterruptedException e) {
                interrupted = true;
            }

            open = false;
            if (log.isDebugEnabled()) {
                log.debug("Scheduler Executor is closed");
            }

            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 立即运行callable
     * 
     * @param callable Callable实例
     * @return
     */
    public Future<?> execute(Callable<?> callable) {
        return scheduledExecutorService.submit(callable);
    }

    /**
     * 立即运行runnable
     * 
     * @param runnable Runnable实例
     * @return
     */
    public Future<?> execute(Runnable runnable) {
        return scheduledExecutorService.submit(runnable);
    }

    /**
     * 执行callable任务
     * @param callable 任务
     * @param delay 延时
     * @param timeUnit 单位
     * @return
     */
    public Future<?> schedule(Callable<?> callable, long delay, TimeUnit timeUnit) {
        return scheduledExecutorService.schedule(callable, delay, timeUnit);
    }

    /**
     * 执行runnable任务
     * @param runnable 任务
     * @param delay 延时
     * @param timeUnit 单位
     * @return
     */
    public Future<?> schedule(Runnable runnable, long delay, TimeUnit timeUnit) {
        return scheduledExecutorService.schedule(runnable, delay, timeUnit);
    }

    public static SchedulerBuilder builder() {
        return new SchedulerBuilder();
    }

    @Getter
    @ToString
    public static final class SchedulerBuilder {
        private String name;
        private long threadTerminationTimeout;
        private int corePoolSize;
        private boolean threadDaemon;

        private SchedulerBuilder() {
            this.name = "Unknow";
            this.threadTerminationTimeout = 5000;
            this.corePoolSize = 1;
        }

        public SchedulerBuilder copy() {
            SchedulerBuilder rslt = new SchedulerBuilder();
            rslt.name = this.name;
            rslt.threadTerminationTimeout = this.threadTerminationTimeout;
            rslt.corePoolSize = this.corePoolSize;
            rslt.threadDaemon = this.threadDaemon;
            return rslt;
        }

        public SchedulerBuilder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * 线程池大小
         * 
         * @param corePoolSize 必须大于零
         * @return
         */
        public SchedulerBuilder withCorePoolSize(int corePoolSize) {
            if(corePoolSize <= 0) {
                throw new IllegalArgumentException("corePoolSize must greater than zero");
            }

            this.corePoolSize = corePoolSize;
            return this;
        }

        /**
         * 默认毫秒
         * @param threadTerminationTimeout
         * @return
         */
        public SchedulerBuilder withThreadTerminationTimeout(long threadTerminationTimeout) {
           return withThreadTerminationTimeout(threadTerminationTimeout, TimeUnit.MILLISECONDS);
        }

        public SchedulerBuilder withThreadTerminationTimeout(long threadTerminationTimeout, TimeUnit timeUnit) {
            this.threadTerminationTimeout = timeUnit.toMicros(threadTerminationTimeout);
            return this;
        }

         public SchedulerBuilder withThreadDaemon(boolean threadDaemon) {
            this.threadDaemon = threadDaemon;
            return this;
         }

        public TaskExecutor build() {
            return new TaskExecutor(this);
        }
    }
}
