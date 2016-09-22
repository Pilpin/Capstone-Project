package com.sylvainautran.nanodegree.capstoneproject;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
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
import com.sylvainautran.nanodegree.capstoneproject.adapters.CallsAdapter;
import com.sylvainautran.nanodegree.capstoneproject.adapters.ClassesAdapter;
import com.sylvainautran.nanodegree.capstoneproject.adapters.FragmentActionModeListener;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.CallsLoader;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.ClassesLoader;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, ActionMode.Callback, FragmentActionModeListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;

    private HashMap<Integer, Long> selectedCalls;
    private RecyclerView.Adapter adapter;

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

        adapter = new CallsAdapter((AppCompatActivity) getActivity(), cursor, this, headers);

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
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.calls_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        MenuItem edit = menu.findItem(R.id.edit_call);
        if(selectedCalls.size() > 1){
            edit.setVisible(false);
        }else{
            edit.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if(selectedCalls.size() > 0) {
            switch (item.getItemId()) {
                case R.id.edit_call:
                    Iterator it1 = selectedCalls.keySet().iterator();
                    int position = (Integer) it1.next();
                    final long id = selectedCalls.get(position);

                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View dialog = inflater.inflate(R.layout.dialog_class_new, null);
                    TextView title = ButterKnife.findById(dialog, R.id.title);
                    title.setText(R.string.add_class);
                    final TextInputEditText editText = ButterKnife.findById(dialog, R.id.name);
                    editText.setText((String) ((AdapterActionModeListener) adapter).getValues(position).get(ClassesAdapter.CLASS_NAME));

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_AlertDialog);
                    builder.setView(dialog);
                    builder.setPositiveButton(R.string.add_class, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ContentValues cv = new ContentValues();
                            cv.put(AppelContract.ClassEntry.COLUMN_NAME, editText.getText().toString());
                            getActivity().getApplicationContext().getContentResolver().update(AppelContract.ClassEntry.CONTENT_URI, cv, AppelContract.ClassEntry._ID + " = ?", new String[] { Long.toString(id) });
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, null);
                    builder.show();
                    break;
                case R.id.delete_call:
                    Iterator it2 = selectedCalls.values().iterator();
                    String ids = "(" + it2.next().toString();
                    while (it2.hasNext()) {
                        ids += ", " + it2.next().toString();
                    }
                    ids += ")";
                    getActivity().getContentResolver().delete(AppelContract.CallEntry.CONTENT_URI, AppelContract.CallEntry._ID + " IN " + ids, null);
                    Snackbar.make(mRecyclerView, "Deleted Saved Selection.", Snackbar.LENGTH_LONG).
                            setAction("Undo", new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    Iterator it = selectedCalls.values().iterator();
                                    String ids = "(" + it.next().toString();
                                    while (it.hasNext()) {
                                        ids += ", " + it.next().toString();
                                    }
                                    ids += ")";
                                    ContentValues cv = new ContentValues();
                                    cv.put(AppelContract.CallEntry.COLUMN_DELETED, AppelContract.PUBLIC);
                                    int t = getActivity().getContentResolver().update(AppelContract.CallEntry.CONTENT_URI, cv, AppelContract.CallEntry._ID + " IN " + ids, null);
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
        selectedCalls.put(position, id);
    }

    @Override
    public void removeSelectedItem(int position, long id) {
        selectedCalls.remove(position);
    }

    @Override
    public void clearSelectedItems() {
        selectedCalls.clear();
    }
}
