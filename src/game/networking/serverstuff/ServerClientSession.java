package game.networking.serverstuff;

import game.auxilary.AuxilaryMethodsXML;
import game.auxilary.PlayerOnServer;
import org.w3c.dom.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Exchanger;

public class ServerClientSession extends Thread {

    Document document;
    private String yourname;
    private final Socket socket;
    private Exchanger<Object> exchanger;

    public void setExchanger(Exchanger<Object> exchanger) {
        this.exchanger = exchanger;
    }

    public ServerClientSession(final Socket socket) throws IOException {
        this.socket = socket;
        this.start();
    }

    public String getYourname() {
        return yourname;
    }

    private void chooseMethods(Document document) throws IOException, ParserConfigurationException {
        String rootName = document.getDocumentElement().getNodeName();
        if (null == rootName) {
            System.out.println("null");
            return;
        } else if ("Player".equals(rootName)) {
            String enemyName = "";
            String playerName = "";
            NodeList nodeList = document.getElementsByTagName("Player");
            Node node = nodeList.item(0);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                NamedNodeMap attributes = node.getAttributes();
                playerName = attributes.getNamedItem("nickName").getNodeValue();
                enemyName = attributes.getNamedItem("EnemyNickName").getNodeValue();
            }

            if ("none".equals(enemyName)) {
                System.out.println("Player no enemy");
                this.yourname = playerName;
                //добавляем игрока в список игроков на сервере, причем он пока свободен
                PlayerOnServer player = new PlayerOnServer(playerName, "not busy");
                Server.getPlayers().add(player);
                if (Server.getPlayers().size() > 1) {
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                Object playerObject = AuxilaryMethodsXML.writeXMLPlayers(Server.getPlayers());
                //отправляем список игроков клиенту
                outputStream.writeObject(playerObject);
                }
            } else if (!"none".equals(enemyName)) {
                System.out.println("Player with enemy");
                String[] playersNicknames = new String[2];
                playersNicknames[0] = playerName;
                playersNicknames[1] = enemyName;
                Server.getRequest().put(playersNicknames[0], playersNicknames[1]);
            }
        } else if ("FireResult".equals(rootName)) {

        } else if ("EnemyFire".equals(rootName)){

        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                document = (Document) inputStream.readObject();
                System.out.println(document.toString());
                chooseMethods(document);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
}



