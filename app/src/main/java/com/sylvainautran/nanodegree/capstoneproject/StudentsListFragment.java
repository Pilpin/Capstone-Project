package com.sylvainautran.nanodegree.capstoneproject;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sylvainautran.nanodegree.capstoneproject.data.adapters.BaseAdapter;
import com.sylvainautran.nanodegree.capstoneproject.data.adapters.ClassStudentsAdapter;
import com.sylvainautran.nanodegree.capstoneproject.data.adapters.StudentsAdapter;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.StudentsLoader;
import com.sylvainautran.nanodegree.capstoneproject.dialogs.ClassStudentsNewDialog;
import com.sylvainautran.nanodegree.capstoneproject.dialogs.StudentsNewDialog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, ActionMode.Callback, View.OnClickListener, View.OnLongClickListener {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private final String SAVE_SELECTED_ITEMS = "selected_items";
    private final String SAVE_ACTION_MODE_STATE = "action_mode";

    private final String STUDENT_ID = "student_id";
    private final String CLASS_STUDENT_ID = "class_student_id";
    private final String BIRTH_DATE = "birthdate";
    private final String FIRST_NAME = "firstname";
    private final String LAST_NAME = "lastname";
    private final String GRADE = "grade";

    public final int ALL_STUDENTS = 15;
    public final int STUDENTS_FROM_CLASS = 30;

    public static final String CLASS_ID = "class_id";

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;

    private boolean isActionMode;
    private ActionMode mActionMode;
    private BaseAdapter adapter;
    private HashMap<Integer, Bundle> selectedStudents;
    private Snackbar snackbar;

    public StudentsListFragment() {
        selectedStudents = new HashMap<>();
    }

    public static StudentsListFragment newInstance() {
        return new StudentsListFragment();
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
        if(savedInstanceState != null){
            isActionMode = savedInstanceState.getBoolean(SAVE_ACTION_MODE_STATE, false);
            selectedStudents = (HashMap<Integer, Bundle>) savedInstanceState.getSerializable(SAVE_SELECTED_ITEMS);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SAVE_SELECTED_ITEMS, selectedStudents);
        outState.putSerializable(SAVE_ACTION_MODE_STATE, isActionMode);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generic, container, false);
        ButterKnife.bind(this, view);

        mRecyclerView.setAdapter(new BaseAdapter(getActivity(), null, null, null, null));
        if(isActionMode){
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(this);
        }

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
            char first_letter = Character.toUpperCase(cursor.getString(StudentsLoader.Query.COLUMN_LASTNAME).charAt(0));
            headers.put(cursor.getLong(StudentsLoader.Query._ID), first_letter);
            while(cursor.moveToNext()){
                char new_first_letter = Character.toUpperCase(cursor.getString(StudentsLoader.Query.COLUMN_LASTNAME).charAt(0));
                if(first_letter != new_first_letter){
                    first_letter = new_first_letter;
                    headers.put(cursor.getLong(StudentsLoader.Query._ID), first_letter);
                }
            }

        }

        Set<Integer> selectedPositions = new HashSet<>();
        if(isActionMode){
            selectedPositions = selectedStudents.keySet();
        }

        switch (loader.getId()){
            case STUDENTS_FROM_CLASS:
                adapter = new ClassStudentsAdapter(getActivity(), cursor, selectedPositions, this, this, headers);
                emptyText = R.string.empty_class_student_list;
                break;
            default:
                adapter = new StudentsAdapter(getActivity(), cursor, selectedPositions, this, this, headers);
        }

        if(adapter.getItemCount() > 0){
            emptyView.setVisibility(View.GONE);
        }else{
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(emptyText);
        }

        adapter.setHasStableIds(true);
        mRecyclerView.swapAdapter(adapter, false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }


    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        if(!isActionMode) {
            isActionMode = true;
            selectedStudents.clear();
        }

        if(snackbar != null && snackbar.isShown()){
            snackbar.dismiss();
        }

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
        MenuItem delete;

        if(getArguments() != null && getArguments().containsKey(CLASS_ID)) {
            edit = menu.findItem(R.id.edit_grade);
            delete = menu.findItem(R.id.delete_student_from_class);
        }else {
            edit = menu.findItem(R.id.edit_student);
            delete = menu.findItem(R.id.delete_student);
        }

        switch(selectedStudents.size()) {
            case 0:
                edit.setVisible(false);
                delete.setVisible(false);
                break;
            case 1:
                edit.setVisible(true);
                delete.setVisible(true);
                break;
            default:
                edit.setVisible(false);
                delete.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if(selectedStudents.size() > 0) {
            switch (item.getItemId()) {
                case R.id.edit_student:
                    editStudent();
                    break;
                case R.id.delete_student:
                    deleteStudents();
                    break;
                case R.id.edit_grade:
                    editGrade();
                    break;
                case R.id.delete_student_from_class:
                    deleteStudentFromClass();
                    break;
            }
            mode.finish();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        isActionMode = false;
        mActionMode = null;
        adapter.clearSelectedItems();
    }

    @Override
    public void onClick(View view) {
        int position = (Integer) view.getTag(R.id.key_position);
        if (mActionMode != null) {
            if(!removeSelectedItem(position)){
                addSelectedItem(position, view);
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        int position = (Integer) view.getTag(R.id.key_position);
        if (mActionMode == null) {
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(this);
            addSelectedItem(position, view);
        }
        return true;
    }


    public void addSelectedItem(int position, View view){
        Bundle values = new Bundle();
        if(view.getTag(R.id.key_student_id) != null) {
            values.putLong(STUDENT_ID, (Long) view.getTag(R.id.key_student_id));
            values.putLong(BIRTH_DATE, (Long) view.getTag(R.id.key_birth_date));
            values.putString(FIRST_NAME, (String) view.getTag(R.id.key_first_name));
            values.putString(LAST_NAME, (String) view.getTag(R.id.key_last_name));
        }
        if(view.getTag(R.id.key_class_student_id) != null) {
            values.putLong(CLASS_STUDENT_ID, (Long) view.getTag(R.id.key_class_student_id));
            values.putString(GRADE, (String) view.getTag(R.id.key_grade));
        }
        selectedStudents.put(position, values);
        adapter.addItem(position);
        editActionModeTitle();
    }

    public boolean removeSelectedItem(int position){
        if(selectedStudents.remove(position) != null){
            adapter.removeItem(position);
            editActionModeTitle();
            return true;
        }
        return false;
    }

    public void editStudent(){
        int position = selectedStudents.keySet().iterator().next();
        Bundle values = selectedStudents.get(position);
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        StudentsNewDialog newFragment = StudentsNewDialog.newInstance(
                R.string.edit_student,
                values.getLong(STUDENT_ID),
                values.getString(FIRST_NAME),
                values.getString(LAST_NAME),
                values.getLong(BIRTH_DATE));
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(R.id.drawer_layout, newFragment, "dialog").addToBackStack(null).commit();
    }

    public void deleteStudents(){
        Bundle values;
        for(Iterator<Integer> iterator = selectedStudents.keySet().iterator(); iterator.hasNext(); ){
            values = selectedStudents.get(iterator.next());
            getActivity().getContentResolver().delete(AppelContract.StudentEntry.CONTENT_URI, AppelContract.StudentEntry._ID + " = " + values.getLong(STUDENT_ID), null);
        }
        snackbar = Snackbar.make(mRecyclerView, getString(R.string.deleted_selection), Snackbar.LENGTH_LONG).
                setAction(getString(R.string.undo), new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ContentValues cv = new ContentValues();
                        Bundle values;
                        for(Iterator<Integer> iterator = selectedStudents.keySet().iterator(); iterator.hasNext(); ){
                            values = selectedStudents.get(iterator.next());
                            cv.put(AppelContract.StudentEntry.COLUMN_DELETED, AppelContract.PUBLIC);
                            getActivity().getContentResolver().update(AppelContract.StudentEntry.CONTENT_URI, cv, AppelContract.StudentEntry._ID + " = " + values.getLong(STUDENT_ID), null);
                            cv.clear();
                        }
                        selectedStudents.clear();
                    }

                }).setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if(event != Snackbar.Callback.DISMISS_EVENT_ACTION){
                    selectedStudents.clear();
                }
                super.onDismissed(snackbar, event);
            }
        });
        snackbar.show();
    }

    public void editGrade(){
        int position = selectedStudents.keySet().iterator().next();
        Bundle values = selectedStudents.get(position);
        FragmentManager fm = getActivity().getFragmentManager();
        ClassStudentsNewDialog classStudentsNewDialog = ClassStudentsNewDialog.newInstance(R.string.edit_grade, values.getLong(CLASS_STUDENT_ID), values.getString(GRADE));
        classStudentsNewDialog.show(fm, "dialog");
    }

    public void deleteStudentFromClass(){
        Bundle values;
        for(Iterator<Integer> iterator = selectedStudents.keySet().iterator(); iterator.hasNext(); ){
            values = selectedStudents.get(iterator.next());
            getActivity().getContentResolver().delete(AppelContract.ClassStudentLinkEntry.CONTENT_URI, AppelContract.ClassStudentLinkEntry._ID + " = " + values.getLong(CLASS_STUDENT_ID), null);
        }
        snackbar = Snackbar.make(mRecyclerView, getString(R.string.deleted_selection), Snackbar.LENGTH_LONG).
                setAction(getString(R.string.undo), new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ContentValues cv = new ContentValues();
                        Bundle values;
                        for(Iterator<Integer> iterator = selectedStudents.keySet().iterator(); iterator.hasNext(); ){
                            values = selectedStudents.get(iterator.next());
                            cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_DELETED, AppelContract.PUBLIC);
                            getActivity().getContentResolver().update(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv, AppelContract.ClassStudentLinkEntry._ID + " = " + values.getLong(CLASS_STUDENT_ID), null);
                            cv.clear();
                        }
                        selectedStudents.clear();
                    }

                }).setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if(event != Snackbar.Callback.DISMISS_EVENT_ACTION){
                    selectedStudents.clear();
                }
                super.onDismissed(snackbar, event);
            }
        });
        snackbar.show();
    }

    private void editActionModeTitle(){
        if(selectedStudents.size() == 0 || selectedStudents.size() == 1){
            mActionMode.setTitle(selectedStudents.size() + " " + getString(R.string.student));
            mActionMode.invalidate();
        }else if(selectedStudents.size() == 2){
            mActionMode.setTitle(selectedStudents.size() + " " + getString(R.string.students));
            mActionMode.invalidate();
        }else{
            mActionMode.setTitle(selectedStudents.size() + " " + getString(R.string.students));
        }
    }
}
