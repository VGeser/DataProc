package ru.nsu.fit.lab15;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import static java.lang.Thread.sleep;

public class ProxyThread implements Runnable {
    private final int id;
    private final BufferedReader clientIn;
    private final BufferedReader endIn;
    private final PrintWriter clientOut;
    private final PrintWriter endOut;

    public ProxyThread(BufferedReader clientIn, PrintWriter clientOut,
                       BufferedReader endIn, PrintWriter endOut,
                       int id){
        this.clientIn = clientIn;
        this.clientOut = clientOut;
        this.endIn = endIn;
        this.endOut = endOut;
        this.id = id;
    }

    @Override
    public void run() {
        try{
            //handshake
            String temp = endIn.readLine(); //get message from end
            ServerTCP.lastProxy[id] = temp;
            clientOut.println(temp); //send it to client
            temp = clientIn.readLine(); //get message from client
            ServerTCP.lastProxy[id] = temp;
            endOut.println(temp); //send it to end

            sleep(100);

            while (!temp.equals("exit")) {
                temp = clientIn.readLine();
                System.out.println("Proxy got client message: " + temp);
                ServerTCP.lastProxy[id] = temp;
                endOut.println(temp);
                temp = endIn.readLine();
                System.out.println("Proxy got server message: " + temp);
                ServerTCP.lastProxy[id] = temp;
                clientOut.println(temp);
            }
            System.out.println("Client "+ id +" disconnected");

        }catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
