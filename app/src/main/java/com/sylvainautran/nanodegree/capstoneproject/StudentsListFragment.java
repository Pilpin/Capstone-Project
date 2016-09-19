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
import android.widget.Toast;

import com.sylvainautran.nanodegree.capstoneproject.adapters.ClassStudentsAdapter;
import com.sylvainautran.nanodegree.capstoneproject.adapters.StudentsAdapter;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelProvider;
import com.sylvainautran.nanodegree.capstoneproject.loaders.StudentsLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public final int ALL_STUDENTS = 15;
    public final int STUDENTS_FROM_CLASS = 30;
    private final String LOG_CAT = this.getClass().getSimpleName();

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;

    public StudentsListFragment() {
    }

    public static StudentsListFragment newInstance() {
        StudentsListFragment fragment = new StudentsListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_students_list, container, false);
        ButterKnife.bind(this, view);

        if(getActivity().getIntent() != null && getActivity().getIntent().getData() != null){

            switch(AppelProvider.buildUriMatcher().match(getActivity().getIntent().getData())){
                case AppelProvider.CLASS_STUDENT_FROM_CLASS:
                    getLoaderManager().initLoader(STUDENTS_FROM_CLASS, null, this);
                    break;
                default:
                    getLoaderManager().initLoader(ALL_STUDENTS, null, this);
            }

        }else{
            getLoaderManager().initLoader(ALL_STUDENTS, null, this);
        }

        return view;
    }

    @Override
    public CursorLoader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case STUDENTS_FROM_CLASS:
                return StudentsLoader.getAllStudentsInClass(getActivity(), getActivity().getIntent().getData());
            default:
                return StudentsLoader.getAllStudents(getActivity());
        }

    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        if(cursor != null && cursor.getCount() > 0){
            emptyView.setVisibility(View.GONE);
        }

        RecyclerView.Adapter adapter;
        switch (loader.getId()){
            case STUDENTS_FROM_CLASS:
                adapter = new ClassStudentsAdapter(getActivity(), cursor);
                break;
            default:
                adapter = new StudentsAdapter(getActivity(), cursor);
        }
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }
}
