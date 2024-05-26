import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ObjectMessage;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.jgroups.Address;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Network implements Receiver
{
    private JChannel channel;
    private String userName;
    private ArrayList<Address> lobby;
    private Address requestedPlayer;
    private ArrayList<Address> playerSent;

    private void start(String inName) throws Exception
    {
        channel = new JChannel();
        channel.setReceiver(this);
        userName = inName;
        playerSent = new ArrayList<Address>();
        channel.setName(inName);
        channel.connect("TicTacToeLobby");
        eventLoop();
        channel.close();
    }

    public void viewAccepted(View newView)
    {
        lobby = new ArrayList<Address>();
        for (Address user : newView)
        {
            if (user.toString().equals(userName) == false)
            {
                lobby.add(user);
            }
        }
        System.out.print(lobby.size() + " other ");
        if (lobby.size() != 1)
        {
            System.out.print("players");
        }
        else
        {
            System.out.print("player");
        }
        System.out.println(" in lobby:");
        for (int i = 0; i < lobby.size(); i++)
        {
            System.out.println(i + 1 + ": " + lobby.get(i));
        }
        if (lobby.size() > 0)
        {
            System.out.println("Enter a number to invite another player.");
        }
        else
        {
            System.out.println("Ask someone else on the network to join, and they'll appear here.");
            System.out.println("If you're not seeing each other, there might be something wrong with your firewall.");
        }
        System.out.println();
        System.out.println();
        System.out.println();
    }

    public void receive(Message msg)
    {
        try
        {
            if (msg.getObject().equals("///.requestGame()"))
            {
                playerSent.add(msg.getSrc());
                System.out.println("request recieved");
                /*String line = "///.acceptGame()" + userName;
                Message name = new ObjectMessage(msg.getSrc(), line);
                channel.send(name);*/
            }
            else if (msg.getObject().toString().indexOf("///.acceptGame()") > -1)
            {
                System.out.println();
            }
            //System.out.println(msg.getSrc() + ": " + msg.getObject());
        }
        catch(Exception e) {
        }
    }

    private void requestGame(Address inPlayer)
    {
        try
        {
            Message msg = new ObjectMessage(inPlayer, "///.requestGame()");
            channel.send(msg);
        }
        catch(Exception e) {
        }
    }

    private void acceptGame()
    {
        System.out.println("Hey");
        while(true)
        {
            if (playerSent.size() > 0)
            {
                System.out.println("Hi");
                break;
            }
        }
    }

    private void eventLoop()
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        new Thread(() -> {acceptGame();}).start();
        while(true)
        {
            try
            {
                String line = in.readLine();
                if (line.toLowerCase().startsWith("exit"))
                {
                    break;
                }
                else if (Integer.parseInt(line) <= lobby.size() && Integer.parseInt(line) > 0)
                {
                    requestedPlayer = lobby.get(Integer.parseInt(line) - 1);
                    requestGame(requestedPlayer);
                    System.out.println(requestedPlayer + " invited.");
                }
                /*Message msg = new ObjectMessage(null, line);
                channel.send(msg);*/
            }
            catch(Exception e) {
            }
        }
    }

    public static void main(String[] args) throws Exception
    {
        Scanner in = new Scanner(System.in);
        System.out.println("Welcome to Tic Tac Toe! Please enter your name!");
        Player you = new Player(in.nextLine());
        System.out.println("Hi, " + you.getName() + "! Searching for other players...");
        new Network().start(you.getName());
    }
}
