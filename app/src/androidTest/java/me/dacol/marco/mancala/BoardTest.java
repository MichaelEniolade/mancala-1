package me.dacol.marco.mancala;

import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.Iterator;

import me.dacol.marco.mancala.gameLib.board.Board;
import me.dacol.marco.mancala.gameLib.board.Bowl;
import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.board.Move;
import me.dacol.marco.mancala.gameLib.board.Tray;
import me.dacol.marco.mancala.gameLib.exceptions.PlayerBrainTypeUnknownException;
import me.dacol.marco.mancala.gameLib.gameController.TurnContext;
import me.dacol.marco.mancala.gameLib.gameController.actions.BoardUpdated;
import me.dacol.marco.mancala.gameLib.gameController.actions.InvalidMove;
import me.dacol.marco.mancala.gameLib.gameController.actions.MoveAction;
import me.dacol.marco.mancala.gameLib.player.Player;
import me.dacol.marco.mancala.gameLib.player.PlayerFactory;
import me.dacol.marco.mancala.gameLib.player.PlayerType;

public class BoardTest extends AndroidTestCase {
    private TurnContext mTurnContext;
    private Player mHumanPlayer;
    private Player mComputerPlayer;
    private TestBlockingObserver mTestBlockingObserver;
    private ArrayList<Player> mPlayers;

    // ---> TEST CASES
    public void testBoardInitialization() {
        initialize();
        Board board = initializeBoard();
        board.buildBoard();

        assertTrue(board.getRepresentation().size() == 13);

        for (Container c : board.getRepresentation()) {
            if (c instanceof Bowl) {
                assertEquals(3, c.getNumberOfSeeds());
            } else if (c instanceof Tray) {
                assertEquals(0, c.getNumberOfSeeds());
            }
        }
    }

    public void testInvalidMoveMoveFromTray() {
        initialize();
        int[] startingStatus = new int[]{3,1,1,1,1,1,4,1,1,1,1,1,1,5};
        int[] expectedStatus = new int[]{3,1,1,1,1,1,4,1,1,1,1,1,1,5};
        int moveFrom = 6; // this is a tray
        Player playingPlayer = mHumanPlayer;

        InvalidMove invalidMove = runInvalidMoveConfiguration(startingStatus, expectedStatus, moveFrom, playingPlayer);
    }

    public void testInvalidMoveFromOpponentBowl() {
        initialize();
        int[] startingStatus = new int[]{3,1,1,1,1,1,4,1,1,1,1,1,1,5};
        int[] expectedStatus = new int[]{3,1,1,1,1,1,4,1,1,1,1,1,1,5};
        int moveFrom = 4; // this is the opponent bowl
        Player playingPlayer = mComputerPlayer;

        InvalidMove invalidMove = runInvalidMoveConfiguration(startingStatus, expectedStatus, moveFrom, playingPlayer);

    }

    public  void testInvalidMoveFromEmptyPlayerBowl() {
        initialize();
        int[] startingStatus = new int[]{3,1,0,1,1,1,4,1,1,1,1,1,1,5};
        int[] expectedStatus = new int[]{3,1,0,1,1,1,4,1,1,1,1,1,1,5};
        int moveFrom = 2; // this is the empty bowl
        Player playingPlayer = mHumanPlayer;

        InvalidMove invalidMove = runInvalidMoveConfiguration(startingStatus, expectedStatus, moveFrom, playingPlayer);
    }

    public void testStandardMove() {
        initialize();
        int[] startingStatus = new int[]{3,1,1,1,1,1,4,1,1,1,1,1,1,5};
        int[] expectedStatus = new int[]{0,2,2,2,1,1,4,1,1,1,1,1,1,5};
        int moveFrom = 0;

        BoardUpdated boardUpdated = runConfiguration(startingStatus, expectedStatus, moveFrom, mHumanPlayer);
        assertTrue(!boardUpdated.isGameEnded());
    }

    public void testStealingSeedsMove() {
        initialize();
        int[] startingStatus = new int[]{3,1,1,0,1,1,4,1,1,1,1,1,1,5};
        int[] expectedStatus = new int[]{0,2,2,0,1,1,6,1,1,1,0,1,1,5};
        int moveFrom = 0;

        BoardUpdated boardUpdated = runConfiguration(startingStatus, expectedStatus, moveFrom, mHumanPlayer);
        assertTrue(!boardUpdated.isGameEnded());
    }

    public void testStealingMoveWithNoOpponentSeeds() {
        initialize();
        int[] startingStatus = new int[]{3,1,1,0,1,1,4,1,1,1,0,1,1,5};
        int[] expectedStatus = new int[]{0,2,2,0,1,1,5,1,1,1,0,1,1,5};
        int moveFrom = 0;

        BoardUpdated boardUpdated = runConfiguration(startingStatus, expectedStatus, moveFrom, mHumanPlayer);
        assertTrue(!boardUpdated.isGameEnded());
    }

    public void testLastGameMove() {
        initialize();
        int[] startingStatus = new int[]{0,0,0,0,0,1,5,1,1,1,1,0,1,5};
        int[] expectedStatus = new int[]{0,0,0,0,0,0,6,1,1,1,1,0,1,5};
        int moveFrom = 5;

        BoardUpdated boardUpdated = runConfiguration(startingStatus, expectedStatus, moveFrom, mHumanPlayer);
        assertTrue(boardUpdated.isGameEnded());
    }

    public void testPlayerPlayAgain() {
        initialize();
        int[] startingStatus = new int[]{0,0,0,1,0,1,5,1,1,1,1,0,1,5};
        int[] expectedStatus = new int[]{0,0,0,1,0,0,6,1,1,1,1,0,1,5};
        int moveFrom = 5;

        BoardUpdated boardUpdated = runConfiguration(startingStatus, expectedStatus, moveFrom, mHumanPlayer);
        assertTrue(!boardUpdated.isGameEnded());
        assertTrue(boardUpdated.isAnotherRound());
    }


    // ---> HELPERS

    // Runs configuration of invalid moves, to check if the system responds well
    // Pay attention put always the human player bow (BH) first (as convention)
    // [ BH, BH, BH, BH, BH, BH, TH, BC, BC, BC, BC, BC, BC, TC ]
    private InvalidMove runInvalidMoveConfiguration(int[] startingStatus,
                                                    int[] expectedStatus,
                                                    int moveFrom,
                                                    Player playingPlayer) {
        Board board = initializeBoard();
        board.buildBoard();

        board.setBoardRepresentation(TestingUtility.createBoardRepresentation(startingStatus,
                mHumanPlayer, mComputerPlayer));
        MoveAction moveAction = new MoveAction(new Move(moveFrom, playingPlayer));

        mTurnContext.push(moveAction);
        mTestBlockingObserver.waitUntilUpdateIsCalled();

        assertTrue(mTurnContext.peek() instanceof InvalidMove);

        InvalidMove invalidMove = null;
        if (mTurnContext.peek() instanceof InvalidMove) {
            invalidMove = (InvalidMove) mTurnContext.pop();

            // Since invalid move no change on the board status
            checkExpectedStatus(invalidMove.getBoardStatus(), expectedStatus);

            assertTrue(moveAction.getLoad() == invalidMove.getLoad());
            assertTrue(playingPlayer == invalidMove.getPlayer());
        }

        cleanUp();

        return invalidMove;
    }

    // Configuration always as an int array, representing the number of seeds in each bowl and tray
    // Pay attention put always the human player bow (BH) first (as convention)
    // [ BH, BH, BH, BH, BH, BH, TH, BC, BC, BC, BC, BC, BC, TC ]
    // This method already check the assertion on the expectedStatus and return boardUpdated object
    // in case more assertion has to be done
    private BoardUpdated runConfiguration(int[] startingBoardStatus,
                                          int[] expectedBoardStatus,
                                          int moveFrom,
                                          Player player) {

        Board board = initializeBoard();
        board.buildBoard();

        // in order to test a move, I've to set the board in a particular state
        board.setBoardRepresentation(TestingUtility.createBoardRepresentation(startingBoardStatus,
                mHumanPlayer, mComputerPlayer));

        // then post a fake MoveAction on the mTurnContext
        MoveAction moveAction = new MoveAction(new Move(moveFrom, player));

        mTurnContext.push(moveAction);
        mTestBlockingObserver.waitUntilUpdateIsCalled();

        // and wait for the BoardUpdated action
        BoardUpdated boardUpdated = null;

        if (mTurnContext.peek() instanceof BoardUpdated) {
            boardUpdated = (BoardUpdated) mTurnContext.pop();

            checkExpectedStatus(boardUpdated.getLoad(), expectedBoardStatus);
        }

        cleanUp();

        return boardUpdated;
    }

    //each test need this call
    private Board initializeBoard() {
        Board board = Board.getInstance()
                .setup(mTurnContext, 6, 1)
                .registerPlayers(mPlayers);

        mTurnContext.addObserver(board);

        return board;
    }

    private void initialize() {
        mTurnContext = TurnContext.getInstance();
        mTurnContext.initialize();

        PlayerFactory playerFactory = new PlayerFactory(mTurnContext, 6, 1);

        try {
            mHumanPlayer = playerFactory.makePlayer(PlayerType.HUMAN, "Kasparov");
            mComputerPlayer = playerFactory.makePlayer(PlayerType.ARTIFICIAL_INTELLIGENCE, "Hal9000");
        } catch (PlayerBrainTypeUnknownException e) {
            e.printStackTrace();
        }

        mTestBlockingObserver = new TestBlockingObserver();

        mTurnContext.addObserver(mTestBlockingObserver);

        //Players
        mPlayers = new ArrayList<Player>();
        mPlayers.add(mHumanPlayer);
        mPlayers.add(mComputerPlayer);
    }

    private void cleanUp() {
        mTurnContext.deleteObservers();
    }

    private void checkExpectedStatus(ArrayList<Container> boardActualStatus, int[] expectedBoardStatus) {
        Iterator<Container> iterator = boardActualStatus.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            assertEquals(expectedBoardStatus[i], iterator.next().getNumberOfSeeds());
        }
    }
}
