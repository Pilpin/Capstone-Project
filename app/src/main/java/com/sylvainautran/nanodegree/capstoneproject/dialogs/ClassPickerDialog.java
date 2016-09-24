package com.sylvainautran.nanodegree.capstoneproject.dialogs;

import android.app.Dialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.adapters.BaseAdapter;
import com.sylvainautran.nanodegree.capstoneproject.adapters.ClassesAdapter;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.ClassesLoader;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClassPickerDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private final String LOG_TAG = this.getClass().getSimpleName();

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;

    private BaseAdapter adapter;

    public ClassPickerDialog(){
    }

    public static ClassPickerDialog newInstance() {
        ClassPickerDialog fragment = new ClassPickerDialog();
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_class_picker, null, false);
        ButterKnife.bind(this, view);

        builder.setTitle(R.string.select_class);
        builder.setView(view).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ClassPickerDialog.this.dismiss();
                    }
                });

        getLoaderManager().initLoader(0, null, this);

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        //getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public CursorLoader onCreateLoader(int id, Bundle args) {
        return ClassesLoader.getAllClasses(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
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

        adapter = new ClassesAdapter(getActivity(), cursor, null, this, null, headers);

        if(adapter.getItemCount() > 0){
            emptyView.setVisibility(View.GONE);
        }else{
            emptyView.setVisibility(View.VISIBLE);
        }

        adapter.setHasStableIds(true);
        mRecyclerView.swapAdapter(adapter, false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(getActivity(), (String) view.getTag(R.id.key_class_id) + " " + (String) view.getTag(R.id.key_class_name), Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
