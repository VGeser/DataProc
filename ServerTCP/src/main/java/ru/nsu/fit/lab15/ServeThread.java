package ru.nsu.fit.lab15;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import static java.lang.Thread.sleep;

public class ServeThread implements Runnable {

    private final int id;
    private final BufferedReader in;
    private final PrintWriter out;

    public ServeThread(BufferedReader in,PrintWriter out, int id) {
        this.in = in;
        this.out = out;
        this.id = id;
    }

    @Override
    public void run() {
        try{
            //handshake
            out.println("My luck to know you");
            System.out.println("Greeted client");

            String current = in.readLine();
            System.out.println("Got greeting from client " +id +": " + current);
            ServerTCP.lastSent[id] = current;

            sleep(100);

            while (!current.equals("exit")) {
                current = in.readLine();
                System.out.println("Got the following line from client "+id+": " + current);
                out.println("heard "+current);
                ServerTCP.lastSent[id] = current;
            }
            System.out.println("Client "+ id +" disconnected");

        }catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
