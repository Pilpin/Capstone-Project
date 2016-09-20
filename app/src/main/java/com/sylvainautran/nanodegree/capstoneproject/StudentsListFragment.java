package com.sylvainautran.nanodegree.capstoneproject;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
    private final String LOG_CAT = this.getClass().getSimpleName();

    public final int ALL_STUDENTS = 15;
    public final int STUDENTS_FROM_CLASS = 30;

    public static final String CLASS_ID = "class_id";

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

    public static StudentsListFragment newInstance(long classId) {
        StudentsListFragment fragment = new StudentsListFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(CLASS_ID, classId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generic, container, false);
        ButterKnife.bind(this, view);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));

        if(getArguments() != null && getArguments().containsKey(CLASS_ID)){
            getLoaderManager().initLoader(STUDENTS_FROM_CLASS, null, this);
        }else{
            getLoaderManager().initLoader(ALL_STUDENTS, null, this);
        }

        return view;
    }

    @Override
    public CursorLoader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case STUDENTS_FROM_CLASS:
                return StudentsLoader.getAllStudentsInClass(getActivity(), getArguments().getLong(CLASS_ID));
            default:
                return StudentsLoader.getAllStudents(getActivity());
        }

    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        int emptyText = R.string.empty_student_list;

        RecyclerView.Adapter adapter;
        switch (loader.getId()){
            case STUDENTS_FROM_CLASS:
                adapter = new ClassStudentsAdapter(getActivity(), cursor);
                emptyText = R.string.empty_class_student_list;
                break;
            default:
                adapter = new StudentsAdapter((AppCompatActivity) getActivity(), cursor);
        }

        if(cursor != null && cursor.getCount() > 0){
            emptyView.setVisibility(View.GONE);
        }else{
            emptyView.setText(emptyText);
        }

        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }
}
