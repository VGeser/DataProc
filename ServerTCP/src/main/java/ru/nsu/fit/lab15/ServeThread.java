package ru.nsu.fit.lab15;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

public class ServeThread implements Runnable {

    List<SocketHandler> sockets;
    private final Queue<SocketHandler> toDelete;

    public ServeThread() {
        toDelete = new LinkedList<>();
    }


    @Override
    public void run() {
        try {
            while (!currentThread().isInterrupted()) {

                synchronized (sockets) {
                    Collections.shuffle(sockets); //fair checking

                    for (SocketHandler sh : sockets) {
                        if (sh.in.ready()) {
                            String current = sh.in.readLine();
                            System.out.println("Got the following line from client " + sh.id + ": " + current);
                            sh.out.println("heard " + current);
                            if (current.equals("exit")) {
                                System.out.println("Client " + sh.id + " disconnected");
                                toDelete.add(sh);
                            } else {
                                ServerTCP.lastSent[sh.id] = current;
                            }
                        }
                    }

                    //connections are explicitly stored in the list, Garbage Collector won't help
                    for (SocketHandler sh : toDelete) {
                        sockets.remove(sh);
                    }

                    toDelete.clear();

                }
                sleep(10); //let smn connect after one round
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
