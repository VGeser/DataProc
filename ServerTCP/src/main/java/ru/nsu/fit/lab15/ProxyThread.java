package ru.nsu.fit.lab15;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

public class ProxyThread implements Runnable {

    List<ProxyHandler> connections;
    private final Queue<ProxyHandler> toDelete;

    public ProxyThread() {
        toDelete = new LinkedList<>();
    }

    @Override
    public void run() {
        try {

            while (!currentThread().isInterrupted()) {

                synchronized (connections) {
                    Collections.shuffle(connections); //fair checking

                    for (ProxyHandler ph : connections) {
                        if (ph.clientIn.ready()) {
                            String temp = ph.clientIn.readLine();
                            System.out.println("Proxy got client message: " + temp);

                            if (temp.equals("exit")) {
                                System.out.println("Client " + ph.id + " disconnected");
                                toDelete.add(ph);
                            } else {
                                ServerTCP.lastProxy[ph.id] = temp;
                            }

                            ph.endOut.println(temp);
                            temp = ph.endIn.readLine();
                            System.out.println("Proxy got server message: " + temp);
                            ph.clientOut.println(temp);
                        }
                    }

                    //connections are explicitly stored in the list, Garbage Collector won't help
                    for (ProxyHandler ph : toDelete) {
                        connections.remove(ph);
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
