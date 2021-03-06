package projectprototype;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JOptionPane;

public class Server implements Serializable {

    /*Used to send circles over socket*/
    private ObjectInputStream input;
    private ObjectOutputStream output;

    private ServerSocket serverSocket;
    private Socket socket;
    private Socket gameSocket;
    private ServerSocket serverGameSocket;

    /*Used to read chat over sockets*/
    private PrintWriter writer;
    private BufferedReader reader;

    protected GamePanel panel;
    protected Lobby lobby;

    private Thread chatThread;
    private Thread clientThread;

    private boolean clientConnected = false;
    private boolean playing = false;

    public Server(GamePanel panel) {
        this.panel = panel;
        String t = JOptionPane.showInputDialog("Please enter server player name:");
        if (t != null && t.length() > 0) { //cancel
            panel.player1 = new Player(t);
            this.lobby = new Lobby(panel.player1, null, panel, false);
            panel.game.showMenu(lobby);
            waitForClient();
        }
    }

    public void chat() {
        clientConnected = true;
        Runnable chat = () -> {
            while (clientConnected && !playing) {
                String msg = null;
                try {
                    msg = reader.readLine();
                } catch (Exception e) {
                    System.err.println("Failed to read.");
                    e.printStackTrace();
                }
                if (msg != null) {
                    if (msg.equals(panel.player2.name + ": " + "ready")) {
                        lobby.ready2.setSelected(!lobby.ready2.isSelected());
                    } else {
                        lobby.chat.append(msg + "\n");
                    }
                } else if (msg == null) {
                    clientConnected = false;
                    lobby.ready2.setSelected(false);
                    lobby.player2Label.setText("");
                    try {
                        disconnect();
                    } catch (Exception e) {
                        System.err.println("Failed to disconnect.");
                    }
                    //waitForClient();
                }
            }
        };
        chatThread = new Thread(chat);
        chatThread.start();
    }

    public void disconnect() throws IOException {
        serverSocket.close();
        serverGameSocket.close();
        socket.close();
    }

    public void waitForClient() {
        Runnable client = () -> {
            try {
                socket = new Socket("localhost", 9001);
                gameSocket = new Socket("localhost", 9002);
                output = new ObjectOutputStream(gameSocket.getOutputStream());
                output.flush();
                input = new ObjectInputStream(gameSocket.getInputStream());
                writer = new PrintWriter(socket.getOutputStream(), true);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                try {
                    panel.player2 = (Player)input.readObject(); //read in player from client
                    if (panel.player2 != null) {
                        lobby.player2Label.setText(panel.player2.name);
                        output.writeObject(panel.player1); //send our player
                        chat(); //open chat
                    }
                } catch (IOException e) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        clientThread = new Thread(client);
        clientThread.start();
    }

    public void send(Circle circle) throws IOException {
        output.writeObject(circle);
    }

    public void send(Player player) throws IOException {
        output.writeObject(player);
    }

    public void send(Game game) throws IOException {
        output.writeObject(game);
    }

    public void send(String msg) {
        if (msg.equals("start")) {
            if (lobby.ready1.isSelected() && lobby.ready2.isSelected()) {
                startServer();
                this.panel.newGame(panel.player1, panel.player2);
                this.panel.timer.start();
                panel.game.showMenu(panel);
            }
        } else if (msg.equals("ready")) {
            lobby.ready1.setSelected(!lobby.ready1.isSelected());
        }
        writer.write(panel.player1.name + ": " + msg + "\n");
        writer.flush();
    }

    public void startServer() {
        playing = true;
        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                boolean found = false;
                while (playing) {
                    Circle circle = null;
                    try {
                        circle = (Circle) input.readObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (circle != null) {
                        for (Circle field : panel.player2.objects) {
                            if (circle.equals(field)) {
                                found = true;
                                field.timer.stop();
                                panel.player2.objects.remove(field);
                                circle = null;
                                break;
                            }
                        }
                        if (!found) {
                            circle = new Circle(circle, panel.player1, Color.blue);
                            panel.player1.objects.add(circle);
                        }
                        found = false;
                    }
                }
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }
}
