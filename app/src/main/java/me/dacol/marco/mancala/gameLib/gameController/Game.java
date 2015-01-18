package me.dacol.marco.mancala.gameLib.gameController;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import me.dacol.marco.mancala.gameLib.board.Board;
import me.dacol.marco.mancala.gameLib.board.StandardBoard;
import me.dacol.marco.mancala.gameLib.gameController.actions.ActivePlayer;
import me.dacol.marco.mancala.gameLib.gameController.actions.BoardUpdated;
import me.dacol.marco.mancala.gameLib.gameController.actions.EvenGame;
import me.dacol.marco.mancala.gameLib.gameController.actions.Winner;
import me.dacol.marco.mancala.gameLib.player.Player;
import me.dacol.marco.mancala.gameLib.player.PlayerFactory;
import me.dacol.marco.mancala.gameLib.player.PlayerType;

public class Game implements Observer {

    private static final String LOG_TAG = Game.class.getSimpleName();

    private List<Player> mPlayers;
    private StandardBoard mBoard;
    private TurnContext mTurnContext;
    private PlayerFactory mPlayerFactory;

    private int mPlayingPlayer;
    private int mNextPlayer;
    private int mTurnNumber;
    private int mNumberOfPlayers = 2;
    private int mNumberOfBowls = 6;
    private int mNumberOfTrays = 1;

    private boolean mEnded;

    // Singleton
    private static Game sInstance = null;
    private boolean mAnotherRoundForPlayer;

    public static Game getInstance() {
        if (sInstance == null) {
            sInstance = new Game();
        }

        return sInstance;
    }

    protected Game() {
        // empty constructor
    }

    /**
     * Run before starting a game, has the job to initialize a new game
     */
    public void setup() {
        mPlayingPlayer = -1;
        mNextPlayer = -1;
        mTurnNumber = 0;
        mEnded = false;
        mPlayers = new ArrayList<Player>();
        mBoard = Board.getInstance();
        mTurnContext = TurnContext.getInstance();
        mAnotherRoundForPlayer = false;

        // add observers
        mTurnContext.addObserver(this);
        mTurnContext.addObserver(mBoard);

        mPlayerFactory = new PlayerFactory(mTurnContext, mNumberOfBowls, mNumberOfTrays);
    }

    /**
     * This starts the game loop, it will end when a player won or retreat from the game
     */
    public void start(Bundle savedState) {
        if (savedState == null) {
            initializeBoard(null);
            nextTurn();
        } else {
            //TODO update test
            mPlayingPlayer = savedState.getInt("playingPlayer");
            mTurnNumber = savedState.getInt("turnNumber");
            initializeBoard(savedState.getIntegerArrayList("boardRepresentation"));
            nextTurn();
        }
    }

    private void nextTurn() {
        if (!isEnded()) {
            newTurn();
        }
    }

    //---> Player methods
    public Player createPlayer(PlayerType type, String name) {

        Player player = mPlayerFactory.makePlayer(type, name);
        addPlayer(player);

        return player;
    }

    /**
     * Register a new player in the game, and subscribe him to the turnContext object, max allowed
     * players is two
     *
     * @param player
     */
    private void addPlayer(Player player) {
        if (mPlayers.size() < mNumberOfPlayers) {
            mPlayers.add(player);
            mTurnContext.addObserver(player);
        }
    }

    /***
     * Select randomly the starting player, it supports only 2 players, if the game is resumed and
     * a Playing player is already defined, then only nextPlayer will be set by this method
     *
     */
    private void setStartingPlayer() {
        if (mPlayingPlayer == -1) {
            mPlayingPlayer = new Random().nextInt(mPlayers.size());
        }

        mNextPlayer = (mPlayers.size() - 1) - mPlayingPlayer;

    }

    // TODO extract this method to an interface so that will be the same on every object that can post on the TurnContext
    private void updateTurnContext() {
        mTurnContext.push(new ActivePlayer(mPlayers.get(mPlayingPlayer), mBoard.getRepresentation()));
    }

    /**
     * Switch the two player, right now it supports only a 2 player game
     *
     */
    private void updatePlayingPlayer() {
        if (mNextPlayer == -1) {
            setStartingPlayer();
        } else if (!mAnotherRoundForPlayer) {
            int temp = mPlayingPlayer;
            mPlayingPlayer = mNextPlayer;
            mNextPlayer = temp;
        } else {
            togglePlayerAnotherRound(); //Set back to false the flag and don't switch players
        }
    }

    // initialization, if there is a previous boardRepresentation i set that board and start from there
    // otherwise ii will initialize a complete new board
    private void initializeBoard(ArrayList<Integer> boardRepresentation) {
        if (mPlayers.size() == mNumberOfPlayers) {
            mBoard.setup(mTurnContext, mNumberOfBowls, mNumberOfTrays)
                    .registerPlayers(mPlayers)
                    .buildBoard(boardRepresentation);
        }
    }

    private void newTurn() {
        mTurnContext.initialize();

        updatePlayingPlayer();
        mTurnNumber += 1;

        updateTurnContext();

    }

    private void anotherTurnForPlayingPlayer(Boolean isAnotherTurn) {
        if (isAnotherTurn) {
            togglePlayerAnotherRound();
        }
    }

    private void togglePlayerAnotherRound() {
        mAnotherRoundForPlayer = !mAnotherRoundForPlayer;
    }

    private void toggleEnd() {
        mEnded = !mEnded;
    }

    private boolean isEnded() {
        return mEnded;
    }

    // TODO: put me in the interface
    public TurnContext getTurnContext() {
        return mTurnContext;
    }

    /**
     * Return the human player, needed from the view to connect the brain to the board UI
     * @return Player
     */
    public Player getPlayerNumber(int playerNumber) {
        return mPlayers.get(playerNumber);
    }

    // TODO this should observe for much more elements like Winner and EvenGame
    @Override
    public void update(Observable observable, Object data) {
        // Take into account only the events that this class can handle
        if (data instanceof BoardUpdated) {
            BoardUpdated boardUpdated = (BoardUpdated) data;
            anotherTurnForPlayingPlayer(boardUpdated.isAnotherRound());
            nextTurn();
        } else if ((data instanceof Winner) || (data instanceof EvenGame)) {
            toggleEnd();
        }
    }

    public int getPlayingPlayer() {
        return mPlayingPlayer;
    }

    public int getTurnNumber() {
        return mTurnNumber;
    }
}
