import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Thread.sleep;

public class Main {

    public static LinkedBlockingQueue<String> responseLines = new LinkedBlockingQueue<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner inScanner = new Scanner(System.in);
        URL url = new URL(inScanner.nextLine());

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        ReadingThread readObject = new ReadingThread(reader, responseLines);
        Thread readingThread = new Thread(readObject);
        readingThread.start();

        sleep(100);
        String current = responseLines.poll();

        int cnt = 0;
        while (current != null) {
            if (cnt < 25) {
                System.out.println(current);
                cnt++;
            } else {
                cnt = 0;
                System.out.println("Press space to scroll down");
                String userKey = inScanner.nextLine();
                if (!(userKey.isBlank())) {
                    break;
                }
            }
            current = responseLines.poll();
        }// http://rsdn.org     https://jsoup.org/

    }
}