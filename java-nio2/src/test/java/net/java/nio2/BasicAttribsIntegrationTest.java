package net.java.nio2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import lombok.extern.slf4j.Slf4j;

/**
 * 基本属性
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class BasicAttribsIntegrationTest {
    private final String HOME = System.getProperty("user.home");
    private BasicFileAttributes basicAttribs;

    @BeforeAll
    public void setup() throws IOException {
        Path home = Paths.get(HOME);
        BasicFileAttributeView basicView = Files.getFileAttributeView(home, BasicFileAttributeView.class);
        basicAttribs = basicView.readAttributes();
    }

    @Test
    public void givenFileTimes_whenComparesThem_ThenCorrect() {
        FileTime created = basicAttribs.creationTime();
        FileTime modified = basicAttribs.lastModifiedTime();
        FileTime accessed = basicAttribs.lastAccessTime();

        log.debug("Created: " + created);
        log.debug("Modified: " + modified);
        log.debug("Accessed: " + accessed);

    }

    @Test
    public void givenPath_whenGetsFileSize_thenCorrect() {
        long size = basicAttribs.size();
        assertTrue(size > 0);
    }

    @Test
    public void givenPath_whenChecksIfDirectory_thenCorrect() {
        boolean isDir = basicAttribs.isDirectory();
        assertTrue(isDir);
    }

    @Test
    public void givenPath_whenChecksIfFile_thenCorrect() {
        boolean isFile = basicAttribs.isRegularFile();
        assertFalse(isFile);
    }

    @Test
    public void givenPath_whenChecksIfSymLink_thenCorrect() {
        boolean isSymLink = basicAttribs.isSymbolicLink();
        assertFalse(isSymLink);
    }

    @Test
    public void givenPath_whenChecksIfOther_thenCorrect() {
        boolean isOther = basicAttribs.isOther();
        assertFalse(isOther);
    }    
}
