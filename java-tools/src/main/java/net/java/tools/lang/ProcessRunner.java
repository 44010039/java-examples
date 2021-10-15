package net.java.tools.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessRunner implements Callable<ProcessResult> {
    private final static int DEFAULT_OUTPUT_LINES = 100;

    private final ProcessRunnerBuilder properties;

    private ProcessBuilder builder;

    private Process process;

    private ProcessRunner(ProcessRunnerBuilder builder) {
        this.properties = builder.copy();
    }

    private void prepare() {
        builder = new ProcessBuilder(properties.command);
        builder.environment().putAll(properties.environments);
        builder.redirectErrorStream(properties.redirectErrorStream);
        if (properties.directory != null) {
            builder.directory(properties.directory.toFile());
        }
        if (properties.redirectOutput != null) {
            builder.redirectOutput(properties.redirectOutput);
        }
        if (properties.redirectError != null) {
            builder.redirectError(properties.redirectError);
        }

        if (log.isDebugEnabled()) {
            log.info("Process runner created: {}", properties.toString());
        }
    }

    public void stop() {
        if (process != null && process.isAlive()) {
            process.destroyForcibly();
        }
    }

    @Override
    public ProcessResult call() {
        List<String> output = new ArrayList<>(DEFAULT_OUTPUT_LINES);
        int exitCode = -1;
        try {
            this.process = builder.start();
            try (InputStream is = process.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader reader = new BufferedReader(isr)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("process {} output: {}", process.hashCode(), line);
                    }
                    output.add(line);
                }
            }
        } catch (IOException e) {
            log.error("Dumper exception", e);
        }

        
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            log.error("Unexpected interruption", e);
        }

        return new ProcessResult(exitCode, output);
    }

    public static ProcessRunnerBuilder builder() {
        return new ProcessRunnerBuilder();
    }

    @Getter
    @ToString
    public static final class ProcessRunnerBuilder {
        private List<String> command;

        private Map<String, String> environments;

        private Path directory;

        public boolean redirectErrorStream = false;

        private Redirect redirectOutput;

        private Redirect redirectError;

        private ProcessRunnerBuilder() {
            this.environments = new HashMap<>();
            this.command = new ArrayList<>();
        }

        public ProcessRunnerBuilder copy() {
            ProcessRunnerBuilder rslt = new ProcessRunnerBuilder();
            rslt.command.addAll(this.command);
            rslt.environments.putAll(this.environments);
            rslt.directory = this.directory;
            rslt.redirectErrorStream = this.redirectErrorStream;
            rslt.redirectOutput = this.redirectOutput;
            return rslt;
        }

        public ProcessRunnerBuilder withCommand(List<String> command) {
            this.command = command;
            return this;
        }

        public ProcessRunnerBuilder withCommand(String... command) {
            this.command = Arrays.asList(command);
            return this;
        }

        public ProcessRunnerBuilder withEnvironment(String key, String value) {
            this.environments.put(key, value);
            return this;
        }

        public ProcessRunnerBuilder withEnvironments(Map<String, String> environments) {
            this.environments = environments;
            return this;
        }

        /**
         * 工作目录
         * 
         * @param path
         * @return
         */
        public ProcessRunnerBuilder withDirectory(String path) {
            this.directory = Paths.get(path);
            return this;
        }

        public ProcessRunnerBuilder withRedirectErrorStream(boolean redirectErrorStream) {
            this.redirectErrorStream = redirectErrorStream;
            return this;
        }

        public ProcessRunnerBuilder withRedirectOutput(String path, boolean isAppend) {
            return withRedirectOutput(Paths.get(path), isAppend);
        }

        public ProcessRunnerBuilder withRedirectOutput(Path path, boolean isAppend) {
            if (isAppend) {
                this.redirectOutput = Redirect.appendTo(path.toFile());
            } else {
                this.redirectOutput = Redirect.to(path.toFile());
            }
            return this;
        }

        public ProcessRunnerBuilder withRedirectError(String path, boolean isAppend) {
            return withRedirectError(Paths.get(path), isAppend);
        }

        public ProcessRunnerBuilder withRedirectError(Path path, boolean isAppend) {
            if (isAppend) {
                this.redirectError = Redirect.appendTo(path.toFile());
            } else {
                this.redirectError = Redirect.to(path.toFile());
            }
            return this;
        }

        public ProcessRunner build() {
            ProcessRunner rslt = new ProcessRunner(this);
            rslt.prepare();
            return rslt;
        }
    }
}
