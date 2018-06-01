package game.networking.serverstuff;

import game.auxilary.AuxilaryMethodsXML;
import game.auxilary.PlayerOnServer;
import org.w3c.dom.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
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
        this.run();
    }

    public String getYourname() {
        return yourname;
    }

    @Override
    public void run() {
        try {
             ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            document = (Document) inputStream.readObject();
            System.out.println("Принято");
            String player1nick = AuxilaryMethodsXML.readXMLPlayer(document, "his");
            this.yourname = player1nick;
            PlayerOnServer player = new PlayerOnServer(player1nick, "not busy");
            Server.getPlayers().add(player);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            //переписать в xml
            Object playerObject = (Object) Server.getPlayers();
            outputStream.writeObject(playerObject);
            document = (Document) inputStream.readObject();
            String[] playersNicknames = AuxilaryMethodsXML.readXMLPlayer(document, "enemy").split(" ");
            Server.getRequest().put(playersNicknames[0], playersNicknames[1]);
            while (true) {
                Object object = inputStream.readObject();
                exchanger.exchange(object);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}



