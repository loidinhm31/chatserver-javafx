package business;

import com.entity.Server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

import javafx.scene.control.TextArea;

/**
 * Client Thread to connect to Server
 *
 * @author  Loi Dinh on Oct-15-2020
 * @version 1.0
 */
public class ClientThread implements Runnable, Serializable {
    private final DataInputStream dis;
    private final DataOutputStream dos;
    private final Socket socket;
    private final Server server;
    private final TextArea txtContent;
    private boolean hungUp = false;             // check the server hung up

    /**
     * Client Thread Constructor
     * @param server
     * @param txtContent
     * @throws IOException
     */
    public ClientThread (Server server, TextArea txtContent) throws IOException {
        this.txtContent = txtContent;
        this.server = server;

        // Create communication channel
        socket = new Socket(server.getHost(), server.getPort());
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        Object line;
        try {
            while (!hungUp) {
                // Read data from server
                line = dis.readUTF();
                // Check content valid
                if (line != null) {
                    // Display data on chat box
                    txtContent.appendText("\n" + server.getHost() + ": " + line.toString());
                }
            }

        } catch (IOException e) {
            System.out.println("EOF Exception: " + e.getMessage());
        } finally {
            // Change connection status
            hungUp = true;

            // Display status on chat box
            txtContent.appendText("\nServer hung up");
            // Clean up communication
            try {
                dos.close();
                dis.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("Closing Error: " + e.getMessage());;
            }

        }
    }

    /**
     * Get input from user and send it to serve
     * @param line
     * @throws IOException
     */
    public void handleUser(Object line) throws IOException {
        // Write data
        dos.writeUTF(line.toString());

        // This condition comes from data which the user sent to the server
        // for identification purpose
        if (!line.toString().startsWith("@")) {
            // Display data sent from user on chat box
            txtContent.appendText("\nMe: " + line.toString());
        }
    }
}