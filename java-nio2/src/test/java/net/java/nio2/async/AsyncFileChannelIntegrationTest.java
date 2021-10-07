package net.java.nio2.async;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/**
 * AsynchronousFileChannel文件读写
 * 
 * Java
 * NIO中的FileChannel是一个连接到文件的通道。可以通过文件通道读写文件。FileChannel无法设置为非阻塞模式，他总是运行在阻塞模式下。
 * 在Java 7中，AsynchronousFileChannel被添加到Java
 * NIO。AsynchronousFileChannel使读取数据，并异步地将数据写入文件成为可能。
 * 
 * 在 Java 7 中，AsynchronousFileChannel 已添加到 Java NIO 中，它可以异步读取数据并将数据写入文件。
 * 先说明，异步和阻塞/非阻塞没有关系，下面简单介绍一下相关概念： 1.
 * 阻塞是线程的一个状态，线程发起任务请求然后一直等，直到到任务完成再把结果返回，如果任务未完成当前线程会被挂起。 2.
 * 非阻塞是发起任务请求之后先马上返回去做别的事，然后再时不时主动查看任务请求是否被完成。（轮询） 3.
 * 同步是同时只能有一个线程处理某个对象或者操作，例如一个线程占用了一个对象，其他线程如果要访问此对象，则需要等之前得线程操作完成返回，相关概念有同步方法、同步代码块、对象锁。
 * 4.
 * 异步是如果完成任务时，遇到一个耗时操作（或者对象已经被别的线程占用了），不等待而是去做其他事情，也不主动查看是否完成，而是等耗时操作完成，发通知再叫线程回来处理结果，常见的例子就是
 * Ajax ，相关概念有回调函数等。
 * 
 * 一般异步都是和非阻塞组合使用的。
 */
public class AsyncFileChannelIntegrationTest {
    /**
     * 使用Futrue读取数据
     * 
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     */
    @Test
    public void givenPath_whenReadsContentWithFuture_thenCorrect()
            throws ExecutionException, InterruptedException, IOException {
        final Path path = Paths.get(URI.create(this.getClass().getClassLoader().getResource("file.txt").toString()));
        final AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);

        final ByteBuffer buffer = ByteBuffer.allocate(1024);

        final Future<Integer> operation = fileChannel.read(buffer, 0);

        operation.get();

        final String fileContent = new String(buffer.array()).trim();
        buffer.clear();

        assertEquals(fileContent, "baeldung.com");
    }

    /**
     * 使用CompletionHandler读取数据
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void givenPath_whenReadsContentWithCompletionHandler_thenCorrect() throws IOException, InterruptedException {
        final Path path = Paths.get(URI.create(this.getClass().getClassLoader().getResource("file.txt").toString()));
        final AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);

        final ByteBuffer buffer = ByteBuffer.allocate(1024);

        fileChannel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                // result is number of bytes read
                // attachment is the buffer
                attachment.flip();
                final String fileContent = new String(buffer.array()).trim();
                assertEquals(fileContent, "baeldung.com");
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {

            }
        });

        // 等待
        TimeUnit.SECONDS.sleep(1);
    }

    /**
     * 使用Futrue写数据
     * 
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void givenPathAndContent_whenWritesToFileWithFuture_thenCorrect()
            throws IOException, ExecutionException, InterruptedException {
        final String fileName = UUID.randomUUID().toString();
        final Path path = Paths.get(fileName);
        final AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE,
                StandardOpenOption.CREATE, StandardOpenOption.DELETE_ON_CLOSE);

        final ByteBuffer buffer = ByteBuffer.allocate(1024);
        final long position = 0;

        buffer.put("hello world".getBytes());
        buffer.flip();

        final Future<Integer> operation = fileChannel.write(buffer, position);
        buffer.clear();

        operation.get();

        final String content = readContent(path);
        assertEquals("hello world", content);
    }

    /**
     * 使用CompletionHandler写数据
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void givenPathAndContent_whenWritesToFileWithHandler_thenCorrect() throws IOException, InterruptedException {
        final String fileName = UUID.randomUUID().toString();
        final Path path = Paths.get(fileName);
        final AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE,
                StandardOpenOption.CREATE, StandardOpenOption.DELETE_ON_CLOSE);

        final ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put("hello world".getBytes());
        buffer.flip();

        fileChannel.write(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                // result is number of bytes written
                // attachment is the buffer
                try {
                    String content = readContent(path);
                    assertEquals("hello world", content);
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {

            }
        });

        // 等待
        TimeUnit.SECONDS.sleep(1);
    }

    //

    private String readContent(Path file) throws ExecutionException, InterruptedException {
        AsynchronousFileChannel fileChannel = null;
        try {
            fileChannel = AsynchronousFileChannel.open(file, StandardOpenOption.READ);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final ByteBuffer buffer = ByteBuffer.allocate(1024);

        final Future<Integer> operation = fileChannel.read(buffer, 0);

        operation.get();

        final String fileContent = new String(buffer.array()).trim();
        buffer.clear();
        return fileContent;
    }
}
