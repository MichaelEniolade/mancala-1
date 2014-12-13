package me.dacol.marco.mancala.gameLib.player.brains;

/***
 * Very dumb brain just to see if it can play a game.
 * It moves from random bowls.
 */
public abstract class BaseBrain implements Brain {

    protected int mNumberOfBowl;
    protected int mNumberOfTray;

    protected boolean mInvalidMove;

    protected AttachedPlayer mAttachedPlayer;

    public BaseBrain(int numberOfBowl, int numberOfTray) {
        mInvalidMove = false;
        mNumberOfBowl = numberOfBowl;
        mNumberOfTray = numberOfTray;
    }

    @Override
    public void attachPlayer(AttachedPlayer attachedPlayer) {
        mAttachedPlayer = attachedPlayer;
    }

    @Override
    public void toggleLastMoveCameUpInvalid() {
        mInvalidMove = !mInvalidMove;
    }

    @Override
    public boolean isHuman() {
        return false;
    }
}
