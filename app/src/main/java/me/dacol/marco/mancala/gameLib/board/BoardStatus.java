package me.dacol.marco.mancala.gameLib.board;

/**
 * Created by Dac on 25/11/14.
 */
public class BoardStatus {

    // singleton!
    private static BoardStatus instance = null;

    protected BoardStatus() {}

    public BoardStatus getInstance() {
        if (instance == null) {
            instance = new BoardStatus();
        }

        return instance;
    }

    // 1 lista di 12 ciotole
    // 2 variabili per i vassoi

}
