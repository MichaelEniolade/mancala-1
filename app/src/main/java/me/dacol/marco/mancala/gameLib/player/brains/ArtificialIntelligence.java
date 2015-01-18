package me.dacol.marco.mancala.gameLib.player.brains;

import android.os.AsyncTask;

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
        new computeMove(mAttachedPlayer).execute(boardStatus);
    }



    @Override
    public boolean isHuman() {
        return false;
    }


    private class computeMove extends AsyncTask<ArrayList<Container>, Void, Integer>{

        private AttachedPlayer mAttachedPlayer;

        private computeMove(AttachedPlayer attachedPlayer) {
            mAttachedPlayer = attachedPlayer;
        }

        @Override
        protected Integer doInBackground(ArrayList<Container>... params) {
            ArrayList<Container> boardStatus = params[0];

            // very easy strategy, check which are his container and then chose a random bowl_selected not empty.
            int chosenBowl = 0;

            int remainingBowl = remainsOnlyOneBowlWithSeed(boardStatus);
            // if no bowl remains no moves can be done...
            if (remainingBowl == 1) {
                chosenBowl = getABowlWithSeeds(boardStatus);
            }
            if (remainingBowl == 2) {
                chosenBowl = getABowlWithSeeds(boardStatus);
            }
            if (remainingBowl >= 3) {
                int randomNumber = mRandom.nextInt(mNumberOfBowl);

                // The first six bowl_selected are the ones of the first player
                if (boardStatus.get(0).getOwner() == mAttachedPlayer) {
                    chosenBowl = randomNumber;
                } else {
                    // so my player bowls are the ones after the tray of player one, bowl_selected 1 of player2 is 6+1
                    // PAY ATTENTION! Here I'm passing the real position in the array of container, not only
                    // the bowl_selected number.
                    // This can be a problem...maybe.
                    chosenBowl = randomNumber + mNumberOfBowl + mNumberOfTray;
                }
            }

            //reset the invalid status
            if (mInvalidMove) toggleLastMoveCameUpInvalid();

            mLastMove = chosenBowl;
            //mAttachedPlayer.onBrainInteraction(chosenBowl);
            return chosenBowl;
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
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mAttachedPlayer.onBrainInteraction(integer);
        }
    }
}
