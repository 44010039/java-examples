package net.java.nio2.base;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import lombok.extern.slf4j.Slf4j;

/**
 * 目录监控
 * 
 * 只能监控当前目录下的，也就是说你在其中一个子目录下创建文件或者文件夹，都是只会返回这个目录的修改事件。
 */
@Slf4j
public class DirectoryWatcherExample {
    public static void main(String[] args) throws IOException, InterruptedException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(System.getProperty("user.home") + "\\Downloads");
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                log.info("Event kind:" + event.kind() + ". File affected: " + event.context() + ".");
            }
            key.reset();
        }

        watchService.close();
    }    
}
