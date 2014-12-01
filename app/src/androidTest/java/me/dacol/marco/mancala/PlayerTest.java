package me.dacol.marco.mancala;

import android.test.AndroidTestCase;

import java.util.ArrayList;

import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.board.Move;
import me.dacol.marco.mancala.gameLib.exceptions.PlayerBrainTypeUnknownException;
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
               computerPlayer = null,
               wrongPlayer = null;
        try {
            humanPlayer = playerFactory.makePlayer(PlayerType.HUMAN, "Kasparov");
            computerPlayer = playerFactory.makePlayer(PlayerType.ARTIFICIAL_INTELLIGENCE, "Hal9000");
            wrongPlayer = playerFactory.makePlayer(1000, "wrong");
        } catch (PlayerBrainTypeUnknownException e) {
            assertEquals("Type: 1000 not known, check PlayerType class", e.getMessage());
        }

        assertTrue(humanPlayer.isHuman());
        assertTrue(!computerPlayer.isHuman());
        assertEquals("Kasparov", humanPlayer.getmName());
        assertEquals("Hal9000", computerPlayer.getmName());

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

        Brain brain = new ArtificialIntelligence(mComputerPlayer, 6, 1);
        Move move = brain.makeMove(mBoardStatus);

        mComputerPlayer.setBrain(brain); //I need a brain with a move to test

        InvalidMove invalidMove = new InvalidMove(move, mBoardStatus, mComputerPlayer);
        mTurnContext.push(invalidMove);
        mTestBlockingObserver.waitUntilUpdateIsCalled();

        assertTrue(mTurnContext.peek() instanceof MoveAction);

        MoveAction moveAction = (MoveAction) mTurnContext.pop();

        assertTrue(moveAction.getLoad().getPlayer() == mComputerPlayer);
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

        try {
            mHumanPlayer = playerFactory.makePlayer(PlayerType.HUMAN, "Kasparov");
            mComputerPlayer = playerFactory.makePlayer(PlayerType.ARTIFICIAL_INTELLIGENCE, "Hal9000");
        } catch (PlayerBrainTypeUnknownException e) {
            e.printStackTrace();
        }

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
