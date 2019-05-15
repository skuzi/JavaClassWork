package ru.hse.kuzyaka.hashcalculator;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

/** Provides interface for counting check sum of directory (or file), using MD5 hash **/
public class MD5Calculator {
    private static final int BUFFER_SIZE = 2048;

    private ForkJoinPool pool;
    private final boolean isParallel;

    /**
     * Constructs MD5Calculator
     * @param isParallel decides whether calculator uses multithreading
     */
    public MD5Calculator(boolean isParallel) {
        this.isParallel = isParallel;
        if (isParallel) {
            pool = new ForkJoinPool(4);
        }
    }

    /**
     * Counts check sum of the file (or directory) at the given path
     * @param path path of directory
     * @return check sum
     * @throws IOException if an error occurred while reading the file
     * @throws NoSuchAlgorithmException if MD5 is not supported
     */
    public byte[] hash(@NotNull Path path) throws IOException, NoSuchAlgorithmException {
        if (!Files.isDirectory(path)) {
            try (DigestInputStream input = new DigestInputStream(new FileInputStream(path.toString()),
                    MessageDigest.getInstance("MD5"))) {
                byte[] buffer = new byte[BUFFER_SIZE];
                while (input.read(buffer) != -1);
                return input.getMessageDigest().digest();
            } catch (IOException e1) {
                throw new IOException("Error occurred while reading the file", e1);
            }
        } else {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(path.getFileName().toString().getBytes());
            if (isParallel) {
                List<HashTask> tasks = new LinkedList<>();
                for (Path subPath : Files.walk(path).filter(Files::isRegularFile).collect(Collectors.toList())) {
                    var task = new HashTask(subPath);
                    pool.submit(task);
                    tasks.add(task);
                }
                for (var task : tasks) {
                    md.update(task.join());
                    if (task.exception != null) {
                        if (task.hasIOException) {
                            throw (IOException) task.exception;
                        } else {
                            throw (NoSuchAlgorithmException) task.exception;
                        }
                    }
                }
                return md.digest();
            } else {
                for (Path subPath : Files.walk(path).filter(Files::isRegularFile).collect(Collectors.toList())) {
                    md.update(hash(subPath));
                }
                return md.digest();
            }
        }
    }


    private class HashTask extends RecursiveTask<byte[]> {
        private Path path;
        private Exception exception;
        private boolean hasIOException = false;
        @Override
        protected byte[] compute() {
            byte[] hash = new byte[0];
            try {
                hash = hash(path);
            } catch (IOException e) {
                exception = new Exception("Error occurred while reading the file", e);
                hasIOException = true;
            } catch (NoSuchAlgorithmException e) {
                exception = new Exception("MD5 is not supported", e);
            }
            return hash;
        }

        private HashTask(Path path) {
            this.path = path;
        }
    }
}
