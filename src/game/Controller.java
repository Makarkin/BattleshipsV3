package game;

import game.auxilary.AuxilaryMethodsPlace;
import game.auxilary.AuxilaryMethodsXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import game.networking.clientstuff.Client;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import static game.Main.yourNickname;
import static javafx.scene.input.MouseButton.*;
import static javafx.scene.paint.Color.*;

public class Controller {
    @FXML
    private Label yourLabel;
    @FXML
    private Label enemyLabel;
    @FXML
    private Label winLabel;
    @FXML
    private GridPane youGridPane;
    List<Integer> counterList = Arrays.asList(0, 4, 7, 10, 12, 14, 16, 17, 18, 19, 20);
    List<Integer> numberOfDeckList = Arrays.asList(4, 3, 3, 2, 2, 2, 1, 1, 1, 1);
    Cell[][] enemyCells = new Cell[10][10];
    ArrayList<IndexVault> itShouldFrozen = new ArrayList<>();
    private int yourSumOfDecks = 20;
    private int enemySumOfDecks = 20;
    private Integer predI = null;
    private Integer predJ = null;
    private int counter = 0;
    private int counterOfDeck = 0;
    private int index = 0;
    private Board yourBoard = new Board();
    private static boolean yourTurn = false;
    private Client client;

    public static void setYourTurn(boolean yourTurn) {
        Controller.yourTurn = yourTurn;
    }

    public Controller() {
        this.client = new Client(this);
    }

    @FXML
    private void makeShot(MouseEvent mouseEvent) throws ParserConfigurationException, IOException, ClassNotFoundException {
        if (yourTurn) {
            Rectangle rectangle = (Rectangle) mouseEvent.getSource();
            Integer i = GridPane.getRowIndex(rectangle);
            Integer j = GridPane.getColumnIndex(rectangle);
            client.setOnFireI(i);
            client.setOnFireJ(j);
/*            if (true) {
                rectangle.setFill(RED);
                enemySumOfDecks--;
                if (enemySumOfDecks == 0) {
                    winLabel.setText("You win!");
                }
            } else {
                rectangle.setFill(BLACK);
            }*/

            yourTurn = false;
        }
    }

    public boolean getShot(Document document) throws IOException, ParserConfigurationException, ClassNotFoundException {
        //принятие координат вражеского залпа
        boolean result = false;
        if (!yourTurn) {
            int iIndex;
            int jIndex;
            NodeList nodeList = document.getElementsByTagName("EnemyFire");
            Node node = nodeList.item(0);
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node childrenNode = children.item(i);
                if (childrenNode.getNodeType() == Node.ELEMENT_NODE) {
                    NamedNodeMap attributes = childrenNode.getAttributes();
                    iIndex = Integer.valueOf(attributes.getNamedItem("i").getNodeValue());
                    jIndex = Integer.valueOf(attributes.getNamedItem("j").getNodeValue());
                    if (yourBoard.getIndexCell(iIndex, jIndex).isWithShip() == true) {
                        yourSumOfDecks--;
                        for (javafx.scene.Node element : youGridPane.getChildren()) {
                            Rectangle rectangle = (Rectangle) element;
                            rectangle.setFill(RED);
                            result = true;
                        }

                        if (yourSumOfDecks == 0) {
                            winLabel.setText("You lose!");
                        }
                    } else {
                        result = false;
                    }
                }
            }

            yourTurn = true;
        }

        return result;
    }

    @FXML
    private void placeShip(MouseEvent mouseEvent) throws IOException {
        MouseButton mouseButton = mouseEvent.getButton();
        Rectangle rectangle = (Rectangle) mouseEvent.getSource();
        Integer i = GridPane.getRowIndex(rectangle);
        Integer j = GridPane.getColumnIndex(rectangle);
        if (i == null) {
            i = 0;
        }

        if (j == null) {
            j = 0;
        }

        Cell probeCell = yourBoard.getIndexCell(i, j);

        if (mouseButton == PRIMARY) {//vertical placing

            if (counterList.contains(counter) && !AuxilaryMethodsPlace.hasShipsNear(i, j, yourBoard)) {
                probeCell.setModifable(true);
            }

            if (!probeCell.isWithShip() && probeCell.isModifable() && counter < 20
                    && AuxilaryMethodsPlace.checkVerticalShipPlaceConstraint(i, j, predI, predJ)
                    && !probeCell.isFrozen()) {
                rectangle.setFill(GREEN);
                counterOfDeck++;
                counter++;
                Cell cell = new Cell();
                cell.setWithShip(true);
                cell.setModifable(false);
                yourBoard.setIndexCell(cell, i, j);
                itShouldFrozen.add(new IndexVault(i, j));
                if (counterOfDeck < numberOfDeckList.get(index)) {
                    if (i > 0 && i < 9) {
                       if (!yourBoard.getIndexCell(i - 1, j).isFrozen()) yourBoard.getIndexCell(i - 1, j).setModifable(true);
                       if (!yourBoard.getIndexCell(i + 1, j).isFrozen()) yourBoard.getIndexCell(i + 1, j).setModifable(true);
                    }
                    if (i == 0) {
                        if (!yourBoard.getIndexCell(i + 1, j).isFrozen()) yourBoard.getIndexCell(i + 1, j).setModifable(true);
                    }
                    if (i == 9) {
                        if (!yourBoard.getIndexCell(i - 1, j).isFrozen()) yourBoard.getIndexCell(i - 1, j).setModifable(true);
                    }
                } else {
                    index++;
                    counterOfDeck = 0;
                    AuxilaryMethodsPlace.freezeCell(itShouldFrozen, yourBoard);
                    itShouldFrozen.clear();
                }
            }
        } else if (mouseButton == SECONDARY) {//horizontal placing

            if (counterList.contains(counter) && !AuxilaryMethodsPlace.hasShipsNear(i, j, yourBoard)) {
                probeCell.setModifable(true);
            }

            if (!probeCell.isWithShip() && probeCell.isModifable() && counter < 20
                    && AuxilaryMethodsPlace.checkHorizontalShipPlaceConstraint(i,j, predI, predJ)
                    && !probeCell.isFrozen()) {
                rectangle.setFill(GREEN);
                counterOfDeck++;
                counter++;
                Cell cell = new Cell();
                cell.setWithShip(true);
                cell.setModifable(false);
                yourBoard.setIndexCell(cell, i, j);
                itShouldFrozen.add(new IndexVault(i, j));
                if (counterOfDeck < numberOfDeckList.get(index)) {
                    if (j > 0 && j < 9) {
                        if (!yourBoard.getIndexCell(i, j - 1).isFrozen()) yourBoard.getIndexCell(i, j - 1).setModifable(true);
                        if (!yourBoard.getIndexCell(i, j + 1).isFrozen()) yourBoard.getIndexCell(i, j + 1).setModifable(true);
                    }
                    if (j == 0) {
                        if (!yourBoard.getIndexCell(i, j + 1).isFrozen()) yourBoard.getIndexCell(i, j + 1).setModifable(true);
                    }
                    if (j == 9) {
                        if (!yourBoard.getIndexCell(i, j - 1).isFrozen()) yourBoard.getIndexCell(i, j - 1).setModifable(true);
                    }
                } else {
                    index++;
                    counterOfDeck = 0;
                    AuxilaryMethodsPlace.freezeCell(itShouldFrozen, yourBoard);
                    itShouldFrozen.clear();
                }
            }
        }
    }

    @FXML
    private void start(ActionEvent actionEvent) {
        System.out.println("Start");
    }

    @FXML
    public void initialize() throws IOException {
        yourLabel.setText(yourNickname);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter IP-address of server");
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");//InetAddress.getByName(scanner.nextLine());
        System.out.println("Enter number of port");
        int port = 6666;//Integer.valueOf(scanner.nextLine());;
        Socket socket = new Socket(inetAddress, port);
        client.setSocket(socket);
        client.start();
    }
}
