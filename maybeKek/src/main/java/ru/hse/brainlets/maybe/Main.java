package ru.hse.brainlets.maybe;

import java.io.*;

public class Main {
    private static Maybe<Integer> readInt(DataInputStream in) {
        try {
            return Maybe.just(in.readInt());
        }
        catch (Exception e) {
            return Maybe.nothing();
        }
    }

    public static void main(String[] args) {
        var fileIn = new File(args[0]);
        var fileOut = new File(args[1]);
        try(var fin = new FileInputStream(fileIn); var din = new DataInputStream(fin); var fout = new FileOutputStream(fileOut); var dout = new DataOutputStream(fout)) {
            while(din.available() > 0) {
                var result = readInt(din).map(i -> i * i);
                if(result.isPresent()) {
                    dout.writeInt(result.get());
                    dout.writeChar('\n');
                } else {
                    dout.writeChars("Nothing\n");
                }

            }
        } catch (Exception e) {
            System.out.println("Oops...");
        }

    }
}
