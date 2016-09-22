package com.sylvainautran.nanodegree.capstoneproject;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sylvainautran.nanodegree.capstoneproject.adapters.AdapterActionModeListener;
import com.sylvainautran.nanodegree.capstoneproject.adapters.ClassStudentsAdapter;
import com.sylvainautran.nanodegree.capstoneproject.adapters.FragmentActionModeListener;
import com.sylvainautran.nanodegree.capstoneproject.adapters.StudentsAdapter;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.StudentsLoader;
import com.sylvainautran.nanodegree.capstoneproject.dialogs.ClassStudentsEditDialog;
import com.sylvainautran.nanodegree.capstoneproject.dialogs.ClassStudentsNewDialog;
import com.sylvainautran.nanodegree.capstoneproject.dialogs.StudentsNewDialog;

import java.util.HashMap;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, ActionMode.Callback, FragmentActionModeListener {
    private final String LOG_TAG = this.getClass().getSimpleName();

    public final int ALL_STUDENTS = 15;
    public final int STUDENTS_FROM_CLASS = 30;

    public static final String CLASS_ID = "class_id";

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;

    private RecyclerView.Adapter adapter;
    private HashMap<Integer, Long> selectedStudents;

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
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int emptyText = R.string.empty_student_list;
        HashMap<Long, Character> headers = null;
        if(cursor != null && cursor.moveToFirst()){
            headers = new HashMap<>();
            char first_letter = cursor.getString(StudentsLoader.Query.COLUMN_LASTNAME).charAt(0);
            headers.put(cursor.getLong(StudentsLoader.Query._ID), first_letter);
            while(cursor.moveToNext()){
                char new_first_letter = cursor.getString(StudentsLoader.Query.COLUMN_LASTNAME).charAt(0);
                if(first_letter != new_first_letter){
                    first_letter = new_first_letter;
                    headers.put(cursor.getLong(StudentsLoader.Query._ID), first_letter);
                }
            }

        }

        switch (loader.getId()){
            case STUDENTS_FROM_CLASS:
                adapter = new ClassStudentsAdapter((AppCompatActivity) getActivity(), cursor, this, headers);
                emptyText = R.string.empty_class_student_list;
                break;
            default:
                adapter = new StudentsAdapter((AppCompatActivity) getActivity(), cursor, this, headers);
        }

        if(adapter.getItemCount() > 0){
            emptyView.setVisibility(View.GONE);
        }else{
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(emptyText);
        }

        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }


    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        Log.d(LOG_TAG, "start action");
        selectedStudents = new HashMap<>();
        MenuInflater inflater = mode.getMenuInflater();
        if(getArguments() != null && getArguments().containsKey(CLASS_ID)) {
            inflater.inflate(R.menu.class_students_edit, menu);
        }else {
            inflater.inflate(R.menu.students_edit, menu);
        }
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        MenuItem edit;
        if(getArguments() != null && getArguments().containsKey(CLASS_ID)) {
            edit = menu.findItem(R.id.edit_grade);
        }else {
            edit = menu.findItem(R.id.edit_student);
        }
        if(selectedStudents.size() > 1){
            edit.setVisible(false);
        }else{
            edit.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if(selectedStudents.size() > 0) {
            switch (item.getItemId()) {
                case R.id.edit_student:
                    Iterator it1 = selectedStudents.keySet().iterator();
                    int position = (Integer) it1.next();
                    long id = selectedStudents.get(position);
                    FragmentManager fragmentManager = ((AppCompatActivity) getActivity()).getSupportFragmentManager();
                    HashMap<String, String> values = ((AdapterActionModeListener) adapter).getValues(position);
                    StudentsNewDialog newFragment = StudentsNewDialog.newInstance(R.string.edit_student, id, values.get(StudentsAdapter.FIRST_NAME), values.get(StudentsAdapter.LAST_NAME), Long.parseLong(values.get(StudentsAdapter.BIRTH_DATE)));
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.add(R.id.drawer_layout, newFragment, "dialog").addToBackStack(null).commit();
                    break;
                case R.id.delete_student:
                    Iterator it2 = selectedStudents.values().iterator();
                    String ids = "(" + it2.next().toString();
                    while (it2.hasNext()) {
                        ids += ", " + it2.next().toString();
                    }
                    ids += ")";
                    Log.e(LOG_TAG, "IDs = " + ids);
                    getActivity().getContentResolver().delete(AppelContract.StudentEntry.CONTENT_URI, AppelContract.StudentEntry._ID + " IN " + ids, null);
                    Snackbar.make(mRecyclerView, "Deleted Saved Selection.", Snackbar.LENGTH_LONG).
                            setAction("Undo", new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    Iterator it = selectedStudents.values().iterator();
                                    String ids = "(" + it.next().toString();
                                    while (it.hasNext()) {
                                        ids += ", " + it.next().toString();
                                    }
                                    ids += ")";
                                    ContentValues cv = new ContentValues();
                                    cv.put(AppelContract.StudentEntry.COLUMN_DELETED, AppelContract.PUBLIC);
                                    int t = getActivity().getContentResolver().update(AppelContract.StudentEntry.CONTENT_URI, cv, AppelContract.StudentEntry._ID + " IN " + ids, null);
                                    Log.d(LOG_TAG, t + "");
                                }

                            }).show();
                    break;
                case R.id.edit_grade:
                    Iterator it4 = selectedStudents.keySet().iterator();
                    int position1 = (Integer) it4.next();
                    long id1 = selectedStudents.get(position1);
                    HashMap<String, String> values1 = ((AdapterActionModeListener) adapter).getValues(position1);
                    FragmentManager fm = ((AppCompatActivity) getActivity()).getSupportFragmentManager();
                    ClassStudentsEditDialog frag = ClassStudentsEditDialog.newInstance(R.string.edit_grade, id1, values1.get(ClassStudentsAdapter.GRADE));
                    frag.show(fm, "dialog");
                    break;
                case R.id.delete_student_from_class:
                    Iterator it3 = selectedStudents.values().iterator();
                    String ids2 = "(" + it3.next().toString();
                    while (it3.hasNext()) {
                        ids2 += ", " + it3.next().toString();
                    }
                    ids2 += ")";
                    Log.e(LOG_TAG, "IDs = " + ids2);
                    getActivity().getContentResolver().delete(AppelContract.ClassStudentLinkEntry.CONTENT_URI, AppelContract.ClassStudentLinkEntry._ID + " IN " + ids2, null);
                    Snackbar.make(mRecyclerView, "Deleted Saved Selection.", Snackbar.LENGTH_LONG).
                            setAction("Undo", new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    Iterator it = selectedStudents.values().iterator();
                                    String ids = "(" + it.next().toString();
                                    while (it.hasNext()) {
                                        ids += ", " + it.next().toString();
                                    }
                                    ids += ")";
                                    ContentValues cv = new ContentValues();
                                    cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_DELETED, AppelContract.PUBLIC);
                                    int t = getActivity().getContentResolver().update(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv, AppelContract.ClassStudentLinkEntry._ID + " IN " + ids, null);
                                    Log.d(LOG_TAG, t + "");
                                }

                            }).show();
                    break;
            }
            mode.finish();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        ((AdapterActionModeListener) adapter).clearSelectedItems();
    }

    @Override
    public void addSelectedItem(int position, long id) {
        selectedStudents.put(position, id);
    }

    @Override
    public void removeSelectedItem(int position, long id) {
        selectedStudents.remove(position);
    }

    @Override
    public void clearSelectedItems() {
        selectedStudents.clear();
    }
}
