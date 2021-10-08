package net.java.nio2.base;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class PathVsFileUnitTest {
    @Test
    public void whenCreateFileAndPath_thenShouldPointTheSame() {
        File file = new File("nio2/tutorial.txt");
        Path path = Paths.get("nio2/tutorial.txt");

        Assertions.assertThat(file.toString()).isEqualTo(path.toString());
        Assertions.assertThat(file.toPath()).isEqualTo(path);
        Assertions.assertThat(path.toFile()).isEqualTo(file);
        Assertions.assertThat(file).isEqualTo(new File("nio2", "tutorial.txt"));
        Assertions.assertThat(path).isEqualTo(Paths.get("nio2", "tutorial.txt"))
                .isEqualTo(Paths.get("nio2").resolve("tutorial.txt"));
    }

    @Test
    public void givenNoFile_whenDelete_thenReportError() {
        File file = new File("tutorial.txt");
        Path path = Paths.get("tutorial.txt");

        Assertions.assertThat(file.delete()).isFalse();
        Assertions.assertThatThrownBy(() -> Files.delete(path)).isInstanceOf(IOException.class)
                .isInstanceOf(NoSuchFileException.class);
    }

    @Test
    public void givenExistedFile_whenReadMetadata_thenShouldBeSame() throws IOException {
        File file = new File("tutorial.txt");
        Path path = Paths.get("tutorial.txt");

        Throwable throwable = Assertions.catchThrowable(() -> Files.createFile(path));
        Assertions.assertThat(throwable).isNull();

        Assertions.assertThat(file.exists()).isEqualTo(Files.exists(path));
        Assertions.assertThat(file.isFile()).isEqualTo(Files.isRegularFile(path));
        Assertions.assertThat(file.isDirectory()).isEqualTo(Files.isDirectory(path));
        Assertions.assertThat(file.canRead()).isEqualTo(Files.isReadable(path));
        Assertions.assertThat(file.canWrite()).isEqualTo(Files.isWritable(path));
        Assertions.assertThat(file.canExecute()).isEqualTo(Files.isExecutable(path));
        Assertions.assertThat(file.isHidden()).isEqualTo(Files.isHidden(path));
    }

    @Test
    public void givenExistedFile_whenUriPathOperations_thenShouldBeSame() throws IOException {
        File file = new File("tutorial.txt");
        Path path = Paths.get("tutorial.txt");

        Throwable throwable = Assertions.catchThrowable(() -> Files.createFile(path));
        Assertions.assertThat(throwable).isNull();

        Assertions.assertThat(file.toURI()).isEqualTo(path.toUri());
        Assertions.assertThat(file.getAbsolutePath()).isEqualTo(path.toAbsolutePath().toString());
        Assertions.assertThat(file.getCanonicalPath()).isEqualTo(path.toRealPath().toString())
                .isEqualTo(path.toRealPath().normalize().toString());
    }

    @AfterEach
    public void afterEach() throws IOException {
        Path path = Paths.get("tutorial.txt");
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }
}
