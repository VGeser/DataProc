import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class ReadingThread implements Runnable {
    private final BufferedReader reader;
    private final LinkedBlockingQueue<String> responseLines;

    public ReadingThread(BufferedReader reader, LinkedBlockingQueue<String> responseLines) {
        this.reader = reader;
        this.responseLines = responseLines;
    }

    @Override
    public void run() {
        String nextLine;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if ((nextLine = reader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            responseLines.add(nextLine);
        }
    }
}
