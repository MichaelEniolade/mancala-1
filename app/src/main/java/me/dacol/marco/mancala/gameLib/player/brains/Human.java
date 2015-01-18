package me.dacol.marco.mancala.gameLib.player.brains;

import java.util.ArrayList;

import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.player.Player;
import me.dacol.marco.mancala.gameUI.OnFragmentInteractionListener;

public class Human extends BaseBrain implements OnFragmentInteractionListener {

    private final static String LOG_TAG = Human.class.getSimpleName();

    public Human(int numberOfBowl, int numberOfTray) {
        super(numberOfBowl, numberOfTray);
    }

    @Override
    public void makeMove(ArrayList<Container> boardStatus, Player player) {
        // This is a real player, no need for a brain. He has a real Brain!
    }

    @Override
    public boolean isHuman() {
        return true;
    }

    @Override
    public void onFragmentInteraction(EventType event, Object data) {
        if ((event == EventType.CHOSEN_BOWL) && (data instanceof Integer)) {
            int choosenBowl = (Integer) data;
            mAttachedPlayer.onBrainInteraction(choosenBowl);
        }
    }
}
