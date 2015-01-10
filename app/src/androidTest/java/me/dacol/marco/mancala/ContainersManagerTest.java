package me.dacol.marco.mancala;

import android.test.AndroidTestCase;

import java.util.ArrayList;

import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.board.ContainersManager;
import me.dacol.marco.mancala.gameLib.gameController.TurnContext;
import me.dacol.marco.mancala.gameLib.gameController.actions.Action;
import me.dacol.marco.mancala.gameLib.gameController.actions.BoardEmptyBowl;
import me.dacol.marco.mancala.gameLib.gameController.actions.BoardPutInTray;
import me.dacol.marco.mancala.gameLib.gameController.actions.BoardPutOneInContainer;
import me.dacol.marco.mancala.gameLib.player.Player;
import me.dacol.marco.mancala.gameLib.player.PlayerFactory;
import me.dacol.marco.mancala.gameLib.player.PlayerType;

public class ContainersManagerTest extends AndroidTestCase {

    private TurnContext mTurnContext;
    private Player mHumanPlayer;
    private Player mComputerPlayer;
    private ArrayList<Container> mContainers;
    private ContainersManager mContainersManager;

    //----> TEST CASES
    public void testInitialization() {
        initialize(new int[]{3,3,3,3,3,3,0,3,3,3,3,3,3,0});

        assertEquals(mContainers.size(), mContainersManager.getNumberContainers());

        for (int i = 0; i < mContainers.size(); i++) {
            assertEquals(mContainers.get(i).getNumberOfSeeds(), mContainersManager.getNumberOfSeedsOf(i));
            assertEquals(mContainers.get(i).getOwner(), mContainersManager.getOwnerOf(i));
        }
    }

    public void testAtomicMovesTracking() {
        initialize(new int[]{3,3,3,3,3,3,0,3,3,1,3,3,3,0});
        int startingBowl = 3;

        //make a move to getContainer the bowl 9 equal to one
        mContainersManager.emptyBowl(9);
        mContainersManager.putASeedIn(3);
        mContainersManager.putSeedsInTrayOf(mHumanPlayer, 9);
        ArrayList<Action> atomicMoves = mContainersManager.getAtomicMoves();

        assertTrue(atomicMoves.get(0) instanceof BoardEmptyBowl);
        assertEquals(9, atomicMoves.get(0).getLoad());

        assertTrue(atomicMoves.get(1) instanceof BoardPutOneInContainer);
        assertEquals(3, atomicMoves.get(1).getLoad());

        assertTrue(atomicMoves.get(2) instanceof BoardPutInTray);
        assertEquals(6, atomicMoves.get(2).getLoad());
        assertEquals(9, ((BoardPutInTray) atomicMoves.get(2)).getNumberOfSeeds());

    }

    public void testGettingOppositeBowl() {
        initialize(new int[]{3,3,3,3,3,3,0,3,3,1,3,3,3,0});
        int startingBowl = 3;

        //make a move to getContainer the bowl 9 equal to one
        mContainersManager.emptyBowl(9);
        mContainersManager.putASeedIn(9);

        assertEquals( mContainers.get(9).getNumberOfSeeds(),
                      mContainersManager.emptyOppositeBowl(startingBowl));
    }

    public void testEmptyBowl() {
        initialize(new int[]{3,3,3,0,3,3,0,3,3,1,3,3,3,0});

        int numberOfSeeds = mContainersManager.emptyBowl(3);

        assertEquals(3, numberOfSeeds);
        assertEquals(mContainers.get(3).getNumberOfSeeds(), mContainersManager.getNumberOfSeedsOf(3));
    }

    public void testGetOwner() {
        initialize(new int[]{3,3,3,0,3,3,0,3,3,1,3,3,3,0});

        assertEquals(mComputerPlayer, mContainersManager.getOwnerOf(9));
        assertEquals(mHumanPlayer, mContainersManager.getOwnerOf(3));
    }

    public void testTrayMethods() {
        initialize(new int[]{3,3,3,0,3,3,0,3,3,1,3,3,3,0});

        assertEquals(mContainers.get(6).getOwner(),
                mContainersManager.getTrayOf(mHumanPlayer).getOwner());

        mContainersManager.putSeedsInTrayOf(mComputerPlayer, 10);

        assertEquals(10, mContainersManager.getNumberOfSeedsInTrayOf(mComputerPlayer));
    }

    //----> HELPERS
    private void initialize(int[] boardRepresentation) {
        mTurnContext = TurnContext.getInstance();
        mTurnContext.initialize();

        PlayerFactory playerFactory = new PlayerFactory(mTurnContext, 6, 1);

        mHumanPlayer = playerFactory.makePlayer(PlayerType.HUMAN, "Kasparov");
        mComputerPlayer = playerFactory.makePlayer(PlayerType.ARTIFICIAL_INTELLIGENCE, "Hal9000");

        mContainers = TestingUtility.createBoardRepresentation(boardRepresentation,
                mHumanPlayer, mComputerPlayer);

        mContainersManager = new ContainersManager(mHumanPlayer, mComputerPlayer);

    }

}
