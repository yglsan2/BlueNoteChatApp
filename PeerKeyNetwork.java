import java.util.*;
import java.io.*;
import java.net.*;
import java.security.SecureRandom;

public class PeerKeyNetwork {
    private int port;
    private Map<String, Socket> peers;  // Pairs connectés
    private Map<String, PrintWriter> peerWriters;
    private ServerSocket serverSocket;
    private Thread listenerThread;
    private String currentKey;  // Clé de l'utilisateur
    private Map<String, String> keyToPeerMap;  // Carte clé -> pair pour chaque utilisateur

    public PeerKeyNetwork(int port) {
        this.port = port;
        this.peers = new HashMap<>();
        this.peerWriters = new HashMap<>();
        this.keyToPeerMap = new HashMap<>();
    }

    // Génère une clé unique pour chaque utilisateur
    public String generateKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[16];  // 128 bits pour la clé
        random.nextBytes(keyBytes);
        StringBuilder key = new StringBuilder();
        for (byte b : keyBytes) {
            key.append(String.format("%02x", b));
        }
        currentKey = key.toString();
        return currentKey;
    }

    // Démarre le serveur et attend les connexions des pairs
    public void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            listenerThread = new Thread(() -> {
                while (true) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        new Thread(new PeerHandler(clientSocket)).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            listenerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Connecte un pair à un autre utilisateur avec la clé fournie
    public void connectToPeerWithKey(String host, int port, String peerKey) {
        try {
            Socket socket = new Socket(host, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            peers.put(host + ":" + port, socket);
            peerWriters.put(host + ":" + port, out);

            // Mappe la clé à l'utilisateur connecté
            keyToPeerMap.put(peerKey, host + ":" + port);
            new Thread(new PeerHandler(socket, peerKey)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Envoie un message à un pair spécifique en utilisant la clé
    public void sendMessage(String peerKey, String message) {
        String peer = keyToPeerMap.get(peerKey);
        if (peer != null) {
            PrintWriter out = peerWriters.get(peer);
            if (out != null) {
                out.println(message);
            }
        }
    }

    // Classe qui gère la communication avec un pair
    private class PeerHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private String peerKey;

        public PeerHandler(Socket socket, String peerKey) {
            this.socket = socket;
            this.peerKey = peerKey;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Message reçu de " + peerKey + ": " + message);
                    // Traiter le message reçu (par exemple, l'afficher dans l'interface utilisateur)
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Arrêter le serveur (quand l'application se ferme)
    public void stopServer() {
        try {
            serverSocket.close();
            listenerThread.interrupt();
            for (Socket socket : peers.values()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Retourne la clé de l'utilisateur
    public String getCurrentKey() {
        return currentKey;
    }

    // Retourne la liste des pairs connectés
    public Set<String> getPeers() {
        return peers.keySet();
    }
}
