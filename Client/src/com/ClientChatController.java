package com;

import business.ClientThread;
import com.entity.Client;
import com.entity.Server;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;

/**
 * Controller for Client Chat UI
 * Thread for Client will start when the user executes connect
 *
 * @author  Loi Dinh on Oct-15-2020
 * @version 1.0
 */
public class ClientChatController {
    @FXML
    private TextArea txtContent;
    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtHostIP;
    @FXML
    private TextField txtPort;
    @FXML
    private TextField txtMessage;
    @FXML
    private Button btnConnect;

    private ClientThread clientThread;

    /**
     * Handle Establish connection when hitting Connect Button
     */
    @FXML
    public void handleConnectActionPerformed() {
        if (clientThread == null) {
            try {
                Client client = new Client(txtUsername.getText().trim(), "");
                Server server = new Server(txtHostIP.getText(),
                        Integer.parseInt(txtPort.getText()));

                // Display connection status on chat box
                txtContent.appendText("Connecting to server...\n");
                // Fire off a new thread to handle incoming messages from server
                clientThread = new ClientThread(server, txtContent);
                Thread t = new Thread(clientThread);
                t.setDaemon(true);
                t.start();

                // Send username to the server, "@" for identification purpose
                clientThread.handleUser("@" + client.getUsername());
                // Confirm connection status on chat box
                txtContent.appendText("Connected to server\n");
                // Disable connect button
                btnConnect.setDisable(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handle Send Message from client to server
     */
    @FXML
    public void handleSendActionPerformed(){
        try {
            // Check input valid
            if (!txtMessage.getText().isEmpty()) {
                // Send data to server
                clientThread.handleUser(txtMessage.getText());
                // Clear Message field
                txtMessage.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * Handle Send File from client to the server
     */
    @FXML
    public void btnAttach() {

    }

}
