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


public class MainActivity extends Activity implements OnFragmentInteractionListener {

    private Game mGame;
    private TurnContext mTurnContext;
    private boolean mIsHumanVsHumanGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIsHumanVsHumanGame = false;

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

    private void startNewGame() {
        BoardFragment boardFragment = BoardFragment.newInstance(Game.getInstance(), mIsHumanVsHumanGame);

        // change the visualized fragment
        popUpNewFragment(boardFragment);
    }

    private void popUpNewFragment(Fragment boardFragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, boardFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(EventType event, Object data) {
        if (event == EventType.NEW_GAME_BUTTON_PRESSED) {
            startNewGame();
        } else if (event == EventType.TOGGLE_ENEMY_KIND) {
            toggleEnemyKind();
        }
    }

    private void toggleEnemyKind() {
        mIsHumanVsHumanGame = !mIsHumanVsHumanGame;
    }
}
