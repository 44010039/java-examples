package net.java.tools.concurrent.task;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * 快速任务执行器。适用于运行时间短的任务
 */
@Slf4j
public abstract class QuickTimerTask implements Runnable {
    public static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "Quick Timer Task");
            thread.setDaemon(true);
            return thread;
        }
    });

    public QuickTimerTask() {
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(this, getDelay(), getPeriod(), TimeUnit.MILLISECONDS);
        log.info("Register QuickTimerTask---- " + this.getClass().getSimpleName());
    }

    public static void cancelQuickTask() {
        SCHEDULED_EXECUTOR_SERVICE.shutdown();
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period) {
        return SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(command, initialDelay, period, TimeUnit.MILLISECONDS);
    }


    /**
     * 获取定时任务的延迟启动时间
     */
    protected long getDelay() {
        return 0;
    }

    /**
     * 获取定时任务的执行频率
     *
     * @return
     */
    protected abstract long getPeriod();
    
}
