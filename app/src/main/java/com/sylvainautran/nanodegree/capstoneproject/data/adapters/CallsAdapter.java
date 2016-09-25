package com.sylvainautran.nanodegree.capstoneproject.data.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.CallsLoader;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.ClassesLoader;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallsAdapter extends BaseAdapter<CallsAdapter.ViewHolder> {
    private HashMap<Long, String> mHeaders;

    public CallsAdapter(Context context, Cursor cursor, Set<Integer> selectedItems, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener, HashMap<Long, String> headers) {
        super(context, cursor, selectedItems, onClickListener, onLongClickListener);
        mHeaders = headers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_call, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        vh.itemView.setOnLongClickListener(mOnLongClickListener);
        vh.itemView.setOnClickListener(mOnClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        if(mHeaders != null && mHeaders.containsKey(mCursor.getLong(ClassesLoader.Query._ID))){
            holder.month.setText(mHeaders.get(mCursor.getLong(ClassesLoader.Query._ID)));
        }else{
            holder.month.setText("");
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mCursor.getLong(CallsLoader.Query.COLUMN_DATETIME));
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        String dateTime = df.format(cal.getTime());
        holder.class_name.setText(mCursor.getString(CallsLoader.Query.COLUMN_CLASS_NAME));
        holder.call_date.setText(dateTime);
        holder.itemView.setActivated(mSelectedItems.contains(position));

        tagView(holder.itemView, position, mCursor.getLong(ClassesLoader.Query._ID), mCursor.getLong(CallsLoader.Query.COLUMN_CLASS_ID), mCursor.getString(CallsLoader.Query.COLUMN_CLASS_NAME),
                mCursor.getLong(CallsLoader.Query.COLUMN_DATETIME));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.month) public TextView month;
        @BindView(R.id.class_name) public TextView class_name;
        @BindView(R.id.call_date) public TextView call_date;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private void tagView(View v, int position, long callId, long classId, String className, long callDate){
        v.setTag(R.id.key_position, position);
        v.setTag(R.id.key_call_id, callId);
        v.setTag(R.id.key_class_id, classId);
        v.setTag(R.id.key_class_name, className);
        v.setTag(R.id.key_call_datetime, callDate);
    }
}