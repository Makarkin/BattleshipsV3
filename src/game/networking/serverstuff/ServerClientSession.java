package game.networking.serverstuff;

import game.IndexVault;
import game.auxilary.AuxilaryMethodsXML;
import game.auxilary.FireCoordinates;
import game.auxilary.PlayerOnServer;
import org.w3c.dom.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Exchanger;

import static game.networking.serverstuff.Server.getServerClientSessionByName;

public class ServerClientSession extends Thread {

    private Document document;
    private String yourname;
    private final Socket socket;
    private Exchanger<Document> exchanger = null;

    public Exchanger<Document> getExchanger() {
        return exchanger;
    }

    public void setExchanger(Exchanger<Document> exchanger) {
        this.exchanger = exchanger;
    }

    public ServerClientSession(final Socket socket) throws IOException {
        this.socket = socket;
        this.start();
    }

    public String getYourname() {
        return yourname;
    }

    private void chooseMethods(Document document) throws IOException, ParserConfigurationException, InterruptedException, ClassNotFoundException {
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
                PlayerOnServer player = new PlayerOnServer(playerName, "not busy");
                Server.addPlayer(player);

                do {sleep(100);
                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    ArrayList<PlayerOnServer> tempPlayers = Server.getPlayers();
                    Object playerObject = AuxilaryMethodsXML.writeXMLPlayers(tempPlayers);
                    outputStream.writeObject(playerObject);
                } while (Server.getPlayers().size() < 2);
            } else if (!"none".equals(enemyName)) {
                System.out.println("Player with enemy");
                String[] playersNicknames = new String[2];
                playersNicknames[0] = playerName;
                playersNicknames[1] = enemyName;
                Server.getRequest().put(playersNicknames[0], playersNicknames[1]);
                sleep(100);
                Set<String> keyset = Server.getRequest().keySet();
                for (String s : keyset) {
                    System.out.println(yourname + " " + s + " " + Server.getRequest().get(s));
                }

                if (Server.getRequest().size() > 1 && Server.getRequest().get(playersNicknames[1]).equals(playersNicknames[0])) {
                    Exchanger<Document> exchanger = new Exchanger<>();
                    if (Server.getServerClientSessionByName(playersNicknames[0]).getExchanger() == null &&
                            Server.getServerClientSessionByName(playersNicknames[1]).getExchanger() == null) {
                        Server.getServerClientSessionByName(playersNicknames[0]).setExchanger(exchanger);
                        Server.getServerClientSessionByName(playersNicknames[1]).setExchanger(exchanger);
                    }

                    Server.getRequest().remove(playersNicknames[0]);
                }

                String maxString = playersNicknames[0];
                    if (playersNicknames[1].hashCode() > maxString.hashCode()) {
                        maxString = playersNicknames[1];
                    }

                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                Object gameStart = AuxilaryMethodsXML.writeXMLSignalToGame(maxString, playersNicknames[0], playersNicknames[1]);
                outputStream.writeObject(gameStart);
            }
        } else if ("FireResult".equals(rootName)) {
            System.out.println("FireResult");
        } else if ("EnemyFire".equals(rootName)) {
            document = exchanger.exchange(document);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(document);
            System.out.println("EnemyFire");
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (exchanger == null) {
                    ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                    document = (Document) inputStream.readObject();
                    chooseMethods(document);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}



