package ru.nsu.fit.lab15;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class ProxyHandler {
    final BufferedReader clientIn;
    final PrintWriter clientOut;
    final BufferedReader endIn;
    final PrintWriter endOut;
    final int id;

    public ProxyHandler(BufferedReader clientIn, PrintWriter clientOut,
                        BufferedReader endIn, PrintWriter endOut,
                        int id) {
        this.clientIn = clientIn;
        this.clientOut = clientOut;
        this.endIn = endIn;
        this.endOut = endOut;
        this.id = id;
    }
}
