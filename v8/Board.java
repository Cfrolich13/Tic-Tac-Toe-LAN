
/**
 * Stores and updates the game board
 *
 * @author Cooper Frolich
 * @version 12/01/2023
 */
public class Board
{
    //Instance variables
    private Player squares[];

    /**
     * Constructor for objects of class Board. Initializes an array of 9 squares.
     */
    public Board()
    {
        //Initialize instance variables
        squares = new Player[9];
    }

    /**
     * Sets the square number passed in to the player object passed in
     * @param squareNum the number of the square to set
     * @param inPlayer the player that's claiming the square
     */
    public void setSquare(int squareNum, Player inPlayer)
    {
        squares[squareNum - 1] = inPlayer;
    }

    /**
     * Returns the object of the player for a specific square
     * @param squareNum the number of the square to return
     * @return the player object occupying the square
     */
    public Player getSquare(int squareNum)
    {
        return squares[squareNum - 1];
    }

    /**
     * Clears the board so a new game can be played
     */
    public void clearBoard()
    {
        squares = new Player[9];
    }

    /**
     * Prints the board with the players' chosen letters
     */
    public void printBoard()
    {
        for (int i = 1; i <= 9; i++)
        {
            Player currentSquare = getSquare(i);
            String currentLetter;

            if (currentSquare == null)
            {
                currentLetter = Integer.toString(i);
            }
            else
            {
                currentLetter = currentSquare.getLetter();
            }

            if (i == 2 | i == 5 | i == 8)
            {
                System.out.print(" | " + currentLetter + " | ");
            }
            else
            {
                System.out.print(currentLetter);
            }

            if (i % 3 == 0)
            {
                System.out.println();
            }
        }
    }
}
