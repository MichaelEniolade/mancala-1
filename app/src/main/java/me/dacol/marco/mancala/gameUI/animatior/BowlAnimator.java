package me.dacol.marco.mancala.gameUI.animatior;

import android.animation.Animator;
import android.os.AsyncTask;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import java.util.ArrayList;

import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.gameController.actions.Action;
import me.dacol.marco.mancala.gameLib.gameController.actions.BoardEmptyBowl;
import me.dacol.marco.mancala.gameLib.gameController.actions.BoardPutInTray;
import me.dacol.marco.mancala.gameLib.gameController.actions.BoardPutOneInContainer;
import me.dacol.marco.mancala.gameUI.pieces.Bowl;
import me.dacol.marco.mancala.logging.Logger;

public class BowlAnimator extends AsyncTask<ArrayList, Object, Void> {

    private static final String LOG_TAG = BowlAnimator.class.getSimpleName();

    private ArrayList<TextView> mBowlUIRepresentation;
    private boolean mReactivateAllButton;
    private boolean mThereAreMovesOnQueue;
    private AsyncResponse mAsyncResponse;

    public BowlAnimator(ArrayList<TextView> bowlUIRepresentation, AsyncResponse asyncResponse) {
        mBowlUIRepresentation = bowlUIRepresentation;
        mReactivateAllButton = false;
        mThereAreMovesOnQueue = false;
        mAsyncResponse = asyncResponse;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // I've to check if all the buttons are active or not, because after i will have to restore
        // the state of all the buttons
        for (int i = 0; i < mBowlUIRepresentation.size(); i++) {
            if (mBowlUIRepresentation.get(i) instanceof Bowl) {
                Bowl b = (Bowl) mBowlUIRepresentation.get(i);
                if (i < 7) {
                    b.setEnabled(false);
                } else if (b.isEnabled()) {
                    mReactivateAllButton = true;
                    b.setEnabled(false);
                }
            }
        }
    }

    @Override
    protected Void doInBackground(ArrayList... params) {
        // trova il bottone da animare
        // carica l'animazione
        // chiama onProggressUpdate, passandogli come parametro l'animazione
        if(android.os.Debug.isDebuggerConnected())
            android.os.Debug.waitForDebugger();

        if (params[0] != null) {
            ArrayList<Action> moveSequence = params[0];
            ArrayList<Container> boardRepresentation = params[1];

            for (Action move : moveSequence) {
                TextView textView = null;
                String newText = "";

                if (move instanceof BoardEmptyBowl) {
                    BoardEmptyBowl m = (BoardEmptyBowl) move;
                    textView = mBowlUIRepresentation.get(m.getLoad());
                    newText = String.valueOf(m.getNumberOfSeeds());
                } else if (move instanceof BoardPutInTray) {
                    BoardPutInTray m = (BoardPutInTray) move;
                    textView = mBowlUIRepresentation.get( m.getLoad() );
                    //int actualNumberOfSeeds = Integer.valueOf(textView.getText().toString());
                    //actualNumberOfSeeds += ((BoardPutInTray) move).getNumberOfSeeds();
                    newText = String.valueOf( m.getNumberOfSeeds() );
                } else if (move instanceof BoardPutOneInContainer) {
                    BoardPutOneInContainer m = (BoardPutOneInContainer) move;
                    textView = mBowlUIRepresentation.get( m.getLoad() );
                    //int actualNumberOfSeeds = Integer.valueOf(textView.getText().toString());
                    //actualNumberOfSeeds += 1;
                    newText = String.valueOf( m.getNumberOfSeeds() );
                }

                //publishProgress(getAnimatorFor(textView), newText, textView);
                publishProgress(newText, textView);

                // sleep thread to see animation sequentially
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        // to separate the animation between player and computer
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
     protected void onProgressUpdate(Object... values) {
        DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
        final OvershootInterpolator overshootInterpolator = new OvershootInterpolator();

        //ValueAnimator valueAnimator = (ValueAnimator) values[0];
        final String newText = (String) values[0];
        final TextView container = (TextView) values[1];

        container.setText(newText);
        container.animate().setDuration(200);

        container.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                container.animate().setListener(null);
                container.setText(newText);
                container.animate().setInterpolator(overshootInterpolator).scaleX(1f).scaleY(1f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        container.animate().setInterpolator(decelerateInterpolator).scaleX(0.7f).scaleY(0.7f);

        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        for (int i = 0; i < mBowlUIRepresentation.size(); i++) {
            if (mBowlUIRepresentation.get(i) instanceof Bowl) {
                Bowl b = (Bowl) mBowlUIRepresentation.get(i);
                if (i < 7) {
                    b.setEnabled(true);
                } else if (mReactivateAllButton) {
                    b.setEnabled(true);
                }
            }
        }

        mAsyncResponse.deliver();

        if(mThereAreMovesOnQueue) {
            Logger.v(LOG_TAG, "Going to execute the queue of moves");
        }
    }
}
