package me.dacol.marco.mancala.statisticsUI;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.dacol.marco.mancala.R;
import me.dacol.marco.mancala.gameUI.OnFragmentInteractionListener;
import me.dacol.marco.mancala.statisticsLib.DBContracts;
import me.dacol.marco.mancala.statisticsLib.DBContracts.GamesHistoryEntry;
import me.dacol.marco.mancala.statisticsLib.StatisticsCallerObject;
import me.dacol.marco.mancala.statisticsLib.StatisticsHelper;

public class StatisticsFragment extends Fragment implements StatisticsCallerObject {

    private OnFragmentInteractionListener mListener;
    private StatisticsHelper mStatisticsHelper;

    public static StatisticsFragment newInstance() {
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
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        StatisticsHelper mStatisticsHelper = StatisticsHelper.getInstance(getActivity());
        mStatisticsHelper.loadAllStatistics(this);
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
        populateHvCStatistics(result.get(0));
        populateHvHStatistics(result.get(1));
        populateLastGames(result.get(2));
    }

    private void populateHvHStatistics(Cursor hvhStatistics) {
        List<TextView> textViews = new ArrayList<>();

        textViews.add((TextView) getView().findViewById(R.id.played_games_HvH_text_view));
        textViews.add((TextView) getView().findViewById(R.id.best_score_HvH_text_view));
        textViews.add((TextView) getView().findViewById(R.id.win_games_HvH_text_view));
        textViews.add((TextView) getView().findViewById(R.id.even_games_HvH_text_view));
        textViews.add((TextView) getView().findViewById(R.id.lose_games_HvH_text_view));

        bindValueToTextView(hvhStatistics, textViews);
    }

    private void populateHvCStatistics(Cursor hvcStatistics) {
        List<TextView> textViews = new ArrayList<>();

        textViews.add((TextView) getView().findViewById(R.id.played_games_HvC_text_view));
        textViews.add((TextView) getView().findViewById(R.id.best_score_HvC_text_view));
        textViews.add((TextView) getView().findViewById(R.id.win_games_HvC_text_view));
        textViews.add((TextView) getView().findViewById(R.id.even_games_HvC_text_view));
        textViews.add((TextView) getView().findViewById(R.id.lose_games_HvC_text_view));

        bindValueToTextView(hvcStatistics, textViews);
    }

    private void bindValueToTextView (Cursor cursor, List<TextView> textViews) {
        cursor.moveToFirst();
        do {
            switch (cursor.getString(0)) {
                case DBContracts.STATS_KEY_PLAYED_GAME:
                    textViews.get(0).setText(cursor.getString(1));
                    break;
                case DBContracts.STATS_KEY_BESTSCORE:
                    textViews.get(1).setText(cursor.getString(1));
                    break;
                case DBContracts.STATS_KEY_WIN_GAME:
                    textViews.get(2).setText(cursor.getString(1));
                    break;
                case DBContracts.STATS_KEY_EVEN_GAME:
                    textViews.get(3).setText(cursor.getString(1));
                    break;
                case DBContracts.STATS_KEY_LOSE_GAME:
                    textViews.get(4).setText(cursor.getString(1));
                    break;
            }
        } while (cursor.moveToNext());
    }

    private void populateLastGames(Cursor lastGames) {
        ListView lastGameListView = (ListView) getView().findViewById(R.id.last_game_list_view);



        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.last_game_list_item,
                lastGames,
                new String[] {//GamesHistoryEntry.COLUMN_NAME_DATE,
                        GamesHistoryEntry.COLUMN_NAME_GAME_TYPE,
                        GamesHistoryEntry.COLUMN_NAME_PLAYER_POINT,
                        GamesHistoryEntry.COLUMN_NAME_OPPONENT_POINT,
                        GamesHistoryEntry._ID
                    },
                new int[] {// R.id.list_item_date_text_view,
                        R.id.list_item_game_type_text_view,
                        R.id.list_item_player_points_text_view,
                        R.id.list_item_opponent_points_text_view,
                        R.id.list_item_win_lose_image_view
                    },
                0
            );

        SimpleCursorAdapter.ViewBinder customViewBinder = new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() != R.id.list_item_win_lose_image_view) {
                    return false;
                }

                ImageView winLoseImage = (ImageView) view;

                int playersPoint = cursor.getInt(
                                        cursor.getColumnIndex(
                                            GamesHistoryEntry.COLUMN_NAME_PLAYER_POINT) );
                int opponentPoint = cursor.getInt(
                                        cursor.getColumnIndex(
                                            GamesHistoryEntry.COLUMN_NAME_OPPONENT_POINT) );


                if (playersPoint > opponentPoint) {
                    winLoseImage.setImageResource(R.drawable.win);
                } else if (playersPoint < opponentPoint) {
                    winLoseImage.setImageResource(R.drawable.lose);
                } else if (playersPoint == opponentPoint) {
                    winLoseImage.setImageResource(R.drawable.even);
                }

                return true;
            }
        };

        simpleCursorAdapter.setViewBinder(customViewBinder);

        lastGameListView.setAdapter(simpleCursorAdapter);
    }

}
