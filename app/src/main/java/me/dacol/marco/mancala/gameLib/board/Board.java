package me.dacol.marco.mancala.gameLib.board;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import me.dacol.marco.mancala.gameLib.gameController.TurnContext;
import me.dacol.marco.mancala.gameLib.gameController.actions.Action;
import me.dacol.marco.mancala.gameLib.gameController.actions.BoardUpdated;
import me.dacol.marco.mancala.gameLib.gameController.actions.EvenGame;
import me.dacol.marco.mancala.gameLib.gameController.actions.InvalidMove;
import me.dacol.marco.mancala.gameLib.gameController.actions.MoveAction;
import me.dacol.marco.mancala.gameLib.gameController.actions.Winner;
import me.dacol.marco.mancala.gameLib.player.Player;

public class Board implements Observer, StandardBoard<Container> {

    private final static String LOG_TAG = Board.class.getSimpleName();

    ArrayList<Container> mContainers;
    List<Player> mPlayers;

    private int mNumberOfBowls;
    private int mNumberOfTrays;
    private TurnContext mTurnContext;
    private Player mWinner;
    private boolean mEvenGame;

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
        mEvenGame = false;
        mWinner = null;
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
        mContainers = new ArrayList<Container>();

        // One of the two player has to be an Human
        int humanPlayerPosition = mPlayers.get(0).isHuman() ? 0 : 1;

        // Create the six bowl of the human player
        for (int position = 0; position < mNumberOfBowls; position++) {
            mContainers.add(new Bowl(mPlayers.get(humanPlayerPosition)));
        }

        mContainers.add(new Tray(mPlayers.get(humanPlayerPosition)));

        for (int position = 0; position < mNumberOfBowls; position++) {
            mContainers.add(new Bowl(mPlayers.get( ( mPlayers.size() - humanPlayerPosition ) - 1 )));
        }

        mContainers.add(new Tray(mPlayers.get( ( mPlayers.size() - humanPlayerPosition ) - 1 )));


    }

    public Player getWinner() {
        return (mEvenGame && (mWinner == null))
                ? null
                : mWinner;
    }

    // ---> Core rules for the game
    private void move(MoveAction moveAction) {
        Move move = moveAction.getLoad();
        Container selectedContainer = getPlayerSelectedContainer(move.getBowlNumber());

        if (isAValidMove(move.getPlayer(), selectedContainer)) {
            boolean anotherRound = spreadSeedFrom(move.getBowlNumber());

            if (isGameEnded()) {
                Action gameEnded;
                if (!mEvenGame) {
                    gameEnded = new Winner(mWinner, getRepresentation());
                }  else {
                    gameEnded = new EvenGame(getRepresentation());
                }

                postOnTurnContext(gameEnded);
            } else {
                postOnTurnContext(new BoardUpdated(getRepresentation(), anotherRound));
            }
        } else {
            postOnTurnContext(new InvalidMove(
                    move,
                    getRepresentation(),
                    move.getPlayer()    //TODO remove this arguments, i already have it in the move obj
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
    private boolean spreadSeedFrom(int containerNumber) {
        int remainingSeeds = ((Bowl) mContainers.get(containerNumber)).emptyBowl();
        Player player = mContainers.get(containerNumber).getOwner();
        boolean lastSeedFallInPlayerTray = false;

        int bowlNumber = nextContainer(containerNumber);

        // If i have more than one seed to spread, I'm ok just spread it and go on
        // If I have to spread the last seed, check the next container is a bowl?
        // -- The playingPlayer (PP) is the owner of the bowl?
        // ---- Yes, move the seed directly to the tray, and stole the opponent seeds in the specular
        //      bowl and put them in the PP tray (if there are no seed in opponent bowl just go on)
        // No, just put the seed there and go on with your life!
        for (; remainingSeeds > 1; remainingSeeds--) {
            mContainers.get(bowlNumber).putOneSeed();
            bowlNumber = nextContainer(bowlNumber);
        }

        if ( (remainingSeeds == 1)
                && (mContainers.get(bowlNumber) instanceof Bowl)
                && (mContainers.get(bowlNumber).getOwner() == player)
                && (mContainers.get(bowlNumber).getNumberOfSeeds() == 0) )
        {

            int wonSeeds = remainingSeeds;
            wonSeeds += getOpponentContainer(bowlNumber).emptyBowl();
            getPlayerTray(player).putSeeds(wonSeeds);
        } else if (mContainers.get(bowlNumber) == getPlayerTray(player)) {
            mContainers.get(bowlNumber).putOneSeed();
            lastSeedFallInPlayerTray = true;
        } else {
            mContainers.get(bowlNumber).putOneSeed();
        }

        return lastSeedFallInPlayerTray;
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

    private Bowl getOpponentContainer(int containerNumber) {
        // The last bowl is in position 12, the first one in position 0
        // So in order to get the opponent bowl I've to get the 12 - actual bowl position
        // ATTENTION!
        // Limit Case: last seeds is dropped in the bowl number zero of player one.
        return (Bowl) mContainers.get(12 - containerNumber);
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
        int playerOneRemainingSeeds = 0;
        int playerTwoRemainingSeeds = 0;

        boolean isEnded = false;

        for (int i = 0 ; i < mNumberOfBowls; i++) {
            playerOneRemainingSeeds += mContainers.get(i).getNumberOfSeeds();
        }
        for (int j = (mNumberOfBowls + mNumberOfTrays) ; j < (mContainers.size() -1); j++) {
            playerTwoRemainingSeeds += mContainers.get(j).getNumberOfSeeds();
        }

        if ((playerOneRemainingSeeds == 0) || (playerTwoRemainingSeeds == 0)) {
            isEnded = true;
        }

        // If the game is ended put all remaining seeds in the player bowl and find the winner
        if (isEnded) {
            if (playerOneRemainingSeeds > 0) {
                for (int j = 0; j < mNumberOfBowls; j++) {
                    ((Bowl) mContainers.get(j)).emptyBowl();
                }
                ((Tray) mContainers.get(6)).putSeeds(playerOneRemainingSeeds);
            } else {
                for (int j = (mNumberOfBowls + mNumberOfTrays); j < (mContainers.size() -1); j++) {
                    ((Bowl) mContainers.get(j)).emptyBowl();
                }
                ((Tray) mContainers.get(13)).putSeeds(playerTwoRemainingSeeds);
            }
            setWinner();
        }

        return isEnded;
    }

    private void setWinner() {
        // I can have 3 ending state,
        // player one wins, player two wins, even game
        // default case, if this is null it means that the game is even
        if (mContainers.get(6).getNumberOfSeeds() > mContainers.get(13).getNumberOfSeeds()) {
            mWinner = mContainers.get(6).getOwner();
        } else if (mContainers.get(6).getNumberOfSeeds() < mContainers.get(13).getNumberOfSeeds()) {
            mWinner = mContainers.get(13).getOwner();
        } else {
            mEvenGame = true;
        }
    }

    public ArrayList<Container> getRepresentation() {
        return mContainers;
    }

    // Mostly for debug purpose, but can be also used to save game...maybe
    public void setBoardRepresentation(ArrayList<Container> representation) {
        mContainers = representation;
    }

    @Override
    public void update(Observable observable, Object data) {
        // board respond only to an action the MoveAction that goes to update the board status
        if (data instanceof MoveAction) {
            move((MoveAction) data);
        }
    }
}
