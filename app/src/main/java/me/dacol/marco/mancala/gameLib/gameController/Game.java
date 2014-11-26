package me.dacol.marco.mancala.gameLib.gameController;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import me.dacol.marco.mancala.gameLib.board.Board;
import me.dacol.marco.mancala.gameLib.player.Player;

public class Game implements Observer {

    private List<Player> mPlayers = new ArrayList<Player>();
    private Board mBoard = null;
    private TurnContext mTurnContext = null;

    // Singleton
    private static Game sInstance = null;

    protected Game() {
        // Called here because this is where players
        // and board need to be subscribed as observers
        // so I need to have this before everything
        initializeTurnContext();
    }

    public Game getInstance() {
        if (sInstance == null) {
            sInstance = new Game();
        }

        return sInstance;
    }

    public void setup() {
        // Check that the player are added to the game before initialize...
        if (mPlayers.size() > 2) {
            // Initialize the board
            initializeBoard();
        } else {
            // TODO die?
            //throw new Exception("You need at least 2 player");
        }
    }

    public void start() {
        // Notify who is the first player that has to move
        mTurnContext.post(new ActivePlayerAction(
                mPlayers.get(chooseStartingPlayer())
        ));
    }

    /***
     *
     * @param player
     */
    public void addPlayer(Player player) {
        mPlayers.add(player);
        mTurnContext.addObserver(player);
    }

    private void initializeTurnContext() {
        mTurnContext = TurnContext.getInstance();
        mTurnContext.initialize();
        mTurnContext.addObserver(this);
    }

    private void initializeBoard() {
        mBoard = Board.getInstance();
        mBoard.initialSetup(mPlayers.size());
        mTurnContext.addObserver(mBoard);
    }

    private int chooseStartingPlayer() {
        return new Random().nextInt(mPlayers.size());
    }

    @Override
    public void update(Observable observable, Object data) {

    }
}
