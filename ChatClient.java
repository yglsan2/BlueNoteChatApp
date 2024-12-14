import java.io.*;
import java.net.*;

public class ChatClient {
    private String serverHost;
    private int serverPort;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ChatClient(String host, int port) {
        this.serverHost = host;
        this.serverPort = port;
    }

    public void start() {
        try {
            socket = new Socket(serverHost, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Message from server: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void closeConnection() throws IOException {
        socket.close();
    }
}
