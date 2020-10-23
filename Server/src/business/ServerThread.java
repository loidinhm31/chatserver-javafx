package business;

import com.entity.Client;
import com.entity.Server;
import controller.ServerBoxController;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Multithreaded chat server
 * Clients connect on port 5000 and give their name
 * Then the server accepts input and broadcasts it to the other clients
 *
 * @author  Loi Dinh on Oct-15-2020
 * @version 1.0
 */
public class ServerThread implements Runnable {
    private Server chatServer;
    private ServerSocket listen;


    public static HashMap<String, ClientHandler> clientHandlerHashMap ;

    /**
     * ServerThread Constructor
     * @param chatServer
     * @param listen
     */
    public ServerThread(Server chatServer, ServerSocket listen) {
        this.chatServer = chatServer;
        this.listen = listen;
        clientHandlerHashMap = new HashMap<>();
    }


    @Override
    public void run() {
        // The usual loop for accepting connections and firing off new threads to handle them
        while (true) {
            try {
                // Accept connection
                Socket socket = listen.accept();

                // Create new handler when client connect
                ClientHandler newClientHandler = identifyUser(socket,
                        new DataInputStream(socket.getInputStream()),
                        new DataOutputStream(socket.getOutputStream()));

                // Create new thread for client and start it
                new Thread(newClientHandler).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get user's information when it connects
     * @param socket
     * @param in
     * @param out
     * @return
     * @throws IOException
     */
    public ClientHandler identifyUser(Socket socket, DataInputStream in, DataOutputStream out) throws IOException {
        ClientHandler incomingHandler = null;
        // Read data when the user connects
        String userInput = in.readUTF();
        // Create a new object of this user
        Client client = new Client();
        if (userInput != null) {
            // First data received when the user connects (noted by "@")
            String username = userInput.substring(userInput.indexOf("@") + 1);
            // Set user's information
            client.setUsername(username);
            System.out.println("It's " + client); // For checking
            // Send data back to user to confirm connection
            out.writeUTF("Welcome "+ client);

            // Create a new handler to manage
            incomingHandler = new ClientHandler(this, socket, client);

            // Add new user to list view and hashmap
            addHandler(username, incomingHandler);

            // Notify other clients when this user connects
            broadcast(incomingHandler, client + " entered the room");
        }
        return incomingHandler;
    }


    /**
     * Add the handler to the list of current client handlers
     * @param username
     * @param handler
     */
    public synchronized void addHandler(String username, ClientHandler handler) {
        Client newClient = new Client(username, "");

        // Add new client to List View
        Platform.runLater(new Runnable() {                                          // Avoid IllegalStageException
            @Override
            public void run() {
                ServerBoxController.clientsList.add(newClient);
            }
        });

        // Add new client to Hashmap
        clientHandlerHashMap.put(username, handler);
    }

    /**
     * Remove the handler from the hashmap of current client handlers
     * @param username
     */
    public synchronized void removeHandler(String username) {
        clientHandlerHashMap.remove(username);
    }


    /**
     * Send the message from the one client handler to all the others (but not echoing back to the originator)
     * @param from
     * @param msg
     * @throws IOException
     */
    public synchronized void broadcast(ClientHandler from, Object msg) throws IOException {
        for (Map.Entry<String, ClientHandler> entry : clientHandlerHashMap.entrySet()) {
            ClientHandler current = entry.getValue();
            if (current != from && clientHandlerHashMap.size() > 1) {
                current.dos.writeUTF(msg.toString());
            }
        }
    }

}
