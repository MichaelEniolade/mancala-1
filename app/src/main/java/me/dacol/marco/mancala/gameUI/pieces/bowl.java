package me.dacol.marco.mancala.gameUI.pieces;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.Button;
import android.widget.GridLayout;

import me.dacol.marco.mancala.R;

public class Bowl extends Button {

    public Bowl(Context context,
                int player,
                boolean isHumanVsHuman,
                GridLayout.LayoutParams params,
                String text,
                int id,
                int width,
                int height)
    {
        super(context);

        Drawable buttonShape;

        if (player == 1) {
            buttonShape = getResources().getDrawable(R.drawable.bg_selector_player_one);
        } else {
            if (isHumanVsHuman) {
                buttonShape = getResources().getDrawable(R.drawable.bg_selector_player_two);
            } else {
                // if it's not human vs human i don't need the click effect on the opponents bowl
                buttonShape = getResources().getDrawable(R.drawable.bg_bowl_player_two);
            }
        }

        if (Build.VERSION.SDK_INT >= 16) {
            this.setBackground(buttonShape);
        } else {
            this.setBackgroundDrawable(buttonShape);
        }

        setLayoutParams(params);
        setText(text);
        setId(id);

        setWidth(width);
        setHeight(height);

        setTextSize(30f);
    }

}
