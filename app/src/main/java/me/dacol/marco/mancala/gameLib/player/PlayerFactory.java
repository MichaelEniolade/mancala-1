package me.dacol.marco.mancala.gameLib.player;

import me.dacol.marco.mancala.gameLib.exceptions.PlayerBrainTypeUnknownException;
import me.dacol.marco.mancala.gameLib.gameController.TurnContext;
import me.dacol.marco.mancala.gameLib.player.brains.ArtificialIntelligence;
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
     * @param type, taken from the constant define in PlayerType
     * @return the player with his own brain
     * @throws me.dacol.marco.mancala.gameLib.exceptions.PlayerBrainTypeUnknownException, if the kind of brain choosen is not available
     */
    public Player makePlayer(int type, String name)
            throws PlayerBrainTypeUnknownException {

        Player player = new Player(mTurnContext, name);

        switch (type) {
            case PlayerType.HUMAN:
                player.setBrain(new Human(player, mNumberOfBowl, mNumberOfTray));
                break;
            case PlayerType.ARTIFICIAL_INTELLIGENCE:
                player.setBrain(new ArtificialIntelligence(player, mNumberOfBowl, mNumberOfTray));
                break;
            default:
                throw new PlayerBrainTypeUnknownException("Type: " + type + " not known, check PlayerType class");
        }

        return player;
    }
}
