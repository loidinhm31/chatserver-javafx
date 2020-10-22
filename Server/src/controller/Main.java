package controller;

import business.ServerThread;
import com.entity.Server;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;

public class Main extends Application {

    public final String SERVER_NAME = "localhost";
    public final int PORT = 5000;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/ui/ServerBox.fxml"));
        primaryStage.setTitle("Server Application");
        primaryStage.setScene(new Scene(root, 250, 500));
        primaryStage.show();

        // Start the server and ready for client's connection
        try {
            Server server = new Server(SERVER_NAME, PORT);
            ServerThread serverThread = new ServerThread(server, new ServerSocket(server.getPort()));
            new Thread(serverThread).start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public static void main(String[] args) {
        launch(args);
    }
}
