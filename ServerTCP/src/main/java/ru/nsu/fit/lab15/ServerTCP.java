package ru.nsu.fit.lab15;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTCP {
    static String[] lastSent;
    static String[] lastProxy;

    public static void main(String[] args) throws IllegalArgumentException {

        if (!(args.length == 3 || args.length == 5)) {
            throw new IllegalArgumentException("Must either be 3 args for end or 5 args for proxy");
        }

        int port = Integer.parseInt(args[0]);
        int max = Integer.parseInt(args[1]);
        String address = args[2];

        int endPort = 0;
        String endAddress = "";
        boolean isProxy = false;

        if (args.length == 5) {
            isProxy = true;
            endPort = Integer.parseInt(args[3]);
            endAddress = args[4];
        }
        //bind to port
        try (ServerSocket serverSocket = new ServerSocket(port, max, InetAddress.getByName(address))

        ) {
            if (isProxy) {
                ExecutorService proxyExecutor = Executors.newFixedThreadPool(max);
                lastProxy = new String[max];
                for (int i = 0; i < max; i++) {
                    Socket clientSocket = serverSocket.accept();
                    PrintWriter clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    //suggests to be used along try-with-resources, but socket is closed at the end of try
                    Socket socket = new Socket(endAddress, endPort);
                    BufferedReader endIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter endOut = new PrintWriter(socket.getOutputStream(), true);
                    ProxyThread pt = new ProxyThread(clientIn, clientOut, endIn, endOut, i);
                    proxyExecutor.execute(pt);
                }
            } else {
                ExecutorService serveExecutor = Executors.newFixedThreadPool(max);
                lastSent = new String[max];
                for (int i = 0; i < max; i++) {
                    Socket clientSocket = serverSocket.accept();
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    ServeThread st = new ServeThread(in, out, i);
                    serveExecutor.execute(st);
                }
            }

        } catch (ConnectException ex) {
            System.out.println("Server cannot accept any more clients");
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
