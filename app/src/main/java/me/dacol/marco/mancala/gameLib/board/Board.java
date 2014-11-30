package me.dacol.marco.mancala.gameLib.board;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import me.dacol.marco.mancala.gameLib.gameController.TurnContext;
import me.dacol.marco.mancala.gameLib.gameController.actions.Action;
import me.dacol.marco.mancala.gameLib.gameController.actions.BoardUpdated;
import me.dacol.marco.mancala.gameLib.gameController.actions.InvalidMove;
import me.dacol.marco.mancala.gameLib.gameController.actions.MoveAction;
import me.dacol.marco.mancala.gameLib.player.Player;

public class Board implements Observer, StandardBoard<Container> {

    ArrayList<Container> mContainers;
    List<Player> mPlayers;

    private int mNumberOfBowls;
    private int mNumberOfTrays;
    private TurnContext mTurnContext;
    private Player mWinner;

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

    public Player getWinner() {
        return mWinner;
    }

    // ---> Core rules for the game
    private void move(MoveAction moveAction) {
        Move move = moveAction.getLoad();
        Container selectedContainer = getPlayerSelectedContainer(move.getBowlNumber());

        if (isAValidMove(move.getPlayer(), selectedContainer)) {
            spreadSeed(move.getBowlNumber(), selectedContainer.getNumberOfSeeds(), move.getPlayer());
            //TODO this is a little confusing maybe I should integrate the position number in any container
            postOnTurnContext(new BoardUpdated(getRepresentation(), isGameEnded()));
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

    //TODO this could look even better in a recursive way
    private void spreadSeed(int containerNumber, int remainingSeeds, Player player) {
        // If i have more than one seed to spread, I'm ok just spread it and go on
        // If I have to spread the last seed, check the next container is a bowl?
        // -- The playingPlayer (PP) is the owner of the bowl?
        // ---- Yes, move the seed directly to the tray, and stole the opponent seeds in the specular
        //      bowl and put them in the PP tray (if there are no seed in opponent bowl just go on)
        // No, just put the seed there and go on with your life!
        for ( ; remainingSeeds > 1; remainingSeeds--) {
            mContainers.get(containerNumber).putOneSeed();
            containerNumber = nextContainer(containerNumber);
        }

        if ( (remainingSeeds == 1)
                && (mContainers.get(containerNumber) instanceof Bowl)
                && (mContainers.get(containerNumber).getOwner() == player)
                && (mContainers.get(containerNumber).getNumberOfSeeds() == 0) )
        {
            int wonSeeds = remainingSeeds;
            wonSeeds += getOpponentContainer(containerNumber).getNumberOfSeeds();
            getPlayerTray(player).putSeeds(wonSeeds);
        } else {
            mContainers.get(containerNumber).putOneSeed();
        }
    }

    private Tray getPlayerTray(Player player) {
        // I know for how the arraylist its built that the tray are in the position 6 and 13
        // just look for the matching one
        int trayOnePosition = 6;
        int trayTwoPosition = 13;
        // Since I've only two player in this game, I guess it's tray number one
        Container trayToReturn = mContainers.get(trayOnePosition);

        if (mContainers.get(trayTwoPosition).getOwner() == player) {
            trayToReturn = mContainers.get(trayTwoPosition);
        }
        return (Tray) trayToReturn; // Cast I'm sure it is a tray because of the way i build the board
    }

    private Container getOpponentContainer(int containerNumber) {
        // The last bowl is in position 12, the first one in position 0
        // So in order to get the opponent bowl I've to get the 12 - actual bowl position
        // ATTENTION! TODO TEST CASE ON THIS
        // Limit Case: last seeds is dropped in the bowl number zero of player one.
        return mContainers.get(12 - containerNumber);
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

    private boolean isGameEnded() {
        // A game is ended when all the bowl of one player are empty
        // Just do the sum of the bowl of each player, if one is zero you are done
        // Pay attention i do not reinizialize the index variable, since I'm going on...
        int playerOneRemainingSeeds = 0;
        int playerTwoRemainingSeeds = 0;
        int i = 0;

        boolean isEnded = false;
        for ( ; i < 6; i++) {
            playerOneRemainingSeeds += mContainers.get(i).getNumberOfSeeds();
        }

        if (playerOneRemainingSeeds == 0) {
            isEnded = true;

        } else {
            for (i += 1 ; i < 13; i++) {
                playerTwoRemainingSeeds += mContainers.get(i).getNumberOfSeeds();
            }

            if (playerTwoRemainingSeeds == 0) {
                isEnded = true;
            }
        }

        // If the game is ended i have a result
        if (isEnded) {
            setWinner();
        }

        return isEnded;
    }

    private void setWinner() {
        // I can have 3 ending state,
        // player one wins, player two wins, even game
        // default case, if this is null it means that the game is even
        mWinner = null;
        if (mContainers.get(6).getNumberOfSeeds() > mContainers.get(13).getNumberOfSeeds()) {
            mWinner = mContainers.get(6).getOwner();
        } else if (mContainers.get(6).getNumberOfSeeds() < mContainers.get(13).getNumberOfSeeds()) {
            mWinner = mContainers.get(13).getOwner();
        }
    }

    public ArrayList<Container> getRepresentation() {
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
