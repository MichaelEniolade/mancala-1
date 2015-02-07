package me.dacol.marco.mancala.gameUI.board.pieces;

import android.content.Context;
import android.view.Gravity;
import android.widget.GridLayout;

/**
 * Helper with static method to easily create new pieces
 */
public class PieceFactory {

    /**
     * Generate a BowlView element to put in a gridlayout
     * @param context application context
     * @param row row of the gridlayout in which this bowl has to be placed
     * @param column column of the gridlayout in which this bowl has to be placed
     * @param text String content to visualize
     * @param id int id for the bowl
     * @param player int indicating player number (1 or 2)
     * @param isHumanVsHuman boolean for the game type, true if is HvH game
     * @param dimension the size of the bowl
     * @return return Bowl ready to be added in the gridlayout
     */
    public static Bowl generateBowl(Context context,
                                    int row,
                                    int column,
                                    String text,
                                    int id,
                                    int player,
                                    boolean isHumanVsHuman,
                                    float dimension)
    {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(row);
        params.columnSpec = GridLayout.spec(column);
        params.setGravity(Gravity.CENTER);

        //int bowlDimension = convertFromDpsToPixel(context, dimension);

        return new Bowl(context, player, isHumanVsHuman, params, text, id, dimension, dimension);
    }

    /**
     * Generate a TrayView element to put in a gridlayout
     * @param context application context
     * @param row row of the gridlayout in which this Tray has to be placed
     * @param column column of the gridlayout in which this Tray has to be placed
     * @param text String content to visualize
     * @param player int indicating player number (1 or 2)
     * @param isHumanVsHuman boolean for the game type, true if is HvH game
     * @param dimension the size of the tray
     * @return return Tray ready to be added in the gridlayout
     */
    public static Tray generateTray(Context context,
                                    int row,
                                    int column,
                                    String text,
                                    int player,
                                    boolean isHumanVsHuman,
                                    float dimension)
    {

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(row);
        params.columnSpec = GridLayout.spec(column);
        params.setGravity(Gravity.CENTER);

        //int trayDimension = convertFromDpsToPixel(context, dimension);

        return new Tray(context, player, params, text, isHumanVsHuman, dimension);
    }

    private static int convertFromDpsToPixel(Context context, float dps) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }
}
