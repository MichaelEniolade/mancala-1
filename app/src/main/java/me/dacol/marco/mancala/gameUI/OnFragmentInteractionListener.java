package me.dacol.marco.mancala.gameUI;

public interface OnFragmentInteractionListener {

    public enum EventType { NEW_HvH_GAME_BUTTON_PRESSED,
                            NEW_HvC_GAME_BUTTON_PRESSED,
                            STATISTICS_BUTTON_PRESSED,
                            PREFERENCES_BUTTON_PRESSED,
                            RESTART_GAME_BUTTON_PRESSED,
                            CHOSEN_BOWL
    };

    void onFragmentInteraction(EventType event, Object data);
}
