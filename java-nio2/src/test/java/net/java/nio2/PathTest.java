package net.java.nio2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PathTest {
    private static final String HOME = System.getProperty("user.home");

    // creating a path
    @Test
    public void givenPathString_whenCreatesPathObject_thenCorrect() {
        Path p = Paths.get("/articles/nio2");
        assertEquals("\\articles\\nio2", p.toString());

    }

    @Test
    public void givenPathParts_whenCreatesPathObject_thenCorrect() {
        Path p = Paths.get("/articles", "nio2");
        assertEquals("\\articles\\nio2", p.toString());

    }

    // retrieving path info
    @Test
    public void givenPath_whenRetrievesFileName_thenCorrect() {
        Path p = Paths.get("/articles/nio2/logs");
        assertEquals("logs", p.getFileName().toString());
    }

    @Test
    public void givenPath_whenRetrievesNameByIndex_thenCorrect() {
        Path p = Paths.get("/articles/nio2/logs");
        assertEquals("articles", p.getName(0).toString());
        assertEquals("nio2", p.getName(1).toString());
        assertEquals("logs", p.getName(2).toString());
    }

    @Test
    public void givenPath_whenCountsParts_thenCorrect() {
        Path p = Paths.get("/articles/nio2/logs");
        assertEquals(3, p.getNameCount());
    }

    @Test
    public void givenPath_whenCanRetrieveSubsequenceByIndex_thenCorrect() {
        Path p = Paths.get("/articles/nio2/logs");
        assertEquals("articles", p.subpath(0, 1).toString());
        assertEquals("articles\\nio2", p.subpath(0, 2).toString());
        assertEquals("articles\\nio2\\logs", p.subpath(0, 3).toString());
        assertEquals("nio2", p.subpath(1, 2).toString());
        assertEquals("nio2\\logs", p.subpath(1, 3).toString());
        assertEquals("logs", p.subpath(2, 3).toString());
    }

    @Test
    public void givenPath_whenRetrievesParent_thenCorrect() {
        Path p1 = Paths.get("/articles/nio2/logs");
        Path p2 = Paths.get("/articles/nio2");
        Path p3 = Paths.get("/articles");
        Path p4 = Paths.get("/");

        assertEquals("\\articles\\nio2", p1.getParent().toString());
        assertEquals("\\articles", p2.getParent().toString());
        assertEquals("\\", p3.getParent().toString());
        assertEquals(null, p4.getParent());
    }

    @Test
    public void givenPath_whenRetrievesRoot_thenCorrect() {
        Path p1 = Paths.get("/articles/nio2/logs");
        Path p2 = Paths.get("c:/articles/nio2/logs");

        assertEquals("\\", p1.getRoot().toString());
        assertEquals("c:\\", p2.getRoot().toString());
    }

    // removing redundancies from path
    @Test
    public void givenPath_whenRemovesRedundancies_thenCorrect1() {
        Path p = Paths.get("/home/./nio2/articles");
        p = p.normalize();
        assertEquals("\\home\\nio2\\articles", p.toString());
    }

    @Test
    public void givenPath_whenRemovesRedundancies_thenCorrect2() {
        Path p = Paths.get("/home/nio2/../articles");
        p = p.normalize();
        assertEquals("\\home\\articles", p.toString());
    }

    // converting a path
    @Test
    public void givenPath_whenConvertsToBrowseablePath_thenCorrect() {
        Path p = Paths.get("/home/nio2/articles.html");
        URI uri = p.toUri();
        // output: file:///C:/home/nio2/articles.html
        log.info(uri.toString());
    }

    @Test
    public void givenPath_whenConvertsToAbsolutePath_thenCorrect() {
        Path p = Paths.get("/home/nio2/articles.html");
        // output: C:\home\nio2\articles.html
        log.info(p.toAbsolutePath().toString());
    }

    @Test
    public void givenAbsolutePath_whenRetainsAsAbsolute_thenCorrect() {
        Path p = Paths.get("E:\\home\\nio2\\articles.html");
        assertEquals("E:\\home\\nio2\\articles.html", p.toAbsolutePath().toString());
    }

    @Test
    public void givenExistingPath_whenGetsRealPathToFile_thenCorrect() throws IOException {
        Path p = Paths.get(HOME);
        assertEquals(HOME, p.toRealPath().toString());
    }

    @Test
    public void givenInExistentPath_whenFailsToConvert_thenCorrect() throws IOException {
        Assertions.assertThrows(NoSuchFileException.class, () -> {
            Path p = Paths.get("E:\\home\\nio2\\articles.html");
            p.toRealPath();
        });

    }

    // joining paths
    @Test
    public void givenTwoPaths_whenJoinsAndResolves_thenCorrect() throws IOException {
        Path p = Paths.get("/nio2/articles");
        assertEquals("\\nio2\\articles\\java", p.resolve("java").toString());
    }

    @Test
    public void givenAbsolutePath_whenResolutionRetainsIt_thenCorrect() throws IOException {
        Path p = Paths.get("/nio2/articles");
        assertEquals("C:\\nio2\\articles\\java", p.resolve("C:\\nio2\\articles\\java").toString());
    }

    @Test
    public void givenPathWithRoot_whenResolutionRetainsIt_thenCorrect2() throws IOException {
        Path p = Paths.get("/nio2/articles");
        assertEquals("\\java", p.resolve("/java").toString());
    }

    // creating a path between 2 paths
    @Test
    public void givenSiblingPaths_whenCreatesPathToOther_thenCorrect() throws IOException {
        Path p1 = Paths.get("articles");
        Path p2 = Paths.get("authors");
        assertEquals("..\\authors", p1.relativize(p2).toString());
        assertEquals("..\\articles", p2.relativize(p1).toString());
    }

    @Test
    public void givenNonSiblingPaths_whenCreatesPathToOther_thenCorrect() throws IOException {
        Path p1 = Paths.get("/nio2");
        Path p2 = Paths.get("/nio2/authors/articles");
        assertEquals("authors\\articles", p1.relativize(p2).toString());
        assertEquals("..\\..", p2.relativize(p1).toString());
    }

    // comparing 2 paths
    @Test
    public void givenTwoPaths_whenTestsEquality_thenCorrect() throws IOException {
        Path p1 = Paths.get("/nio2/articles");
        Path p2 = Paths.get("/nio2/articles");
        Path p3 = Paths.get("/nio2/authors");

        assertTrue(p1.equals(p2));
        assertFalse(p1.equals(p3));
    }

    @Test
    public void givenPath_whenInspectsStart_thenCorrect() {
        Path p1 = Paths.get("/nio2/articles");
        assertTrue(p1.startsWith("/nio2"));
    }

    @Test
    public void givenPath_whenInspectsEnd_thenCorrect() {
        Path p1 = Paths.get("/nio2/articles");
        assertTrue(p1.endsWith("articles"));
    }
}
