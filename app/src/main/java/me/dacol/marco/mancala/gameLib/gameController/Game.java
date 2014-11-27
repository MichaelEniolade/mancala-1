package me.dacol.marco.mancala.gameLib.gameController;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import me.dacol.marco.mancala.gameLib.board.Board;
import me.dacol.marco.mancala.gameLib.gameController.actions.ActivePlayer;
import me.dacol.marco.mancala.gameLib.gameController.actions.BoardUpdated;
import me.dacol.marco.mancala.gameLib.gameController.actions.Winner;
import me.dacol.marco.mancala.gameLib.player.Player;

public class Game implements Observer {

    private List<Player> mPlayers;
    private Board mBoard;
    private TurnContext mTurnContext;

    private int mPlayingPlayer;
    private int mNextPlayer;
    private int mTurnNumber;

    private boolean mEnded;

    // Singleton
    private static Game sInstance = null;

    public Game getInstance() {
        if (sInstance == null) {
            sInstance = new Game();
        }

        return sInstance;
    }

    protected Game() {
        setup();

        // Called here because this is where players
        // and board need to be subscribed as observers
        // so I need to have this before everything
        initializeTurnContext();
    }

    /**
     * Run before starting a game, has the job to initialize a new game
     */
    public void setup() {
        mPlayingPlayer = 0;
        mNextPlayer = 0;
        mTurnNumber = 0;
        mEnded = false;
        mPlayers = new ArrayList<Player>();
        mBoard = null;
        mTurnContext = null;
    }

    /**
     * This starts the game loop, it will end when a player won or retreat from the game
     */
    public void start() {
        while (!isEnded()) {
            newTurn();
        }

        if (isEnded()) {
            Player winningPlayer = getWinner();
            announceTheWinner(winningPlayer);
        } else {
            // TODO How can i end up here?!
        }
     }

    //---> Player methods
    private void createPlayer() {
        // player created and added to the game, player can be of two types
        // Human and AI -> I need a player factory!
    }

    /**
     * Register a new player in the game, and subscribe him to the turnContext object
     *
     * @param player
     */
    public void addPlayer(Player player) {
        mPlayers.add(player);
        mTurnContext.addObserver(player);
    }

    private void turnPlayer() {
        if (mTurnNumber == 0) {
            chooseStartingPlayer();
        } else {
            updatePlayingPlayer();
        }
        updateTurnContext();
    }

    /***
     * Select randomly the starting player, it supports only 2 players
     */
    private void chooseStartingPlayer() {
        mPlayingPlayer = new Random().nextInt(mPlayers.size());
        mNextPlayer = (mPlayers.size() -1) - mPlayingPlayer;
    }

    private void updateTurnContext() {
        mTurnContext.push(new ActivePlayer(mPlayers.get(mPlayingPlayer)));
    }

    /**
     * Switch the two player, right now it supports only a 2 player game
     */
    private void updatePlayingPlayer() {
        int temp = mPlayingPlayer;
        mPlayingPlayer = mNextPlayer;
        mNextPlayer = temp;
    }

    private Player getWinner() {
        return mBoard.getWinner();
    }

    private void announceTheWinner(Player player) {
        mTurnContext.push(new Winner(player));
    }

    //---> Turn methods
    private void initializeTurnContext() {
        mTurnContext = TurnContext.getInstance();
        mTurnContext.addObserver(this);
    }

    private void initializeBoard() {
        mBoard = Board.getInstance();
        mBoard.initialSetup(mPlayers.size());
        mTurnContext.addObserver(mBoard);
    }

    private void newTurn() {
        mTurnContext.initialize();

        if (mTurnNumber == 0) {
            initializeBoard();
        }

        turnPlayer();
        mTurnNumber += 1;
    }

    private void checkForWinner(BoardUpdated boardUpdated) {
        if (boardUpdated.thereIsAWinner()) {
            toggleEnd();
        }
    }

    private void toggleEnd() {
        mEnded = !mEnded;
    }

    private boolean isEnded() {
        return mEnded;
    }

    //---> interfaces
    @Override
    public void update(Observable observable, Object data) {
        // qui vanno considerati solo gli ultimi eventi che questo oggetto può maneggiare.
        if (mTurnContext.peek() instanceof BoardUpdated) {
            BoardUpdated boardUpdated = (BoardUpdated) mTurnContext.pop();
            checkForWinner(boardUpdated);
        }
    }
}
