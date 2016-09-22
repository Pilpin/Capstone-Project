package com.sylvainautran.nanodegree.capstoneproject;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
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
import com.sylvainautran.nanodegree.capstoneproject.adapters.ClassesAdapter;
import com.sylvainautran.nanodegree.capstoneproject.adapters.FragmentActionModeListener;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.ClassesLoader;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.StudentsLoader;
import com.sylvainautran.nanodegree.capstoneproject.dialogs.ClassNewDialog;
import com.sylvainautran.nanodegree.capstoneproject.dialogs.ClassStudentsNewDialog;

import java.util.HashMap;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClassesListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, ActionMode.Callback, FragmentActionModeListener {
    private final String LOG_TAG = this.getClass().getSimpleName();

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;

    private HashMap<Integer, Long> selectedClasses;
    private RecyclerView.Adapter adapter;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generic, container, false);
        ButterKnife.bind(this, view);
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

        adapter = new ClassesAdapter((AppCompatActivity) getActivity(), cursor, this, headers);

        if(adapter.getItemCount() > 0){
            emptyView.setVisibility(View.GONE);
        }else{
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(R.string.empty_class_list);
        }


        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.classes_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        MenuItem edit = menu.findItem(R.id.edit_class);
        if(selectedClasses.size() > 1){
            edit.setVisible(false);
        }else{
            edit.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if(selectedClasses.size() > 0) {
            switch (item.getItemId()) {
                case R.id.edit_class:
                    Iterator it1 = selectedClasses.keySet().iterator();
                    int position = (Integer) it1.next();
                    long id = selectedClasses.get(position);
                    HashMap<String, String> values = ((AdapterActionModeListener) adapter).getValues(position);
                    String class_name = values.get(ClassesAdapter.CLASS_NAME);

                    FragmentManager fm = ((AppCompatActivity) getActivity()).getSupportFragmentManager();
                    ClassNewDialog frag = ClassNewDialog.newInstance(id, class_name);
                    frag.show(fm, "dialog");
                    break;
                case R.id.delete_class:
                    Iterator it2 = selectedClasses.values().iterator();
                    String ids = "(" + it2.next().toString();
                    while (it2.hasNext()) {
                        ids += ", " + it2.next().toString();
                    }
                    ids += ")";
                    Log.e(LOG_TAG, "IDs = " + ids);
                    getActivity().getContentResolver().delete(AppelContract.ClassEntry.CONTENT_URI, AppelContract.ClassEntry._ID + " IN " + ids, null);
                    Snackbar.make(mRecyclerView, "Deleted Saved Selection.", Snackbar.LENGTH_LONG).
                            setAction("Undo", new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    Iterator it = selectedClasses.values().iterator();
                                    String ids = "(" + it.next().toString();
                                    while (it.hasNext()) {
                                        ids += ", " + it.next().toString();
                                    }
                                    ids += ")";
                                    ContentValues cv = new ContentValues();
                                    cv.put(AppelContract.ClassEntry.COLUMN_DELETED, AppelContract.PUBLIC);
                                    int t = getActivity().getContentResolver().update(AppelContract.ClassEntry.CONTENT_URI, cv, AppelContract.ClassEntry._ID + " IN " + ids, null);
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
        selectedClasses.put(position, id);
    }

    @Override
    public void removeSelectedItem(int position, long id) {
        selectedClasses.remove(position);
    }

    @Override
    public void clearSelectedItems() {
        selectedClasses.clear();
    }
}
