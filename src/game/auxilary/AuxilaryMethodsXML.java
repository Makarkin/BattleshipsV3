package game.auxilary;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class AuxilaryMethodsXML {

    public static Object transferResultOfFire(boolean b) throws ParserConfigurationException, IOException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element root = document.createElement("FireResult");
        Element element = document.createElement("shot");
        element.setAttribute("result", String.valueOf(b));
        root.appendChild(element);
        document.appendChild(root);
        Object object = (Object) document;
        return object;
    }

    public static boolean acceptResultOfFire(Object object) throws IOException, ClassNotFoundException {
        boolean result = false;
        Document document = (Document) object;
        NodeList nodeList = document.getElementsByTagName("FireResult");
        Node node = nodeList.item(0);
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node childrenNode = children.item(i);
            if (childrenNode.getNodeType() == Node.ELEMENT_NODE) {
                NamedNodeMap attributes = childrenNode.getAttributes();
                result = Boolean.valueOf(attributes.getNamedItem("result").getNodeValue());
            }
        }

        return result;
    }

    public static Object writeXMLFire(int i, int j) throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element root = document.createElement("EnemyFire");
        Element element = document.createElement("fire_on");
        element.setAttribute("i", String.valueOf(i));
        element.setAttribute("j", String.valueOf(j));
        root.appendChild(element);
        document.appendChild(root);
        return (Object) document;
    }

    public static synchronized String readXMLPlayer(Object object, String hisNickOrHisEnemyNick) throws IOException, ClassNotFoundException {
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

    public static synchronized Object writeXMLPlayer(String yourNickname, String enemyNickname) throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element root = document.createElement("Player");
        root.setAttribute("nickName", yourNickname);
        root.setAttribute("EnemyNickName", enemyNickname);
        document.appendChild(root);
        return (Object) document;
    }
}
