package ru.nsu.fit.lab15;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestServer {

    @Test
    public void testServerResponse() {
        String[] args = buildServerArgs(3434, 1, "localhost");
        //main method by default is executed in (surprisingly) main thread
        new Thread(() -> ServerTCP.main(args)).start();
        String serverAnswer;
        try {
            sleep(500);
            Socket socket = new Socket("localhost", 3434);
            serverAnswer = readFromServer(socket);
            writeToServer("Hello, it's me Test Module", socket);
            socket.close();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertEquals("My luck to know you", serverAnswer);
    }

    private String[] buildServerArgs(int endPort, int totalConnections, String endAddress) {
        String portString = Integer.toString(endPort);
        String totalString = Integer.toString(totalConnections);
        return new String[]{portString, totalString, endAddress};
    }

    private void writeToServer(String message, Socket socket) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        out.write(message);
        out.flush();
    }

    private String readFromServer(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String res = in.readLine();
        return res;
    }

    @Test
    public void testEndOneConnection() throws InterruptedException {
        String[] argsS = buildServerArgs(3434, 1, "localhost");
        String[] argsC = buildClientArgs(3434, "localhost",'t');
        new Thread(() -> ServerTCP.main(argsS)).start();
        sleep(500);
        new Thread(() -> ClientTCP.main(argsC)).start();
        sleep(1000);
        assertEquals("Just so you could take advantage of me", ServerTCP.lastSent[0]);
    }

    private String[] buildClientArgs(int port, String address, char cons) {
        String portString = Integer.toString(port);
        return new String[]{portString, address, String.valueOf(cons)};
    }

    @Test
    public void testProxyOneConnection() throws InterruptedException {
        String[] argsEndServer = buildServerArgs(2525, 1, "localhost");
        String[] argsProxyServer = buildServerArgs(3434, 1, "localhost",
                2525, "localhost");
        String[] argsC = buildClientArgs(3434, "localhost",'t');
        new Thread(() -> ServerTCP.main(argsEndServer)).start();
        sleep(500);
        new Thread(() -> ServerTCP.main(argsProxyServer)).start();
        sleep(500);
        new Thread(() -> ClientTCP.main(argsC)).start();
        sleep(1000);
        assertEquals("Just so you could take advantage of me", ServerTCP.lastProxy[0]);
    }

    private String[] buildServerArgs(int proxyPort, int proxyConnections, String proxyAddress,
                                     int endPort, String endAddress) {
        String proxyPortString = Integer.toString(proxyPort);
        String proxyConString = Integer.toString(proxyConnections);
        String endPortString = Integer.toString(endPort);
        return new String[]{proxyPortString, proxyConString, proxyAddress,
                endPortString, endAddress};
    }

    @Test
    public void testEndSeveralClients() throws InterruptedException {
        String[] argsEndServer = buildServerArgs(3434, 3, "localhost");
        String[] argsC = buildClientArgs(3434, "localhost",'t');
        new Thread(() -> ServerTCP.main(argsEndServer)).start();
        sleep(100);
        for (int i = 0; i < 3; i++) {
            new Thread(() -> ClientTCP.main(argsC)).start();
            sleep(100);
        }
        sleep(1000);
        for (int i = 0; i < 3; i++) {
            assertEquals("Just so you could take advantage of me", ServerTCP.lastSent[i]);
        }
    }

    @Test
    public void testProxySeveralClients() throws InterruptedException {
        String[] argsEndServer = buildServerArgs(2525, 3, "localhost");
        String[] argsC = buildClientArgs(3434, "localhost",'t');
        String[] argsProxyServer = buildServerArgs(3434, 3, "localhost",
                2525, "localhost");
        new Thread(() -> ServerTCP.main(argsEndServer)).start();
        sleep(100);
        new Thread(() -> ServerTCP.main(argsProxyServer)).start();
        sleep(100);
        for (int i = 0; i < 3; i++) {
            new Thread(() -> ClientTCP.main(argsC)).start();
            sleep(100);
        }
        sleep(1000);
        for (int i = 0; i < 3; i++) {
            assertEquals("Just so you could take advantage of me", ServerTCP.lastProxy[i]);
        }
    }

    @ParameterizedTest
    @MethodSource("argumentProvider")
    public void testIncorrectArgumentsServer(String[] args) {
        assertThrows(IllegalArgumentException.class, () ->  ServerTCP.main(args));
    }

    public static Stream<Arguments> argumentProvider() {
        return Stream.of(
                Arguments.of((Object) new String[]{"2", "3", "7", "6767"}),
                Arguments.of((Object) new String[]{"2525", "3"}),
                Arguments.of((Object) new String[]{""}),
                Arguments.of((Object) new String[]{"2"})
        );
    }

    @ParameterizedTest
    @MethodSource("argumentProvider")
    public void testIncorrectArgumentsClient(String[] args) {
        assertThrows(IllegalArgumentException.class, () ->  ClientTCP.main(args));
    }


}

