package me.dacol.marco.mancala.gameLib.player;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.board.Move;
import me.dacol.marco.mancala.gameLib.gameController.TurnContext;
import me.dacol.marco.mancala.gameLib.gameController.actions.ActivePlayer;
import me.dacol.marco.mancala.gameLib.gameController.actions.InvalidMove;
import me.dacol.marco.mancala.gameLib.gameController.actions.MoveAction;
import me.dacol.marco.mancala.gameLib.player.brains.AttachedPlayer;
import me.dacol.marco.mancala.gameLib.player.brains.Brain;

public class Player implements Observer, AttachedPlayer {

    private static final String LOG_TAG = Player.class.getSimpleName();

    private TurnContext mTurnContext;
    private Brain mBrain;
    private String mName;

    public Player(TurnContext turnContext, Brain brain, String name) {
        mTurnContext = turnContext;
        mName = name;
        mBrain = brain;

        // Connects player to his Brain
        brain.attachPlayer(this);
    }

    public boolean isHuman() {
        return mBrain.isHuman();
    }

    private void timeToPlay(ArrayList<Container> boardRepresentation) {
        mBrain.makeMove(boardRepresentation, this);
    }

    private void didAnInvalidMove(ArrayList<Container> boardRepresentation) {
        mBrain.toggleLastMoveCameUpInvalid();
        timeToPlay(boardRepresentation);
    }

    private void sendMoveToBoard(int bowlNumber) {
        Move move = new Move(bowlNumber, this);
        mTurnContext.push(new MoveAction(move));
    }

    public String getName() {
        return mName;
    }

    public Brain getBrain() {
        return mBrain;
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof ActivePlayer) {
            // == checks is the object reference is the same, in this case if they point to the same
            // object this means that it's my turn and i pop from the stack,
            // otherwise let it go, someone else will pick up the call.
            if (((ActivePlayer) data).getLoad() == this) {
                timeToPlay(((ActivePlayer) data).getBoardRepresentation());
            }
        } else if (data instanceof InvalidMove) {
            // If the player has done an invalid move, this action is fired on the stack, the player
            // now need to remake the move
            if (((InvalidMove) data).getPlayer() == this) {
                didAnInvalidMove(((InvalidMove) data).getBoardStatus());
            }
        }
    }

    @Override
    public void onBrainInteraction(int move) {
        sendMoveToBoard(move);
    }


}
