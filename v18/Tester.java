
/**
 * Write a description of class Tester here.
 *
 * @author Cooper Frolich
 * @version 12/04/2023
 */
public class Tester
{
    public static void main(String args[])
    {
        //Creating objects
        Player player1 = new Player("Joe", "X");
        Player player2 = new Player("Sam", "O");
        Board gameBoard = new Board();
        Rules gameRules = new Rules(gameBoard);

        //Testing board
        gameBoard.setSquare(1, player1);
        gameBoard.setSquare(5, player2);

        gameBoard.printBoard();

        gameBoard.clearBoard();
        System.out.println();
        gameBoard.printBoard();

        gameBoard.setSquare(3, player1);
        System.out.println();
        gameBoard.printBoard();

        gameBoard.setSquare(8, player2);
        System.out.println();
        gameBoard.printBoard();

        //Scoring tests
        System.out.println("Player 1 wins: " + player1.getWins());
        System.out.println("Player 1 losses: " + player1.getLosses());
        System.out.println("Player 1 ties: " + player1.getTies());
        System.out.println("Player 2 wins: " + player2.getWins());
        System.out.println("Player 2 losses: " + player2.getLosses());
        System.out.println("Player 2 ties: " + player2.getTies());

        player1.incrementTies();
        player2.incrementLosses();
        player2.incrementWins();

        System.out.println("Player 1 wins: " + player1.getWins());
        System.out.println("Player 1 losses: " + player1.getLosses());
        System.out.println("Player 1 ties: " + player1.getTies());
        System.out.println("Player 2 wins: " + player2.getWins());
        System.out.println("Player 2 losses: " + player2.getLosses());
        System.out.println("Player 2 ties: " + player2.getTies());

        //Rules tests                                       //Desired behaviors:
        System.out.println(gameRules.isAvailable());       //true

        //Checks if move is out of bounds
        System.out.println(gameRules.isLegal(9));           //valid
        System.out.println(gameRules.isLegal(10));          //bounds
        System.out.println(gameRules.isLegal(1));           //valid
        System.out.println(gameRules.isLegal(0));           //bounds
        System.out.println(gameRules.isLegal(-3));          //bounds
        System.out.println(gameRules.isLegal(15));          //bounds

        //Checks if space is available
        System.out.println(gameRules.isLegal(8));           //occupied
        System.out.println(gameRules.isLegal(8));           //occupied
        System.out.println(gameRules.isLegal(3));           //occupied
        System.out.println(gameRules.isLegal(5));           //valid

        //Test real scenario
        gameBoard.setSquare(5, player1);
        gameBoard.printBoard();
        System.out.println(gameRules.isAvailable());       //true

        gameBoard.setSquare(4, player2);
        gameBoard.printBoard();
        System.out.println(gameRules.isAvailable());       //true

        //Path 1:
        /*
        gameBoard.setSquare(7, player1);
        gameBoard.printBoard();
        System.out.println(gameRules.isAvailable());       //false
        System.out.println(gameRules.isTie());              //false
         */

        //Path 2:
        /**/
        gameBoard.setSquare(6, player1);
        gameBoard.printBoard();
        System.out.println(gameRules.isAvailable());       //true

        gameBoard.setSquare(9, player2);
        gameBoard.printBoard();
        System.out.println(gameRules.isAvailable());       //true

        gameBoard.setSquare(2, player1);
        gameBoard.printBoard();
        System.out.println(gameRules.isAvailable());       //true

        gameBoard.setSquare(1, player2);
        gameBoard.printBoard();
        System.out.println(gameRules.isAvailable());       //true

        System.out.println(gameRules.isLegal(3));           //occupied
        gameBoard.setSquare(3, player2);
        gameBoard.printBoard();
        System.out.println(gameRules.isAvailable());       //true

        gameBoard.setSquare(7, player1);
        gameBoard.printBoard();
        System.out.println(gameRules.isAvailable());       //false
        //System.out.println(gameRules.isTie());              //true
        /**/

        System.out.println(gameRules.getWinner());

        /*
         * Questionable things:
         * There are 3 methods in rules that are almost exactly the same
         * Player shouldn't be needed to determine legality of move
         */

    }
}
