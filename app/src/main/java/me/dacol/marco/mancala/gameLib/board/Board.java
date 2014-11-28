package me.dacol.marco.mancala.gameLib.board;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import me.dacol.marco.mancala.gameLib.gameController.TurnContext;
import me.dacol.marco.mancala.gameLib.gameController.actions.Action;
import me.dacol.marco.mancala.gameLib.gameController.actions.InvalidMove;
import me.dacol.marco.mancala.gameLib.gameController.actions.MoveAction;
import me.dacol.marco.mancala.gameLib.player.Player;

public class Board implements Observer, StandardBoard<Container> {

    ArrayList<Container> mContainers;
    List<Player> mPlayers;

    private int mNumberOfBowls;
    private int mNumberOfTrays;
    private int mNumberOfPlayers;
    private TurnContext mTurnContext;

    // Singleton
    private static Board sInstance = null;

    protected Board() {}

    public static Board getInstance() {
        if (sInstance == null) {
            sInstance = new Board();
        }
        return sInstance;
    }

    public Board setup(TurnContext turnContext, int numberOfBowl, int numberOfTray) {
        mNumberOfBowls = numberOfBowl;
        mNumberOfTrays = numberOfTray;
        mTurnContext = turnContext;
        return this; // allows concatenation
    }

    public Board registerPlayers(List<Player> players) {
        mPlayers = players;
        return this; // allows concatenation
    }

    /***
     * Builds the board, with the bowls and the trays, first is usually the human player but it
     * not mandatory, just a simplification for now.
     *
     */
    public void buildBoard() {
        // reset the board from any previous game
        int position;
        mContainers = new ArrayList<Container>();

        // One of the two player has to be an Human, TODO force this in the game class, or not??
        int humanPlayerPosition = mPlayers.get(0).isHuman() ? 0 : 1;

        // Create the six bowl of the human player
        for (position=0; position < mNumberOfBowls; position++) {
            mContainers.add(new Bowl(mPlayers.get(humanPlayerPosition)));
        }

        position +=1;
        mContainers.add(new Tray(mPlayers.get(humanPlayerPosition)));

        for (position += 1; position < (position + mNumberOfBowls); position++) {
            mContainers.add(new Bowl(mPlayers.get( ( mPlayers.size() - humanPlayerPosition ) - 1 )));
        }

        position +=1;
        mContainers.add(new Tray(mPlayers.get( ( mPlayers.size() - humanPlayerPosition ) - 1 )));

    }

    // ---> Core rules for the game
    private void move(MoveAction moveAction) {
        Move move = moveAction.getLoad();
        Container selectedContainer = getPlayerSelectedContainer(move.getBowlNumber());

        if (isAValidMove(move.getPlayer(), selectedContainer)) {
            executeMove(move, selectedContainer);
        } else {
            postOnTurnContext(new InvalidMove(
                    move,
                    getRepresentation(),
                    move.getPlayer()
            ));
        }
    }

    private boolean isAValidMove(Player player, Container selectedContainer) {
        boolean isValid = false;
        // When a move is invalid?
        // 1. the bowl have zero seeds in it
        // 2. the bowl is not owned by the player
        // 3. the player selected a tray
        if ((selectedContainer instanceof Bowl)
                && (selectedContainer.getOwner() == player)
                && (selectedContainer.getNumberOfSeeds() > 0)) {
            isValid = true;
        }
        return isValid;
    }

    private Container getPlayerSelectedContainer(int number) {
        return mContainers.get(number);
    }

    //TODO rewrite me in a non recursive way
    private int spreadSeed(int containerNumber, int remainingSeeds) {
        // If i have more than one seed to spread, I'm ok just spread it and call recursion
        // If I have to spread the last seed, check the next container is a bowl?
        // -- The playingPlayer (PP) is the owner of the bowl?
        // ---- Yes, move the seed directly to the tray, and stole the opponent seeds in the specular
        //      bowl and put them in the PP tray (if there are no seed in opponent bowl just go on)
        // No, just put the seed there and go on with your life!
        if (remainingSeeds > 1 ) {
            mContainers.get(containerNumber).putOneSeed();
            return spreadSeed(nextContainer(containerNumber), remainingSeeds - 1 );
        } else if ( (remainingSeeds == 1) && (mContainers.get(containerNumber) instanceof Bowl) ) {
            if (mContainers.get(containerNumber).getOwner() == player) {
                //Move the seed in the tray and stole opponent seeds
            }
        } else {
            mContainers.get(containerNumber).putOneSeed();
            return -1;
        }
        return -1;
    }

    private int nextContainer(int actualContainerPosition) {
        int totalNumberOfContainer = (mNumberOfBowls + mNumberOfTrays) * 2; //14 in my case, but remember it starts form 0!!
        int nextContainer = actualContainerPosition + 1;

        if ( nextContainer == totalNumberOfContainer) {
            nextContainer = 0;
        }

        return  nextContainer;
    }

    private void postOnTurnContext(Action action) {
        mTurnContext.push(action);
    }

    public boolean checkForWinner() {
        //TODO check if there is a winner in the current board situation
        return false;
    }

    public Player getWinner() {
        //TODO return the winning player
        return null;
    }

    private ArrayList<Container> getRepresentation() {
        return mContainers;
    }

    @Override
    public void update(Observable observable, Object data) {
        // board respond only to an action the MoveAction that goes to update the board status
        if (mTurnContext.peek() instanceof MoveAction) {
            move((MoveAction) mTurnContext.pop());
        }
    }
}
