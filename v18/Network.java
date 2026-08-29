import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ObjectMessage;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.jgroups.logging.CustomLogFactory;
import org.jgroups.logging.JDKLogImpl;
import org.jgroups.logging.Log;
import org.jgroups.logging.LogFactory;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.Address;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Network implements Receiver {
    public JChannel channel;
    public static JChannel gameChannel;
    private String userName;
    private ArrayList<Address> lobby;
    private Address requestedPlayer;
    private ArrayList<Address> playerSent;
    private String gameCluster;
    private AtomicReference<Boolean> messageRefBoolean;
    public static int ping;

    private void start(String inName) throws Exception {
        long startTime = System.nanoTime(); // Benchmark time connecting to cluster. This depends on the computer and
                                            // the network. This is really slow at school.
        channel = new JChannel();

        GMS gms = channel.getProtocolStack().findProtocol(GMS.class);
        if (gms != null) {
            gms.setMaxLeaveAttempts(1);
            gms.setLeaveTimeout(500);
        }

        channel.setReceiver(this);
        userName = inName;
        playerSent = new ArrayList<Address>();
        channel.setName(inName);
        channel.connect("TicTacToeLobby");
        long endTime = System.nanoTime(); // Stop timer
        ping = (int) ((endTime - startTime) / 1000000);
        // System.out.println("Ping: " + ping);
        eventLoop();
        channel.close();
        System.out.println("Thanks for playing!");
    }

    public void viewAccepted(View newView) {
        lobby = new ArrayList<Address>();
        for (Address user : newView) {
            if (user.equals(channel.getAddress()) == false) {
                lobby.add(user);
            }
        }
        System.out.print(lobby.size() + " other ");
        if (lobby.size() != 1) {
            System.out.print("players");
        } else {
            System.out.print("player");
        }
        System.out.println(" in lobby:");
        for (int i = 0; i < lobby.size(); i++) {
            System.out.println(i + 1 + ": " + lobby.get(i));
        }
        if (lobby.size() > 0) {
            System.out.println("Enter a number to invite another player, or type \"exit\" to quit.");
        } else {
            System.out.println("Ask someone else on the network to join, and they'll appear here.");
            System.out.println("If you're not seeing each other, there might be something wrong with your firewall.");
        }
        System.out.println();
        System.out.println();
        System.out.println();
    }

    private void connectToGame(String gameCluster, Address sender, Address reciever, boolean isSender)
            throws Exception {
        messageRefBoolean.set(true);
        channel.disconnect();
        Player player1 = new Player(sender.toString());
        Player player2 = new Player(reciever.toString());
        Player thisPlayer;
        Address opponent;
        if (isSender) {
            thisPlayer = player1;
            opponent = reciever;
        } else {
            thisPlayer = player2;
            opponent = sender;
            Thread.sleep((int) (1000 + ping));
        }
        // System.out.println("Before connect");
        MultiplayerGame game = new MultiplayerGame();
        gameChannel = channel;
        gameChannel.setReceiver(game);
        gameChannel.connect(gameCluster);
        // System.out.println("After connect");
        game.main(player1, player2, thisPlayer/* , opponent */);
        gameChannel.disconnect();
        channel.setReceiver(this);
        channel.connect("TicTacToeLobby");
        messageRefBoolean.set(false);
        viewAccepted(channel.getView());
    }

    public void receive(Message msg) {
        try {
            if (msg.getObject().equals("///.requestGame()")) {
                playerSent.add(msg.getSrc());
                System.out.println(msg.getSrc() + " invited you to a game. Accept? (y/n)");
            } else if (msg.getObject().toString().indexOf("///.acceptGame()") == 0) {
                gameCluster = msg.getObject().toString().substring(16);
                System.out.println("Invite accepted. Connecting to " + gameCluster + "...");
                // connectToGame(gameCluster, msg.getSrc(), channel.getAddress(), false);

                new Thread(() -> {
                    try {
                        connectToGame(gameCluster, msg.getSrc(), channel.getAddress(), false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();

            } else if (msg.getObject().equals("///.declineGame()")) {
                System.out.println(msg.getSrc() + " declined.");
            }
            // System.out.println(msg.getSrc() + ": " + msg.getObject());
        } catch (Exception e) {
        }
    }

    private void requestGame(Address inPlayer) {
        try {
            Message msg = new ObjectMessage(inPlayer, "///.requestGame()");
            channel.send(msg);
        } catch (Exception e) {
        }
    }

    private void acceptGame() {
        Address opponent = playerSent.get(playerSent.size() - 1);
        String newCluster = "TicTacToeMatch-";
        newCluster += channel.getAddress().toString();
        newCluster += "-";
        newCluster += opponent;
        newCluster += "-";
        newCluster += (int) (Math.random() * 9999);
        final String NEW_CLUSTER = newCluster;
        String line = "///.acceptGame()" + newCluster;
        Message msg = new ObjectMessage(opponent, line);
        /*
         * new Thread(() ->
         * {
         */try {
            channel.send(msg);
            System.out.println("Invite accepted. Connecting to " + NEW_CLUSTER + "...");
            connectToGame(NEW_CLUSTER, channel.getAddress(), opponent, true);
        } catch (Exception e) {
        }
        // }).start();
    }

    private void declineGame() {
        Address opponent = playerSent.remove(playerSent.size() - 1);
        Message msg = new ObjectMessage(opponent, "///.declineGame()");
        try {
            channel.send(msg);
        } catch (Exception e) {
        }
        System.out.println("Invite declined.");
    }

    private void eventLoop() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        messageRefBoolean = new AtomicReference<>();
        messageRefBoolean.set(false);
        while (true) {
            try {
                // System.out.println("Loop");
                while (messageRefBoolean.get()) {
                    // System.out.println(messageRefBoolean.get());
                    Thread.sleep(1000);
                }
                if (in.ready()) {
                    String line = in.readLine();
                    // in.readLine();
                    // System.out.print("Lines");
                    // System.out.println("Line: " + line);
                    if (line.toLowerCase().startsWith("exit")) {
                        break;
                    } else if (line.equals("y")) {
                        acceptGame();
                    } else if (line.equals("n")) {
                        declineGame();
                    } else if (Integer.parseInt(line) <= lobby.size() && Integer.parseInt(line) > 0) {
                        requestedPlayer = lobby.get(Integer.parseInt(line) - 1);
                        requestGame(requestedPlayer);
                        System.out.println(requestedPlayer + " invited.");
                    }
                    /*
                     * Message msg = new ObjectMessage(null, line);
                     * channel.send(msg);
                     */
                }
            } catch (Exception e) {
            }
        }
    }

    private static void iHateWarnings() {
        // Suppress only WARNING level logs, while keeping INFO, SEVERE, and banners
        for (Handler handler : Logger.getLogger("").getHandlers()) {
            handler.setFilter(record -> {
                if (record.getLoggerName() != null && record.getLoggerName().startsWith("org.jgroups")) {
                    return record.getLevel() != Level.WARNING;
                }
                return true;
            });
        }


        LogFactory.setCustomLogFactory(new CustomLogFactory() {
        @Override
        public Log getLog(Class<?> clazz) {
            return new JDKLogImpl(clazz) {
                @Override public boolean isWarnEnabled() { return false; }
                @Override public void warn(String msg) {}
                @Override public void warn(String msg, Object... args) {}
                @Override public void warn(String msg, Throwable t) {}
            };
        }
        @Override
        public Log getLog(String category) {
            return new JDKLogImpl(category) {
                @Override public boolean isWarnEnabled() { return false; }
                @Override public void warn(String msg) {}
                @Override public void warn(String msg, Object... args) {}
                @Override public void warn(String msg, Throwable t) {}
            };
        }
    });

    }

    public static void main(String[] args) throws Exception {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("jgroups.diagnostics.enabled", "false");
        System.setProperty("jgroups.bind_addr", "SITE_LOCAL");

        iHateWarnings();

        @SuppressWarnings("resource")
        Scanner in = new Scanner(System.in);
        System.out.println("Welcome to Tic Tac Toe! Please enter your name.");
        Player you = new Player(in.nextLine());
        System.out.println("Hi, " + you.getName() + "! Searching for other players...");
        new Network().start(you.getName());
    }
}
