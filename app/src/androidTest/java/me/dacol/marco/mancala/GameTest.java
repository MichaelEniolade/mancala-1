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

public class GameTest extends AndroidTestCase {
    /* what to test here?
     * After adding a player and starting a game the class post on action context the right action
     */
    private final static String LOG_TAG = "GameTest";

    private TurnContext mTurnContext;
    private Player mHumanPlayer;
    private Player mComputerPlayer;
    private TestBlockingObserver mTestBlockingObserver;
    private ArrayList<Player> mPlayers;

    // ---> TEST CASES
    public void testBoardInitialization() {
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

    public void testInvalidMove() {
        int[] startingStatus = new int[]{3,1,1,1,1,1,4,1,1,1,1,1,1,5};
        int[] expectedStatus = new int[]{3,1,1,1,1,1,4,1,1,1,1,1,1,5};
        int moveFrom = 6; // this is a tray
        Player playingPlayer = mHumanPlayer;

        InvalidMove invalidMove = runInvalidMoveConfiguration(startingStatus, expectedStatus, moveFrom, playingPlayer);

        assertTrue(playingPlayer == invalidMove.getPlayer());


    }

    public void testStandardMove() {
        int[] startingStatus = new int[]{3,1,1,1,1,1,4,1,1,1,1,1,1,5};
        int[] expectedStatus = new int[]{0,2,2,2,1,1,4,1,1,1,1,1,1,5};
        int moveFrom = 0;

        BoardUpdated boardUpdated = runConfiguration(startingStatus, expectedStatus, moveFrom);
        assertTrue(!boardUpdated.isGameEnded());
    }

    public void testStealingSeedsMove() {
        int[] startingStatus = new int[]{3,1,1,0,1,1,4,1,1,1,1,1,1,5};
        int[] expectedStatus = new int[]{0,2,2,0,1,1,6,1,1,1,0,1,1,5};
        int moveFrom = 0;

        BoardUpdated boardUpdated = runConfiguration(startingStatus, expectedStatus, moveFrom);
        assertTrue(!boardUpdated.isGameEnded());
    }

    public void testStealingMoveWithNoOpponentSeeds() {
        int[] startingStatus = new int[]{3,1,1,0,1,1,4,1,1,1,0,1,1,5};
        int[] expectedStatus = new int[]{0,2,2,0,1,1,5,1,1,1,0,1,1,5};
        int moveFrom = 0;

        BoardUpdated boardUpdated = runConfiguration(startingStatus, expectedStatus, moveFrom);
        assertTrue(!boardUpdated.isGameEnded());
    }

    public void testLastGameMove() {
        int[] startingStatus = new int[]{0,0,0,0,0,1,5,1,1,1,1,0,1,5};
        int[] expectedStatus = new int[]{0,0,0,0,0,0,6,1,1,1,1,0,1,5};
        int moveFrom = 5;

        BoardUpdated boardUpdated = runConfiguration(startingStatus, expectedStatus, moveFrom);
        assertTrue(boardUpdated.isGameEnded());
    }

    public void testPlayerPlayAgain() {
        int[] startingStatus = new int[]{0,0,0,1,0,1,5,1,1,1,1,0,1,5};
        int[] expectedStatus = new int[]{0,0,0,1,0,0,6,1,1,1,1,0,1,5};
        int moveFrom = 5;

        BoardUpdated boardUpdated = runConfiguration(startingStatus, expectedStatus, moveFrom);
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

        board.setBoardRepresentation(createBoardRepresentation(startingStatus));
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
        }

        return invalidMove;
    }

    // Configuration always as an int array, representing the number of seeds in each bowl and tray
    // Pay attention put always the human player bow (BH) first (as convention)
    // [ BH, BH, BH, BH, BH, BH, TH, BC, BC, BC, BC, BC, BC, TC ]
    // This method already check the assertion on the expectedStatus and return boardUpdated object
    // in case more assertion has to be done
    private BoardUpdated runConfiguration(int[] startingBoardStatus,
                                          int[] expectedBoardStatus, int moveFrom) {

        Board board = initializeBoard();
        board.buildBoard();

        // in order to test a move, I've to set the board in a particular state
        board.setBoardRepresentation(createBoardRepresentation(startingBoardStatus));

        // then post a fake MoveAction on the mTurnContext
        MoveAction moveAction = new MoveAction(new Move(moveFrom, mHumanPlayer));

        mTurnContext.push(moveAction);
        mTestBlockingObserver.waitUntilUpdateIsCalled();

        // and wait for the BoardUpdated action
        BoardUpdated boardUpdated = null;

        if (mTurnContext.peek() instanceof BoardUpdated) {
            boardUpdated = (BoardUpdated) mTurnContext.pop();

            checkExpectedStatus(boardUpdated.getLoad(), expectedBoardStatus);
        }

        return boardUpdated;
    }

    //each test need this call
    private Board initializeBoard() {
        try {
            initialize();
        } catch (PlayerBrainTypeUnknownException e) {
            e.printStackTrace();
        }

        Board board = Board.getInstance()
                .setup(mTurnContext, 6, 1)
                .registerPlayers(mPlayers);

        mTurnContext.addObserver(board);

        return board;
    }

    private void initialize() throws PlayerBrainTypeUnknownException {
        mTurnContext = TurnContext.getInstance();
        PlayerFactory playerFactory = new PlayerFactory(mTurnContext, 6, 1);
        mHumanPlayer = playerFactory.makePlayer(PlayerType.HUMAN, "Kasparov");
        mComputerPlayer = playerFactory.makePlayer(PlayerType.ARTIFICIAL_INTELLIGENCE, "Hal9000");
        mTestBlockingObserver = new TestBlockingObserver();

        mTurnContext.addObserver(mTestBlockingObserver);

        //Players
        mPlayers = new ArrayList<Player>();
        mPlayers.add(mHumanPlayer);
        mPlayers.add(mComputerPlayer);
    }

    // Gets an array with the actual board status, B B B B B B T B B B B B B T
    private ArrayList<Container> createBoardRepresentation(int[] seeds) {
        ArrayList<Container> boardRepresentation = new ArrayList<Container>();

        for (int i = 0; i < 6; i++) {
            boardRepresentation.add(bowlWithAnyNumberOfSeeds(seeds[i], mHumanPlayer));
        }

        boardRepresentation.add(trayWithAnyNumberOfSeeds(seeds[6], mHumanPlayer));

        for (int i = 7; i < 13; i++) {
            boardRepresentation.add(bowlWithAnyNumberOfSeeds(seeds[i], mComputerPlayer));
        }

        boardRepresentation.add(trayWithAnyNumberOfSeeds(seeds[13], mComputerPlayer));

        return  boardRepresentation;
    }

    private void checkExpectedStatus(ArrayList<Container> boardActualStatus, int[] expectedBoardStatus) {
        Iterator<Container> iterator = boardActualStatus.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            assertEquals(expectedBoardStatus[i], iterator.next().getNumberOfSeeds());
        }
    }

    private Bowl bowlWithAnyNumberOfSeeds(int numberOfSeeds, Player player) {
        Bowl bowl = new Bowl(player);
        bowl.emptyBowl();
        for (int i = 0; i < numberOfSeeds; i++) {
            bowl.putOneSeed();
        }

        return bowl;
    }

    private Tray trayWithAnyNumberOfSeeds(int numberOfSeeds, Player player) {
        Tray tray = new Tray(player);

        for (int i = 0; i < numberOfSeeds; i++) {
            tray.putOneSeed();
        }

        return tray;
    }
}
