package ru.hse.kuzyaka.myftp;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String LAUNCH__ERROR = "launch format: <host name> <port number>";
    private static final String QUERY_FORMAT = "list query: <1 : int> <path: String> + \n " +
            "get query: <2 : int> <path: String>";
    private Scanner in;
    private PrintStream out;
    private String hostName;
    private int portNumber;

    public Client(String hostName, int portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.in = new Scanner(System.in);
        this.out = new PrintStream(System.out, true);
        out.println(QUERY_FORMAT);
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println(LAUNCH__ERROR);
            return;
        }

        String hostName = args[0];
        int portNumber;
        try {
            portNumber = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println(LAUNCH__ERROR);
            return;
        }
        new Client(hostName, portNumber).start();
    }

    private void start() {
        while (in.hasNext()) {
            String query = in.nextLine();
            String[] args = query.split(" ");
            byte type;
            if (args.length != 2) {
                out.println("bad query!");
                out.println(QUERY_FORMAT);
                continue;
            } else {
                try {
                    type = Byte.parseByte(args[0]);
                } catch (NumberFormatException e) {
                    out.println("bad query!");
                    out.println(QUERY_FORMAT);
                    continue;
                }
            }
            String path = args[1];
            QueryStatus status = processQuery(type, path);
            switch (status) {
                case SUCCESS:
                    if (type == QueryType.GET_QUERY.value()) {
                        out.println("Success!");
                    }
                    break;
                case ANSWER_ERROR:
                    out.println("Error occurred while getting answer");
                    break;
                case CONNECT_ERROR:
                    out.println("error occurred while connecting to server");
                    break;
                case FILE_NOT_FOUND:
                    out.println("file not found");
            }
        }
    }

    private QueryStatus processQuery(byte type, String path) {
        try (Socket socket = new Socket(hostName, portNumber)) {
            var dataIn = new DataInputStream(socket.getInputStream());
            var dataOut = new DataOutputStream(socket.getOutputStream());
            dataOut.writeByte(type);
            dataOut.write(path.getBytes());
            dataOut.flush();
            socket.shutdownOutput();
            try {
                if (type == QueryType.LIST_QUERY.value()) {
                    listFiles(dataIn);
                } else {
                    try {
                        byte[] file = saveFile(dataIn);
                        for (byte b : file) {
                            out.println(Integer.toHexString(b & 0xFF));
                        }
                    } catch (FileNotFoundException e) {
                        return QueryStatus.FILE_NOT_FOUND;
                    }
                }
            } catch (IOException e) {
                return QueryStatus.ANSWER_ERROR;
            }
        } catch (IOException e) {
            return QueryStatus.CONNECT_ERROR;
        }

        return QueryStatus.SUCCESS;
    }

    private byte[] saveFile(DataInputStream dataIn) throws IOException {
        int size = dataIn.readInt();
        if (size == -1) {
            throw new FileNotFoundException("file not found on server");
        }
        byte[] buffer = new byte[size];
        int read = dataIn.read(buffer);
        if (read != size || dataIn.available() > 0) {
            throw new IOException("input has the wrong format");
        }
        return buffer;
    }

    private void listFiles(DataInputStream dataIn) throws IOException {
        int size = dataIn.readInt();
        if (size == -1) {
            throw new FileNotFoundException("file not found on server");
        }
        for (int i = 0; i < size; i++) {
            String fileName = dataIn.readUTF();
            boolean isDir = dataIn.readBoolean();
            out.println(fileName + " is " + (isDir ? "directory" : "file"));
        }
    }
}