import java.util.Scanner;
import org.jgroups.Address;
import org.jgroups.Receiver;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ObjectMessage;
import org.jgroups.View;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The main class for Tic Tac Toe that asks for player's names and letters, then starts the game.
 *
 * @author Cooper
 * @version 12/07/2023
 */
public class MultiplayerGame implements Receiver
{
    private View view;
    private Address opponent = null;
    private AtomicReference<ArrayList> messageRefStrings;
    private AtomicReference<Integer> messageRefInteger;
    private AtomicReference<Boolean> messageRefBoolean;
    private AtomicReference<CountDownLatch> latchRef;
    private boolean readyPlayAgain = false;

    public void receive(Message msg)
    {
        try
        {
            /*System.out.println("Received.");
            System.out.println(msg.getSrc() + ": " + msg.getObject());*/
            Object contents = msg.getObject();
            if (contents instanceof ArrayList)
            {
                ArrayList<String> letters = (ArrayList) contents;
                messageRefStrings.set(letters);
                CountDownLatch latch = latchRef.get(); // Get the latch from the reference
                if (latch != null) {
                    latch.countDown(); // Signal that the message has been received
                }
            }
            else if (contents instanceof Integer)
            {
                Integer move = (Integer) contents;
                //System.out.println("We like numbers 'round here: " + move);
                messageRefInteger.set(move);
                CountDownLatch latch = latchRef.get(); // Get the latch from the reference
                if (latch != null) {
                    latch.countDown(); // Signal that the message has been received
                }
            }
            else if (contents instanceof Boolean)
            {
                Boolean playAgain = (Boolean) contents;
                messageRefBoolean.set(playAgain);
                CountDownLatch latch = latchRef.get(); // Get the latch from the reference
                if (latch != null) {
                    latch.countDown(); // Signal that the message has been received
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
        //System.out.println("ArrayList: " + newView);
    }

    public Address getOpponent(String oppName)
    {
        for (Address player : view)
        {
            //System.out.println(player);
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
        Player otherPlayer;
        boolean playAgain = true;

        //System.out.println("In game2");
        //new Network().channel.setReceiver(this);
        //System.out.println("In game3");
        //System.out.println("Player 1: " + player1.getName());
        //System.out.println("Player 2: " + player2.getName());
        messageRefStrings = new AtomicReference<>(); // Reference to store the received message
        messageRefInteger = new AtomicReference<>();
        messageRefBoolean = new AtomicReference<>();

        // Set up the receiver with a reference to the AtomicReference and a place for the latch
        latchRef = new AtomicReference<>();

        view = Network.gameChannel.getView();

        System.out.println("Waiting for opponent...");
        if (player1 == thisPlayer)
        {
            //Set opponent variable
            //Thread.sleep((int)(1000 + Network.ping * 2));
            otherPlayer = player2;
            while (opponent == null)
            {
                opponent = getOpponent(otherPlayer.getName());
                Thread.sleep(1000);
            }
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
                System.out.println("I told you not to shoot the messenger! " + e);
            }
            System.out.println(player1.getName() + ", you will be \"" + player1.getLetter() + ".\" " + player2.getName() + " will get \"" + player2.getLetter() + ".\"");
        }
        else
        {
            //Set opponent variable
            otherPlayer = player1;
            opponent = getOpponent(otherPlayer.getName());
            System.out.println("Opponent found: " + opponent);
            CountDownLatch latch = new CountDownLatch(1);
            latchRef.set(latch); // Set the latch reference

            System.out.println(player1.getName() + " is choosing a letter.");
            latch.await(); // Wait for the message to be received
            //in.nextLine();
            //System.out.println("Message transfered.");
            ArrayList<String> letters = messageRefStrings.get();
            player1.setLetter(letters.get(0));
            player2.setLetter(letters.get(1));

            System.out.println(player1.getName() + " chose \"" + player1.getLetter() + ".\" " + player2.getName() + ", you will be \"" + player2.getLetter() + ".\"");
        }

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
                if (currentPlayer == thisPlayer) //This player's turn
                {
                    //Place player's letter on a square
                    System.out.println("Your turn, " + currentPlayer.getName() + ". Where do you want to place your letter?");

                    /*String input = in.nextLine();
                    while (input.equals("\n"))
                    {
                    System.out.println("Line");
                    input = in.nextLine();
                    }*/
                    /*while (!in.hasNextInt())
                    {
                    in.nextLine();
                    }*/
                    gameBoard.printBoard();
                    int requestedSquare = in.nextInt();// = in.nextInt();
                    while (gameRules.isLegal(requestedSquare) != "valid")
                    {
                        System.out.println(gameRules.isLegal(requestedSquare) + ", " + currentPlayer.getName() + ".");
                        gameBoard.printBoard();
                        requestedSquare = in.nextInt();
                    }
                    gameBoard.setSquare(requestedSquare, currentPlayer);
                    try
                    {
                        Network.gameChannel.send(new ObjectMessage(opponent, requestedSquare)); //Send move to other player
                    }
                    catch (Exception e) {
                    }
                }
                else //Waiting for other player's turn
                {
                    CountDownLatch latch = new CountDownLatch(1);
                    latchRef.set(latch); // Set the latch reference
                    System.out.println(otherPlayer.getName() + " is making a move. You're next, " + thisPlayer.getName() + ".");
                    gameBoard.printBoard();
                    System.out.println("Waiting for opponent...");
                    //Wait for move
                    latch.await(); // Wait for the message to be received
                    //System.out.println("Message transfered.");
                    int move = messageRefInteger.get();
                    //System.out.println(move);
                    gameBoard.setSquare(move, otherPlayer);
                    //in.nextInt();
                }
            } //Game ends when someone wins or board is full

            gameBoard.printBoard();

            //Congratulates winner and updates score
            if (gameRules.getWinner() == null)
            {
                System.out.println("Game over! It's a tie!");
                player1.incrementTies();
                player2.incrementTies();
            }
            else
            {

                if (gameRules.getWinner() == thisPlayer)
                {
                    System.out.println("Congratulations, " + gameRules.getWinner().getName() + "! You won!");                    
                }
                else
                {
                    System.out.println("Game over! " + gameRules.getWinner().getName() + " won.");
                }
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

            in.nextLine();

            //Ask if they want to play again
            
            CountDownLatch latch = new CountDownLatch(1);
            latchRef.set(latch); // Set the latch reference
            
            Thread askPlayAgain = new Thread(() ->
                        {
                            System.out.println("Do you want to play again? (y/n)");
                            if (in.nextLine().equals("y"))
                            {
                                readyPlayAgain = true;
                            }
                            else
                            {
                                readyPlayAgain = false;
                            }
                            try
                            {
                                Network.gameChannel.send(new ObjectMessage(opponent, readyPlayAgain));
                            }
                            catch (Exception e){
                            }
                    });
            askPlayAgain.start();

            latch.await();
            boolean oppPlayAgain = messageRefBoolean.get();
            if (oppPlayAgain)
            {
                System.out.print(otherPlayer.getName() + " would like to play again. ");
                askPlayAgain.start();
            }
            else
            {
                System.out.println(otherPlayer.getName() + " left the game.");
                playAgain = false;
                try
                {
                    Network.gameChannel.send(new ObjectMessage(opponent, false));
                }
                catch (Exception e) {
                }
            }

            gameBoard.clearBoard();
        }

        //End program
        System.out.println("Returning to lobby...");
    }
}
