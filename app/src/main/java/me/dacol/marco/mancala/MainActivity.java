package me.dacol.marco.mancala;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import me.dacol.marco.mancala.gameUI.NewGameFragment;
import me.dacol.marco.mancala.gameUI.OnFragmentInteractionListener;
import me.dacol.marco.mancala.gameUI.board.BoardFragment;
import me.dacol.marco.mancala.preferences.PreferencesFragment;
import me.dacol.marco.mancala.statisticsLib.DBContracts;
import me.dacol.marco.mancala.statisticsUI.StatisticsFragment;


public class MainActivity extends Activity implements OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, NewGameFragment.newInstance())
                    .commit();
        }
    }

    private void startNewGame(String gameType) {
        boolean isHumanVsHumanGame = (gameType.equals( DBContracts.GAME_TYPE_HvH ));
        BoardFragment boardFragment = BoardFragment.newInstance(isHumanVsHumanGame);

        // change the visualized fragment
        popUpNewFragment(boardFragment);
    }

    private void openStatistics() {
        StatisticsFragment statisticsFragment = StatisticsFragment.newInstance();
        popUpNewFragment(statisticsFragment);
    }

    private void openPreferences() {
        PreferencesFragment preferenceFragment = new PreferencesFragment();
        popUpNewFragment(preferenceFragment);
    }

    private void restartNewGame(BoardFragment activeBoardFragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().remove(activeBoardFragment);
        fragmentManager.popBackStack();
        fragmentManager.beginTransaction().commit();

        BoardFragment newBoardFragment = BoardFragment.newInstance(activeBoardFragment.isGameHumanVsHuman());
        popUpNewFragment(newBoardFragment);
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
        } else if (event == EventType.PREFERENCES_BUTTON_PRESSED) {
            openPreferences();
        } else if (event == EventType.RESTART_GAME_BUTTON_PRESSED) {
            restartNewGame((BoardFragment) data);
        }
    }
}
