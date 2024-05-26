import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ObjectMessage;
import org.jgroups.Receiver;
import org.jgroups.View;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Network implements Receiver
{
    JChannel channel;
    String userName = "Cooper"; // = System.getProperty("user.name", "n/a");
    
    private void start() throws Exception
    {
        channel = new JChannel(); //Currently using UDP. Replace with TCP later.
        channel.setReceiver(this);
        channel.connect("ChatCluster");
        eventLoop();
        channel.close();
    }
    
    public void viewAccepted(View newView)
    {
        System.out.println("** view: " + newView);
    }
    
    public void receive(Message msg)
    {
        System.out.println(msg.getSrc() + ": " + msg.getObject());
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
