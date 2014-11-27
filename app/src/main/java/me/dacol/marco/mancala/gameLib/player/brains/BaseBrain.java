package me.dacol.marco.mancala.gameLib.player.brains;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.board.Move;
import me.dacol.marco.mancala.gameLib.player.Player;

/***
 * Very dumb brain just to see if it can play a game.
 * It moves from random bowls.
 */
public abstract class BaseBrain implements Brain {

    private final int fNumberOfBowl = 6;
    private final int fNumberOfTray = 1;

    private Player mPlayer;
    private Stack<Move> mMoves;
    private boolean mInvalidMove;

    public BaseBrain(Player player) {
        mPlayer = player;
        mMoves = new Stack<Move>();
        mInvalidMove = false;
    }

    @Override
    public Move makeMove(ArrayList<Container> boardStatus) {
        // very easy strategy, check which are his container and then chose a random bowl not empty.
        Move move;

        int random = new Random().nextInt(fNumberOfBowl-1);

        if(mInvalidMove) {
            Move lastInvalidMove = mMoves.pop();
            if ((lastInvalidMove.getBowlNumber() == (random+fNumberOfBowl+fNumberOfTray-1))
                    || (lastInvalidMove.getBowlNumber() == random)) {
                random = new Random().nextInt(fNumberOfBowl-1);
            }
        }

        // The first six bowl are the ones of the first player
        // TODO extract in a method
        if (boardStatus.get(0).getOwner() == mPlayer) {
            move = new Move(random, mPlayer);
        } else {
            // so my player bowls are the ones after the tray of player one, bowl 1 of player2 is 6+1
            move = new Move(random+fNumberOfBowl+fNumberOfTray-1, mPlayer);
        }

        mMoves.push(move); //Ok, this brain is dumb and cannot think of a strategy...but who cares!

        //reset the invalid status
        if(mInvalidMove) toggleLastMoveCameUpInvalid();

        return move;
    }

    @Override
    public void toggleLastMoveCameUpInvalid() {
        mInvalidMove = !mInvalidMove;
    }
}
