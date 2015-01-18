package me.dacol.marco.mancala.gameUI.board;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import me.dacol.marco.mancala.R;
import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.gameController.Game;
import me.dacol.marco.mancala.gameLib.gameController.actions.Action;
import me.dacol.marco.mancala.gameLib.gameController.actions.ActivePlayer;
import me.dacol.marco.mancala.gameLib.gameController.actions.BoardUpdated;
import me.dacol.marco.mancala.gameLib.gameController.actions.EvenGame;
import me.dacol.marco.mancala.gameLib.gameController.actions.Winner;
import me.dacol.marco.mancala.gameLib.player.Player;
import me.dacol.marco.mancala.gameLib.player.PlayerType;
import me.dacol.marco.mancala.gameUI.OnFragmentInteractionListener;

import me.dacol.marco.mancala.gameUI.animatior.AsyncResponse;
import me.dacol.marco.mancala.gameUI.animatior.BowlAnimator;

import me.dacol.marco.mancala.logging.Logger;
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
public class BoardFragment extends Fragment implements Observer, View.OnClickListener, AsyncResponse {
    private static final String LOG_TAG = BoardFragment.class.getSimpleName();

    private ArrayList<OnFragmentInteractionListener> mPlayerBrainListeners;

    private ArrayList<TextView> mBoardTextViewRepresentation;

    private String mStartingPlayerName;
    private BowlAnimator mBowlAnimator;
    private ArrayList<ArrayList<Action>> mAtomicMovesQueue;

    private Game mGame;

    private boolean mIsHumanVsHuman;
    private boolean mAreAnimationActive;
    private OnFragmentInteractionListener mFragmentInteractionListener;
    private ArrayList<ArrayList<Container>> mBoardRepresentationQueue;

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

    @Override
    public void onAttach(Activity activity) {
        setTargetFragment(this, 0);
        mFragmentInteractionListener = (OnFragmentInteractionListener) activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAtomicMovesQueue = new ArrayList<>();
        mBoardRepresentationQueue = new ArrayList<>();

        Bundle args = getArguments();
        mIsHumanVsHuman = args.getBoolean("isHvH");

        mGame = Game.getInstance();
        
        // initialize the game engine
        mGame.setup();

        // add players to the game
        addPlayers();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBoardTextViewRepresentation = null;
        mStartingPlayerName = null;

        View rootView = inflater.inflate(R.layout.fragment_board, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mAreAnimationActive = defaultSharedPreferences.getBoolean(PreferencesFragment.KEY_PLAYER_ANIMATION, false);

        // initialize the statisticsHelper
        // register the fragment and the statisticsHelper to the turnContext
        mGame.getTurnContext().addObserver(this);

        StatisticsHelper statisticsHelper = StatisticsHelper.getInstance(getActivity());
        mGame.getTurnContext().addObserver(statisticsHelper);

        mGame.start(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // to support standby and destoy
        // transform the boardRep in a array of int
        ArrayList<Integer> boardRepresentation = new ArrayList<>();
        for (TextView t : mBoardTextViewRepresentation) {
            boardRepresentation.add( Integer.valueOf( t.getText().toString() ) );
        }

        // save tha playing player number
        int playingPlayer = mGame.getPlayingPlayer();
        // save the turn number
        int turnNumber = mGame.getTurnNumber() - 1;

        // put all int the outState Bundle
        outState.putIntegerArrayList("boardRepresentation", boardRepresentation);
        outState.putInt("playingPlayer", playingPlayer);
        outState.putInt("turnNumber", turnNumber);
    }

    @Override
    public void onDetach() {
        Logger.v(LOG_TAG, "deattached");
        mBoardTextViewRepresentation = null;

        // need to delete the observers when detaching the fragment otherwise it will crash next
        // time it is fired up, because the setup board will be called earlier than the
        // onActivityCreated method, and the Context provided by getActivity() will result null
        mGame.getTurnContext().deleteObservers();
        mFragmentInteractionListener = null;
        super.onDetach();
    }

    // creates and add players to the game, recovering player name from the preferences
    private void addPlayers() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String playerName = defaultSharedPreferences.getString(PreferencesFragment.KEY_PLAYER_NAME,
                getResources().getString(R.string.player_default_name));

        Player player = mGame.createPlayer(PlayerType.HUMAN, playerName);

        Player opponent;
        if (mIsHumanVsHuman) {
            opponent = mGame.createPlayer(PlayerType.HUMAN,
                    getResources().getString(R.string.opponent_name));
        } else {
            opponent = mGame.createPlayer(PlayerType.ARTIFICIAL_INTELLIGENCE,
                    getResources().getString(R.string.opponent_name));
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
                    isHumanVsHuman,
                    getResources().getDimension(R.dimen.container_dsp_width_dimension)
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
                isHumanVsHuman,
                getActivity().getResources().getDimension(R.dimen.container_dsp_width_dimension)
        );

        mBoardTextViewRepresentation.add(trayPlayerOne);

        for (int i = 7; i <= 12; i++) {
            Bowl bowl = PieceFactory.generateBowl(
                    getActivity(),
                    0,
                    12-i,
                    boardRepresentation.get(i).toString(),
                    i,
                    2,
                    isHumanVsHuman,
                    getActivity().getResources().getDimension(R.dimen.container_dsp_width_dimension)
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
                isHumanVsHuman,
                getResources().getDimension(R.dimen.container_dsp_width_dimension)
        );

        mBoardTextViewRepresentation.add(trayPlayerTwo);

        TextView textView = (TextView) getView().findViewById(R.id.player_turn_text_view);
        textView.setText(getResources().getString(R.string.player_turn) + mStartingPlayerName);

        TextView playerName = (TextView) getView().findViewById(R.id.player_name);
        playerName.setText(boardRepresentation.get(0).getOwner().getName());

        OpponentLabel opponentName = (OpponentLabel) getView().findViewById(R.id.opponent_name);
        opponentName.setIsHumanVsHuman(isHumanVsHuman);
        opponentName.setText(boardRepresentation.get(7).getOwner().getName());

        // This show the starting status of the board

        addToBoardView((GridLayout) getView().findViewById(R.id.board_grid_layout));
    }

    private void addToBoardView(GridLayout board) {
        for (TextView t : mBoardTextViewRepresentation) {
            board.addView(t);
        }
    }

    private void updateBoard(ArrayList<Container> boardRepresentation, ArrayList<Action> atomicMoves) {
        // User can choose as preference if the animation system is active or not
        if (!mAreAnimationActive || !mIsHumanVsHuman) {
            if (mBoardTextViewRepresentation != null) {
                for (int i = 0; i < boardRepresentation.size(); i++) {
                    mBoardTextViewRepresentation.get(i).setText(boardRepresentation.get(i).toString());
                }
            }
        } else {
            // never used so start a new one
            if ((mBowlAnimator == null) || (mBowlAnimator.getStatus() == AsyncTask.Status.FINISHED)) {
                mBowlAnimator = new BowlAnimator(mBoardTextViewRepresentation, this);
            }

            // check the actual state of the animator thread, if is running add the update to the queue
            if (mBowlAnimator.getStatus() == AsyncTask.Status.PENDING) {
                Logger.v(LOG_TAG, "pending");
                mBowlAnimator.execute(atomicMoves, boardRepresentation);
            } else if (mBowlAnimator.getStatus() == AsyncTask.Status.RUNNING) {
                Logger.v(LOG_TAG, "running");
                mAtomicMovesQueue.add(atomicMoves);
                mBoardRepresentationQueue.add(boardRepresentation);
            }
        }
    }

    private void updatePlayingPlayerText(String name) {
        TextView playerTurnText = (TextView) getView().findViewById(R.id.player_turn_text_view);
        playerTurnText.setText(name + getResources().getString(R.string.player_turn));
        mStartingPlayerName = name;
    }

    /**
     * When the game is ended, on the board is visualized a special text instead of the turn player
     * and a button for restart the game
     * @param name name of the winner player or null if even game
     * @param winner flag true if there is a winner false if even game
     */
    private void updateBoardForEndGame(String name, boolean winner) {
        // Publish the winners name
        TextView playerTurnText = (TextView) getView().findViewById(R.id.player_turn_text_view);
        if (winner) {
            playerTurnText.setText(name + getResources().getString(R.string.the_winner_is));
        } else {
            updatePlayingPlayerText(getResources().getString(R.string.the_game_ended_even));
        }

        // Move the textView aside
        GridLayout.LayoutParams textViewParams = new GridLayout.LayoutParams();
        textViewParams.rowSpec = GridLayout.spec(1);
        textViewParams.columnSpec = GridLayout.spec(1,2);
        textViewParams.setGravity(Gravity.CENTER);

        playerTurnText.setLayoutParams(textViewParams);

        // show the ImageButton for another game
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(1);
        params.columnSpec = GridLayout.spec(3,2);
        params.setGravity(Gravity.CENTER);
        params.width = (int) getResources().getDimension(R.dimen.bowl_button_width);
        params.height = (int) getResources().getDimension(R.dimen.bowl_button_width);

        ImageButton imageButton = new ImageButton(getActivity());
        imageButton.setLayoutParams(params);
        imageButton.setImageResource(R.drawable.play_again);
        imageButton.setOnClickListener(this);

        // Add the imageButton to the boardLayout
        GridLayout gridLayout = (GridLayout) getView().findViewById(R.id.board_grid_layout);
        gridLayout.addView(imageButton);
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

    public boolean isGameHumanVsHuman() {
        return mIsHumanVsHuman;
    }
    private boolean isBoardInitialized() {
        return (mBoardTextViewRepresentation != null);
    }

    // ----- INTERFACES METHOD
    @Override
    public void update(Observable observable, Object data) {
        // I'm interested in the Action containing the board update only
        ArrayList<Container> containers = null;

        if (data instanceof ActivePlayer) {
            if (!isBoardInitialized()) {
                setupBoard(((ActivePlayer) data).getBoardRepresentation());
            }
            updatePlayingPlayerText(((ActivePlayer) data).getLoad().getName());
        } else if (data instanceof BoardUpdated) {
            containers = ((BoardUpdated) data).getLoad();
            updateBoard(containers, ((BoardUpdated) data).getAtomicMoves() );
        } else if (data instanceof Winner) {
            Winner winner = (Winner) data;
            updateBoard(winner.getboardStatus(), winner.getAtomicMoves() );
            updateBoardForEndGame(((Winner) data).getLoad().getName(), true);
        } else if (data instanceof EvenGame) {
            EvenGame evenGame = (EvenGame) data;
            updateBoard(evenGame.getLoad(), evenGame.getAtomicMoves() );
            updateBoardForEndGame(null, false);
        }
    }

    // Interact with the Human Player Brain
    @Override
    public void onClick(View v) {
        if (v instanceof Bowl) {
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
        } else if (v instanceof ImageButton) {
            mGame.getTurnContext().deleteObservers();
            mFragmentInteractionListener.onFragmentInteraction(
                    OnFragmentInteractionListener.EventType.RESTART_GAME_BUTTON_PRESSED, this);
        }
    }

    @Override
    public void deliver() {
        if (mAtomicMovesQueue.size() > 0) {
            ArrayList<Action> atomicMoves = mAtomicMovesQueue.get(0);
            ArrayList<Container> boardRepresentation = mBoardRepresentationQueue.get(0);
            mAtomicMovesQueue.remove(0);
            mBoardRepresentationQueue.remove(0);
            updateBoard(boardRepresentation, atomicMoves);
        }
    }
}