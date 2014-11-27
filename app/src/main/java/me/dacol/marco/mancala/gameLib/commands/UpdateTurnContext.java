package me.dacol.marco.mancala.gameLib.commands;

import me.dacol.marco.mancala.gameLib.gameController.TurnContext;
import me.dacol.marco.mancala.gameLib.gameController.actions.ActivePlayer;
import me.dacol.marco.mancala.gameLib.player.Player;

public class UpdateTurnContext implements Command {

    private TurnContext mTurnContext;
    private Player mPlayer;

    public UpdateTurnContext(Player player) {
        mTurnContext = TurnContext.getInstance();
        mPlayer = player;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void execute() {
        mTurnContext.push(new ActivePlayer(mPlayer));
    }
}
