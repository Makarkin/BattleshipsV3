package game.networking.serverstuff;

import game.auxilary.PlayerOnServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;

public class Server extends Thread {
    private int port;
    private boolean flag = true;
    private static ArrayList<PlayerOnServer> players = new ArrayList<>();;
    private ArrayList<ServerClientSession> listOfSessions = new ArrayList<>();
    private static HashMap<ServerClientSession, ServerClientSession> gameSession;
    private static HashMap<String, String> request = new HashMap<>();

    static synchronized ArrayList<PlayerOnServer> getPlayers() {
        return players;
    }

    public ArrayList<ServerClientSession> getListOfSessions() {
        return listOfSessions;
    }

    public synchronized static HashMap<ServerClientSession, ServerClientSession> getGameSession() {
        return gameSession;
    }

    public synchronized static HashMap<String, String> getRequest() {
        return request;
    }

    public Server(int port) {
        this.port = port;
    }

    public ServerClientSession getServerClientSessionByName(String yourname) {
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
                Set<String> keys = request.keySet();
                for (String key : keys) {
                    String value = request.get(key);
                    if (keys.contains(value)) {
                        Exchanger<Object> exchanger = new Exchanger<>();
                        getServerClientSessionByName(key).setExchanger(exchanger);
                        getServerClientSessionByName(value).setExchanger(exchanger);
                        gameSession.put(getServerClientSessionByName(key), getServerClientSessionByName(value));
                        request.remove(key);
                    }
                }
            }

            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
