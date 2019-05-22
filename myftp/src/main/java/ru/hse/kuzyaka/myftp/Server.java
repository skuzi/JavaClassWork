package ru.hse.kuzyaka.myftp;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class Server {

    private final static int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        try {
            run();
        } catch (Exception ignored) {}
    }

    public static void run() throws Exception {

        InetAddress host = InetAddress.getByName("localhost");
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(host, 1984));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            if (selector.select() <= 0)
                continue;

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            while (iterator.hasNext()) {

                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    SocketChannel channel = serverSocketChannel.accept();
                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_READ);
                }
                if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer inpBuf = ByteBuffer.allocate(BUFFER_SIZE);
                    channel.read(inpBuf);

                    DataInputStream inputStream = new DataInputStream(new ByteBufferBackedInputStream(inpBuf));

                    ByteBuffer outBuf = ByteBuffer.allocate(BUFFER_SIZE);
                    outBuf.clear();
                    DataOutputStream outputStream = new DataOutputStream(new ByteBufferBackedOutputStream(outBuf));

                    int query = inputStream.readInt();

                    if (query == 1) {
                        String path = inputStream.readUTF();
                        putList(path, outputStream);
                    } else {
                        String path = inputStream.readUTF();
                        putFile(path, outputStream);
                    }

                    outBuf.flip();
                    while(outBuf.hasRemaining()) {
                        channel.write(outBuf);
                    }
                }
            }
        }
    }

    private static void putFile(String path, DataOutputStream outputStream) throws IOException {
        File file = new File(path);

        outputStream.writeLong(!file.isFile() ? -1 : (int) file.length());

        if (!file.exists() || file.isDirectory()) {
            return;
        }

        byte[] buffer  = new byte[BUFFER_SIZE];

        try (FileInputStream inputStream = new FileInputStream(path)) {
            for (int size = inputStream.read(buffer); size > 0; size = inputStream.read(buffer)) {
                outputStream.write(buffer, 0, size);
            }
        }
    }

    private static void putList(String path, DataOutputStream outputStream) throws IOException {
        File[] files = new File(path).listFiles();
        outputStream.writeInt(files == null ? -1 : files.length);

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file != null) {
                outputStream.writeUTF(file.getName());
                outputStream.writeBoolean(file.isDirectory());
            }
        }
    }

    private static class ByteBufferBackedInputStream extends InputStream {

        private ByteBuffer buf;

        public ByteBufferBackedInputStream(ByteBuffer buf) {
            this.buf = buf;
        }

        @Override
        public int read() {
            if (!buf.hasRemaining()) {
                return -1;
            }
            return buf.get() & 0xFF;
        }

        @Override
        public int read(@NotNull byte[] bytes, int off, int len) {
            if (!buf.hasRemaining()) {
                return -1;
            }

            len = Math.min(len, buf.remaining());
            buf.get(bytes, off, len);
            return len;
        }
    }

    public static class ByteBufferBackedOutputStream extends OutputStream {

        private ByteBuffer buf;

        public ByteBufferBackedOutputStream(ByteBuffer buf) {
            this.buf = buf;
        }

        @Override
        public void write(int b) {
            buf.put((byte) b);
        }

        @Override
        public void write(@NotNull byte[] bytes, int off, int len)  {
            buf.put(bytes, off, len);
        }

    }

    public static Process start() throws IOException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = Server.class.getCanonicalName();

        ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, className);

        return builder.start();
    }
}


