import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class HTTPEcho {

    private static final int BUFFERSIZE = 1024;

    public static void main( String[] args) throws IOException {

        int port = Integer.parseInt(args[0]);
        ServerSocket welcomeSocket = new ServerSocket(port);
        String inputHeader = "HTTP/1.1 200 ok\r\n\r\n";

        try {

            while(true){
                Socket connectionSocket = welcomeSocket.accept();
                /* Simplification for me, shorter code*/
                InputStream inpClient = connectionSocket.getInputStream();
                OutputStream outClient = connectionSocket.getOutputStream();

                byte [] fromClientBuffer = new byte[BUFFERSIZE];

                int fromServerLength = inpClient.read(fromClientBuffer);

                String sentenceToClient = new String(fromClientBuffer, 0, fromServerLength, StandardCharsets.UTF_8);
                System.out.println(sentenceToClient);

                outClient.write(inputHeader.getBytes());
                outClient.write(sentenceToClient.getBytes(StandardCharsets.UTF_8));

                connectionSocket.close();
            }

        } catch (IOException ex) {

        }
    }
}