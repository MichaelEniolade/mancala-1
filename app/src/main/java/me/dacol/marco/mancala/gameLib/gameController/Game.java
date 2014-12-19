package me.dacol.marco.mancala.gameLib.gameController;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import me.dacol.marco.mancala.gameLib.board.Board;
import me.dacol.marco.mancala.gameLib.board.StandardBoard;
import me.dacol.marco.mancala.gameLib.exceptions.NumberOfPlayersException;
import me.dacol.marco.mancala.gameLib.exceptions.ToManyPlayerException;
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
        mPlayingPlayer = 0;
        mNextPlayer = 0;
        mTurnNumber = 0;
        mEnded = false;
        mPlayers = new ArrayList<Player>();
        mBoard = null;
        mTurnContext = null;
        mAnotherRoundForPlayer = false;

        // Called here because this is where players
        // and board need to be subscribed as observers
        // so I need to have this before everything
        initializeTurnContext();

        mPlayerFactory = new PlayerFactory(mTurnContext, mNumberOfBowls, mNumberOfTrays);
    }

    /**
     * This starts the game loop, it will end when a player won or retreat from the game
     */
    public void start() {
        nextTurn();
    }

    private void nextTurn() {
        if (!isEnded()) {
            try {
                newTurn();
            } catch (NumberOfPlayersException e) {
                e.printStackTrace();
            }
        }
    }

    //---> Player methods
    public void createPlayer(PlayerType type, String name)
            throws ToManyPlayerException {

        Player player = mPlayerFactory.makePlayer(type, name);
        addPlayer(player);
    }

    /**
     * Register a new player in the game, and subscribe him to the turnContext object
     *
     * @param player
     */
    private void addPlayer(Player player) throws ToManyPlayerException {
        if (mPlayers.size() < mNumberOfPlayers) {
            mPlayers.add(player);
            mTurnContext.addObserver(player);
        } else {
            throw new ToManyPlayerException("Maximum allowed number of player is: " + mNumberOfPlayers);
        }
    }

    private void playingPlayer() {
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

    // TODO extract this method to an interface so that will be the same on every object that can post on the TurnContext
    private void updateTurnContext() {
    //TODO eliminate that mBoad.getRepresentation, game knows about the board throught the boardupdated event
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
            togglePlayerAnotherRound(); //Set back to false the flag and don't switch players
        }
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

        playingPlayer();
        mTurnNumber += 1;
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
}
