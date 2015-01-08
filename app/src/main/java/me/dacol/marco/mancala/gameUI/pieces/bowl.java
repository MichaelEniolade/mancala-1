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
            buttonShape = getResources().getDrawable(R.drawable.bg_selector_player_two);
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
