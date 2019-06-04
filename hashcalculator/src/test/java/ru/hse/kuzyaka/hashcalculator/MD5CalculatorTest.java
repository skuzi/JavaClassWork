package ru.hse.kuzyaka.hashcalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class MD5CalculatorTest {
    private static final byte[] AAAHASH = new byte[]
            {-120, -128, -51, -116, 31, -76, 2, 88, 87, 121, 118, 111, 104, 27, -122, -117};
    private static final byte[] BBBHASH = new byte[]
            {-51, -84, 16, -41, 31, -57, -36, -117, -128, 100, -84, -99, -69, -13, -9, 67};
    private static final byte[] DIRHASH = new byte[]
            {113, 46, -10, 5, 46, 22, -113, -103, 92, 78, -67, 60, -52, -34, 103, 2};
    private MD5Calculator concurrentHasher;
    private MD5Calculator singleHasher;

    @BeforeEach
    void setUp() {
        singleHasher = new MD5Calculator(false);
        concurrentHasher = new MD5Calculator(true);
    }

    @Test
    void testSimpleSingle1() throws IOException, NoSuchAlgorithmException {
        assertArrayEquals(AAAHASH, singleHasher.hash(
                Paths.get("src", "test", "resources", "fileWithAAA")));
    }

    @Test
    void testSimpleSingle2() throws IOException, NoSuchAlgorithmException {
        assertArrayEquals(BBBHASH, singleHasher.hash(
                Paths.get("src", "test", "resources", "fileWithBBB")));
    }

    @Test
    void testSimpleMulti1() throws IOException, NoSuchAlgorithmException {
        assertArrayEquals(AAAHASH, concurrentHasher.hash(
                Paths.get("src", "test", "resources", "fileWithAAA")));
    }

    @Test
    void testSimpleMulti2() throws IOException, NoSuchAlgorithmException {
        assertArrayEquals(BBBHASH, concurrentHasher.hash(
                Paths.get("src", "test", "resources", "fileWithBBB")));
    }

    @Test
    void testDirSingle() throws IOException, NoSuchAlgorithmException {
        assertArrayEquals(DIRHASH, singleHasher.hash(
                Paths.get("src", "test", "resources", "dir")
        ));
    }

    @Test
    void testDirMulti() throws IOException, NoSuchAlgorithmException {
        assertArrayEquals(DIRHASH, concurrentHasher.hash(
                Paths.get("src", "test", "resources", "dir")
        ));
    }
}