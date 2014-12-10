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

    private int mNumberOfBowl;
    private int mNumberOfTray;

    private Stack<Move> mMoves;
    private boolean mInvalidMove;

    public BaseBrain(int numberOfBowl, int numberOfTray) {
        mMoves = new Stack<Move>();
        mInvalidMove = false;
        mNumberOfBowl = numberOfBowl;
        mNumberOfTray = numberOfTray;
    }

    // This method is very ugly, it's just for test purpose...
    @Override
    public Move makeMove(ArrayList<Container> boardStatus, Player player) {
        // very easy strategy, check which are his container and then chose a random bowl not empty.
        Move move;

        int random = new Random().nextInt(mNumberOfBowl);

        if(mInvalidMove) {
            Move lastInvalidMove = mMoves.pop();
            if ((lastInvalidMove.getBowlNumber() == (random + mNumberOfBowl + mNumberOfTray -1))
                    || (lastInvalidMove.getBowlNumber() == random)) {
                random = new Random().nextInt(mNumberOfBowl);
            }
        }

        // The first six bowl are the ones of the first player
        // TODO extract in a method
        if (boardStatus.get(0).getOwner() == player) {
            move = new Move(random, player);
        } else {
            // so my player bowls are the ones after the tray of player one, bowl 1 of player2 is 6+1
            // PAY ATTENTION! Here I'm passing the real position in the array of container, not only
            // the bowl number.
            // This can be a problem...maybe.
            move = new Move(random+ mNumberOfBowl + mNumberOfTray -1, player);
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
