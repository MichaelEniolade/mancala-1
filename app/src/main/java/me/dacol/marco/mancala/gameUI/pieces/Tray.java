package me.dacol.marco.mancala.gameUI.pieces;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.TextView;

import me.dacol.marco.mancala.R;

public class Tray extends TextView {

    public static Tray factory(Context context, int row, int column, String text, int color) {
        Tray tray = new Tray(context, color);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(row);
        params.columnSpec = GridLayout.spec(column);
        params.setGravity(Gravity.FILL);

        tray.setLayoutParams(params);
        tray.setText(text);
        tray.setGravity(Gravity.CENTER);

        return tray;

    }

    public Tray(Context context, int color) {
        super(context);

        GradientDrawable buttonShape = (GradientDrawable) getResources().getDrawable( R.drawable.tray );
        buttonShape.setColor( getResources().getColor(color) );

        if (Build.VERSION.SDK_INT >= 16) {
            this.setBackground(buttonShape);
        } else {
            this.setBackgroundDrawable( buttonShape );
        }
    }

}
