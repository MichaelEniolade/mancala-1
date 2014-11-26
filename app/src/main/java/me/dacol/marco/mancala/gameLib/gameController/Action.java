package me.dacol.marco.mancala.gameLib.gameController;

public abstract class Action<T> {

    private T load;

    protected Action(T load) {
        this.load = load;
    }

    public T get() {
        return load;
    }

}
