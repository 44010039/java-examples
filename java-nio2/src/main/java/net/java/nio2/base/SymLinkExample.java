package net.java.nio2.base;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SymLinkExample {
    public void createSymbolicLink(Path link, Path target) throws IOException {
        if (Files.exists(link)) {
            Files.delete(link);
        }
        Files.createSymbolicLink(link, target);
    }

    public void createHardLink(Path link, Path target) throws IOException {
        if (Files.exists(link)) {
            Files.delete(link);
        }
        Files.createLink(link, target);
    }

    public Path createTextFile() throws IOException {
        byte[] content = IntStream.range(0, 10000)
            .mapToObj(i -> i + System.lineSeparator())
            .reduce("", String::concat)
            .getBytes(StandardCharsets.UTF_8);
        Path filePath = Paths.get(".", "target.txt");
        Files.write(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return filePath;
    }

    public void printLinkFiles(Path path) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path file : stream) {
                if (Files.isDirectory(file)) {
                    // 递归调用
                    printLinkFiles(file);
                } else if (Files.isSymbolicLink(file)) {
                    log.info("File link {} with target {}", file, Files.readSymbolicLink(file));
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new SymLinkExample().printLinkFiles(Paths.get("."));
    }    
}
