import java.net.*;
import java.io.*;

public class test {

    public static void main(String[] args) throws Exception {

        if (args == null || args.length < 1) {
            throw new IllegalArgumentException("Must specify port number");
        }

        int port = Integer.parseInt(args[0]);
        ServerSocket welcomeSocket = new ServerSocket(port);

        System.out.println("Connecting to port:" + port);
        while (true) {

            Socket connectionSocket = welcomeSocket.accept();
            System.out.println("\nSuccessfully connected with client");
            InputStream input = connectionSocket.getInputStream();
            OutputStream output = connectionSocket.getOutputStream();

            byte[] fromClientBuffer = new byte[1024];
            int clientLength = 0;

            StringBuilder sb = new StringBuilder();
            connectionSocket.setSoTimeout(5000);
            while (clientLength != -1) {
                try {
                    clientLength = input.read(fromClientBuffer);
                    if (clientLength == -1) {
                        break;
                    }
                    sb.append(new String(fromClientBuffer, 0, clientLength));
                    char slashR = sb.charAt(sb.length() - 2);
                    char slashN = sb.charAt(sb.length() - 1);
                    if (slashR == '\r' && slashN == '\n') {
                        clientLength = -1;
                    }
                } catch (SocketTimeoutException e) {
                    clientLength = -1;
                }
            }

            String toClient = sb.toString();
            String[] array = toClient.split("\\r\\n");

            String firstline = array[0];

            String[] splitArray = firstline.split("[?&= ]");

            String hostname = null;
            String portNumber = null;
            String string = null;
            StringBuilder stringBuilder = new StringBuilder();

            if (splitArray.length > 1) {
                if (splitArray[1].equals("/ask")) {
                    for (int i = 0; i < splitArray.length; i++) {
                        if (splitArray[i].equals("hostname"))
                            hostname = splitArray[++i];
                        else if (splitArray[i].equals("port"))
                            portNumber = splitArray[++i];
                        else if (splitArray[i].equals("string"))
                            string = splitArray[++i];
                    }
                }
            }

            if (hostname != null && portNumber != null && splitArray[0].equals("GET") && splitArray[splitArray.length - 1].equals("HTTP/1.1")) {
                if ((splitArray.length == 9 && splitArray[6].equals("string")) || (splitArray.length == 7 && string == null)) {
                    try {
                        String toWebServer = TCPClient.askServer(hostname, Integer.parseInt(portNumber), string);
                        stringBuilder.append("HTTP/1.1 200 ok\r\n\r\n");
                        stringBuilder.append(toWebServer);
                    } catch (IOException e) {
                        stringBuilder.append("HTTP/1.1 404 Not Found\r\n");
                    }
                } else {
                    stringBuilder.append("HTTP/1.1 400 Bad Request\r\n");
                }
            } else {
                stringBuilder.append("HTTP/1.1 400 Bad Request\r\n");
            }

            String responseFromServer = stringBuilder.toString();
            System.out.println("Response from server: " + responseFromServer);
            output.write(responseFromServer.getBytes());

            output.close();
            input.close();
            connectionSocket.close();
        }
    }
}

