package me.dacol.marco.mancala.statisticsUI;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.dacol.marco.mancala.R;
import me.dacol.marco.mancala.gameUI.OnFragmentInteractionListener;
import me.dacol.marco.mancala.statisticsLib.StatisticsCallerObject;
import me.dacol.marco.mancala.statisticsLib.StatisticsHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticsFragment extends Fragment implements StatisticsCallerObject {

    private OnFragmentInteractionListener mListener;
    private StatisticsHelper mStatisticsHelper;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticsFragment newInstance(String param1, String param2) {
        StatisticsFragment fragment = new StatisticsFragment();

        return fragment;
    }

    public StatisticsFragment() {
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
        StatisticsHelper mStatisticsHelper = new StatisticsHelper(getActivity());
        mStatisticsHelper.getLoadAll(this);

        return inflater.inflate(R.layout.fragment_statistics, container, false);
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
    public void postResults(List<Cursor> result) {
        TextView textView = (TextView) getView().findViewById(R.id.text);
        int HvCStatisticsCount = result.get(0).getCount();
        int HvHStatisticsCount = result.get(1).getCount();
        int GameStatisticsCount = result.get(2).getCount();

        textView.setText(
                "HvCStatistics: " + HvCStatisticsCount +
                " HvHStatistics: " + HvHStatisticsCount +
                " GameStats: " + GameStatisticsCount );

    }

}
