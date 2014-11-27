package me.dacol.marco.mancala.gameLib.gameController.actions;

public abstract class Action<T> {

    private T mLoad;

    public Action(T load) {
        this.mLoad = load;
    }

    public T getLoad() {
        return mLoad;
    }

}
