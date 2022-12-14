package ru.nsu.fit.nioproxy;

import java.io.Console;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    private static final int BUFFER_SIZE = 1024;


    public static void main(String[] args) {

        int port = Integer.parseInt(args[0]);
        Console console = System.console();
        String nextLine;

        try {
            InetSocketAddress clientAddress =
                    new InetSocketAddress(InetAddress.getLocalHost(), port);
            SocketChannel clientSocket = SocketChannel.open(clientAddress);

            nextLine = console.readLine();
            while (!nextLine.equals("exit")) {
                ByteBuffer myBuffer = ByteBuffer.allocate(BUFFER_SIZE);
                myBuffer.put(nextLine.getBytes());
                myBuffer.flip();
                clientSocket.write(myBuffer);
                System.out.println("Sent " + new String(myBuffer.array()).trim());

                myBuffer.clear();
                myBuffer = ByteBuffer.allocate(BUFFER_SIZE);
                clientSocket.read(myBuffer);
                myBuffer.flip();
                System.out.println(new String(myBuffer.array()).trim());
                nextLine = console.readLine();
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}