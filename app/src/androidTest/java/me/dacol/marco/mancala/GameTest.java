package me.dacol.marco.mancala;

import android.test.AndroidTestCase;

import java.util.ArrayList;

import me.dacol.marco.mancala.gameLib.board.Board;
import me.dacol.marco.mancala.gameLib.board.Bowl;
import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.board.Move;
import me.dacol.marco.mancala.gameLib.board.Tray;
import me.dacol.marco.mancala.gameLib.exceptions.PlayerBrainTypeUnknownException;
import me.dacol.marco.mancala.gameLib.gameController.TurnContext;
import me.dacol.marco.mancala.gameLib.gameController.actions.BoardUpdated;
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

    private void initialize() throws PlayerBrainTypeUnknownException {
        mTurnContext = TurnContext.getInstance();
        PlayerFactory playerFactory = new PlayerFactory(mTurnContext, 6, 1);
        mHumanPlayer = playerFactory.makePlayer(PlayerType.HUMAN, "john");
        mComputerPlayer = playerFactory.makePlayer(PlayerType.ARTIFICIAL_INTELLIGENCE, "Hal9000");
        mTestBlockingObserver = new TestBlockingObserver();

        mTurnContext.addObserver(mTestBlockingObserver);

        //Players
        mPlayers = new ArrayList<Player>();
        mPlayers.add(mHumanPlayer);
        mPlayers.add(mComputerPlayer);

    }

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

    /*
     * Gets an array with the actual board status, B B B B B B T B B B B B B T
     */
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

    //TEST CASES
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

    public void testStandardMove() {
        Board board = initializeBoard();
        board.buildBoard();

        int[] boardRepresentation = new int[]{3,1,1,1,1,1,4,1,1,1,1,1,1,5};
        int[] expectedRepresentation = new int[]{0,2,2,2,1,1,4,1,1,1,1,1,1,5};

        int moveFrom = 0;

        // in order to test a move, I've to set the board in a particular state
        board.setBoardRepresentation(createBoardRepresentation(
                boardRepresentation)
        );

        // then post a fake MoveAction on the mTurnContext
        MoveAction moveAction = new MoveAction(
                new Move(moveFrom,mHumanPlayer)
        );
        mTurnContext.push(moveAction);
        mTestBlockingObserver.waitUntilUpdateIsCalled();

        // and wait for the BoardUpdated action
        BoardUpdated boardUpdated;

        assertTrue(mTurnContext.peek() instanceof BoardUpdated);

        if (mTurnContext.peek() instanceof BoardUpdated) {
            boardUpdated = (BoardUpdated) mTurnContext.pop();

            int i = 0;
            for (Container c : (ArrayList<Container>) boardUpdated.getLoad()) {
                assertEquals(expectedRepresentation[i], c.getNumberOfSeeds());
            }

            assertTrue(!boardUpdated.isGameEnded());
        }

    }

    private void testStealingSeedsMove() {
        Board board = initializeBoard();
        board.buildBoard();

        int[] boardRepresentation = new int[]{3,1,1,0,1,1,4,1,1,1,1,1,1,5};
        int[] expectedRepresentation = new int[]{0,2,2,0,1,1,6,1,1,1,0,1,1,5};

        int moveFrom = 0;

        // in order to test a move, I've to set the board in a particular state
        board.setBoardRepresentation(createBoardRepresentation(
                        boardRepresentation)
        );

        // then post a fake MoveAction on the mTurnContext
        MoveAction moveAction = new MoveAction(
                new Move(moveFrom,mHumanPlayer)
        );
        mTurnContext.push(moveAction);
        mTestBlockingObserver.waitUntilUpdateIsCalled();

        // and wait for the BoardUpdated action
        BoardUpdated boardUpdated;

        assertTrue(mTurnContext.peek() instanceof BoardUpdated);

        if (mTurnContext.peek() instanceof BoardUpdated) {
            boardUpdated = (BoardUpdated) mTurnContext.pop();

            int i = 0;
            for (Container c : (ArrayList<Container>) boardUpdated.getLoad()) {
                assertEquals(expectedRepresentation[i], c.getNumberOfSeeds());
            }

            assertTrue(!boardUpdated.isGameEnded());
        }
    }

    private void testStealingMoveWithNoOpponentSeeds() {
        Board board = initializeBoard();
        board.buildBoard();

        int[] boardRepresentation = new int[]{3,1,1,0,1,1,4,1,1,1,1,0,1,5};
        int[] expectedRepresentation = new int[]{0,2,2,0,1,1,5,1,1,1,0,1,1,5};

        int moveFrom = 0;

        // in order to test a move, I've to set the board in a particular state
        board.setBoardRepresentation(createBoardRepresentation(
                        boardRepresentation)
        );

        // then post a fake MoveAction on the mTurnContext
        MoveAction moveAction = new MoveAction(
                new Move(moveFrom,mHumanPlayer)
        );
        mTurnContext.push(moveAction);
        mTestBlockingObserver.waitUntilUpdateIsCalled();

        // and wait for the BoardUpdated action
        BoardUpdated boardUpdated;

        assertTrue(mTurnContext.peek() instanceof BoardUpdated);

        if (mTurnContext.peek() instanceof BoardUpdated) {
            boardUpdated = (BoardUpdated) mTurnContext.pop();

            int i = 0;
            for (Container c : (ArrayList<Container>) boardUpdated.getLoad()) {
                assertEquals(expectedRepresentation[i], c.getNumberOfSeeds());
            }

            assertTrue(!boardUpdated.isGameEnded());
        }

    }

    private void testLastGameMove() {
        Board board = initializeBoard();
        board.buildBoard();

        int[] boardRepresentation = new int[]{0,0,0,0,0,1,5,1,1,1,1,0,1,5};
        int[] expectedRepresentation = new int[]{0,0,0,0,0,0,6,1,1,1,0,1,1,5};

        int moveFrom = 5;

        // in order to test a move, I've to set the board in a particular state
        board.setBoardRepresentation(createBoardRepresentation(
                        boardRepresentation)
        );

        // then post a fake MoveAction on the mTurnContext
        MoveAction moveAction = new MoveAction(
                new Move(moveFrom,mHumanPlayer)
        );
        mTurnContext.push(moveAction);
        mTestBlockingObserver.waitUntilUpdateIsCalled();

        // and wait for the BoardUpdated action
        BoardUpdated boardUpdated;

        assertTrue(mTurnContext.peek() instanceof BoardUpdated);

        if (mTurnContext.peek() instanceof BoardUpdated) {
            boardUpdated = (BoardUpdated) mTurnContext.pop();

            int i = 0;
            for (Container c : (ArrayList<Container>) boardUpdated.getLoad()) {
                assertEquals(expectedRepresentation[i], c.getNumberOfSeeds());
            }

            assertTrue(boardUpdated.isGameEnded());

        }

    }
}