package me.dacol.marco.mancala.gameLib.player;

import java.util.Observable;
import java.util.Observer;

import me.dacol.marco.mancala.gameLib.board.Move;
import me.dacol.marco.mancala.gameLib.gameController.TurnContext;
import me.dacol.marco.mancala.gameLib.gameController.actions.ActivePlayer;
import me.dacol.marco.mancala.gameLib.gameController.actions.InvalidMove;
import me.dacol.marco.mancala.gameLib.gameController.actions.MoveAction;
import me.dacol.marco.mancala.gameLib.player.brains.Brain;

public class Player implements Observer {

    private TurnContext mTurnContext;
    private Brain mBrain;
    private String mName;

    public Player(TurnContext turnContext, String name) {
        mTurnContext = turnContext;
        mName = name;
    }

    private void timeToPlay(ActivePlayer activePlayer) {
        Move move = mBrain.makeMove(activePlayer.getBoardRepresentation());
        sendMoveToBoard(move);
    }

    private void didAnInvalidMove(InvalidMove invalidMove) {
        mBrain.toggleLastMoveCameUpInvalid();
        Move move = mBrain.makeMove(invalidMove.getBoardStatus());
        sendMoveToBoard(move);
    }

    public void setBrain(Brain brain) {
        mBrain = brain;
    }

    private void sendMoveToBoard(Move move) {
        mTurnContext.push(new MoveAction(move));
    }

    @Override
    public void update(Observable observable, Object data) {
        if (mTurnContext.peek() instanceof ActivePlayer) {
            ActivePlayer activePlayer = (ActivePlayer) mTurnContext.peek();
            // == checks is the object reference is the same, in this case if they point to the same
            // object this means that it's my turn and i pop from the stack,
            // otherwise let it go, someone else will pick up the call.
            if (activePlayer.getLoad() == this) {
                mTurnContext.pop();
                timeToPlay(activePlayer);
            }
        } else if (mTurnContext.peek() instanceof InvalidMove) {
            InvalidMove invalidMove = (InvalidMove) mTurnContext.peek();
            // If the player has done an invalid move, this action is fired on the stack, the player
            // now need to remake the move
            if (invalidMove.getLoad() == this) {
                mTurnContext.pop();
                didAnInvalidMove(invalidMove);
            }
        }
    }
}
