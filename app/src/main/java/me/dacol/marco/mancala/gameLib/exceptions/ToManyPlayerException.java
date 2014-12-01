package me.dacol.marco.mancala.gameLib.exceptions;

public class ToManyPlayerException extends Exception {
    public ToManyPlayerException(String detailMessage) {
        super(detailMessage);
    }
}
