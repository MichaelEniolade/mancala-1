package me.dacol.marco.mancala.gameLib.commands;

public interface Command {

    public boolean isValid();
    public void execute();

}
