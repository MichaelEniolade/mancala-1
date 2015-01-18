package me.dacol.marco.mancala.gameLib.board;

import java.util.ArrayList;

import me.dacol.marco.mancala.gameLib.gameController.actions.Action;
import me.dacol.marco.mancala.gameLib.gameController.actions.BoardEmptyBowl;
import me.dacol.marco.mancala.gameLib.gameController.actions.BoardPutInTray;
import me.dacol.marco.mancala.gameLib.gameController.actions.BoardPutOneInContainer;
import me.dacol.marco.mancala.gameLib.player.Player;

public class ContainersManager  {

    private ArrayList<Container> mContainers;
    public final static int NUMBER_OF_BOWLS = 6;
    private ArrayList<Action> mAtomicMoves;
    private int mSelectedTray;

    public ContainersManager(Player humanPlayer, Player opponentPlayer, ArrayList<Integer> boardRepresentation) {
        mContainers = new ArrayList<Container>();
        if (boardRepresentation == null) {
            // Create the six bowl_selected of the human player
            for (int position = 0; position < NUMBER_OF_BOWLS; position++) {
                mContainers.add(new Bowl(humanPlayer));
            }

            mContainers.add(new Tray(humanPlayer));

            for (int position = 0; position < NUMBER_OF_BOWLS; position++) {
                mContainers.add(new Bowl(opponentPlayer));
            }

            mContainers.add(new Tray(opponentPlayer));
        } else {
            // Create the six bowl_selected of the human player
            for (int position = 0; position < NUMBER_OF_BOWLS; position++) {
                mContainers.add(new Bowl(humanPlayer, boardRepresentation.get(position)));
            }

            mContainers.add(new Tray(humanPlayer, boardRepresentation.get(6)));

            for (int position = 7; position < 7 + NUMBER_OF_BOWLS; position++) {
                mContainers.add(new Bowl(opponentPlayer, boardRepresentation.get(position)));
            }

            mContainers.add(new Tray(opponentPlayer, boardRepresentation.get(13)));

        }
        mAtomicMoves = new ArrayList<Action>();
    }

    /**
     * Returns the Moves Tracking Array for an sequence of the changes made, and clear the list
     * @return
     */
    public ArrayList<Action> getAtomicMoves() {
        ArrayList<Action> arrayList = (ArrayList<Action>) mAtomicMoves.clone();
        mAtomicMoves.clear();

        return arrayList;
    }

    /**
     * Return the size of the array of Containers
     * @return
     */
    public int getNumberContainers() {
        return mContainers.size();
    }

    /**
     * Return the ArrayList of the Containers
     * @return
     */
    public ArrayList<Container> getRepresentation() {
        return mContainers;
    }

    /**
     * Returns the Container in that position
     * @param containerNumber
     * @return
     */
    public Container getContainer(int containerNumber) {
        return mContainers.get(containerNumber);
    }

    /**
     * Returns the owner of the container
     * @param containerNumber
     * @return
     */
    public Player getOwnerOf(int containerNumber) {
        return mContainers.get(containerNumber).getOwner();
    }

    /**
     * Returns the number of seeds the container, no matter which kind of container it is
     * @param containerNumber
     * @return
     */
    public int getNumberOfSeedsOf(int containerNumber) {
        return mContainers.get(containerNumber).getNumberOfSeeds();
    }

    /**
     * Get the tray of the specified player, it is based on the assumption that in this game there
     * are only two palyers and the tray position in the array is fixed
     * @param player
     * @return
     */
    public Tray getTrayOf(Player player) {
        // I know for how the arraylist its built that the tray are in the position 6 and 13
        // just look for the matching one
        int trayOnePosition = 6;
        int trayTwoPosition = 13;
        // Since I've only two player in this game, I guess it's tray number one
        mSelectedTray = trayOnePosition;

        if (mContainers.get(mSelectedTray).getOwner() != player) {
            mSelectedTray = trayTwoPosition;
        }

        return (Tray) mContainers.get(mSelectedTray);
    }

    /**
     * Get the number of seeds in a specific player tray
     * @param player
     * @return
     */
    public int getNumberOfSeedsInTrayOf(Player player) {
        return getTrayOf(player).getNumberOfSeeds();
    }

    /**
     * Puts a seed in a container, no matter which kind of container it is
     * @param containerNumber
     */
    public void putASeedIn(int containerNumber) {
        mContainers.get(containerNumber).putOneSeed();
        int newNumberOfSeeds = mContainers.get(containerNumber).getNumberOfSeeds();
        mAtomicMoves.add(new BoardPutOneInContainer(containerNumber, newNumberOfSeeds));
    }

    /**
     * Empty the indicated bowl, add the moves to the moves tracker and return the number of seeds in
     * that bowl
     * @param containerNumber
     * @return
     */
    public int emptyBowl(int containerNumber) {
        int numberOfSeeds = ((Bowl)mContainers.get(containerNumber)).emptyBowl();
        mAtomicMoves.add(new BoardEmptyBowl(containerNumber, false, 0));

        return numberOfSeeds;
    }

    /**
     * Empty the opposite bowl in the board, with respect of the actual selected bowl
     * @param bowlNumber
     * @return
     */
    public int emptyOppositeBowl(int bowlNumber) {
        // The last bowl_selected is in position 12, the first one in position 0
        // So in order to getContainer the opponent bowl_selected I've to getContainer
        // the 12 - actual bowl_selected position
        int containerNumber = 12 - bowlNumber;
        Bowl bowl = ((Bowl) mContainers.get(containerNumber));
        int numberOfSeeds = bowl.emptyBowl();

        mAtomicMoves.add(new BoardEmptyBowl(containerNumber, true, 0));

        return numberOfSeeds;

    }

    /**
     * Puts any number of seed in the specified player tray
     * @param player
     * @param numberOfSeeds
     */
    public void putSeedsInTrayOf(Player player, int numberOfSeeds) {
        this.getTrayOf(player).putSeeds(numberOfSeeds);
        int newNumberOfSeeds = getTrayOf(player).getNumberOfSeeds();
        mAtomicMoves.add(new BoardPutInTray(mSelectedTray, numberOfSeeds, newNumberOfSeeds));
    }

    /**
     * Sets a previous representation, useful for resuming a game
     * @param boardRepresentation
     */
    public void setRepresenation(ArrayList<Container> boardRepresentation) {
        mContainers = boardRepresentation;
    }

}