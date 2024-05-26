import java.util.Scanner;
import org.jgroups.Address;
import org.jgroups.Receiver;
import org.jgroups.JChannel;

/**
 * The main class for Tic Tac Toe that asks for player's names and letters, then starts the game.
 *
 * @author Cooper
 * @version 12/07/2023
 */
public class MultiplayerGame implements Receiver
{
    public void main(Player player1, Player player2, Player thisPlayer, Address opponent)
    {
        //Initialize variables
        System.out.println("In game");
        Scanner in = new Scanner(System.in);
        Board gameBoard = new Board();
        Rules gameRules = new Rules(gameBoard);
        Player currentPlayer;
        boolean playAgain = true;
        //new Network().channel.setReceiver(this);

        if (player1 == thisPlayer)
        {
            System.out.println(player1.getName() + ", would you like X or O?");
            player1.setLetter(in.nextLine());
            if (player1.getLetter().toLowerCase().equals("x"))
            {
                player1.setLetter("X");
                player2.setLetter("O");
            }
            else if (player1.getLetter().toLowerCase().equals("o"))
            {
                player1.setLetter("O");
                player2.setLetter("X");
            }
            else
            {
                System.out.println("Some people are just special, I guess. What letter would you like to give " + player2.getName() + "?");
                player2.setLetter(in.nextLine());
            }
        }
        else
        {
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
