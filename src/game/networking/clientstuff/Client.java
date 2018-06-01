package game.networking.clientstuff;

import game.auxilary.AuxilaryMethodsXML;
import game.auxilary.PlayerOnServer;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import static game.Main.yourNickname;
import static game.auxilary.AuxilaryMethodsXML.*;

public class Client extends Thread {
    int i = 0;
    private Scanner scanner = new Scanner(System.in);
    private final InetAddress host;
    private final int port;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public synchronized ObjectInputStream getInputStream() {
        return inputStream;
    }

    public synchronized ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    public Client(InetAddress inetAddress, int port) {
        this.host = inetAddress;
        this.port = port;
        //this.start();
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(this.host, this.port)) {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(writeXMLPlayer(yourNickname, "none"));
            System.out.println("Создано");
            inputStream = new ObjectInputStream(socket.getInputStream());
            ArrayList<PlayerOnServer> players = (ArrayList<PlayerOnServer>) inputStream.readObject();
            System.out.println("Отправлено");

            System.out.println("Choose number of not busy player: ");
            for (PlayerOnServer player : players) {
                System.out.println(i + player.getNickName() + " " + player.getStatus());
                i++;
            }

            int numberOfEnemyPlayer = Integer.valueOf(scanner.nextLine());
            PlayerOnServer enemyPlayer = players.get(numberOfEnemyPlayer);
            if (enemyPlayer.getStatus() == "not busy") {
                outputStream.writeObject(writeXMLPlayer(yourNickname, enemyPlayer.getNickName()));
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
