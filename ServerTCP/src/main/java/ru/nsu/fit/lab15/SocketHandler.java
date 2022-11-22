package ru.nsu.fit.lab15;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class SocketHandler {
    final int id;
    final BufferedReader in;
    final PrintWriter out;

    public SocketHandler(int id, BufferedReader in, PrintWriter out) {
        this.id = id;
        this.in = in;
        this.out = out;
    }
}
