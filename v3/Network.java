import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ObjectMessage;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.jgroups.Address;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Network implements Receiver
{
    JChannel channel;
    String userName = "Cooper"; // = System.getProperty("user.name", "n/a");
    ArrayList<Address> lobby = new ArrayList<Address>();

    private void start() throws Exception
    {
        channel = new JChannel(); //Currently using UDP. Replace with TCP later.
        channel.setReceiver(this);
        channel.setName(userName);
        channel.connect("ChatCluster");
        eventLoop();
        channel.close();
    }

    public void viewAccepted(View newView)
    {
        //System.out.println("** view: " + newView);
        for (Address user : newView)
        {
            /*try
            {
                String line = "///.requestUserName()";
                Message msg = new ObjectMessage(null, line);
                channel.send(msg);
            }
            catch(Exception e) {
            }*/
            if (user.toString().equals(userName) == false)
            {
                lobby.add(user);
            }
            //System.out.println("Username: " + user.toString());
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
    }

    public void receive(Message msg)
    {
        try
        {
            /*if (msg.getObject().equals("///.requestUserName()"))
            {
                String line = "///.returnUserName()" + userName;
                Message name = new ObjectMessage(msg.getSrc(), line);
                channel.send(name);
            }
            else if (msg.getObject().toString().indexOf("///.returnUserName()") > -1)
            {
                System.out.println();
            }*/
            System.out.println(msg.getSrc() + ": " + msg.getObject());
        }
        catch(Exception e) {
        }
    }

    private void eventLoop()
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while(true)
        {
            try
            {
                System.out.print("> ");
                System.out.flush();
                String line = in.readLine();
                if (line.toLowerCase().startsWith("exit"))
                {
                    break;
                }
                line = "[" + userName + "] " + line;
                Message msg = new ObjectMessage(null, line);
                channel.send(msg);
            }
            catch(Exception e) {
            }
        }
    }

    public static void main(String[] args) throws Exception
    {
        //username = "Cooper";
        new Network().start();
    }
}
