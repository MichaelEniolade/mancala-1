package me.dacol.marco.mancala.gameUI.board.pieces;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.Button;
import android.widget.GridLayout;

import me.dacol.marco.mancala.R;

public class Bowl extends Button {

    private boolean mIsHumanVsHuman;
    private boolean mIsOpponentContainer;

    public Bowl(Context context,
                int player,
                boolean isHumanVsHuman,
                GridLayout.LayoutParams params,
                String text,
                int id,
                float width,
                float height)
    {
        super(context);

        mIsHumanVsHuman = isHumanVsHuman;
        mIsOpponentContainer = (player == 2);

        Drawable buttonShape;

        if (player == 1) {
            // Load shape for player one bowls
            buttonShape = getResources().getDrawable(R.drawable.bg_selector_player_one);
            setTextColor(Color.WHITE);
        } else {
            // Load shape for player two bowls
            if (isHumanVsHuman) {
                // If is HvH load the selectable graphics and rotate text 180 degree
                buttonShape = getResources().getDrawable(R.drawable.bg_selector_player_two);
            } else {
                // if it's not human vs human i don't need the click effect on the opponents bowl
                buttonShape = getResources().getDrawable(R.drawable.bg_bowl_player_two);
            }
            setTextColor(Color.BLACK);
        }

        if (Build.VERSION.SDK_INT >= 16) {
            this.setBackground(buttonShape);
        } else {
            this.setBackgroundDrawable(buttonShape);
        }

        setLayoutParams(params);
        setText(text);
        setId(id);

        setWidth((int) width);
        setHeight((int) height);



        setTextSize(getResources().getDimension(R.dimen.bowl_text_size));


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
