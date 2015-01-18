package me.dacol.marco.mancala.gameUI;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import me.dacol.marco.mancala.MainActivity;
import me.dacol.marco.mancala.R;


public class NewGameFragment extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = NewGameFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     */
    public static NewGameFragment newInstance() {
        NewGameFragment fragment = new NewGameFragment();
        // If this fragment need some arguments you have to make it via bundle
        return fragment;
    }

    public NewGameFragment() {
        // Required empty public constructor
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewRoot = inflater.inflate(R.layout.fragment_new_game, container, false);

        // attach the fragment to the button
        ImageButton newHvHGameButton = (ImageButton) viewRoot.findViewById(R.id.new_HvH_game_button);
        newHvHGameButton.setOnClickListener(this);

        ImageButton newHvCGameButton = (ImageButton) viewRoot.findViewById(R.id.new_HvC_game_button);
        newHvCGameButton.setOnClickListener(this);

        ImageButton statisticsButton = (ImageButton) viewRoot.findViewById(R.id.statistics_button);
        statisticsButton.setOnClickListener(this);

        ImageButton preferencesButton = (ImageButton) viewRoot.findViewById(R.id.preferences_button);
        preferencesButton.setOnClickListener(this);

        return viewRoot;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.new_HvH_game_button) {
            mListener.onFragmentInteraction(MainActivity.EventType.NEW_HvH_GAME_BUTTON_PRESSED, null);
        } else if (v.getId() == R.id.new_HvC_game_button) {
            mListener.onFragmentInteraction(MainActivity.EventType.NEW_HvC_GAME_BUTTON_PRESSED, null);
        } else if (v.getId() == R.id.statistics_button) {
            mListener.onFragmentInteraction(MainActivity.EventType.STATISTICS_BUTTON_PRESSED, null);
        } else if (v.getId() == R.id.preferences_button) {
            mListener.onFragmentInteraction(MainActivity.EventType.PREFERENCES_BUTTON_PRESSED, null);
        }
    }
}
