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

public class Proxy {
    private static Selector selector = null;
    private static SocketChannel endSocket = null;

    public static void main(String[] args) {

        int portOwn = Integer.parseInt(args[0]);
        String endAddress = args[1];
        int portEnd = Integer.parseInt(args[2]);

        try {
            InetSocketAddress myAddress =
                    new InetSocketAddress(InetAddress.getByName(endAddress), portEnd);
            endSocket = SocketChannel.open(myAddress);

            selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            ServerSocket serverSocket = serverSocketChannel.socket();
            InetSocketAddress ownAddress = new InetSocketAddress(InetAddress.getLocalHost(), portOwn);
            serverSocket.bind(ownAddress);

            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, serverSocketChannel.validOps(), null);

            while (selector.select() > 0) {

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> i = selectedKeys.iterator();

                while (i.hasNext()) {
                    SelectionKey key = i.next();
                    i.remove();
                    if (key.isAcceptable()) {
                        processAcceptEvent(serverSocketChannel);
                    } else if (key.isReadable()) {
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        SocketChannel client = (SocketChannel) key.channel();
                        client.read(buffer);
                        buffer.flip();
                        endSocket.write(buffer);

                        buffer = ByteBuffer.allocate(1024);
                        endSocket.read(buffer);
                        buffer.flip();
                        client.write(buffer);
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

}
