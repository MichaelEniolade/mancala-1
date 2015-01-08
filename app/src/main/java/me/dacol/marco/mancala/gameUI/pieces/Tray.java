package me.dacol.marco.mancala.gameUI.pieces;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.TextView;

import me.dacol.marco.mancala.R;

public class Tray extends TextView {

    public Tray(Context context,
                int player,
                GridLayout.LayoutParams params,
                String text,
                int width)
    {
        super(context);

        Drawable trayShape;

        if (player == 1) {
            trayShape = getResources().getDrawable(R.drawable.bg_tray_player_one);
        } else {
            trayShape = getResources().getDrawable(R.drawable.bg_tray_player_two);
        }

        if (Build.VERSION.SDK_INT >= 16) {
            this.setBackground(trayShape);
        } else {
            this.setBackgroundDrawable( trayShape );
        }

        setLayoutParams(params);
        setText(text);
        setGravity(Gravity.CENTER);

        setWidth(width);

        setTextColor(Color.WHITE);
        setTextSize(40f);

    }

}
