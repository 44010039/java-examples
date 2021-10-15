package net.java.tools.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import net.java.tools.lang.ProcessRunner.ProcessRunnerBuilder;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProcessRunnerTest {
    private ExecutorService executorService;

    @BeforeAll
    public void beforeAll() {
        executorService = Executors.newSingleThreadExecutor();
    }

    @AfterAll
    public void afterAll() {
        executorService.shutdown();
    }

    @Test
    public void givenCommand_whenRedirectErrorStreamIsTrue_thenSuccess()
            throws IOException, InterruptedException, ExecutionException {
        ProcessRunner runner = ProcessRunner.builder().withCommand("java", "-version").withRedirectErrorStream(true)
                .build();
        Future<ProcessResult> futuer = executorService.submit(runner);
        ProcessResult rslt = futuer.get();

        assertEquals(0, rslt.getExitCode());
        assertTrue(rslt.getOutput().size() > 0);
        assertTrue(rslt.getOutputText().indexOf("java version") >= 0);
    }

    @Test
    public void givenCommand_whenRedirectErrorStreamIsFalse_thenSuccess()
            throws IOException, InterruptedException, ExecutionException {
        ProcessRunner runner = ProcessRunner.builder().withCommand("java", "-version").withRedirectErrorStream(false)
                .build();
        Future<ProcessResult> futuer = executorService.submit(runner);
        ProcessResult rslt = futuer.get();

        assertEquals(0, rslt.getExitCode());
        assertTrue(rslt.getOutput().size() == 0);
    }

    @Test
    public void givenLongCommand_whenTimeOut_thenException() {
        // 此命令一直运行
        ProcessRunner runner = ProcessRunner.builder().withRedirectErrorStream(true)
                .withCommand("ping", "127.0.0.1", "-t").build();
        Future<ProcessResult> futuer = executorService.submit(runner);

        assertThrows(TimeoutException.class, () -> futuer.get(1, TimeUnit.SECONDS));

        // 必须添加，否则整类测试时会一直执行
        runner.stop();
    }

    @Test
    public void givenErrorCommand_thenIOException() {
        ProcessRunner runner = ProcessRunner.builder().withCommand("abcde", "-version").build();
        Future<ProcessResult> futuer = executorService.submit(runner);
        assertThrows(ExecutionException.class, () -> futuer.get());

    }

    @Test
    public void givenErrorCommandOptions_thenSuccess() throws InterruptedException, ExecutionException {
        ProcessRunner runner = ProcessRunner.builder().withCommand("java", "-v").build();
        Future<ProcessResult> futuer = executorService.submit(runner);

        ProcessResult rslt = futuer.get();

        assertNotEquals(0, rslt.getExitCode());
    }

    @Test
    public void givenCommand_whenModifyEnvironment() throws IOException, InterruptedException, ExecutionException {
        ProcessRunner runner = ProcessRunner.builder().withCommand(getGreetingCommand())
                .withEnvironment("GREETING", "Springboot").build();
        Future<ProcessResult> futuer = executorService.submit(runner);
        ProcessResult rslt = futuer.get();

        assertEquals(0, rslt.getExitCode());
        assertTrue(rslt.getOutputText().indexOf("Springboot") >= 0);
    }

    @Test
    public void givenList_whenModifyWorkingDir() throws IOException, InterruptedException, ExecutionException {
        ProcessRunner runner = ProcessRunner.builder().withCommand(getDirectoryListingCommand()).withDirectory("src")
                .build();
        Future<ProcessResult> futuer = executorService.submit(runner);
        ProcessResult rslt = futuer.get();

        assertEquals(0, rslt.getExitCode());
        assertTrue(rslt.getOutputText().indexOf("test") >= 0);
    }

    @Test
    public void givenCommand_whenRedirectStandardOutput_thenSuccessWriting()
            throws IOException, InterruptedException, ExecutionException {
        Path redirectOutput = Paths.get("target\\java-version.log");

        ProcessRunner runner = ProcessRunner.builder().withCommand("java", "-version")
                // 必须是true, 才能输出到文件
                .withRedirectErrorStream(true)
                .withRedirectOutput(redirectOutput, false).build();
        Future<ProcessResult> futuer = executorService.submit(runner);
        ProcessResult rslt = futuer.get();

        assertEquals(0, rslt.getExitCode());

        List<String> lines = Files.lines(redirectOutput).collect(Collectors.toList());
        assertEquals(3, lines.size());
    }

    @Test
    public void givenCommand_whenRedirectStandardOutput_thenSuccessAppending()
            throws IOException, InterruptedException, ExecutionException {
        Path redirectOutput = Paths.get("target\\java-version.log");

        ProcessRunnerBuilder runnerBuilder = ProcessRunner.builder().withCommand("java", "-version")
                .withRedirectErrorStream(true).withRedirectOutput(redirectOutput, false);

        ProcessRunner runner = runnerBuilder.build();
        executorService.submit(runner);

        ProcessRunner runner2 = runnerBuilder.copy().withRedirectOutput(redirectOutput, true).build();
        Future<ProcessResult> futuer = executorService.submit(runner2);
        futuer.get();

        List<String> lines = Files.lines(redirectOutput).collect(Collectors.toList());
        assertEquals(6, lines.size());
    }

    private List<String> getDirectoryListingCommand() {
        return isWindows() ? Arrays.asList("cmd.exe", "/c", "dir") : Arrays.asList("/bin/sh", "-c", "ls");
    }

    private List<String> getGreetingCommand() {
        return isWindows() ? Arrays.asList("cmd.exe", "/c", "echo %GREETING%")
                : Arrays.asList("/bin/bash", "-c", "echo $GREETING");
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }
}
