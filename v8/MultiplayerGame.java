import java.util.Scanner;
import org.jgroups.Address;
import org.jgroups.Receiver;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ObjectMessage;
import org.jgroups.View;
import java.util.ArrayList;

/**
 * The main class for Tic Tac Toe that asks for player's names and letters, then starts the game.
 *
 * @author Cooper
 * @version 12/07/2023
 */
public class MultiplayerGame implements Receiver
{
    private View view;
    private Address opponent;

    public void receive(Message msg)
    {
        try
        {
            System.out.println("Received.");
            System.out.println(msg.getSrc() + ": " + msg.getObject());
            if (msg.getObject() instanceof ArrayList)
            {
                ArrayList<String> letters = msg.getObject();
                System.out.println("Letters: ");
                for (String letter : letters)
                {
                    System.out.println(letter);
                }
            }
        }
        catch(Exception e) {
            System.out.println("Bad reception: " + e);
        }
    }

    public void viewAccepted(View newView)
    {
        view = newView;
        System.out.println("ArrayList: " + newView);
    }

    public Address getOpponent(String oppName)
    {
        for (Address player : view)
        {
            System.out.println(player);
            if (player.toString().equals(oppName))
            {
                return player;
            }
        }
        return null;
    }

    public void main(Player player1, Player player2, Player thisPlayer/*, Address opponent*/) throws InterruptedException
    {
        //Initialize variables
        //System.out.println("In game1");
        Scanner in = new Scanner(System.in);
        Board gameBoard = new Board();
        Rules gameRules = new Rules(gameBoard);
        Player currentPlayer;
        boolean playAgain = true;
        //System.out.println("In game2");
        //new Network().channel.setReceiver(this);
        //System.out.println("In game3");
        //System.out.println("Player 1: " + player1.getName());
        //System.out.println("Player 2: " + player2.getName());
        Thread.sleep(3000);
        view = Network.gameChannel.getView();

        System.out.println("Finding opponent...");
        if (player1 == thisPlayer)
        {
            //Set opponent variable
            //System.out.println("View: " + view);
            opponent = getOpponent(player2.getName());
            System.out.println("Opponent found: " + opponent);

            //Set letters
            System.out.println(player1.getName() + ", would you like X or O?");
            player1.setLetter(in.nextLine());
            if (player1.getLetter().toUpperCase().equals("X"))
            {
                player1.setLetter("X");
                player2.setLetter("O");
            }
            else if (player1.getLetter().toUpperCase().equals("O"))
            {
                player1.setLetter("O");
                player2.setLetter("X");
            }
            else
            {
                System.out.println("It's always someone. What letter would you like to give " + player2.getName() + "?");
                player2.setLetter(in.nextLine());
            }

            ArrayList<String> letters = new ArrayList<String>();
            letters.add(player1.getLetter());
            letters.add(player2.getLetter());

            try
            {
                //Thread.sleep(1000);
                Network.gameChannel.send(new ObjectMessage(opponent, letters));
            }
            catch (Exception e) {
                System.out.println("I told you not to shoot the messenger!");
            }
        }
        else
        {
            //Set opponent variable
            System.out.println("View: " + view);
            opponent = getOpponent(player1.getName());
            System.out.println("Opponent found: " + opponent);

            //Set letters
            /*try
            {
                Thread.sleep(1000);
                Network.gameChannel.send(new ObjectMessage(opponent, "Just checkin' in"));
            }
            catch (Exception e) {
                System.out.println("Houston, we have a problem.");
            }*/
            System.out.println(player1.getName() + " is choosing a letter.");
            in.nextLine();
        }

        System.out.println(player1.getName() + ", you will be \"" + player1.getLetter() + ".\" " + player2.getName() + " will get \"" + player2.getLetter() + ".\"");

        //Play game until players end it.
        while (playAgain)
        {
            int round = 0;
            System.out.print("Let's start! ");
            //Game loop
            while (gameRules.isAvailable() && gameRules.getWinner() == null)
            {
                //Switch between players for taking turns
                round++;
                if (round %2 == 1)
                {
                    currentPlayer = player1;
                }
                else
                {
                    currentPlayer = player2;
                }
                //Place player's letter on a square
                System.out.println("Your turn, " + currentPlayer.getName() + ". Where do you want to place your letter?");
                gameBoard.printBoard();
                int requestedSquare = in.nextInt();
                while (gameRules.isLegal(requestedSquare) != "valid")
                {
                    System.out.println(gameRules.isLegal(requestedSquare) + ", " + currentPlayer.getName() + ".");
                    gameBoard.printBoard();
                    requestedSquare = in.nextInt();
                }
                gameBoard.setSquare(requestedSquare, currentPlayer);
            } //Game ends when someone wins or board is full

            gameBoard.printBoard();

            //Congratulates winner and updates score
            if (gameRules.getWinner() == null)
            {
                System.out.println("MultiplayerGame over! That's a tie!");
                player1.incrementTies();
                player2.incrementTies();
            }
            else
            {
                System.out.println("Congratulations, " + gameRules.getWinner().getName() + "! You won!");
                if (gameRules.getWinner() == player1)
                {
                    player1.incrementWins();
                    player2.incrementLosses();
                }
                else
                {
                    player1.incrementLosses();
                    player2.incrementWins();
                }
            }

            //Print score
            System.out.println("Score:");
            System.out.printf("%8s","");
            System.out.printf("%8s",player1.getName());
            System.out.printf("%8s",player2.getName());
            System.out.println();

            System.out.printf("%8s","Wins");
            System.out.printf("%8d",player1.getWins());
            System.out.printf("%8d",player2.getWins());
            System.out.println();

            System.out.printf("%8s","Losses");
            System.out.printf("%8d",player1.getLosses());
            System.out.printf("%8d",player2.getLosses());
            System.out.println();

            System.out.printf("%8s","Ties");
            System.out.printf("%8d",player1.getTies());
            System.out.printf("%8d",player2.getTies());
            System.out.println();

            in.nextLine(); //I don't really get why this needs to be here, but I won't question it if it works

            //Ask if they want to play again
            System.out.println("Do you want to play again? (y/n)");

            if (in.nextLine().equals("y"))
            {
                playAgain = true;
                gameBoard.clearBoard();
            }
            else
            {
                playAgain = false;
            }
        }

        //End program
        System.out.println("Thanks for playing!");
    }
}
