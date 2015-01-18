package me.dacol.marco.mancala.gameLib.gameController.actions;

import java.util.ArrayList;

import me.dacol.marco.mancala.gameLib.board.Container;

public class EvenGame extends Action<ArrayList<Container>> {

    private ArrayList<Action> mAtomicMoves;

    public EvenGame(ArrayList<Container> load, ArrayList<Action> atomicMoves) {
        super(load);
        mAtomicMoves = atomicMoves;
    }

    public ArrayList<Action> getAtomicMoves() {
        return mAtomicMoves;
    }
}
