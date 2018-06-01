package game.networking.serverstuff;

import game.auxilary.AuxilaryMethodsXML;
import game.auxilary.PlayerOnServer;
import org.w3c.dom.*;

import javax.xml.parsers.ParserConfigurationException;
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
        //start() не запускает run()
        //this.start();
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
            //получаем имя подключившегося игрока
            String player1nick = AuxilaryMethodsXML.readXMLPlayer(document, "his");
            this.yourname = player1nick;

            //добавляем игрока в список игроков на сервере, причем он пока свободен
            PlayerOnServer player = new PlayerOnServer(player1nick, "not busy");
            Server.getPlayers().add(player);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            Object playerObject = AuxilaryMethodsXML.writeXMLPlayers(Server.getPlayers());

            //отправляем список игроков клиенту
            outputStream.writeObject(playerObject);

            //читаем запрос от клиента на желаемого противника
            document = (Document) inputStream.readObject();
            String[] playersNicknames = AuxilaryMethodsXML.readXMLPlayer(document, "enemy").split(" ");

            //составляем список запросов клиентов на игру друг с другом
            Server.getRequest().put(playersNicknames[0], playersNicknames[1]);

            //тут должно происходить взаимодействие между клиентами с выбранным оппонентом
            // т.е в одной игровой сессии, на сервере им дали exchanger
            //для обмена данными. Исходящие данные отправляем по exchange(), входящие берем от клиентов
/*            while (true) {
                Object object = inputStream.readObject();
                exchanger.exchange(object);
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } /*catch (InterruptedException e) {
            e.printStackTrace();
        }*/ catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
}



