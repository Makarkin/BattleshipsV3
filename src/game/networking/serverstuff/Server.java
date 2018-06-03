package game.networking.serverstuff;

import game.auxilary.FireCoordinates;
import game.auxilary.PlayerOnServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server extends Thread {
    private int port;
    private boolean flag = true;
    private static volatile ArrayList<PlayerOnServer> players = new ArrayList<>();;
    private static ArrayList<ServerClientSession> listOfSessions = new ArrayList<>();
    private static volatile HashMap<String, String> request = new HashMap<>();

    static ArrayList<PlayerOnServer> getPlayers() throws InterruptedException {
        return players;
    }

    static void addPlayer(PlayerOnServer player) {
        players.add(player);
    }

    public static HashMap<String, String> getRequest() {
        return request;
    }

    public Server(int port) {
        this.port = port;
    }

    public static ServerClientSession getServerClientSessionByName(String yourname) {
        for (ServerClientSession session : listOfSessions) {
            if (session.getYourname().equals(yourname)) {
                return session;
            }
        }

        return null;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(this.port);
            while (flag) {
                System.out.println("Waiting connection on port:" + this.port);
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected to server");
                ServerClientSession clientSession = new ServerClientSession(clientSocket);
                listOfSessions.add(clientSession);
            }

            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
