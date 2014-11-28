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
        if (isAValidMove(moveAction)) {
            executeMove(moveAction.getLoad());
        } else {
            postOnTurnContext(new InvalidMove(
                    moveAction.getLoad(),
                    getRepresentation(),
                    moveAction.getLoad().getPlayer()
            ));
        }
    }

    private boolean isAValidMove(MoveAction moveAction) {
        Move move = moveAction.getLoad();
        boolean isValid = false;
        // When a move is invalid?
        // 1. the bowl have zero seeds in it
        // 2. the bowl is not owned by the player
        // 3. the player selected a tray
        Container selectedContainer = getPlayerSelectedContainer(move.getBowlNumber());
        if ((selectedContainer instanceof Bowl)
                && (selectedContainer.getOwner() == move.getPlayer())
                && (selectedContainer.numberOfSeeds > 0)) {
            isValid = true;
        }
        return isValid;
    }

    private Container getPlayerSelectedContainer(int number) {
        return mContainers.get(number);
    }

    private void executeMove(Move move) {

        //TODO execute the move and then post the BoardUpdated action on the turn context
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
