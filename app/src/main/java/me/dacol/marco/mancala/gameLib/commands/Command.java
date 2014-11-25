package me.dacol.marco.mancala.gameLib.commands;

/**
 * Created by Dac on 25/11/14.
 */
public interface Command {

    public boolean isValid();
    public void execute();

}
