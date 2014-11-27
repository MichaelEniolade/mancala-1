package me.dacol.marco.mancala.gameLib.player;

import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

import me.dacol.marco.mancala.gameLib.gameController.TurnContext;
import me.dacol.marco.mancala.gameLib.gameController.actions.ActivePlayer;
import me.dacol.marco.mancala.gameLib.gameController.actions.InvalidMove;
import me.dacol.marco.mancala.gameLib.player.brains.Brain;

public class Player implements Observer {

    private TurnContext mTurnContext;
    private Brain mBrain;
    private Stack<Integer> mMoves; //keeps track of the player moves

    public Player(TurnContext turnContext) {
        mTurnContext = turnContext;
        mMoves = new Stack<Integer>();
    }

    private void timeToPlay(ActivePlayer activePlayer) {
        // get the board status, and ask the brain for a move
    }

    private void didAnInvalidMove(InvalidMove invalidMove) {
        // get the board status and last move and ask a brain for a new move
    }

    public void setBrain(Brain brain) {
        this.mBrain = brain;
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
