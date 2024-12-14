import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class BlueNoteChatApp {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 12345;
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;

    private static JFrame frame;
    private static JTextArea messageArea;
    private static JTextField messageField;
    private static JButton sendButton;

    public static void main(String[] args) {
        setupUI();
        connectToServer(DEFAULT_HOST, DEFAULT_PORT);
    }

    private static void setupUI() {
        frame = new JFrame("Blue Note Chat");
        frame.setLayout(new BorderLayout());

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        frame.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        messageField = new JTextField();
        frame.add(messageField, BorderLayout.SOUTH);

        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        frame.add(sendButton, BorderLayout.EAST);

        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static void connectToServer(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String incomingMessage;
            while ((incomingMessage = in.readLine()) != null) {
                messageArea.append(incomingMessage + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            out.println(message);
            messageField.setText("");
        }
    }
}
