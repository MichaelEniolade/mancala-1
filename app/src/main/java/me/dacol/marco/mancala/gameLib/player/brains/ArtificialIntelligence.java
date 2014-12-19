package me.dacol.marco.mancala.gameLib.player.brains;

import java.util.ArrayList;
import java.util.Random;

import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.player.Player;

public class ArtificialIntelligence extends BaseBrain {
    private final static String LOG_TAG = ArtificialIntelligence.class.getSimpleName();

    protected Random mRandom;
    private int mLastMove;
    public ArtificialIntelligence(int numberOfBowl, int numberOfTray) {
        super(numberOfBowl, numberOfTray);
        mRandom = new Random();
    }

    @Override
    public void makeMove(ArrayList<Container> boardStatus, Player player) {
        // very easy strategy, check which are his container and then chose a random bowl not empty.
        int choosenBowl = 0;

        int remainingBowl = remainsOnlyOneBowlWithSeed(boardStatus);
        if (remainingBowl == 1) {
            choosenBowl = getABowlWithSeeds(boardStatus);
        }
        if (remainingBowl == 2) {
            choosenBowl = getABowlWithSeeds(boardStatus);
        }
        if (remainingBowl >= 3) {
            int randomNumber = mRandom.nextInt(mNumberOfBowl);

            // The first six bowl are the ones of the first player
            // TODO extract in a method
            if (boardStatus.get(0).getOwner() == player) {
                choosenBowl = randomNumber;
            } else {
                // so my player bowls are the ones after the tray of player one, bowl 1 of player2 is 6+1
                // PAY ATTENTION! Here I'm passing the real position in the array of container, not only
                // the bowl number.
                // This can be a problem...maybe.
                choosenBowl = randomNumber + mNumberOfBowl + mNumberOfTray;
            }
        }

        //reset the invalid status
        if(mInvalidMove) toggleLastMoveCameUpInvalid();

        mLastMove = choosenBowl;
        mAttachedPlayer.onBrainInteraction(choosenBowl);
    }

    private int getABowlWithSeeds(ArrayList<Container> boardStatus) {
        int bowl = 0;
        for (Container c : boardStatus) {
            if (c.getNumberOfSeeds() != 0) {
                bowl = boardStatus.indexOf(c);
            }
        }
        return bowl;
    }

    private int remainsOnlyOneBowlWithSeed(ArrayList<Container> boardStatus) {
        int remainingBowlWithSeed = 0;
        for (Container c : boardStatus) {
            if (c.getNumberOfSeeds() != 0) {
                remainingBowlWithSeed++;
            }
        }

        return remainingBowlWithSeed;
    }

    @Override
    public boolean isHuman() {
        return false;
    }
}
