package me.dacol.marco.mancala.gameUI;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import me.dacol.marco.mancala.MainActivity;
import me.dacol.marco.mancala.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewGameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewGameFragment extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = NewGameFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewGameFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewGameFragment newInstance(String param1, String param2) {
        NewGameFragment fragment = new NewGameFragment();
        // If this fragment need some arguments you have to make it via bundle
        return fragment;
    }

    public NewGameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewRoot = inflater.inflate(R.layout.fragment_new_game, container, false);

        // attach the fragment to the button
        Button newHvHGameButton = (Button) viewRoot.findViewById(R.id.new_HvH_game);
        newHvHGameButton.setOnClickListener(this);

        Button newHvCGameButton = (Button) viewRoot.findViewById(R.id.new_HvC_game);
        newHvCGameButton.setOnClickListener(this);

        Button statisticsButton = (Button) viewRoot.findViewById(R.id.statistics);
        statisticsButton.setOnClickListener(this);

        return viewRoot;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (me.dacol.marco.mancala.gameUI.OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        // TODO passare tutto il riferimento al bottone?
        if (v.getId() == R.id.new_HvH_game) {
            mListener.onFragmentInteraction(MainActivity.EventType.NEW_HvH_GAME_BUTTON_PRESSED, null);
        } else if (v.getId() == R.id.new_HvC_game) {
            mListener.onFragmentInteraction(MainActivity.EventType.NEW_HvC_GAME_BUTTON_PRESSED, null);
        } else if (v.getId() == R.id.statistics) {
            mListener.onFragmentInteraction(MainActivity.EventType.STATISTICS_BUTTON_PRESSED, null);
        }
    }
}
