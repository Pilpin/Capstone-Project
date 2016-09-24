package com.sylvainautran.nanodegree.capstoneproject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
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

import com.sylvainautran.nanodegree.capstoneproject.data.adapters.ClassesAdapter;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.ClassesLoader;
import com.sylvainautran.nanodegree.capstoneproject.dialogs.ClassNewDialog;
import com.sylvainautran.nanodegree.capstoneproject.utils.AdapterKeys;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClassesListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, ActionMode.Callback, View.OnClickListener, View.OnLongClickListener {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private final String SAVE_SELECTED_ITEMS = "selected_items";
    private final String SAVE_ACTION_MODE_STATE = "action_mode";

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;

    private boolean isActionMode;
    private ActionMode mActionMode;
    private ClassesAdapter adapter;
    private HashMap<Integer, String[]> selectedClasses;
    private Snackbar snackbar;

    public ClassesListFragment() {
        selectedClasses = new HashMap<>();
    }

    public static ClassesListFragment newInstance() {
        ClassesListFragment fragment = new ClassesListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            isActionMode = savedInstanceState.getBoolean(SAVE_ACTION_MODE_STATE, false);
            selectedClasses = (HashMap<Integer, String[]>) savedInstanceState.getSerializable(SAVE_SELECTED_ITEMS);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SAVE_SELECTED_ITEMS, selectedClasses);
        outState.putSerializable(SAVE_ACTION_MODE_STATE, isActionMode);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generic, container, false);
        ButterKnife.bind(this, view);

        if(isActionMode){
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(this);
        }

        getLoaderManager().initLoader(0, null, this);
        return view;
    }

    @Override
    public CursorLoader onCreateLoader(int id, Bundle args) {
        return ClassesLoader.getAllClasses(getActivity());
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        HashMap<Long, Character> headers = null;
        if(cursor != null && cursor.moveToFirst()){
            headers = new HashMap<>();
            char first_letter = cursor.getString(ClassesLoader.Query.COLUMN_NAME).charAt(0);
            headers.put(cursor.getLong(ClassesLoader.Query._ID), first_letter);
            while(cursor.moveToNext()){
                char new_first_letter = cursor.getString(ClassesLoader.Query.COLUMN_NAME).charAt(0);
                if(first_letter != new_first_letter){
                    first_letter = new_first_letter;
                    headers.put(cursor.getLong(ClassesLoader.Query._ID), first_letter);
                }
            }

        }

        Set<Integer> selectedPositions = new HashSet<>();
        if(isActionMode){
            selectedPositions = selectedClasses.keySet();
        }
        adapter = new ClassesAdapter(getActivity(), cursor, selectedPositions, this, this, headers);

        if(adapter.getItemCount() > 0){
            emptyView.setVisibility(View.GONE);
        }else{
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(R.string.empty_class_list);
        }

        adapter.setHasStableIds(true);
        mRecyclerView.swapAdapter(adapter, false);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        if(!isActionMode) {
            isActionMode = true;
            selectedClasses.clear();
        }

        if(snackbar != null && snackbar.isShown()){
            snackbar.dismiss();
        }

        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.classes_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        MenuItem edit = menu.findItem(R.id.edit_class);
        MenuItem delete = menu.findItem(R.id.delete_class);

        switch(selectedClasses.size()) {
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
        if(selectedClasses.size() > 0) {
            switch (item.getItemId()) {
                case R.id.edit_class:
                    editClass();
                    break;
                case R.id.delete_class:
                    deleteClasses();
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
        int position = Integer.parseInt((String) view.getTag(R.id.key_position));
        if (mActionMode != null) {
            if(!removeSelectedItem(position)){
                addSelectedItem(position, view);
            }
        }else{
            int id = Integer.parseInt((String) view.getTag(R.id.key_class_id));
            String className = (String) view.getTag(R.id.key_class_name);
            Intent intent = new Intent(Intent.ACTION_VIEW, AppelContract.ClassStudentLinkEntry.buildClassStudentLinkFromClassUri(id));
            intent.putExtra(ClassStudentsListActivity.CLASS_NAME, className);
            getActivity().startActivity(intent);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (mActionMode == null) {
            int position = Integer.parseInt((String) view.getTag(R.id.key_position));
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(this);
            addSelectedItem(position, view);
        }
        return true;
    }


    public void addSelectedItem(int position, View view){
        String[] values = new String[AdapterKeys.KEYS_COUNT];
        values[AdapterKeys.key_class_id] = (String) view.getTag(R.id.key_class_id);
        values[AdapterKeys.key_class_name] = (String) view.getTag(R.id.key_class_name);
        selectedClasses.put(position, values);
        adapter.addItem(position);
        if(selectedClasses.size() == 0 || selectedClasses.size() == 1 || selectedClasses.size() == 2){
            mActionMode.invalidate();
        }
    }

    public boolean removeSelectedItem(int position) {
        if (selectedClasses.remove(position) != null) {
            adapter.removeItem(position);
            if (selectedClasses.size() == 0 || selectedClasses.size() == 1 || selectedClasses.size() == 2) {
                mActionMode.invalidate();
            }
            return true;
        }
        return false;
    }

    public void editClass(){
        int position = selectedClasses.keySet().iterator().next();
        String[] values = selectedClasses.get(position);
        long id = Long.parseLong(values[AdapterKeys.key_class_id]);
        Log.d(LOG_TAG, "position " + position + " id " + id);
        FragmentManager fragmentManager = ((AppCompatActivity) getActivity()).getSupportFragmentManager();
        ClassNewDialog classNewDialog = ClassNewDialog.newInstance(id, values[AdapterKeys.key_class_name]);
        classNewDialog.show(fragmentManager, "dialog");
    }

    public void deleteClasses(){
        for(Iterator<Integer> iterator = selectedClasses.keySet().iterator(); iterator.hasNext(); ){
            String[] values = selectedClasses.get(iterator.next());
            getActivity().getContentResolver().delete(AppelContract.ClassEntry.CONTENT_URI, AppelContract.ClassEntry._ID + " = " + values[AdapterKeys.key_class_id], null);
        }
        snackbar = Snackbar.make(mRecyclerView, "Deleted Saved Selection.", Snackbar.LENGTH_LONG).
                setAction("Undo", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ContentValues cv = new ContentValues();
                        for(Iterator<Integer> iterator = selectedClasses.keySet().iterator(); iterator.hasNext(); ){
                            String[] values = selectedClasses.get(iterator.next());
                            cv.put(AppelContract.ClassEntry.COLUMN_DELETED, AppelContract.PUBLIC);
                            getActivity().getContentResolver().update(AppelContract.ClassEntry.CONTENT_URI, cv, AppelContract.ClassEntry._ID + " = " + values[AdapterKeys.key_class_id], null);
                            cv.clear();
                        }
                        selectedClasses.clear();
                    }

                }).setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if(event != Snackbar.Callback.DISMISS_EVENT_ACTION){
                    selectedClasses.clear();
                }
                super.onDismissed(snackbar, event);
            }
        });
        snackbar.show();
    }
}
