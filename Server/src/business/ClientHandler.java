package business;

import com.entity.Client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import controller.ServerBoxController;
import javafx.scene.control.TextArea;

/**
 * Handle communication between a server and one client
 *
 * @author  Loi Dinh on Oct-15-2020
 * @version 1.0
 */
public class ClientHandler implements Runnable {
    public DataOutputStream dos;

    private DataInputStream dis;
    private final Socket socket;
    private final Client client;
    private TextArea txtContent;
    private ServerThread serverThread;

    private boolean communicateServer = false;


    public ClientHandler(ServerThread serverThread, Socket socket, Client client) {
        this.client = client;
        this.serverThread = serverThread;
        this.socket = socket;
    }



    /**
     * Configure chat box for server-client connection
     * @param txtContent
     */
    public void setTxtContent(TextArea txtContent) {
        communicateServer = true;
        System.out.println("Server is joining");
        this.txtContent = txtContent;
    }


    @Override
    public void run() {
        try {
            // Communication channel
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            // Chat away
            while (true) {
                // Read data
                Object line = dis.readUTF();

                do {
                    String msg = client.getUsername() + ": " + line.toString();
                    // Transfer data to other users
                    serverThread.broadcast(this, msg);
                    // Only receive data once
                    line = null;

                    // Display data on chat box when server-client connection establish
                    if (communicateServer) {
                        txtContent.appendText("\n" + msg);
                    }
                } while (line != null);

            }

        } catch (IOException e) {
            System.out.println("EOF Exception when user disconnect: " + e.getMessage());

            // Remove Handler when user disconnect
            serverThread.removeHandler(client.getUsername());
            ServerBoxController.removeUser(client.getUsername());

        } finally {
            // Display status on chat box when client are communicating with server
            if (communicateServer) {
                txtContent.appendText("\nUser " + client.getUsername() + " was disconnected");
            }

            // Clean up communication
            try {
                dis.close();
                dos.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("Close communication: " + e.getMessage());
            }
        }

    }

    /**
     * Send data to client in server-client connection
     * @param line
     * @throws IOException
     */
    public void send(Object line) throws IOException {
        // Send a message from the server
        dos.writeUTF(line.toString());
        txtContent.appendText("\nMe from Server: " + line);
    }
}