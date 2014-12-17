package me.dacol.marco.mancala;

import android.test.AndroidTestCase;

import java.util.ArrayList;

import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.board.Move;
import me.dacol.marco.mancala.gameLib.gameController.TurnContext;
import me.dacol.marco.mancala.gameLib.gameController.actions.ActivePlayer;
import me.dacol.marco.mancala.gameLib.gameController.actions.InvalidMove;
import me.dacol.marco.mancala.gameLib.gameController.actions.MoveAction;
import me.dacol.marco.mancala.gameLib.player.Player;
import me.dacol.marco.mancala.gameLib.player.PlayerFactory;
import me.dacol.marco.mancala.gameLib.player.PlayerType;
import me.dacol.marco.mancala.gameLib.player.brains.ArtificialIntelligence;
import me.dacol.marco.mancala.gameLib.player.brains.Brain;

public class PlayerTest extends AndroidTestCase {
    /* What to test here?
    * - Player factory
    * - Player ability to post and to receive action
    * -- Receive:
    * --- activePlayer
    * --- invalidMove
    * -- Post:
    * --- moveAction
    */

    private TurnContext mTurnContext;
    private Player mHumanPlayer;
    private Player mComputerPlayer;
    private TestBlockingObserver mTestBlockingObserver;
    private ArrayList<Container> mBoardStatus;

    public void testPlayerFactory() {
        basicInitialize();
        PlayerFactory playerFactory = new PlayerFactory(mTurnContext, 6, 1);

        Player humanPlayer = null,
               computerPlayer = null;

        humanPlayer = playerFactory.makePlayer(PlayerType.HUMAN, "Kasparov");
        computerPlayer = playerFactory.makePlayer(PlayerType.ARTIFICIAL_INTELLIGENCE, "Hal9000");

        assertTrue(humanPlayer.isHuman());
        assertTrue(!computerPlayer.isHuman());
        assertEquals("Kasparov", humanPlayer.getName());
        assertEquals("Hal9000", computerPlayer.getName());

        cleanUp();
    }


    public void testRespondToActivePlayerAction() {
        fullInitialize();

        ActivePlayer activePlayer = new ActivePlayer(mComputerPlayer, mBoardStatus);
        mTurnContext.push(activePlayer);
        mTestBlockingObserver.waitUntilUpdateIsCalled();

        assertTrue(mTurnContext.peek() instanceof MoveAction);

        MoveAction moveAction = (MoveAction) mTurnContext.pop();

        assertTrue(moveAction.getLoad().getPlayer() == mComputerPlayer);
        assertTrue(moveAction.getLoad().getBowlNumber() >= 0);

        cleanUp();
    }

    public void testRespondToInvalidMoveAction() {
        fullInitialize();

        Brain brain = new ArtificialIntelligence(6, 1);
        Player playerWithInvalidMove = new Player(mTurnContext, brain, "Carl");

        mTurnContext.addObserver(playerWithInvalidMove);

        Move move = new Move(3, playerWithInvalidMove);

        InvalidMove invalidMove = new InvalidMove(move, mBoardStatus, playerWithInvalidMove);
        mTurnContext.push(invalidMove);
        mTestBlockingObserver.waitUntilUpdateIsCalled();

        assertTrue(mTurnContext.peek() instanceof MoveAction);

        MoveAction moveAction = (MoveAction) mTurnContext.pop();

        assertTrue(moveAction.getLoad().getPlayer() == playerWithInvalidMove);
        assertTrue(moveAction.getLoad().getBowlNumber() != move.getBowlNumber());

        cleanUp();
    }

    // ---> HELPERS
    private void basicInitialize() {
        mTurnContext = TurnContext.getInstance();
        mTurnContext.initialize();
    }

    private void fullInitialize() {
        basicInitialize();

        PlayerFactory playerFactory = new PlayerFactory(mTurnContext, 6, 1);

        mHumanPlayer = playerFactory.makePlayer(PlayerType.HUMAN, "Kasparov");
        mComputerPlayer = playerFactory.makePlayer(PlayerType.ARTIFICIAL_INTELLIGENCE, "Hal9000");

        mTestBlockingObserver = new TestBlockingObserver();

        mTurnContext.addObserver(mTestBlockingObserver);
        mTurnContext.addObserver(mHumanPlayer);
        mTurnContext.addObserver(mComputerPlayer);

        mBoardStatus = TestingUtility.createBoardRepresentation(new int[]{3,1,0,1,1,1,4,1,1,1,1,1,1,5},
                mHumanPlayer, mComputerPlayer);
    }

    private void cleanUp() {
        mTurnContext.deleteObservers();
    }

}
