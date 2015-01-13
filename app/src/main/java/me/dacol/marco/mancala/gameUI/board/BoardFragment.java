package me.dacol.marco.mancala.gameUI.board;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
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
import me.dacol.marco.mancala.gameLib.exceptions.ToManyPlayerException;
import me.dacol.marco.mancala.gameLib.gameController.Game;
import me.dacol.marco.mancala.gameLib.gameController.actions.Action;
import me.dacol.marco.mancala.gameLib.gameController.actions.ActivePlayer;
import me.dacol.marco.mancala.gameLib.gameController.actions.BoardUpdated;
import me.dacol.marco.mancala.gameLib.gameController.actions.EvenGame;
import me.dacol.marco.mancala.gameLib.gameController.actions.Winner;
import me.dacol.marco.mancala.gameLib.player.PlayerType;
import me.dacol.marco.mancala.gameUI.OnFragmentInteractionListener;
import me.dacol.marco.mancala.gameUI.animatior.BowlAnimator;
import me.dacol.marco.mancala.gameUI.pieces.Bowl;
import me.dacol.marco.mancala.gameUI.pieces.PieceFactory;
import me.dacol.marco.mancala.gameUI.pieces.Tray;

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

    private ArrayList<TextView> mBoardUIRepresentation;

    private ArrayList<Container> mStartingBoard;
    private TextView mPlayerTurnText;
    private String mStartingPlayerName;
    private BowlAnimator mBowlAnimator;

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
                (OnFragmentInteractionListener) game.getPlayerNumber(0).getBrain(), 0);

        if (isHumanVsHuman) {
            boardFragment.attachHumanPlayerBrain(
                    (OnFragmentInteractionListener) game.getPlayerNumber(1).getBrain(), 1);
        }
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
        mBowlAnimator = null;
        mBoardUIRepresentation = new ArrayList<TextView>();
        GridLayout.LayoutParams params;

        // Here I've to check if the choosen game is human vs human, i need to attach
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

            mBoardUIRepresentation.add(bowl);
        }

        // Add the tray for player one
        Tray trayPlayerOne = PieceFactory.generateTray(
                getActivity(),
                1,
                5,
                boardRepresentation.get(6).toString(),
                1
        );

        mBoardUIRepresentation.add(trayPlayerOne);

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

            mBoardUIRepresentation.add(bowl);
        }

        // Add the tray for computer
        Tray trayPlayerTwo = PieceFactory.generateTray(
                getActivity(),
                1,
                0,
                boardRepresentation.get(13).toString(),
                2
        );

        mBoardUIRepresentation.add(trayPlayerTwo);

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
        updateBoard(boardRepresentation, null);
    }

    private void addToBoardView(GridLayout board) {
        board.removeAllViews();

        for (TextView t : mBoardUIRepresentation) {
            board.addView(t);
        }

        board.addView(mPlayerTurnText);
    }

    private void updateBoard(ArrayList<Container> boardRepresentation, ArrayList<Action> atomicMoves) {

        /* Keep this code, could be an option to not have the animation
        if (mBoardUIRepresentation != null) {
            for (int i=0; i < boardRepresentation.size(); i++) {
                mBoardUIRepresentation.get(i).setText(boardRepresentation.get(i).toString());
            }
        }
        */
        BowlAnimator bowlAnimator = new BowlAnimator(getActivity(), mBoardUIRepresentation);

        if (bowlAnimator.getStatus() == AsyncTask.Status.PENDING) {
            bowlAnimator.execute(atomicMoves);
        } else if (bowlAnimator.getStatus() == AsyncTask.Status.RUNNING) {
            bowlAnimator.addMoves(atomicMoves);
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
            updateBoard(containers, ((BoardUpdated) data).getAtomicMoves() );
        } else if (data instanceof Winner) {
            updateBoard(((Winner) data).getboardStatus(), null);
            updatePlayingPlayerTextWithWinnerName(((Winner) data).getLoad().getName());
        } else if (data instanceof EvenGame) {
            updateBoard(((EvenGame) data).getLoad(), null);
            updatePlayingPlayerText("The game ended, even...Shame on you!"); //TODO put me in string.xml
        }
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
    //TODO maybe there is some way more elegant to do this
    @Override
    public void onClick(View v) {
        int bowlNumber = v.getId();
        if (bowlNumber < 6) {
            // TODO animation is working
/*          final Button b = (Button) v;
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
            valueAnimator.start();*/

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