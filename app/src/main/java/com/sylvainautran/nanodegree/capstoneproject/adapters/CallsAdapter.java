package com.sylvainautran.nanodegree.capstoneproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sylvainautran.nanodegree.capstoneproject.CallsDetailsActivity;
import com.sylvainautran.nanodegree.capstoneproject.ClassStudentsListActivity;
import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.CallsLoader;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.StudentsLoader;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallsAdapter extends RecyclerView.Adapter<CallsAdapter.ViewHolder> implements AdapterActionModeListener {
    public static final String CALL_DATE = "call_date";
    public static final String CLASS_ID = "class_id";

    private Cursor mCursor;
    private AppCompatActivity mActivity;
    private ActionMode mActionMode;
    private FragmentActionModeListener mActionModeListener;
    private HashMap<Integer, Long> selectedCalls;

    public CallsAdapter(AppCompatActivity activity, Cursor cursor, FragmentActionModeListener actionModeListener) {
        mCursor = cursor;
        mActivity = activity;
        mActionModeListener = actionModeListener;
        selectedCalls = new HashMap<>();
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(CallsLoader.Query._ID);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.list_item_call, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mActionMode != null){
                    if(!removeSelectedItem(vh.getAdapterPosition(), getItemId(vh.getAdapterPosition()))){
                        addSelectedItem(vh.getAdapterPosition(), getItemId(vh.getAdapterPosition()));
                    }
                }else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, AppelContract.CallStudentLinkEntry.buildCallStudentLinkUriWithCallAndClass(getClassId(vh.getAdapterPosition()), getItemId(vh.getAdapterPosition())));
                    intent.putExtra(CallsDetailsActivity.CLASS_NAME, vh.class_name.getText());
                    intent.putExtra(CallsDetailsActivity.CALL_DATE, getCallDate(vh.getAdapterPosition()));
                    mActivity.startActivity(intent);
                }
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(mActionMode == null) {
                    mActionMode = mActivity.startSupportActionMode(mActionModeListener);
                    addSelectedItem(vh.getAdapterPosition(), getItemId(vh.getAdapterPosition()));
                }
                return true;
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setActivated(selectedCalls.keySet().contains(position));
        mCursor.moveToPosition(position);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mCursor.getLong(CallsLoader.Query.COLUMN_DATETIME));
        DateFormat df = DateFormat.getInstance();
        String time = df.format(cal.getTime());
        holder.class_name.setText(mCursor.getString(CallsLoader.Query.COLUMN_CLASS_NAME));
        holder.call_date.setText(time);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    @Override
    public void clearSelectedItems() {
        Iterator it = selectedCalls.keySet().iterator();
        while(it.hasNext()){
            notifyItemChanged((Integer) it.next());
        }
        selectedCalls.clear();
        mActionMode = null;
    }

    @Override
    public HashMap getValues(int position){
        mCursor.moveToPosition(position);
        HashMap<String, String> values = new HashMap<>();
        values.put(CLASS_ID, Long.toString(mCursor.getLong(CallsLoader.Query.COLUMN_CLASS_ID)));
        values.put(CALL_DATE, Long.toString(mCursor.getLong(CallsLoader.Query.COLUMN_DATETIME)));
        return values;
    }

    public void addSelectedItem(int position, long id){
        mActionModeListener.addSelectedItem(position, id);
        selectedCalls.put(position, id);
        notifyItemChanged(position);
        mActionMode.setTitle(Integer.toString(selectedCalls.size()));
        if(selectedCalls.size() == 2){
            mActionMode.invalidate();
        }
    }

    public boolean removeSelectedItem(int position, long id){
        mActionModeListener.removeSelectedItem(position, id);
        if(selectedCalls.remove(position) != null){
            mActionMode.setTitle(Integer.toString(selectedCalls.size()));
            if(selectedCalls.size() == 1){
                mActionMode.invalidate();
            }
            notifyItemChanged(position);
            return true;
        }
        return false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.class_name) public TextView class_name;
        @BindView(R.id.call_date) public TextView call_date;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private long getClassId(int position){
        mCursor.moveToPosition(position);
        return mCursor.getLong(CallsLoader.Query.COLUMN_CLASS_ID);
    }

    private long getCallDate(int position){
        mCursor.moveToPosition(position);
        return mCursor.getLong(CallsLoader.Query.COLUMN_DATETIME);
    }
}