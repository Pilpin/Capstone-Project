package com.sylvainautran.nanodegree.capstoneproject;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;
import com.sylvainautran.nanodegree.capstoneproject.data.adapters.BaseAdapter;
import com.sylvainautran.nanodegree.capstoneproject.data.adapters.CallStudentsAdapter;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.CallsLoader;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.StatsLoader;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallsDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = this.getClass().getSimpleName();
    public static final String CLASS_ID = "class_id";
    public static final String CALL_ID = "call_id";
    private final int CALL_STUDENTS = 10;
    private final int CALL_STATS = 11;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;

    private RecyclerView.Adapter adapter;

    public CallsDetailsFragment() {
    }

    public static CallsDetailsFragment newInstance(long callId, long classId) {
        CallsDetailsFragment fragment = new CallsDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(CLASS_ID, classId);
        bundle.putLong(CALL_ID, callId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layout;
        if(getResources().getBoolean(R.bool.tablet_land)){
            layout = R.layout.calls_list_fragment;
        }else{
            layout = R.layout.fragment_generic;
        }

        View view = inflater.inflate(layout, container, false);
        ButterKnife.bind(this, view);

        mRecyclerView.setAdapter(new BaseAdapter(getActivity(), null, null, null, null));

        if(getArguments() != null && getArguments().containsKey(CLASS_ID) && getArguments().containsKey(CALL_ID)) {
            getLoaderManager().initLoader(CALL_STUDENTS, null, this);
            getLoaderManager().initLoader(CALL_STATS, null, this);
        }
        return view;
    }

    @Override
    public CursorLoader onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader;
        switch (id){
            case CALL_STATS:
                cursorLoader = StatsLoader.getStatsForOne(getActivity(), getArguments().getLong(CALL_ID));
                break;
            case CALL_STUDENTS:
                cursorLoader = CallsLoader.getAllCallDetails(getActivity(), getArguments().getLong(CALL_ID), getArguments().getLong(CLASS_ID));
                break;
            default:
                return null;
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()){
            case CALL_STATS:
                if(cursor.moveToFirst()){
                    int presents = cursor.getInt(StatsLoader.Query.PRESENT);
                    int total = cursor.getInt(StatsLoader.Query.TOTAL);
                    int left = cursor.getInt(StatsLoader.Query.LEFT);
                    boolean option = cursor.getInt(StatsLoader.Query.OPTION) == AppelContract.CallEntry.RECORD_LEAVING_TIME;
                    int absents = total - presents;


                    ((UpdateUI) getActivity()).updateCall(presents, absents, left, option);
                }
                break;
            case CALL_STUDENTS:
                adapter = new CallStudentsAdapter(getActivity(), cursor);

                if(adapter.getItemCount() > 0){
                    emptyView.setVisibility(View.GONE);
                }else{
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setText(R.string.empty_call_student_list);
                }

                ((UpdateUI) getActivity()).updateTotal(adapter.getItemCount());

                adapter.setHasStableIds(true);
                mRecyclerView.swapAdapter(adapter, false);
                break;
            default:

        }

    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    public interface UpdateUI {

        void updateTotal(int total);

        void updateCall(int presents, int absents, int left, boolean option);
    }
}
