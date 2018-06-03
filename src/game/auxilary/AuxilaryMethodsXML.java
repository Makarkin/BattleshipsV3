package game.auxilary;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;

public class AuxilaryMethodsXML {

    public static Object transferFireCoordinatesFromServer(FireCoordinates fireCoordinates) throws ParserConfigurationException, IOException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element root = document.createElement("EnemyFire");
        Element element = document.createElement("fire_on");
        element.setAttribute("i", String.valueOf(fireCoordinates.getI()));
        element.setAttribute("j", String.valueOf(fireCoordinates.getJ()));
        root.appendChild(element);
        document.appendChild(root);
        Object object = (Object) document;
        return object;
    }

    public static Object writeResultOfFire(Boolean result) throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element root = document.createElement("Result");
        root.setAttribute("Has got?", String.valueOf(result));
        document.appendChild(root);
        return (Object) document;
    }

    public static Object writeXMLFire(int i, int j, String enemyName) throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element root = document.createElement("EnemyFire");
        Element element = document.createElement("fire_on");
        element.setAttribute("i", String.valueOf(i));
        element.setAttribute("j", String.valueOf(j));
        element.setAttribute("to", enemyName);
        root.appendChild(element);
        document.appendChild(root);
        return (Object) document;
    }

    public static FireCoordinates readXMLFire(Document document) throws ParserConfigurationException {
        int i = 0;
        int j = 0;
        String to = "";
        NodeList nodeList = document.getElementsByTagName("EnemyFire");
        Node node = nodeList.item(0);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            NamedNodeMap attributes = node.getAttributes();
            i = Integer.valueOf(attributes.getNamedItem("i").getNodeValue());
            j = Integer.valueOf(attributes.getNamedItem("j").getNodeValue());
            to = attributes.getNamedItem("to").getNodeValue();
        }

        return new FireCoordinates(i, j, to);
    }

    public static String readXMLPlayer(Object object, String hisNickOrHisEnemyNick) throws IOException, ClassNotFoundException {
        String result = "";
        Document document = (Document) object;
        NodeList nodeList = document.getElementsByTagName("Player");
        Node node = nodeList.item(0);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            NamedNodeMap attributes = node.getAttributes();
            if (hisNickOrHisEnemyNick.equals("his")) {
            result = String.valueOf(attributes.getNamedItem("nickName").getNodeValue());
            } else {
                result = String.valueOf(attributes.getNamedItem("nickName").getNodeValue()) + " "
                        + String.valueOf(attributes.getNamedItem("EnemyNickName").getNodeValue());
            }
        }

        return result;
    }

    public static Object writeXMLPlayer(String yourNickname, String enemyNickname) throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element root = document.createElement("Player");
        root.setAttribute("nickName", yourNickname);
        root.setAttribute("EnemyNickName", enemyNickname);
        document.appendChild(root);
        return (Object) document;
    }

    public static Object writeXMLSignalToGame(String firstTurnNick, String yourname, String enemyname) throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element root = document.createElement("GameCanStart");
        root.setAttribute("FirstTurn", firstTurnNick);
        root.setAttribute("enemyName", firstTurnNick);
        document.appendChild(root);
        return (Object) document;
    }

    public static String[] readXMLSignalToGame(Document document) throws ParserConfigurationException {
        String[] result = new String[2];
        NodeList nodeList = document.getElementsByTagName("GameCanStart");
        Node node = nodeList.item(0);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            NamedNodeMap attributes = node.getAttributes();
            result[0] = attributes.getNamedItem("FirstTurn").getNodeValue();
            result[1] = attributes.getNamedItem("enemyName").getNodeValue();
        }

        return result;
    }

    public static Object writeXMLPlayers(ArrayList<PlayerOnServer> players) throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element root = document.createElement("Players");
        for (int i = 0;  i < players.size(); i++) {
        Element element = document.createElement("Player");
        element.setAttribute("nickName", players.get(i).getNickName());
        element.setAttribute("status", players.get(i).getStatus());
        root.appendChild(element);
        }

        document.appendChild(root);
        return (Object) document;
    }

    public static ArrayList<PlayerOnServer> readXMLPlayers(Document document) throws IOException, ClassNotFoundException {
        ArrayList<PlayerOnServer> players = new ArrayList<>();
        String playerNickname;
        String playerStatus;
        NodeList nodeList = document.getElementsByTagName("Players");
        Node node = nodeList.item(0);
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node childrenNode = children.item(i);
            if (childrenNode.getNodeType() == Node.ELEMENT_NODE) {
                NamedNodeMap attributes = childrenNode.getAttributes();
                playerNickname = attributes.getNamedItem("nickName").getNodeValue();
                playerStatus = attributes.getNamedItem("status").getNodeValue();
                players.add(new PlayerOnServer(playerNickname, playerStatus));
            }
        }

        return players;
    }
}
