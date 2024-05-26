
/**
 * Stores information about players
 *
 * @author Cooper Frolich
 * @version 12/01/2023
 */
public class Player
{
    //Instance variables
    private String name;
    private String letter;
    private int wins;
    private int losses;
    private int ties;

    /**
     * Constructor for objects of class Player. Initializes name and letter to input, initializes wins, losses, and ties to 0.
     * @param inName name of the player
     * @param inLetter player's letter, usually X or O
     */
    public Player(String inName, String inLetter)
    {
        //Initialize instance variables
        name = inName;
        letter = inLetter;
        wins = 0;
        losses = 0;
        ties = 0;
    }

    /**
     * Overloaded constructor for objects of class Player. Only initializes name to input, temporarily initializes letter to X, and initializes wins, losses, and ties to 0.
     * @param inName name of the player
     */
    public Player(String inName)
    {
        //Initialize instance variables
        name = inName;
        letter = "X";
        wins = 0;
        losses = 0;
        ties = 0;
    }

    /**
     * Sets the player's letter to input
     * @param inLetter new letter for the player
     */
    public void setLetter(String inLetter)
    {
        letter = inLetter;
    }

    /**
     * Returns the player's letter
     * @return the letter chosen by the player
     */
    public String getLetter()
    {
        return letter;
    }

    /**
     * Returns the player's name
     * @return the name of the player
     */
    public String getName()
    {
        return name;
    }

    /**
     * Increments the player's wins by one
     */
    public void incrementWins()
    {
        wins++;
    }

    /**
     * Increments the player's losses by one
     */
    public void incrementLosses()
    {
        losses++;
    }

    /**
     * Increments the player's ties by one
     */
    public void incrementTies()
    {
        ties++;
    }

    /**
     * Returns the player's wins
     * @return the number of games won by the player
     */
    public int getWins()
    {
        return wins;
    }

    /**
     * Returns the player's losses
     * @return the number of games lost by the player
     */
    public int getLosses()
    {
        return losses;
    }

    /**
     * Returns the player's ties
     * @return the number of games tied by the player
     */
    public int getTies()
    {
        return ties;
    }
}
