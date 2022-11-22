package ru.nsu.fit.lab15;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;
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

            int threadNum = max / 1000 + 1;
            byte currentThread = 0;

            if (isProxy) {
                lastProxy = new String[max];
                ProxyThread[] proxyThreads = new ProxyThread[threadNum];
                List<List<ProxyHandler>> threadsConnections = new Vector<>(threadNum);
                for (int i = 0; i < threadNum; i++) {
                    List<ProxyHandler> phs = new Vector<>(1000);
                    threadsConnections.add(phs);
                }
                ExecutorService proxyExecutor = Executors.newFixedThreadPool(threadNum);

                for (int i = 0; i < max; i++) {
                    Socket clientSocket = serverSocket.accept();

                    PrintWriter clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    //suggests to be used along try-with-resources, but socket is closed at the end of try
                    Socket socket = new Socket(endAddress, endPort);
                    BufferedReader endIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter endOut = new PrintWriter(socket.getOutputStream(), true);

                    ProxyHandler ph = new ProxyHandler(clientIn, clientOut, endIn, endOut, i);

                    //handshake
                    String temp = endIn.readLine();
                    clientOut.println(temp);
                    temp = clientIn.readLine();
                    endOut.println(temp);

                    if (i < threadNum) { // first connection of thread
                        ProxyThread pt = new ProxyThread();
                        proxyThreads[i] = pt;
                        pt.connections = threadsConnections.get(i);
                        pt.connections.add(ph);
                        proxyExecutor.execute(proxyThreads[currentThread]);
                    } else {
                        synchronized (threadsConnections.get(currentThread)) {
                            threadsConnections.get(currentThread).add(ph);
                        }
                    }

                    // add new socket to threads in turns
                    currentThread = (byte) ((currentThread + 1) % threadNum);

                }
            } else {
                lastSent = new String[max];
                ServeThread[] serveThreads = new ServeThread[threadNum];
                List<List<SocketHandler>> threadsSockets = new Vector<>(threadNum);
                for (int i = 0; i < threadNum; i++) {
                    List<SocketHandler> shs = new Vector<>(1000);
                    threadsSockets.add(shs);
                }
                ExecutorService serveExecutor = Executors.newFixedThreadPool(threadNum);

                for (int i = 0; i < max; i++) {
                    Socket clientSocket = serverSocket.accept();

                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    SocketHandler sh = new SocketHandler(i, in, out);

                    //handshake
                    out.println("My luck to know you");
                    System.out.println("Greeted client " + i);
                    System.out.println("Got greeting from client " + i + ": " + in.readLine());

                    if (i < threadNum) { // first socket of thread
                        ServeThread st = new ServeThread();
                        serveThreads[i] = st;
                        st.sockets = threadsSockets.get(i);
                        st.sockets.add(sh);
                        serveExecutor.execute(serveThreads[currentThread]);
                    } else {
                        synchronized (threadsSockets.get(currentThread)) {
                            threadsSockets.get(currentThread).add(sh);
                        }
                    }

                    // add new socket to threads in turns
                    currentThread = (byte) ((currentThread + 1) % threadNum);
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
