package controller;

import business.ClientHandler;
import business.ServerThread;
import com.entity.Client;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Controller for Box Server UI
 * Display available users on List View
 *
 * @author  Loi Dinh on Oct-15-2020
 * @version 1.0
 */
public class ServerBoxController {

    public static ObservableList<Client> clientsList = FXCollections.observableArrayList();

    @FXML
    private ListView<Client> clientListView;

    /**
     * Initialize Controller
     */
    public void initialize() {
        // Set items for List View by observable list
        clientListView.setItems(clientsList);
    }


    /**
     * Double click to open chat box with specific client
     * @param evt
     */
    @FXML
    public void handleClientsMouseClicked(MouseEvent evt) {
        try {
            if (evt.getClickCount() == 2) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ChatBox.fxml"));
                Parent root = (Parent) loader.load();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));

                // Get current view on list view
                String clientName = (clientListView.getSelectionModel().getSelectedItem()).getUsername();
                // Set title and show the chat box
                stage.setTitle("Chat with " + clientName);
                stage.show();

                // Set chat field
                ChatBoxController chatBoxController = loader.getController();
                chatBoxController.setUsername(clientName);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove user from list view
     * @param username
     */
    public static void removeUser(String username) {
        for(int i = 0; i < clientsList.size(); ++i) {
            Client client = clientsList.get(i);
            Platform.runLater(new Runnable() {                           // Avoid IllegalStageException
                @Override
                public void run() {
                    clientsList.removeIf(client -> client.getUsername().equalsIgnoreCase(username));
                }
            });

        }

    }
}
