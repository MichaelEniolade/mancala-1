package me.dacol.marco.mancala.gameLib.gameController;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import me.dacol.marco.mancala.gameLib.board.Board;
import me.dacol.marco.mancala.gameLib.board.StandardBoard;
import me.dacol.marco.mancala.gameLib.exceptions.NumberOfPlayersException;
import me.dacol.marco.mancala.gameLib.exceptions.PlayerBrainTypeUnknownException;
import me.dacol.marco.mancala.gameLib.exceptions.ToManyPlayerException;
import me.dacol.marco.mancala.gameLib.gameController.actions.ActivePlayer;
import me.dacol.marco.mancala.gameLib.gameController.actions.BoardUpdated;
import me.dacol.marco.mancala.gameLib.gameController.actions.Winner;
import me.dacol.marco.mancala.gameLib.player.Player;
import me.dacol.marco.mancala.gameLib.player.PlayerFactory;

public class Game implements Observer {

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
        setup();

        // Called here because this is where players
        // and board need to be subscribed as observers
        // so I need to have this before everything
        initializeTurnContext();
    }

    /**
     * Run before starting a game, has the job to initialize a new game
     */
    private void setup() {
        mPlayingPlayer = 0;
        mNextPlayer = 0;
        mTurnNumber = 0;
        mEnded = false;
        mPlayers = new ArrayList<Player>();
        mBoard = null;
        mTurnContext = null;
        mPlayerFactory = new PlayerFactory(mTurnContext, mNumberOfBowls, mNumberOfTrays);
        mAnotherRoundForPlayer = false;
    }

    /**
     * This starts the game loop, it will end when a player won or retreat from the game
     */
    public void start() throws NumberOfPlayersException {
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
    private void createPlayer(int type, String name) throws
            PlayerBrainTypeUnknownException, ToManyPlayerException {

        Player player = mPlayerFactory.makePlayer(type, name);
        addPlayer(player);
    }

    /**
     * Register a new player in the game, and subscribe him to the turnContext object
     *
     * @param player
     */
    public void addPlayer(Player player) throws ToManyPlayerException {
        if (mPlayers.size() < mNumberOfPlayers) {
            mPlayers.add(player);
            mTurnContext.addObserver(player);
        } else {
            throw new ToManyPlayerException();
        }
    }

    private void PlayingPlayer() {
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
        mTurnContext.push(new ActivePlayer(mPlayers.get(mPlayingPlayer), mBoard.getRepresentation()));
    }

    /**
     * Switch the two player, right now it supports only a 2 player game
     */
    private void updatePlayingPlayer() {
        if (!mAnotherRoundForPlayer) {
            int temp = mPlayingPlayer;
            mPlayingPlayer = mNextPlayer;
            mNextPlayer = temp;
        } else {
            togglePlayerAnotherRound();
        }
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

    private void initializeBoard() throws NumberOfPlayersException {
        if (mPlayers.size() == mNumberOfPlayers) {
            mBoard = Board.getInstance();

            mBoard.setup(mTurnContext, mNumberOfBowls, mNumberOfTrays)
                    .registerPlayers(mPlayers)
                    .buildBoard();

            mTurnContext.addObserver(mBoard);
        } else {
            throw new NumberOfPlayersException("Number of player is: " + mPlayers.size()
                    + ", it should be: " + mNumberOfPlayers);
        }

    }

    private void newTurn() throws NumberOfPlayersException {
        mTurnContext.initialize();

        if (mTurnNumber == 0) {
            initializeBoard();
        }

        PlayingPlayer();
        mTurnNumber += 1;
    }

    private void checkForGameEnd(BoardUpdated boardUpdated) {
        if (boardUpdated.isGameEnded()) {
            toggleEnd();
        }
        if (boardUpdated.isAnotherRound()) {
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

    public TurnContext getTurnContext() {
        return mTurnContext;
    }

    //---> interfaces
    @Override
    public void update(Observable observable, Object data) {
        // Take into account only the events that this class can handle
        if (mTurnContext.peek() instanceof BoardUpdated) {
            BoardUpdated boardUpdated = (BoardUpdated) mTurnContext.pop();
            checkForGameEnd(boardUpdated);
        }
    }
}
