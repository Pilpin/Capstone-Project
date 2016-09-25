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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.data.adapters.BaseAdapter;
import com.sylvainautran.nanodegree.capstoneproject.data.adapters.ClassesAdapter;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.ClassesLoader;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClassPickerDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private final String LOG_TAG = this.getClass().getSimpleName();
    public static final String CLASS_ID = "class_id";
    public static final String CLASS_NAME = "class_name";

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;

    private BaseAdapter adapter;
    private DialogListener mListener;

    public ClassPickerDialog(){
    }

    public static ClassPickerDialog newInstance() {
        ClassPickerDialog fragment = new ClassPickerDialog();
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_AlertDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_picker, null, false);
        ButterKnife.bind(this, view);
        mRecyclerView.setAdapter(new BaseAdapter(getActivity(), null, null, null, null));
        builder.setTitle(R.string.select_class);
        builder.setView(view).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ClassPickerDialog.this.dismiss();
                    }
                });
        emptyView.setText(R.string.empty_class_list);

        getLoaderManager().initLoader(0, null, this);

        return builder.create();
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
            char first_letter = Character.toUpperCase(cursor.getString(ClassesLoader.Query.COLUMN_NAME).charAt(0));
            headers.put(cursor.getLong(ClassesLoader.Query._ID), first_letter);
            while(cursor.moveToNext()){
                char new_first_letter = Character.toUpperCase(cursor.getString(ClassesLoader.Query.COLUMN_NAME).charAt(0));
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
        Bundle bundle = new Bundle();
        bundle.putLong(CLASS_ID, (Long) view.getTag(R.id.key_class_id));
        bundle.putString(CLASS_NAME, (String) view.getTag(R.id.key_class_name));
        mListener.onDismissDialog(bundle);
        dismiss();
    }

    public void setListener(DialogListener listener){
        mListener = listener;
    }
}
