package me.dacol.marco.mancala.gameUI.pieces;

import android.content.Context;
import android.view.Gravity;
import android.widget.GridLayout;

public class PieceFactory {

    private static final float DPS_WIDTH_DIMENSION = 80f; //TODO move me to dimens.xml

    public static Bowl generateBowl(Context context, int row, int column, String text, int id, int player, boolean isHumanVsHuman) {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(row);
        params.columnSpec = GridLayout.spec(column);


        int bowlDimension = convertFromDpsToPixel(context, DPS_WIDTH_DIMENSION);

        return new Bowl(context, player, isHumanVsHuman, params, text, id, bowlDimension, bowlDimension);
    }

    public static Tray generateTray(Context context, int row, int column, String text, int player) {

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(row);
        params.columnSpec = GridLayout.spec(column);
        params.setGravity(Gravity.FILL_VERTICAL);

        int trayDimension = convertFromDpsToPixel(context, DPS_WIDTH_DIMENSION);

        return new Tray(context, player, params, text, trayDimension);
    }

    private static int convertFromDpsToPixel(Context context, float dps) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }
}
