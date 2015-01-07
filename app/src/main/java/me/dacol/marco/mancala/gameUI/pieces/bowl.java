package me.dacol.marco.mancala.gameUI.pieces;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.widget.Button;
import android.widget.GridLayout;

import me.dacol.marco.mancala.R;

public class Bowl extends Button {

    public static Bowl factory(Context context, int row, int column, String text, int id, int color) {
        Bowl bowl = new Bowl(context, color);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(row);
        params.columnSpec = GridLayout.spec(column);

        bowl.setLayoutParams(params);
        bowl.setText(text);
        bowl.setId(id);

        return bowl;

    }

    public Bowl(Context context, int color) {
        super(context);

        GradientDrawable buttonShape = (GradientDrawable) getResources().getDrawable( R.drawable.bowl );
        buttonShape.setColor( getResources().getColor(color));

        if (Build.VERSION.SDK_INT >= 16) {
            this.setBackground(buttonShape);
        } else {
            this.setBackgroundDrawable( buttonShape );
        }
    }
}
