package me.dacol.marco.mancala.gameUI.board;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import me.dacol.marco.mancala.R;
import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.exceptions.ToManyPlayerException;
import me.dacol.marco.mancala.gameLib.gameController.Game;
import me.dacol.marco.mancala.gameLib.gameController.actions.ActivePlayer;
import me.dacol.marco.mancala.gameLib.gameController.actions.BoardUpdated;
import me.dacol.marco.mancala.gameLib.gameController.actions.EvenGame;
import me.dacol.marco.mancala.gameLib.gameController.actions.Winner;
import me.dacol.marco.mancala.gameLib.player.PlayerType;
import me.dacol.marco.mancala.gameUI.OnFragmentInteractionListener;

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

    private ArrayList<Container> mStartingBoard;
    private TextView mPlayerTurnText;
    private String mStartingPlayerName;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static BoardFragment newInstance(Game game, boolean isHumanVsHuman) {
        BoardFragment fragment = new BoardFragment();

        // Initialize
        game.setup();
        game.getTurnContext().addObserver(fragment);

        addPlayers(game, fragment, isHumanVsHuman);
        game.start();
        return fragment;
    }

    private static void addPlayers(Game game, BoardFragment fragment, boolean isHumanVsHuman) {
        // add players to the game
        try {
            game.createPlayer(PlayerType.HUMAN, "1");

            if (isHumanVsHuman) {
                game.createPlayer(PlayerType.HUMAN, "2");
            } else {
                game.createPlayer(PlayerType.ARTIFICIAL_INTELLIGENCE, "2");
            }

        } catch (ToManyPlayerException e) {
            // TODO falla risalire ancora fino ad arrivare alla main activity
            e.printStackTrace();
        }

        connectBoardViewToPlayersBrain(fragment, game, isHumanVsHuman);

    }

    private static void connectBoardViewToPlayersBrain(BoardFragment boardFragment, Game game, boolean isHumanVsHuman) {
        // attach players to the board
        boardFragment.attachHumanPlayerBrain(
                (OnFragmentInteractionListener) game.getPlayerNumber(0).getBrain()
                , 0);

        if (isHumanVsHuman) {
            boardFragment.attachHumanPlayerBrain(
                    (OnFragmentInteractionListener) game.getPlayerNumber(1).getBrain()
                    , 1);
        }
    }

    public BoardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupBoard(mStartingBoard);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_board, container, false);
        GridLayout board = (GridLayout) rootView.findViewById(R.id.boardView);

        if (mStartingBoard != null) {
            addToBoardView(board);
        }

        return rootView;
    }

    //TODO: serialize the event, so the UI can be updated more slowly
    private void setupBoard(ArrayList<Container> boardRepresentation) {

        mBoardTextViewRepresentation = new ArrayList<TextView>();

        GridLayout.LayoutParams params;

        // make 6 bowls for each player on row 0 and 2
        // For Human Player, I'm always sure this is the human player
        for (int i = 0; i <= 5; i++) {
            // set layout parameters
            params = new GridLayout.LayoutParams();
            params.rowSpec = GridLayout.spec(2);
            params.columnSpec = GridLayout.spec(i);

            // create the button
            Button button = new Button(getActivity());
            button.setLayoutParams(params);
            button.setText(boardRepresentation.get(i).toString());
            button.setOnClickListener(this);
            button.setId(i);

            GradientDrawable buttonShape = (GradientDrawable) getResources().getDrawable( R.drawable.bowl );
            buttonShape.setColor( getResources().getColor(R.color.playerOneBowl));

            if (Build.VERSION.SDK_INT >= 16) {
                button.setBackground(buttonShape);
            } else {
                button.setBackgroundDrawable( buttonShape );
            }

            mBoardTextViewRepresentation.add(button);
        }

        // Add the tray for player one
        params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(1);
        params.columnSpec = GridLayout.spec(5);
        params.setGravity(Gravity.FILL);

        TextView trayPlayerOne = new TextView(getActivity());
        trayPlayerOne.setLayoutParams(params);
        trayPlayerOne.setText(boardRepresentation.get(6).toString());
        trayPlayerOne.setGravity(Gravity.CENTER);

        GradientDrawable trayOneShape = (GradientDrawable) getResources().getDrawable( R.drawable.tray );
        trayOneShape.setColor(getResources().getColor(R.color.playerOneTray));

        if (Build.VERSION.SDK_INT >= 16) {
            trayPlayerOne.setBackground( trayOneShape );
        } else {
            trayPlayerOne.setBackgroundDrawable( trayOneShape );
        }

        mBoardTextViewRepresentation.add(trayPlayerOne);

        // Here I've to check if the choosen game is human vs human, i need to attach
        // the button to the player brain
        boolean isHumanVsHuman = false;
        if (boardRepresentation.get(7).getOwner().isHuman()) {
            isHumanVsHuman = true;
        }

        for (int i = 5; i >= 0; i--) {
            // set layout parameters
            params = new GridLayout.LayoutParams();
            params.rowSpec = GridLayout.spec(0);
            params.columnSpec = GridLayout.spec(i);

            // create the button
            Button button = new Button(getActivity());
            button.setLayoutParams(params);
            button.setText(boardRepresentation.get(12-i).toString());
            button.setId(12-i);
            if (isHumanVsHuman) button.setOnClickListener(this);

            GradientDrawable buttonShape = (GradientDrawable) getResources().getDrawable( R.drawable.bowl );
            buttonShape.setColor( getResources().getColor(R.color.playerTwoBowl));

            if (Build.VERSION.SDK_INT >= 16) {
                button.setBackground(buttonShape);
            } else {
                button.setBackgroundDrawable( buttonShape );
            }

            mBoardTextViewRepresentation.add(button);
        }

        // Add the tray for computer
        params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(1);
        params.columnSpec = GridLayout.spec(0);
        params.setGravity(Gravity.FILL);

        TextView trayPlayerTwo = new TextView(getActivity());
        trayPlayerTwo.setLayoutParams(params);
        trayPlayerTwo.setText(boardRepresentation.get(13).toString());
        trayPlayerTwo.setGravity(Gravity.CENTER);

        GradientDrawable trayTwoShape = (GradientDrawable) getResources().getDrawable( R.drawable.tray );
        trayTwoShape.setColor(getResources().getColor(R.color.playerTwoTray));

        if (Build.VERSION.SDK_INT >= 16) {
            trayPlayerTwo.setBackground( trayTwoShape );
        } else {
            trayPlayerTwo.setBackgroundDrawable( trayTwoShape );
        }

        mBoardTextViewRepresentation.add(trayPlayerTwo);

        // Something in the middle
        params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(1);
        params.columnSpec = GridLayout.spec(1,4);
        params.setGravity(Gravity.CENTER);

        TextView textView = new TextView(getActivity());
        textView.setText("Player turn: " + mStartingPlayerName);
        textView.setLayoutParams(params);

        mPlayerTurnText = textView;

        // This show the starting status of the board
        updateBoard(boardRepresentation);
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
            mStartingBoard = ((ActivePlayer) data).getBoardRepresentation();
            updatePlayingPlayerText(((ActivePlayer) data).getLoad().getName());
        } else if (data instanceof BoardUpdated) {
            containers = ((BoardUpdated) data).getLoad();
            updateBoard(containers);
        } else if (data instanceof Winner) {
            updateBoard(((Winner) data).getboardStatus());
            updatePlayingPlayerTextWithWinnerName(((Winner) data).getLoad().getName());
        } else if (data instanceof EvenGame) {
            updateBoard(((EvenGame) data).getLoad());
            updatePlayingPlayerText("The game ended, even...Shame on you!");
        }
    }

    private void updatePlayingPlayerText(String name) {
        if (mPlayerTurnText != null) {
            mPlayerTurnText.setText("Player's turn: " + name);
        } else {
            mStartingPlayerName = name;
        }
    }

    private void updatePlayingPlayerTextWithWinnerName(String name) {
        if (mPlayerTurnText != null) {
            mPlayerTurnText.setText("THE WINNER IS: " + name);
        }
    }

    // Interact with the Human Player Brain
    //TODO maybe there is some way more elegant to do this
    @Override
    public void onClick(View v) {
        int bowlNumber = v.getId();
        if (bowlNumber < 6) {

            // TODO animation is working
            final Button b = (Button) v;
            ValueAnimator animator = ValueAnimator.ofInt();

            ValueAnimator valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(),
                    getResources().getColor( R.color.selectedBowl),
                    getResources().getColor(R.color.playerOneBowl));
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    GradientDrawable bowlShape = (GradientDrawable) b.getBackground();
                    bowlShape.setColor( (Integer)animation.getAnimatedValue() );
                }
            });
            valueAnimator.setDuration(1000);
            valueAnimator.start();

            mPlayerBrainListeners.get(0)
                    .onFragmentInteraction(
                            OnFragmentInteractionListener.EventType.CHOOSEN_BOWL, bowlNumber);
        } else {
            mPlayerBrainListeners.get(1)
                    .onFragmentInteraction(
                            OnFragmentInteractionListener.EventType.CHOOSEN_BOWL, bowlNumber);
        }
    }

    public void attachHumanPlayerBrain(OnFragmentInteractionListener brain, int playerNumber) {
        if (mPlayerBrainListeners == null) {
            mPlayerBrainListeners = new ArrayList<OnFragmentInteractionListener>();
        }
        mPlayerBrainListeners.add(playerNumber, brain);
    }
}