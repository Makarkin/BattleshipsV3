package game;

import game.networking.clientstuff.Client;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main extends Application {
    public static String yourNickname;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Battleships");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }


    public static void main(String[] args) throws UnknownHostException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your nickname");
        yourNickname = scanner.nextLine();
        launch(args);
    }
}
