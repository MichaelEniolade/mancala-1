package me.dacol.marco.mancala;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import me.dacol.marco.mancala.gameLib.gameController.Game;
import me.dacol.marco.mancala.gameLib.gameController.TurnContext;
import me.dacol.marco.mancala.gameUI.NewGameFragment;
import me.dacol.marco.mancala.gameUI.OnFragmentInteractionListener;
import me.dacol.marco.mancala.gameUI.board.BoardFragment;
import me.dacol.marco.mancala.statisticsLib.DBContracts;
import me.dacol.marco.mancala.statisticsUI.StatisticsFragment;


public class MainActivity extends Activity implements OnFragmentInteractionListener {

    private Game mGame;
    private TurnContext mTurnContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().hide();

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new NewGameFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startNewGame(String gameType) {
        boolean isHumanVsHumanGame = (gameType.equals( DBContracts.GAME_TYPE_HvH ));
        BoardFragment boardFragment = BoardFragment.newInstance(Game.getInstance(), isHumanVsHumanGame, this);

        // change the visualized fragment
        popUpNewFragment(boardFragment);
    }

    private void openStatistics() {
        StatisticsFragment statisticsFragment = StatisticsFragment.newInstance(null, null);

        popUpNewFragment(statisticsFragment);
    }

    private void popUpNewFragment(Fragment boardFragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, boardFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(EventType event, Object data) {
        if (event == EventType.NEW_HvC_GAME_BUTTON_PRESSED) {
            startNewGame(DBContracts.GAME_TYPE_HvC);
        } else if (event == EventType.NEW_HvH_GAME_BUTTON_PRESSED) {
            startNewGame(DBContracts.GAME_TYPE_HvH);
        } else if (event == EventType.STATISTICS_BUTTON_PRESSED) {
            openStatistics();
        }
    }

}
