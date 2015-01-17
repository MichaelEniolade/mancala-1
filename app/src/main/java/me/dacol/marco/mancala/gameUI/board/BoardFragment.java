package me.dacol.marco.mancala.gameUI.board;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import me.dacol.marco.mancala.R;
import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.gameController.Game;
import me.dacol.marco.mancala.gameLib.gameController.actions.ActivePlayer;
import me.dacol.marco.mancala.gameLib.gameController.actions.BoardUpdated;
import me.dacol.marco.mancala.gameLib.gameController.actions.EvenGame;
import me.dacol.marco.mancala.gameLib.gameController.actions.Winner;
import me.dacol.marco.mancala.gameLib.player.Player;
import me.dacol.marco.mancala.gameLib.player.PlayerType;
import me.dacol.marco.mancala.gameUI.OnFragmentInteractionListener;
import me.dacol.marco.mancala.gameUI.board.pieces.Bowl;
import me.dacol.marco.mancala.gameUI.board.pieces.PieceFactory;
import me.dacol.marco.mancala.gameUI.board.pieces.Tray;
import me.dacol.marco.mancala.preferences.PreferencesFragment;
import me.dacol.marco.mancala.statisticsLib.StatisticsHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link me.dacol.marco.mancala.gameUI.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BoardFragment extends Fragment implements Observer, View.OnClickListener {
    private static final String LOG_TAG = BoardFragment.class.getSimpleName();

    private ArrayList<OnFragmentInteractionListener> mPlayerBrainListeners;

    private ArrayList<TextView> mBoardTextViewRepresentation;

    private TextView mPlayerTurnText;
    private String mStartingPlayerName;

    private Game mGame;
    private boolean mIsHumanVsHuman;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static BoardFragment newInstance(boolean isHumanVsHuman) {
        Bundle args = new Bundle();
        args.putBoolean("isHvH", isHumanVsHuman);

        BoardFragment boardFragment = new BoardFragment();

        boardFragment.setArguments(args);

        return boardFragment;
    }

    public BoardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBoardTextViewRepresentation = null;
        mPlayerTurnText = null;
        mStartingPlayerName = null;

        Bundle args = getArguments();
        mIsHumanVsHuman = args.getBoolean("isHvH");

        mGame = Game.getInstance();

        // initialize the statisticsHelper
        StatisticsHelper statisticsHelper = StatisticsHelper.getInstance(getActivity());
        
        // initialize the game engine
        mGame.setup();

        // register the fragment and the statisticsHelper to the turnContext
        mGame.getTurnContext().addObserver(this);
        mGame.getTurnContext().addObserver(statisticsHelper);

        // add players to the game
        addPlayers();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_board, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // start the game after the view is setup, in this way i have no problem of null pointer exception
        // when populating the layout
        mGame.start();
    }

    // creates and add players to the game, recovering player name from the preferences
    private void addPlayers() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String playerName = defaultSharedPreferences.getString(PreferencesFragment.KEY_PLAYER_NAME, "Player"); // TODO put me in string.xml

        Player player = mGame.createPlayer(PlayerType.HUMAN, playerName);

        Player opponent;
        if (mIsHumanVsHuman) {
            opponent = mGame.createPlayer(PlayerType.HUMAN, "Opponent");    // TODO put me in string.xml
        } else {
            opponent = mGame.createPlayer(PlayerType.ARTIFICIAL_INTELLIGENCE, "Opponent"); //TODO put me in string.xml
        }

        connectBoardViewToPlayersBrain(player, opponent);

    }

    // attach players brain to the board, to capture the moves
    private void connectBoardViewToPlayersBrain(Player player, Player opponent) {
        attachHumanPlayerBrain(( OnFragmentInteractionListener) player.getBrain(), 0);

        if (mIsHumanVsHuman) {
            attachHumanPlayerBrain(( OnFragmentInteractionListener) opponent.getBrain(), 1);
        }
    }

    private void setupBoard(ArrayList<Container> boardRepresentation) {

        mBoardTextViewRepresentation = new ArrayList<TextView>();
        GridLayout.LayoutParams params;

        // Here I've to check if the chosen game is human vs human, i need to attach
        // the button to the player brain
        boolean isHumanVsHuman = false;
        if (boardRepresentation.get(7).getOwner().isHuman()) {
            isHumanVsHuman = true;
        }

        // make 6 bowls for each player on row 0 and 2
        // For Human Player, I'm always sure this is the human player
        for (int i = 0; i <= 5; i++) {
            Bowl bowl = PieceFactory.generateBowl(
                    getActivity(),
                    2,
                    i,
                    boardRepresentation.get(i).toString(),
                    i,
                    1,
                    isHumanVsHuman
            );

            bowl.setOnClickListener(this);

            mBoardTextViewRepresentation.add(bowl);
        }

        // Add the tray for player one
        Tray trayPlayerOne = PieceFactory.generateTray(
                getActivity(),
                1,
                5,
                boardRepresentation.get(6).toString(),
                1,
                isHumanVsHuman
        );

        mBoardTextViewRepresentation.add(trayPlayerOne);

        for (int i = 5; i >= 0; i--) {
            Bowl bowl = PieceFactory.generateBowl(
                    getActivity(),
                    0,
                    i,
                    boardRepresentation.get(12-1).toString(),
                    12-i,
                    2,
                    isHumanVsHuman
            );

            if (isHumanVsHuman) {
                bowl.setOnClickListener(this);
            }

            mBoardTextViewRepresentation.add(bowl);
        }

        // Add the tray for computer
        Tray trayPlayerTwo = PieceFactory.generateTray(
                getActivity(),
                1,
                0,
                boardRepresentation.get(13).toString(),
                2,
                isHumanVsHuman
        );

        mBoardTextViewRepresentation.add(trayPlayerTwo);

        // Something in the middle
        params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(1);
        params.columnSpec = GridLayout.spec(1,4);
        params.setGravity(Gravity.CENTER);

        TextView textView = new TextView(getActivity());
        textView.setText("Player turn: " + mStartingPlayerName); //TODO put me in string.xml
        textView.setLayoutParams(params);

        mPlayerTurnText = textView;

        // This show the starting status of the board
        addToBoardView((GridLayout) getView().findViewById(R.id.board_grid_layout));
    }

    private void addToBoardView(GridLayout board) {
        board.removeAllViews();

        for (TextView t : mBoardTextViewRepresentation) {
            board.addView(t);
        }

        board.addView(mPlayerTurnText);
    }

    private void updateBoard(ArrayList<Container> boardRepresentation) {
        if (mBoardTextViewRepresentation != null) {
            for (int i=0; i < boardRepresentation.size(); i++) {
                mBoardTextViewRepresentation.get(i).setText(boardRepresentation.get(i).toString());
            }
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        // I'm interested in the Action containing the board update only
        ArrayList<Container> containers = null;

        if (data instanceof ActivePlayer) {
            if (!boardIsInitialized()) {
                setupBoard(((ActivePlayer) data).getBoardRepresentation());
            }
            updatePlayingPlayerText(((ActivePlayer) data).getLoad().getName());
        } else if (data instanceof BoardUpdated) {
            containers = ((BoardUpdated) data).getLoad();
            updateBoard(containers);
        } else if (data instanceof Winner) {
            updateBoard(((Winner) data).getboardStatus());
            updatePlayingPlayerTextWithWinnerName(((Winner) data).getLoad().getName());
        } else if (data instanceof EvenGame) {
            updateBoard(((EvenGame) data).getLoad());
            updatePlayingPlayerText("The game ended, even...Shame on you!"); //TODO put me in string.xml
        }
    }

    private boolean boardIsInitialized() {
        return (mBoardTextViewRepresentation != null);
    }

    private void updatePlayingPlayerText(String name) {
        if (mPlayerTurnText != null) {
            mPlayerTurnText.setText("Player's turn: " + name); //TODO put me in string.xml
        } else {
            mStartingPlayerName = name;
        }
    }

    private void updatePlayingPlayerTextWithWinnerName(String name) {
        if (mPlayerTurnText != null) {
            mPlayerTurnText.setText("THE WINNER IS: " + name); //TODO put me in string.xml
        }
    }

    // Interact with the Human Player Brain
    @Override
    public void onClick(View v) {
        int bowlNumber = v.getId();
        if (bowlNumber < 6) {
            mPlayerBrainListeners.get(0)
                    .onFragmentInteraction(
                            OnFragmentInteractionListener.EventType.CHOSEN_BOWL, bowlNumber);
        } else {
            mPlayerBrainListeners.get(1)
                    .onFragmentInteraction(
                            OnFragmentInteractionListener.EventType.CHOSEN_BOWL, bowlNumber);
        }
    }

    /**
     * This is an interface for your brain, each time your brain pick a bowl on the screen, thanks to
     * this method the game library can know and refresh the view.
     * @param brain listening brain of the human player
     * @param playerNumber the position in which this listener has to be added
     */
    public void attachHumanPlayerBrain(OnFragmentInteractionListener brain, int playerNumber) {
        if (mPlayerBrainListeners == null) {
            mPlayerBrainListeners = new ArrayList<>();
        }
        mPlayerBrainListeners.add(playerNumber, brain);
    }
}