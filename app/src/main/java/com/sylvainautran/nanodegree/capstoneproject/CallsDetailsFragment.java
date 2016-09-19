package com.sylvainautran.nanodegree.capstoneproject;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sylvainautran.nanodegree.capstoneproject.adapters.CallStudentsAdapter;
import com.sylvainautran.nanodegree.capstoneproject.loaders.CallsLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallsDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = this.getClass().getSimpleName();

    public static final String CLASS_ID = "class_id";
    public static final String CALL_ID = "call_id";

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;

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
        View view = inflater.inflate(R.layout.fragment_calls_details, container, false);
        ButterKnife.bind(this, view);

        if(getArguments() != null && getArguments().containsKey(CLASS_ID) && getArguments().containsKey(CALL_ID)) {
            getLoaderManager().initLoader(0, null, this);
        }
        return view;
    }

    @Override
    public CursorLoader onCreateLoader(int id, Bundle args) {
        return CallsLoader.getAllCallsFromClass(getActivity(), getArguments().getLong(CALL_ID), getArguments().getLong(CLASS_ID));
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        if(cursor != null && cursor.getCount() > 0){
            emptyView.setVisibility(View.GONE);
        }
        RecyclerView.Adapter adapter = new CallStudentsAdapter(getActivity(), cursor);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }
}
