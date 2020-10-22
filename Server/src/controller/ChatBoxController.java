package controller;

import business.ClientHandler;
import business.ServerThread;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Controller for Chat Box UI
 * Use TextArea to set configure for ClientHandler
 *
 * @author  Loi Dinh on Oct-15-2020
 * @version 1.0
 */
public class ChatBoxController {
    @FXML
    private TextArea txtContent;
    @FXML
    private TextField txtMessage;

    private ClientHandler clientHandler;

    private String username;

    /**
     * Configure Client Handler
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;

        // Get handler of current user by key from HashMap
        clientHandler = ServerThread.clientHandlerHashMap.get(username);
        clientHandler.setTxtContent(txtContent);                                // Configure chat field

    }

    /**
     * Handle Send Data from Server
     */
    @FXML
    public void handleSendActionPerformed() {
        try {
            // Check input valid
            if (!txtMessage.getText().isEmpty()) {
                // Send data to the user
                clientHandler.send(txtMessage.getText());
                // Clear Message field after hit button
                txtMessage.clear();
            }
        } catch (Exception e) {
            System.out.println("Error Sending Message: " + e.getMessage());
        }
    }

    /**
     * Handle when hitting ENTER
     * @param keyEvent
     */
    @FXML
    public void handleKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            handleSendActionPerformed();
        }
    }


}
