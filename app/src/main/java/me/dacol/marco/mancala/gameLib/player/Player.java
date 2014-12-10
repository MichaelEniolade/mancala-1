package me.dacol.marco.mancala.gameLib.player;

import java.util.Observable;
import java.util.Observer;

import me.dacol.marco.mancala.gameLib.board.Move;
import me.dacol.marco.mancala.gameLib.gameController.TurnContext;
import me.dacol.marco.mancala.gameLib.gameController.actions.ActivePlayer;
import me.dacol.marco.mancala.gameLib.gameController.actions.InvalidMove;
import me.dacol.marco.mancala.gameLib.gameController.actions.MoveAction;
import me.dacol.marco.mancala.gameLib.player.brains.Brain;
import me.dacol.marco.mancala.gameLib.player.brains.Human;

public class Player implements Observer {

    private TurnContext mTurnContext;
    private Brain mBrain;
    private String mName;

    public Player(TurnContext turnContext, Brain brain, String name) {
        mTurnContext = turnContext;
        mName = name;
        mBrain = brain;
    }

    public boolean isHuman() {
        return (mBrain instanceof Human);
    }

    private void timeToPlay(ActivePlayer activePlayer) {
        Move move = mBrain.makeMove(activePlayer.getBoardRepresentation(), this);
        sendMoveToBoard(move);
    }

    private void didAnInvalidMove(InvalidMove invalidMove) {
        mBrain.toggleLastMoveCameUpInvalid();
        Move move = mBrain.makeMove(invalidMove.getBoardStatus(), this);
        sendMoveToBoard(move);
    }

    private void sendMoveToBoard(Move move) {
        mTurnContext.push(new MoveAction(move));
    }

    public String getmName() {
        return mName;
    }

    @Override
    public void update(Observable observable, Object data) {
        if (mTurnContext.peek() instanceof ActivePlayer) {
            // == checks is the object reference is the same, in this case if they point to the same
            // object this means that it's my turn and i pop from the stack,
            // otherwise let it go, someone else will pick up the call.
            if (((ActivePlayer) mTurnContext.peek()).getLoad() == this) {
                timeToPlay((ActivePlayer) mTurnContext.pop());
            }
        } else if (mTurnContext.peek() instanceof InvalidMove) {
            // If the player has done an invalid move, this action is fired on the stack, the player
            // now need to remake the move
            if (((InvalidMove) mTurnContext.peek()).getPlayer() == this) {
                didAnInvalidMove((InvalidMove) mTurnContext.pop());
            }
        }
    }
}
