package ru.nsu.fit.lab15;

import java.io.*;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class ClientTCP {
    static String[] lines = new String[]{
            "Then I got you off your knees",
            "Put you right back on your feet",
            "Just so you could take advantage of me",
            "exit"
    };

    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("Must be 3 args");
        }

        int port = Integer.parseInt(args[0]);
        String address = args[1];
        boolean readConsole = args[2].equals("c");

        try (Socket socket = new Socket(address, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String current = in.readLine();
            System.out.println("Got greeting from server: " + current);

            out.println("Hello it's client");
            System.out.println("Greeted server");

            sleep(100);

            String nextLine = "";
            Console console = System.console();
            int i = 0;

            while (!nextLine.equals("exit")) {
                if (!readConsole) {
                    nextLine = lines[i];
                    i++;
                } else {
                    nextLine = console.readLine();
                }
                out.println(nextLine);
                current = in.readLine();
                System.out.println("Got the following line from server: " + current);
            }
            out.println("exit");

        } catch (IOException ex) {
            System.out.println("Client exception: " + ex.getMessage());
            ex.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
