package me.dacol.marco.mancala.gameLib.player;

import me.dacol.marco.mancala.gameLib.exception.playerBrainTypeUknownException;
import me.dacol.marco.mancala.gameLib.gameController.TurnContext;
import me.dacol.marco.mancala.gameLib.player.brains.ArtificialIntelligence;
import me.dacol.marco.mancala.gameLib.player.brains.Human;

public class PlayerFactory {

    /***
     * Factory to define which kind of player is going to play the game
     * @param type, taken from the constant define in PlayerType
     * @return the player with his own brain
     * @throws playerBrainTypeUknownException, if the kind of brain choosen is not available
     */
    public static Player makePlayer(int type, TurnContext turnContext)
            throws playerBrainTypeUknownException {

        Player player = new Player(turnContext);

        switch (type) {
            case PlayerType.HUMAN:
                player.setBrain(new Human());
                break;
            case PlayerType.ARTIFICIAL_INTELLIGENCE:
                player.setBrain(new ArtificialIntelligence());
                break;
            default:
                throw new playerBrainTypeUknownException();
        }

        return player;
    }
}
