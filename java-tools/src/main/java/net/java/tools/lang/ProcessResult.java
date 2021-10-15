package net.java.tools.lang;

import java.util.Iterator;
import java.util.List;

import lombok.Getter;

/**
 * 命令结果
 */
@Getter
public class ProcessResult {
    /**
     * 返回代码
     */
    private final int exitCode;

    /**
     * 消息
     */
    private final List<String> output;

    public ProcessResult(int exitCode, List<String> output) {
        this.exitCode = exitCode;
        this.output = output;
    }

    /**
     * 消息文本
     * @return
     */
    public String getOutputText() {
        StringBuilder sb = new StringBuilder();

        Iterator<String> iterator = output.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next());
            if (iterator.hasNext()) {
                sb.append(System.lineSeparator());
            }
        }

        return sb.toString();
    }  

    public boolean isError() {
        return this.exitCode != 0;
    }

    public boolean isOk() {
        return !this.isError();
    }
}
