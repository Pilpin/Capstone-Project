package com.sylvainautran.nanodegree.capstoneproject.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.data.adapters.BaseAdapter;
import com.sylvainautran.nanodegree.capstoneproject.data.adapters.CallMonthsAdapter;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.CallsLoader;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallMonthsPickerDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private final String LOG_TAG = this.getClass().getSimpleName();
    public static final String CLASS_ID = "class_id";
    public static final String MONTH_NAME = "month_name";
    public static final String MONTH_NUMBER = "month_number";
    public static final String YEAR = "year";
    public static final String CALLS_START_DATE = "call_start_date";
    public static final String CALLS_END_DATE = "call_end_date";

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;

    private BaseAdapter adapter;
    private DialogListener mListener;

    public CallMonthsPickerDialog(){
    }

    public static CallMonthsPickerDialog newInstance(long classId) {
        CallMonthsPickerDialog fragment = new CallMonthsPickerDialog();
        Bundle bundle = new Bundle();
        bundle.putLong(CLASS_ID, classId);
        fragment.setArguments(bundle);
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
        builder.setTitle(R.string.select_month);
        builder.setView(view).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CallMonthsPickerDialog.this.dismiss();
                    }
                });
        emptyView.setText(R.string.empty_call_month_picker_list);

        getLoaderManager().initLoader(0, null, this);

        return builder.create();
    }

    @Override
    public CursorLoader onCreateLoader(int id, Bundle args) {
        return CallsLoader.getAllCallMonthsFromClass(getActivity(), getArguments().getLong(CLASS_ID));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        HashMap<Long, String> headers = null;
        if(cursor != null && cursor.moveToFirst()){
            headers = new HashMap<>();
            String year = cursor.getString(CallsLoader.Query.YEAR);
            headers.put(cursor.getLong(CallsLoader.Query._ID), year);
            while(cursor.moveToNext()){
                String new_year = cursor.getString(CallsLoader.Query.YEAR);
                if(!new_year.equals(year)){
                    year = new_year;
                    headers.put(cursor.getLong(CallsLoader.Query._ID), cursor.getString(CallsLoader.Query.YEAR));
                }
            }
        }

        adapter = new CallMonthsAdapter(getActivity(), cursor, null, this, null, headers);

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
        bundle.putString(MONTH_NUMBER, (String) view.getTag(R.id.key_month_number));
        bundle.putString(MONTH_NAME, (String) view.getTag(R.id.key_month_name));
        bundle.putInt(YEAR, (Integer) view.getTag(R.id.key_year));
        bundle.putLong(CALLS_START_DATE, (Long) view.getTag(R.id.key_call_date_start));
        bundle.putLong(CALLS_END_DATE, (Long) view.getTag(R.id.key_call_date_end));
        mListener.onDismissDialog(bundle);
        dismiss();
    }

    public void setListener(DialogListener listener){
        mListener = listener;
    }
}
