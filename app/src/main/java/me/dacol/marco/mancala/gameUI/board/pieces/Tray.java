package me.dacol.marco.mancala.gameUI.board.pieces;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.TextView;

import me.dacol.marco.mancala.R;

public class Tray extends TextView {

    private boolean mIsHumanVsHuman;
    private boolean mIsOpponentContainer;

    public Tray(Context context,
                int player,
                GridLayout.LayoutParams params,
                String text,
                boolean isHumanVsHuman,
                float width)
    {
        super(context);

        mIsHumanVsHuman = isHumanVsHuman;
        mIsOpponentContainer = (player == 2);

        Drawable trayShape;

        if (player == 1) {
            trayShape = getResources().getDrawable(R.drawable.bg_tray_player_one);
            setTextColor(Color.WHITE);
        } else {
            trayShape = getResources().getDrawable(R.drawable.bg_tray_player_two);
            setTextColor(Color.BLACK);
        }

        if (Build.VERSION.SDK_INT >= 16) {
            this.setBackground(trayShape);
        } else {
            this.setBackgroundDrawable( trayShape );
        }

        setLayoutParams(params);
        setText(text);
        setGravity(Gravity.CENTER);

        setWidth((int) width);
        setHeight((int) width);

        setTextSize(getResources().getDimension(R.dimen.tray_text_size));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mIsHumanVsHuman && mIsOpponentContainer) {
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
