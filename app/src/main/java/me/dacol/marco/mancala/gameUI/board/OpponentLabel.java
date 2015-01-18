package me.dacol.marco.mancala.gameUI.board;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

public class OpponentLabel extends TextView {


    private boolean mIsHumanVsHuman;

    public OpponentLabel(Context context) {
        super(context);
        mIsHumanVsHuman = false;
    }

    public OpponentLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
        mIsHumanVsHuman = false;
    }

    public OpponentLabel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mIsHumanVsHuman = false;
    }

    public void setIsHumanVsHuman(boolean mIsHumanVsHuman) {
        this.mIsHumanVsHuman = mIsHumanVsHuman;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mIsHumanVsHuman) {
            setGravity(Gravity.RIGHT);
            // rotate the button of 180 degree
            //This saves off the matrix that the canvas applies to draws, so it can be restored later.
            canvas.save();

            //now we change the matrix
            //We need to rotate around the center of our text
            //Otherwise it rotates around the origin, and that's bad.
            float py = this.getHeight() / 2.0f;
            float px = this.getWidth() / 2.0f;
            canvas.rotate(180, px, py);

            //draw the text with the matrix applied.
            super.onDraw(canvas);

            //restore the old matrix.
            canvas.restore();
        } else {
            // otherwise draw normal a normal button
            super.onDraw(canvas);
        }
    }
}
