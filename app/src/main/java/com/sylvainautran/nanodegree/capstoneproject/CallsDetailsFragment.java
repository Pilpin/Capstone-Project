package com.sylvainautran.nanodegree.capstoneproject;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;
import com.sylvainautran.nanodegree.capstoneproject.data.adapters.BaseAdapter;
import com.sylvainautran.nanodegree.capstoneproject.data.adapters.CallStudentsAdapter;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.CallsLoader;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.StatsLoader;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallsDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = this.getClass().getSimpleName();
    public static final String CLASS_ID = "class_id";
    public static final String CALL_ID = "call_id";
    public static final String SELECTION = "filter";
    public static final String SELECT_ALL_STUDENTS = null;
    public static final String SELECT_MATERNELLE = AppelContract.ClassStudentLinkEntry.COLUMN_GRADE + " IN ('PS', 'MS', 'GS')";
    public static final String SELECT_PRIMAIRE = AppelContract.ClassStudentLinkEntry.COLUMN_GRADE + " IN ('CP', 'CE1', 'CE2', 'CM1', 'CM2')";
    private final int CALL_STUDENTS = 10;
    private final int CALL_STATS = 11;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;
    @BindView(R.id.fastscroll)
    FastScroller mFastScroller;
    @BindView(R.id.bottomBar)
    ViewGroup bottombar;
    @BindView(R.id.total)
    TextView totalTV;
    @BindView(R.id.present)
    TextView presentTV;
    @BindView(R.id.absent)
    TextView absentTV;

    private int total, present, absent, left;
    private boolean option;
    private String selectionString;

    private RecyclerView.Adapter adapter;

    public CallsDetailsFragment() {
    }

    public static CallsDetailsFragment newInstance(long callId, long classId, String selection) {
        CallsDetailsFragment fragment = new CallsDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(CLASS_ID, classId);
        bundle.putLong(CALL_ID, callId);
        bundle.putString(SELECTION, selection);
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
            layout = R.layout.fragment_calls_details;
        }

        View view = inflater.inflate(layout, container, false);
        ButterKnife.bind(this, view);

        mRecyclerView.setAdapter(new BaseAdapter(getActivity(), null, null, null, null));
        mFastScroller.setRecyclerView(mRecyclerView);

        if(getArguments() != null && getArguments().containsKey(CLASS_ID) && getArguments().containsKey(CALL_ID)) {
            selectionString = getArguments().getString(SELECTION, null);
            getLoaderManager().initLoader(CALL_STUDENTS, null, this);
        }
        return view;
    }

    @Override
    public CursorLoader onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader;
        switch (id){
            case CALL_STATS:
                cursorLoader = StatsLoader.getStatsForOne(getActivity(), getArguments().getLong(CALL_ID), selectionString);
                break;
            case CALL_STUDENTS:
                cursorLoader = CallsLoader.getAllCallDetails(getActivity(), getArguments().getLong(CALL_ID), getArguments().getLong(CLASS_ID), selectionString);
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


                    updateStats(presents, absents, left, option);
                }
                break;
            case CALL_STUDENTS:
                HashMap<Integer, String> ltpAll = null;

                if(cursor != null){
                    ltpAll = new HashMap<>();
                    while(cursor.moveToNext()){
                        ltpAll.put(cursor.getPosition(), cursor.getString(CallsLoader.Query.COLUMN_LASTNAME).substring(0, 1));
                    }
                }

                adapter = new CallStudentsAdapter(getActivity(), cursor, ltpAll);

                if(adapter.getItemCount() > 0){
                    emptyView.setVisibility(View.GONE);
                }else{
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setText(R.string.empty_call_student_list);
                }

                updateTotal(adapter.getItemCount());
                getLoaderManager().initLoader(CALL_STATS, null, this);

                adapter.setHasStableIds(true);
                mRecyclerView.swapAdapter(adapter, false);
                mFastScroller.setRecyclerView(mRecyclerView);
                break;
            default:

        }

    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    private void updateTotal(int total) {
        this.total = total;
        totalTV.setText(getString(R.string.stats_total, total));
        isCallEnded();
    }

    private void updateStats(int present, int absent, int left, boolean option) {
        this.present = present;
        this.absent = absent;
        this.left = left;
        this.option = option;
        if (option) {
            presentTV.setText(getString(R.string.stats_present, present) + " [" + (total - absent - left) + "]");
        }else {
            presentTV.setText(getString(R.string.stats_present, present));
        }
        absentTV.setText(getString(R.string.stats_absent, absent));
        isCallEnded();
    }

    private void isCallEnded(){
        if(option){
            if(total != 0 && total == left + absent){
                animateBottomBar();
            }
        }else{
            if(total != 0 && total == present + absent){
                animateBottomBar();
            }
        }
    }

    private void animateBottomBar(){
        Integer colorFrom = getResources().getColor(R.color.colorPrimary);
        Integer colorTo = getResources().getColor(R.color.colorPresent);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                bottombar.setBackgroundColor((Integer) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }
}
