
/**
 * Returns information about the board to assist in the game's logic
 *
 * @author Cooper Frolich
 * @version 12/05/2023
 */
public class Rules
{
    //Instance variables
    private Board gameBoard;

    /**
     * Constructor for objects of class Rules
     * @param inGameBoard the Board object to use when checking rules
     */
    public Rules(Board inGameBoard)
    {
        //Initialize instance variables
        gameBoard = inGameBoard;
    }

    /**
     * Returns whether a move is valid or what makes it invalid
     * @param squareNum the square being checked
     * @return if it's valid or what the problem is
     */
    public String isLegal(int squareNum)
    {
        String legalCheck = "";
        if (squareNum >= 1 && squareNum <= 9)
        {
            if (gameBoard.getSquare(squareNum) == null)
            {
                legalCheck = "valid";
            }
            else
            {
                legalCheck = squareNum + " is already taken. Please enter an available square on the board";
            }
        }
        else
        {
            legalCheck = squareNum + " is out of bounds. Please enter an available square on the board";
        }
        return legalCheck;
    }

    /**
     * Returns whether there are any available squares on the board
     * @return if any squares are open
     */
    public boolean isAvailable()
    {
        boolean availableSquares = false;
        for (int i = 1; i <= 9; i++)
        {
            if (gameBoard.getSquare(i) == null)
            {
                availableSquares = true;
            }
        }

        return availableSquares;
    }   

    /**
     * Returns the winner of game
     * @return the object of the player with three squares in a row, column, or diagonal
     */
    public Player getWinner()
    {
        Player winner;

        if (gameBoard.getSquare(1) != null && gameBoard.getSquare(1) == gameBoard.getSquare(2) && gameBoard.getSquare(1) == gameBoard.getSquare(3)) //Checking for horizontal wins
        {
            winner = gameBoard.getSquare(1);
        }
        else if (gameBoard.getSquare(4) != null && gameBoard.getSquare(4) == gameBoard.getSquare(5) && gameBoard.getSquare(4) == gameBoard.getSquare(6))
        {
            winner = gameBoard.getSquare(4);
        }
        else if (gameBoard.getSquare(7) != null && gameBoard.getSquare(7) == gameBoard.getSquare(8) && gameBoard.getSquare(7) == gameBoard.getSquare(9))
        {
            winner = gameBoard.getSquare(7);
        }
        else if (gameBoard.getSquare(1) != null && gameBoard.getSquare(1) == gameBoard.getSquare(4) && gameBoard.getSquare(1) == gameBoard.getSquare(7)) //Checking for vertical wins
        {
            winner = gameBoard.getSquare(1);
        }
        else if (gameBoard.getSquare(2) != null && gameBoard.getSquare(2) == gameBoard.getSquare(5) && gameBoard.getSquare(2) == gameBoard.getSquare(8))
        {
            winner = gameBoard.getSquare(2);
        }
        else if (gameBoard.getSquare(3) != null && gameBoard.getSquare(3) == gameBoard.getSquare(6) && gameBoard.getSquare(3) == gameBoard.getSquare(9))
        {
            winner = gameBoard.getSquare(3);
        }
        else if (gameBoard.getSquare(1) != null && gameBoard.getSquare(1) == gameBoard.getSquare(5) && gameBoard.getSquare(1) == gameBoard.getSquare(9)) //Checking for diagonal wins
        {
            winner = gameBoard.getSquare(1);
        }
        else if (gameBoard.getSquare(3) != null && gameBoard.getSquare(3) == gameBoard.getSquare(5) && gameBoard.getSquare(3) == gameBoard.getSquare(7))
        {
            winner = gameBoard.getSquare(3);
        }
        else
        {
            winner = null;
        }
        return winner;
    }
}
