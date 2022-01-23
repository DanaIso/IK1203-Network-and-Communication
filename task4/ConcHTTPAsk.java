import java.net.*;
import java.io.*;

public class ConcHTTPAsk {

    public static void main(String[] args) throws IOException {

        int port = Integer.parseInt(args[0]);
        ServerSocket welcomeSocket = new ServerSocket(port);

        try {
            while (true) {
                Socket connectionSocket = welcomeSocket.accept();
                MyRunnable run = new MyRunnable(connectionSocket);
                new Thread(run).start();
            }
        } catch (IOException e) {
            System.out.println("Error");
        }
    }
}