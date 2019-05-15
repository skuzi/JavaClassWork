package ru.hse.kuzyaka.hashcalculator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Path path = Paths.get(args[0]);
        long startTime;
        long multiThreadTime;
        long singleThreadTime;
        byte[] hashMultiThread;
        byte[] hashSingleThread;
        try {
            startTime = System.currentTimeMillis();
            hashMultiThread = new MD5Calculator(true).hash(path);
            multiThreadTime = System.currentTimeMillis() - startTime;
            startTime = System.currentTimeMillis();
            hashSingleThread = new MD5Calculator(false).hash(path);
            singleThreadTime = System.currentTimeMillis() - startTime;
        } catch (NoSuchAlgorithmException | IOException e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.println("Single thread result is: " + Arrays.toString(hashSingleThread));
        System.out.println("Single thread time is: " + singleThreadTime);
        System.out.println("-------------------------------------");
        System.out.println("Multi thread result is: " + Arrays.toString(hashMultiThread));
        System.out.println("Multi thread time is: " + multiThreadTime);
    }
}
