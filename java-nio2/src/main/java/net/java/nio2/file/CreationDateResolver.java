package net.java.nio2.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Optional;

/**
 * 文件创建时间属性
 */
public class CreationDateResolver {
    public Instant resolveCreationTimeWithBasicAttributes(Path path) {
        try {
            final BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
            final FileTime fileTime = attr.creationTime();

            return fileTime.toInstant();
        } catch (IOException ex) {
            throw new RuntimeException("An issue occured went wrong when resolving creation time", ex);
        }
    }

    public Optional<Instant> resolveCreationTimeWithAttribute(Path path) {
        try {
            final FileTime creationTime = (FileTime) Files.getAttribute(path, "creationTime");

            return Optional
              .ofNullable(creationTime)
              .map((FileTime::toInstant));
        } catch (IOException ex) {
            throw new RuntimeException("An issue occured went wrong when resolving creation time", ex);
        }
    }    
}
