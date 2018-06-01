package game.networking.clientstuff;

import game.auxilary.AuxilaryMethodsXML;
import game.auxilary.PlayerOnServer;
import game.networking.serverstuff.Server;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import static game.Main.yourNickname;
import static game.auxilary.AuxilaryMethodsXML.*;

public class Client extends Thread {
    int i = 0;
    private Scanner scanner = new Scanner(System.in);
    private final Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public synchronized ObjectInputStream getInputStream() {
        return inputStream;
    }

    public synchronized ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    public Client(final Socket socket) {
        this.socket = socket;
    }

    private void chooseMethods(Document document) throws IOException, ClassNotFoundException, ParserConfigurationException {
        String rootName = document.getDocumentElement().getNodeName();
        if (rootName == null) {
            return;
        } else if ("Players".equals(rootName)) {
            System.out.println("Players");
            ArrayList<PlayerOnServer> players = AuxilaryMethodsXML.readXMLPlayers(document);
            //выбираем свободного игрока и отправляем запрос на игру с ним
            System.out.println("Choose number of not busy player: ");
            for (PlayerOnServer player : players) {
                if (!player.getNickName().equals(yourNickname)) {
                    System.out.println(i + " " + player.getNickName() + " " + player.getStatus());
                }

                i++;
            }

            int numberOfEnemyPlayer = Integer.valueOf(scanner.nextLine());
            PlayerOnServer enemyPlayer = players.get(numberOfEnemyPlayer);
            if ("not busy".equals(enemyPlayer.getStatus())) {
                Object requestPlayer = writeXMLPlayer(yourNickname, enemyPlayer.getNickName());
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(requestPlayer);
            }
        } else if ("FireResult".equals(rootName)) {

        } else if ("EnemyFire".equals(rootName)) {

        }
    }

    @Override
    public void run() {
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            //отправляем имя игрока на сервер
            outputStream.writeObject(writeXMLPlayer(yourNickname, "none"));
            inputStream = new ObjectInputStream(socket.getInputStream());
            while (true) {
                Document document = (Document) inputStream.readObject();
                chooseMethods(document);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
