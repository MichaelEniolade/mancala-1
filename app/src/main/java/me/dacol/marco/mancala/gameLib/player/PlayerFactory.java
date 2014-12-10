package me.dacol.marco.mancala.gameLib.player;

import me.dacol.marco.mancala.gameLib.gameController.TurnContext;
import me.dacol.marco.mancala.gameLib.player.brains.ArtificialIntelligence;
import me.dacol.marco.mancala.gameLib.player.brains.Brain;
import me.dacol.marco.mancala.gameLib.player.brains.Human;

public class PlayerFactory {

    private TurnContext mTurnContext;
    private int mNumberOfBowl;
    private int mNumberOfTray;

    public PlayerFactory(TurnContext turnContext, int numberOfBowl, int numberOfTray) {
        mTurnContext = turnContext;
        mNumberOfBowl = numberOfBowl;
        mNumberOfTray = numberOfTray;
    }


    /***
     * Factory to define which kind of player is going to play the game
     * @param playerType, taken from the constant define in PlayerType
     * @return the player with his own brain
     * @throws me.dacol.marco.mancala.gameLib.exceptions.PlayerBrainTypeUnknownException, if the kind of brain choosen is not available
     */
    public Player makePlayer(PlayerType playerType, String name) {

        Brain brain = null;

        switch (playerType) {
            case HUMAN:
                brain = new Human(mNumberOfBowl, mNumberOfTray);
                break;
            case ARTIFICIAL_INTELLIGENCE:
                brain = new ArtificialIntelligence(mNumberOfBowl, mNumberOfTray);
                break;
        }

        Player player = new Player(mTurnContext, brain, name);

        return player;
    }
}
