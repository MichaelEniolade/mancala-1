package me.dacol.marco.mancala;

import android.test.AndroidTestCase;

import me.dacol.marco.mancala.gameLib.exceptions.ToManyPlayerException;
import me.dacol.marco.mancala.gameLib.gameController.Game;
import me.dacol.marco.mancala.gameLib.gameController.TurnContext;
import me.dacol.marco.mancala.gameLib.gameController.actions.ActivePlayer;
import me.dacol.marco.mancala.gameLib.gameController.actions.BoardUpdated;
import me.dacol.marco.mancala.gameLib.gameController.actions.EvenGame;
import me.dacol.marco.mancala.gameLib.gameController.actions.InvalidMove;
import me.dacol.marco.mancala.gameLib.gameController.actions.MoveAction;
import me.dacol.marco.mancala.gameLib.gameController.actions.Winner;
import me.dacol.marco.mancala.gameLib.player.PlayerType;

public class GameTest extends AndroidTestCase {

    private Game mGame;
    private TurnContext mTurnContext;
    private TestBlockingObserver mTestBlockingObserver;

    // disabled because can use a lot of time...players are dumb
    private void testSimulatedGame() {
        initialize();

        mGame.start();

        mTestBlockingObserver.waitUntilUpdateIsCalled();

        boolean isEnded = false;

        while (!isEnded) {
            assertTrue(mTurnContext.peek() instanceof ActivePlayer);

            mTestBlockingObserver.waitUntilUpdateIsCalled();

            if (mTurnContext.peek() instanceof MoveAction) {
                assertTrue(mTurnContext.peek() instanceof MoveAction);
                mTestBlockingObserver.waitUntilUpdateIsCalled();
            } else {
                assertTrue(mTurnContext.peek() instanceof InvalidMove);
                mTestBlockingObserver.waitUntilUpdateIsCalled();

                assertTrue(mTurnContext.peek() instanceof MoveAction);
                mTestBlockingObserver.waitUntilUpdateIsCalled();
            }

            assertTrue(mTurnContext.peek() instanceof BoardUpdated);

            isEnded = (mTurnContext.peek() instanceof Winner) || (mTurnContext.peek() instanceof EvenGame);

            mTestBlockingObserver.waitUntilUpdateIsCalled();
        }

        assertTrue(mTurnContext.peek() instanceof Winner);
    }

    private void initialize() {
        mGame = Game.getInstance();
        mGame.setup();

        try {
            mGame.createPlayer(PlayerType.HUMAN, "Kasparov");
            mGame.createPlayer(PlayerType.ARTIFICIAL_INTELLIGENCE, "Hal9000");
        } catch (ToManyPlayerException e) {
            assertEquals("Maximum allowed number of player is: 2", e.getMessage());
        }

        mTurnContext = mGame.getTurnContext();
        mTestBlockingObserver = new TestBlockingObserver();

        mTurnContext.addObserver(mTestBlockingObserver);
    }

}
