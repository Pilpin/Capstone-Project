package com.sylvainautran.nanodegree.capstoneproject;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

import com.sylvainautran.nanodegree.capstoneproject.adapters.CallsAdapter;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.CallsLoader;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.ClassesLoader;
import com.sylvainautran.nanodegree.capstoneproject.utils.AdapterKeys;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, ActionMode.Callback, View.OnClickListener, View.OnLongClickListener {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private final String SAVE_SELECTED_ITEMS = "selected_items";
    private final String SAVE_ACTION_MODE_STATE = "action_mode";

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;

    private boolean isActionMode;
    private ActionMode mActionMode;
    private CallsAdapter adapter;
    private HashMap<Integer, String[]> selectedCalls;
    private Snackbar snackbar;

    public CallsListFragment() {
        selectedCalls = new HashMap<>();
    }

    public static CallsListFragment newInstance() {
        CallsListFragment fragment = new CallsListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            isActionMode = savedInstanceState.getBoolean(SAVE_ACTION_MODE_STATE, false);
            selectedCalls = (HashMap<Integer, String[]>) savedInstanceState.getSerializable(SAVE_SELECTED_ITEMS);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SAVE_SELECTED_ITEMS, selectedCalls);
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
        return CallsLoader.getAllCalls(getActivity());
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        HashMap<Long, String> headers = null;
        if(cursor != null && cursor.moveToFirst()){
            headers = new HashMap<>();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(cursor.getLong(CallsLoader.Query.COLUMN_DATETIME));
            String month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) + "\n" + cal.get(Calendar.YEAR);
            headers.put(cursor.getLong(ClassesLoader.Query._ID), month);
            while(cursor.moveToNext()){
                String new_month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) + "\n" + cal.get(Calendar.YEAR);
                if(!new_month.equals(month)){
                    month = new_month;
                    headers.put(cursor.getLong(ClassesLoader.Query._ID), month);
                }
            }

        }

        Set<Integer> selectedPositions = new HashSet<>();
        if(isActionMode){
            selectedPositions = selectedCalls.keySet();
        }
        adapter = new CallsAdapter(getActivity(), cursor, selectedPositions, this, this, headers);

        if(adapter.getItemCount() > 0){
            emptyView.setVisibility(View.GONE);
        }else{
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(R.string.empty_call_list);
        }

        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        if(!isActionMode) {
            isActionMode = true;
            selectedCalls.clear();
        }

        if(snackbar != null && snackbar.isShown()){
            snackbar.dismiss();
        }

        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.calls_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        MenuItem delete = menu.findItem(R.id.delete_call);

        switch(selectedCalls.size()) {
            case 0:
                delete.setVisible(false);
                break;
            default:
                delete.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if(selectedCalls.size() > 0) {
            switch (item.getItemId()) {
                case R.id.delete_call:
                    for(Iterator<Integer> iterator = selectedCalls.keySet().iterator(); iterator.hasNext(); ){
                        String[] values = selectedCalls.get(iterator.next());
                        getActivity().getContentResolver().delete(AppelContract.CallEntry.CONTENT_URI, AppelContract.CallEntry._ID + " = " + values[AdapterKeys.key_call_id], null);
                    }
                    snackbar = Snackbar.make(mRecyclerView, "Deleted Saved Selection.", Snackbar.LENGTH_LONG).
                            setAction("Undo", new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    ContentValues cv = new ContentValues();
                                    for(Iterator<Integer> iterator = selectedCalls.keySet().iterator(); iterator.hasNext(); ){
                                        String[] values = selectedCalls.get(iterator.next());
                                        cv.put(AppelContract.CallEntry.COLUMN_DELETED, AppelContract.PUBLIC);
                                        getActivity().getContentResolver().update(AppelContract.CallEntry.CONTENT_URI, cv, AppelContract.CallEntry._ID + " = " + values[AdapterKeys.key_call_id], null);
                                        cv.clear();
                                    }
                                    selectedCalls.clear();
                                }

                            }).setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            if(event != Snackbar.Callback.DISMISS_EVENT_ACTION){
                                selectedCalls.clear();
                            }
                            super.onDismissed(snackbar, event);
                        }
                    });
                    snackbar.show();
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
        }else {
            int callId = Integer.parseInt((String) view.getTag(R.id.key_call_id));
            int classId = Integer.parseInt((String) view.getTag(R.id.key_class_id));
            long callDate = Long.parseLong((String) view.getTag(R.id.key_call_datetime));
            String className = (String) view.getTag(R.id.key_class_name);
            Intent intent = new Intent(Intent.ACTION_VIEW, AppelContract.CallStudentLinkEntry.buildCallStudentLinkUriWithCallAndClass(classId, callId));
            intent.putExtra(CallsDetailsActivity.CLASS_NAME, className);
            intent.putExtra(CallsDetailsActivity.CALL_DATE, callDate);
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
        final String[] values = new String[13];
        values[AdapterKeys.key_call_id] = (String) view.getTag(R.id.key_call_id);
        values[AdapterKeys.key_class_id] = (String) view.getTag(R.id.key_class_id);
        values[AdapterKeys.key_call_datetime] = (String) view.getTag(R.id.key_call_datetime);
        values[AdapterKeys.key_class_name] = (String) view.getTag(R.id.key_class_name);
        selectedCalls.put(position, values);
        adapter.addItem(position);
        if(selectedCalls.size() == 0 || selectedCalls.size() == 1 || selectedCalls.size() == 2){
            mActionMode.invalidate();
        }
    }

    public boolean removeSelectedItem(int position) {
        if (selectedCalls.remove(position) != null) {
            adapter.removeItem(position);
            if (selectedCalls.size() == 0 || selectedCalls.size() == 1 || selectedCalls.size() == 2) {
                mActionMode.invalidate();
            }
            return true;
        }
        return false;
    }
}
