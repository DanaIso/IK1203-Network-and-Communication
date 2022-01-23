import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class TCPClient {

    private static final int BUFFERSIZE = 1024;
    private static final int TIME_OUT = 4000;

    public static String askServer(String hostname, int port, String ToServer) throws IOException {

        if(ToServer == null) {
            return askServer(hostname, port);
        }

        Socket clientSocket = new Socket(hostname, port);

        byte[] fromUserBuffer = (ToServer+ "\r\n").getBytes(StandardCharsets.UTF_8);
        byte[] fromServerBuffer = new byte[BUFFERSIZE];

        clientSocket.getOutputStream().write(fromUserBuffer);
        clientSocket.setSoTimeout(TIME_OUT);
        StringBuilder incoming = new StringBuilder();

        int fromServerLength = 0;
        for ( int i = 0; clientSocket.isConnected() && fromServerLength != -1; i = i) {
            try{
                fromServerLength = clientSocket.getInputStream().read(fromServerBuffer);
                if (fromServerLength != -1) {
                    incoming.append(new String(fromServerBuffer, 0, fromServerLength, StandardCharsets.UTF_8));
                }
            }
            catch (SocketTimeoutException e) {
                fromServerLength = -1;
            }
        }

        String decResponse = incoming.toString();
        clientSocket.close();
        return decResponse;
    }

    public static String askServer(String hostname, int port) throws IOException {
        Socket clientSocket = new Socket(hostname, port);
        byte[] fromServerBuffer = new byte[BUFFERSIZE];

        clientSocket.setSoTimeout(TIME_OUT);
        int fromServerLength = 0;
        for ( int i = 0; clientSocket.isConnected() && i == 0; i=i) {
            try{
                fromServerLength = clientSocket.getInputStream().read(fromServerBuffer);
                break;
            }
            catch (SocketTimeoutException e) {
                i = -1;
            }
        }

        String decResponse = new String(fromServerBuffer, 0, fromServerLength, StandardCharsets.UTF_8);
        clientSocket.close();
        return decResponse;
    }
}