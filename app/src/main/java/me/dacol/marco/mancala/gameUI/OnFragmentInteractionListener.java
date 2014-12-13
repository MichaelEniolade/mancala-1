package me.dacol.marco.mancala.gameUI;

public interface OnFragmentInteractionListener {

    public enum EventType { NEW_GAME_BUTTON_PRESSED, CHOOSEN_BOWL };

    void onFragmentInteraction(EventType event, Object data);
}
