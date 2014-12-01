package me.dacol.marco.mancala;

import java.util.ArrayList;

import me.dacol.marco.mancala.gameLib.board.Bowl;
import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.board.Tray;
import me.dacol.marco.mancala.gameLib.player.Player;

public class TestingUtility {

    // Gets an array with the actual board status, B B B B B B T B B B B B B T
    public static ArrayList<Container> createBoardRepresentation(int[] seeds, Player humanPlayer, Player computerPlayer) {
        ArrayList<Container> boardRepresentation = new ArrayList<Container>();

        for (int i = 0; i < 6; i++) {
            boardRepresentation.add(bowlWithAnyNumberOfSeeds(seeds[i], humanPlayer));
        }

        boardRepresentation.add(trayWithAnyNumberOfSeeds(seeds[6], humanPlayer));

        for (int i = 7; i < 13; i++) {
            boardRepresentation.add(bowlWithAnyNumberOfSeeds(seeds[i], computerPlayer));
        }

        boardRepresentation.add(trayWithAnyNumberOfSeeds(seeds[13], computerPlayer));

        return  boardRepresentation;
    }

    private static Bowl bowlWithAnyNumberOfSeeds(int numberOfSeeds, Player player) {
        Bowl bowl = new Bowl(player);
        bowl.emptyBowl();
        for (int i = 0; i < numberOfSeeds; i++) {
            bowl.putOneSeed();
        }

        return bowl;
    }

    private static Tray trayWithAnyNumberOfSeeds(int numberOfSeeds, Player player) {
        Tray tray = new Tray(player);

        for (int i = 0; i < numberOfSeeds; i++) {
            tray.putOneSeed();
        }

        return tray;
    }
}
