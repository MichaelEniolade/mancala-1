package me.dacol.marco.mancala.gameLib.board;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import me.dacol.marco.mancala.gameLib.gameController.TurnContext;
import me.dacol.marco.mancala.gameLib.gameController.actions.Action;
import me.dacol.marco.mancala.gameLib.gameController.actions.ActivePlayer;
import me.dacol.marco.mancala.gameLib.gameController.actions.BoardUpdated;
import me.dacol.marco.mancala.gameLib.gameController.actions.EvenGame;
import me.dacol.marco.mancala.gameLib.gameController.actions.InvalidMove;
import me.dacol.marco.mancala.gameLib.gameController.actions.MoveAction;
import me.dacol.marco.mancala.gameLib.gameController.actions.Winner;
import me.dacol.marco.mancala.gameLib.player.Player;

public class Board implements Observer, StandardBoard<Container> {

    private final static String LOG_TAG = Board.class.getSimpleName();

    ContainersManager mContainersManager;
    List<Player> mPlayers;

    private int mNumberOfBowls;
    private int mNumberOfTrays;
    private TurnContext mTurnContext;
    private Player mWinner;
    private boolean mEvenGame;

    private Player mActivePlayer;

    // Singleton
    private static Board sInstance = null;

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
    public void buildBoard(ArrayList<Integer> boardRepresentation) {
        // One of the two player has to be an Human
        int humanPlayerPosition = mPlayers.get(0).isHuman() ? 0 : 1;

        mContainersManager = new ContainersManager(
                mPlayers.get(humanPlayerPosition),
                mPlayers.get( ( mPlayers.size() - humanPlayerPosition ) - 1 ),
                boardRepresentation);
    }

    public Player getWinner() {
        return (mEvenGame && (mWinner == null))
                ? null
                : mWinner;
    }

    // ---> Core rules for the game
    private void move(MoveAction moveAction) {
        Move move = moveAction.getLoad();
        Container selectedContainer = mContainersManager.getContainer(move.getBowlNumber());
        boolean anotherRound = false;

        if (isAValidMove(move.getPlayer(), selectedContainer)) {
            anotherRound = spreadSeedFrom(move.getBowlNumber());

            if (isGameEnded()) {
                Action gameEnded;
                if (!mEvenGame) {
                    gameEnded = new Winner(mWinner, getRepresentation(), mContainersManager.getAtomicMoves());
                }  else {
                    gameEnded = new EvenGame(getRepresentation(), mContainersManager.getAtomicMoves());
                }

                postOnTurnContext(gameEnded);
            } else {
                postOnTurnContext(new BoardUpdated(getRepresentation(),
                        anotherRound, mContainersManager.getAtomicMoves()));
            }

        } else if (!isGameEnded()) {
            postOnTurnContext(new InvalidMove(
                    move,
                    getRepresentation(),
                    mActivePlayer
            ));
        }


    }

    private boolean isAValidMove(Player player, Container selectedContainer) {
        boolean isValid = false;
        // When a move is invalid?
        // 0. when a player is playing in his turn
        // 1. the bowl_selected have zero seeds in it
        // 2. the bowl_selected is not owned by the player
        // 3. the player selected a tray
        if (player == mActivePlayer) {
            if ((selectedContainer instanceof Bowl)
                    && (selectedContainer.getOwner() == player)
                    && (selectedContainer.getNumberOfSeeds() > 0)) {
                isValid = true;
            }
        }
        return isValid;
    }

    //TODO maybe it can be better with a container manager, we will see the next iteration
    private boolean spreadSeedFrom(int containerNumber) {
        int remainingSeeds = mContainersManager.emptyBowl(containerNumber);

        Player player = mContainersManager.getOwnerOf(containerNumber);
        boolean lastSeedFallInPlayerTray = false;

        int bowlNumber = nextContainer(containerNumber);

        // If i have more than one seed to spread, I'm ok just spread it and go on
        // If I have to spread the last seed, check the next container is a bowl_selected?
        // -- The playingPlayer (PP) is the owner of the bowl_selected?
        // ---- Yes, move the seed directly to the tray, and stole the opponent seeds in the specular
        //      bowl_selected and put them in the PP tray (if there are no seed in opponent bowl_selected just go on)
        // No, just put the seed there and go on with your life!
        for (; remainingSeeds > 1; remainingSeeds--) {
            mContainersManager.putASeedIn(bowlNumber);
            bowlNumber = nextContainer(bowlNumber);
        }

        if ( (remainingSeeds == 1)
                && (mContainersManager.getContainer(bowlNumber) instanceof Bowl)
                && (mContainersManager.getOwnerOf(bowlNumber) == player)
                && (mContainersManager.getNumberOfSeedsOf(bowlNumber) == 0) )
        {
            mContainersManager.putASeedIn(bowlNumber);
            int wonSeeds = mContainersManager.emptyBowl(bowlNumber);
            wonSeeds += mContainersManager.emptyOppositeBowl(bowlNumber);
            mContainersManager.putSeedsInTrayOf(player, wonSeeds);
        //TODO this can became a boolean method in the container manager
        } else if (mContainersManager.getContainer(bowlNumber) == mContainersManager.getTrayOf(player)) {
            mContainersManager.putASeedIn(bowlNumber);
            lastSeedFallInPlayerTray = true;
        } else {
            mContainersManager.putASeedIn(bowlNumber);
        }

        return lastSeedFallInPlayerTray;
    }

    //TODO this could also be ported in the ContainersManager, maybe returning directly the bowl
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
        // A game is ended when all the bowl_selected of one player are empty
        // Just do the sum of the bowl_selected of each player, if one is zero you are done
        int playerOneRemainingSeeds = 0;
        int playerTwoRemainingSeeds = 0;

        boolean isEnded = false;

        Player playerOne = mContainersManager.getOwnerOf(0);
        Player playerTwo = mContainersManager.getOwnerOf(7);

        for (int i = 0 ; i < mNumberOfBowls; i++) {
            playerOneRemainingSeeds += mContainersManager.getNumberOfSeedsOf(i);
        }
        for (int j = (mNumberOfBowls + mNumberOfTrays) ; j < (mContainersManager.getNumberContainers() - 1); j++) {
            playerTwoRemainingSeeds += mContainersManager.getNumberOfSeedsOf(j);
        }

        if ((playerOneRemainingSeeds == 0) || (playerTwoRemainingSeeds == 0)) {
            isEnded = true;
        }

        // If the game is ended put all remaining seeds in the player bowl_selected and find the winner
        if (isEnded) {
            if (playerOneRemainingSeeds > 0) {
                for (int j = 0; j < mNumberOfBowls; j++) {
                    mContainersManager.emptyBowl(j);
                }
                mContainersManager.putSeedsInTrayOf( playerOne, playerOneRemainingSeeds);
            } else {
                for (int j = (mNumberOfBowls + mNumberOfTrays);
                     j < (mContainersManager.getNumberContainers() -1); j++)
                {
                    mContainersManager.emptyBowl(j);
                }
                mContainersManager.putSeedsInTrayOf(playerTwo, playerTwoRemainingSeeds);
            }
            setWinner();
        }

        return isEnded;
    }

    private void setWinner() {
        Player playerOne = mContainersManager.getOwnerOf(0);
        Player playerTwo = mContainersManager.getOwnerOf(7);

        // I can have 3 ending state,
        // player one wins, player two wins, even game
        // default case, if this is null it means that the game is even
        if (mContainersManager.getNumberOfSeedsInTrayOf(playerOne) >
                mContainersManager.getNumberOfSeedsInTrayOf(playerTwo))
        {
            mWinner = playerOne;
        }
        else if (mContainersManager.getNumberOfSeedsInTrayOf(playerOne) <
                mContainersManager.getNumberOfSeedsInTrayOf(playerTwo))
        {
            mWinner = playerTwo;
        }
        else
        {
            mEvenGame = true;
        }
    }

    public ArrayList<Container> getRepresentation() {
        return mContainersManager.getRepresentation();
    }

    // Mostly for debug purpose, but can be also used to save game...maybe
    public void setBoardRepresentation(ArrayList<Container> representation) {
        mContainersManager.setRepresenation(representation);
    }

    @Override
    public void update(Observable observable, Object data) {
        // board respond only to an action the MoveAction that goes to update the board status
        if (data instanceof MoveAction) {
            move((MoveAction) data);
        } else if (data instanceof ActivePlayer) {
            mActivePlayer = ((ActivePlayer) data).getLoad();
        }
    }
}
