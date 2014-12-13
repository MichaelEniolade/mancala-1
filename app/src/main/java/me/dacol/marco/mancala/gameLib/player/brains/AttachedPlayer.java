package me.dacol.marco.mancala.gameLib.player.brains;

/**
 * Listener for the communication between the Brain and his Player
 */
public interface AttachedPlayer {
    void onBrainInteraction(int move);
}
