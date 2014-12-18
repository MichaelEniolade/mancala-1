package me.dacol.marco.mancala.gameUI;

import me.dacol.marco.mancala.gameLib.exceptions.NumberOfPlayersException;
import me.dacol.marco.mancala.gameLib.exceptions.ToManyPlayerException;
import me.dacol.marco.mancala.gameLib.gameController.Game;
import me.dacol.marco.mancala.gameLib.gameController.TurnContext;
import me.dacol.marco.mancala.gameLib.player.PlayerType;
import me.dacol.marco.mancala.gameUI.board.BoardFragment;
import me.dacol.marco.mancala.logging.Logger;

public class GameThread implements Runnable {

    private static final String LOG_TAG = GameThread.class.getSimpleName();

    private Game mGame;
    private TurnContext mTurnContext;

    public GameThread(String threadName) {
        mGame = Game.getInstance();

        mTurnContext = mGame.getTurnContext();
    }

    @Override
    public void run() {
        mGame.start();
    }

    public TurnContext getTurnContext() {
        return mTurnContext;
    }

    public void attachBoardViewToGameLogic(BoardFragment boardFragment) throws NumberOfPlayersException {
        mGame.setupBoard();
        Logger.v(LOG_TAG, "setupBoard");
        // This because I need to connect the real Brain of the player to the Model in the game
        boardFragment.attachHumanPlayerBrain(
                (OnFragmentInteractionListener) mGame.getHumanPlayer().getBrain());

        boardFragment.setInitialBoardState(mGame.getBoardStatus());

        mTurnContext.addObserver(boardFragment);
    }

    public void createPlayer(PlayerType type, String name) throws ToManyPlayerException {

        mGame.createPlayer(type, name);
        Logger.v(LOG_TAG, "creato player: " + name);
    }

    public void resetGame() {
        mGame.reset();
    }
}
