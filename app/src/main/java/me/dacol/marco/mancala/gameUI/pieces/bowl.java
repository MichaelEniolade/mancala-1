package me.dacol.marco.mancala.gameUI.pieces;

import android.content.Context;
import android.widget.Button;

public class Bowl extends Button {

    public Bowl factory(Context context) {
        Bowl bowl = new Bowl(context);

        return bowl;

    }

    public Bowl(Context context) {
        super(context);
    }
}
