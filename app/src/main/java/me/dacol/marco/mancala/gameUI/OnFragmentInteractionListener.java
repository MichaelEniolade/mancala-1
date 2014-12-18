package me.dacol.marco.mancala.gameUI;

public interface OnFragmentInteractionListener {

    public enum EventType { NEW_GAME_BUTTON_PRESSED, TOGGLE_ENEMY_KIND, CHOOSEN_BOWL };

    void onFragmentInteraction(EventType event, Object data);
}
