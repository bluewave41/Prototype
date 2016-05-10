package projectprototype;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JOptionPane;

public class Client {

    /*Used to send circles over socket*/
<<<<<<< HEAD
    private static ObjectInputStream input;
    private static ObjectOutputStream output;
    
=======
    private ObjectInputStream input;
    private ObjectOutputStream output;

>>>>>>> upstream/develop
    /*Used to read chat over sockets*/
    private PrintWriter writer;
    private BufferedReader reader;

    private GamePanel panel;

    public Client(String address, GamePanel panel) {
        this.panel = panel;
        createPlayer();
        openSocket(address);
        try {
            send(panel.player1);
            panel.player2 = (Player) input.readObject();
        } catch (Exception e) {
            System.err.println("Failed to send player.");
        }
        startListening();
        chat();
    }

    public void openSocket(String address) {
        try {
            Socket socket = new Socket(address, 4444);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
<<<<<<< HEAD
            System.out.println("Client connected to server.");
        } catch (Exception e) {}
        startListening();
=======
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void chat() {
        Runnable chat = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String msg = null;
                    try {
                        msg = reader.readLine();
                    } catch (Exception e) {
                        System.err.println("Failed to read message.");
                    }
                    if (msg != null) {
                        System.out.println("What");
                        panel.game.chat.append(msg + "\n");
                        msg = null;
                    }
                }
            }
        };
        Thread chatThread = new Thread(chat);
        chatThread.start();
    }

    public void createPlayer() {
        String t = JOptionPane.showInputDialog("Please enter client player name:");
        panel.player1 = new Player(t);
>>>>>>> upstream/develop
    }

    public void send(Circle circle) throws IOException {
        output.writeObject(circle);
    }

    public void send(Player player) throws IOException {
        output.writeObject(player);
    }
    
    public void send(String msg) {
        writer.write(msg);
        writer.flush();
    }

    public void startListening() {
        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
<<<<<<< HEAD
                try {
                    send(clientPlayer);
                    System.out.println("Client sent client player data to server.");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    serverPlayer = (Player) input.readObject();
                    System.out.println("Received server player data from server");
                } catch (Exception e) {
                    e.printStackTrace();
                }
=======
>>>>>>> upstream/develop
                while (true) {

                    Circle circle = null;
                    try {
                        circle = (Circle) input.readObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (circle != null) {
                        circle = new Circle(circle);
                        //circle.player = GamePanel.player1;
                        //GamePanel.player1.objects.add(circle);
                    }
                }
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }
}
