package net.java.nio2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

public class SymLinkExampleManualTest {
    /**
     * 注意异常：客户端没有所需的特权。
     * 
     * 参考：
     * https://stackoverflow.com/questions/23217460/how-to-create-soft-symbolic-link-using-java-nio-files
     * 
     * 运行bin目录中的Microsoft-Windows-GroupPolicy-ClientExtensions.bat文件即可
     * @throws IOException
     */
    // @Disabled("客户端没有所需的特权")
    @Test
    public void whenUsingFiles_thenCreateSymbolicLink() throws IOException {
        SymLinkExample example = new SymLinkExample();
        Path filePath = example.createTextFile();
        Path linkPath = Paths.get(".", "symbolic_link.txt");
        example.createSymbolicLink(linkPath, filePath);
        assertTrue(Files.isSymbolicLink(linkPath));
    }

    @Test
    public void whenUsingFiles_thenCreateHardLink() throws IOException {
        SymLinkExample example = new SymLinkExample();
        Path filePath = example.createTextFile();
        Path linkPath = Paths.get(".", "hard_link.txt");
        example.createHardLink(linkPath, filePath);
        assertFalse(Files.isSymbolicLink(linkPath));
        assertEquals(filePath.toFile()
            .length(),
            linkPath.toFile()
                .length());
    }    
}
