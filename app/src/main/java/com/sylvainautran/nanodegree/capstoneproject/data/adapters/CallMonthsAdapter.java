package com.sylvainautran.nanodegree.capstoneproject.data.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import java.util.Locale;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallMonthsAdapter extends BaseAdapter<CallMonthsAdapter.ViewHolder> {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private HashMap<Long, String> mHeaders;

    public CallMonthsAdapter(Context context, Cursor cursor, @Nullable Set<Integer> selectedItems, View.OnClickListener onClickListener, @Nullable View.OnLongClickListener onLongClickListener,
                             HashMap<Long, String> headers) {
        super(context, cursor, selectedItems, onClickListener, onLongClickListener);
        mHeaders = headers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_call_month, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        vh.itemView.setOnLongClickListener(mOnLongClickListener);
        vh.itemView.setOnClickListener(mOnClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        if(mHeaders != null && mHeaders.containsKey(mCursor.getLong(CallsLoader.Query._ID))){
            holder.year.setText(mHeaders.get(mCursor.getLong(CallsLoader.Query._ID)));
        }else{
            holder.year.setText("");
        }

        Calendar startDate = Calendar.getInstance();
        startDate.set(mCursor.getInt(CallsLoader.Query.YEAR), Integer.parseInt(mCursor.getString(CallsLoader.Query.MONTH)), 1, 0, 0, 0);
        startDate.set(Calendar.MILLISECOND, 0);
        holder.month.setText(startDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
        holder.itemView.setActivated(mSelectedItems.contains(position));

        Calendar endDate = (Calendar) startDate.clone();
        endDate.set(Calendar.DAY_OF_MONTH, startDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        endDate.add(Calendar.DAY_OF_MONTH, 1);

        tagView(holder.itemView, Integer.toString(position),
                mCursor.getString(CallsLoader.Query._ID),
                Long.toString(startDate.getTimeInMillis()),
                Long.toString(endDate.getTimeInMillis()));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.month) public TextView month;
        @BindView(R.id.year) public TextView year;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private void tagView(View v, String position, String callId, String dateStart, String dateEnd){
        v.setTag(R.id.key_position, position);
        v.setTag(R.id.key_call_id, callId);
        v.setTag(R.id.key_call_date_start, dateStart);
        v.setTag(R.id.key_call_date_end, dateEnd);
    }
}