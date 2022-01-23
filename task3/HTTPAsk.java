import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class HTTPAsk {

    private static final int BUFFERSIZE = 1024;

    public static void main(String[] args) throws IOException {

        int port = Integer.parseInt(args[0]);
        ServerSocket welcomeSocket = new ServerSocket(port);
        String ok = "HTTP/1.1 200 OK\r\n\r\n";
        String not_found = "HTTP/1.1 404 Not Found\r\n";
        String bad_req = "HTTP/1.1 400 Bad Request\r\n";

        while (true) {

            Socket connectionSocket = welcomeSocket.accept();
            /* Simplification for me, shorter code*/
            InputStream inpClient = connectionSocket.getInputStream();
            OutputStream outClient = connectionSocket.getOutputStream();

            byte[] fromClientBuffer = new byte[BUFFERSIZE];
            int fromClientLength = inpClient.read(fromClientBuffer);

            String sentenceToClient = new String(fromClientBuffer, 0, fromClientLength, StandardCharsets.UTF_8);

            String[] allRows = sentenceToClient.split("\\r\\n");
            String rad1 = allRows[0];
            String[] extractParam = rad1.split("[?=& ]");

            String host = null;
            String toServer = null;
            int hostPort = 0;

            if (extractParam.length > 1) {
                if ((extractParam[0].equals("GET")) && (extractParam[1].equals("/ask")) && extractParam[extractParam.length - 1].equals("HTTP/1.1")) {
                    for (int i = 0; i < extractParam.length; i++) {
                        if (extractParam[i].equals("hostname")) {
                            host = extractParam[++i];
                        } else if (extractParam[i].equals("port")) {
                            hostPort = Integer.parseInt(extractParam[++i]);
                        } else if (extractParam[i].equals("string")) {
                            toServer = extractParam[++i];
                        }
                    }
                }
            }

            if (host != null && hostPort != 0) {
                if ((extractParam.length == 9 && extractParam[6].equals("string")) || extractParam.length == 7) {
                    try {
                        String theResponse = ok + TCPClient.askServer(host, hostPort, toServer);
                        System.out.println(theResponse);
                        outClient.write(theResponse.getBytes(StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        outClient.write(not_found.getBytes(StandardCharsets.UTF_8));
                        connectionSocket.close();
                    }
                } else {
                    outClient.write(bad_req.getBytes(StandardCharsets.UTF_8));
                }
            } else {
                outClient.write(bad_req.getBytes(StandardCharsets.UTF_8));
            }
            connectionSocket.close();
        }
    }
}