package ru.nsu.fit.nioproxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
    private static Selector selector = null;

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        try {
            selector = Selector.open();

            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            ServerSocket serverSocket = serverSocketChannel.socket();
            InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), port);
            serverSocket.bind(address);

            serverSocketChannel.configureBlocking(false);
            int ops = serverSocketChannel.validOps();
            serverSocketChannel.register(selector, ops, null);

            while (selector.select() > 0) {

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> i = selectedKeys.iterator();

                while (i.hasNext()) {
                    SelectionKey key = i.next();
                    i.remove();
                    if (!key.isValid()) continue;
                    if (key.isAcceptable()) {
                        processAcceptEvent(serverSocketChannel);
                    } else if (key.isReadable()) {
                        handleRead(key);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processAcceptEvent(ServerSocketChannel mySocket) throws IOException {
        System.out.println("Connection Accepted");
        SocketChannel myClient = mySocket.accept();
        myClient.configureBlocking(false);
        myClient.register(selector, SelectionKey.OP_READ);
    }

    private static void handleRead(SelectionKey key)
            throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        client.read(buffer);
        String data = new String(buffer.array()).trim();

        if (data.length() > 0) {
            System.out.println("Received message: " + data);
            buffer.clear();
            buffer.put("success!".getBytes());
            buffer.flip();
            client.write(buffer);
            if (data.equalsIgnoreCase("exit")) {
                client.close();
                System.out.println("Connection closed");
            }
        }
    }
}