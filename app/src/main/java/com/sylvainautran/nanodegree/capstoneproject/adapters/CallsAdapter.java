package com.sylvainautran.nanodegree.capstoneproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sylvainautran.nanodegree.capstoneproject.CallsDetailsActivity;
import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;
import com.sylvainautran.nanodegree.capstoneproject.loaders.CallsLoader;
import com.sylvainautran.nanodegree.capstoneproject.loaders.ClassesLoader;

import java.text.DateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallsAdapter extends RecyclerView.Adapter<CallsAdapter.ViewHolder> {
    private Cursor mCursor;
    private Context mContext;

    public CallsAdapter(Context context, Cursor cursor) {
        mCursor = cursor;
        mContext = context;

    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(CallsLoader.Query._ID);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_call, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, AppelContract.CallStudentLinkEntry.buildCallStudentLinkUriWithCallAndClass(getClassId(vh.getAdapterPosition()), getItemId(vh.getAdapterPosition()
                )));
                intent.putExtra(CallsDetailsActivity.CLASS_NAME, vh.class_name.getText());
                intent.putExtra(CallsDetailsActivity.CALL_DATE, getCallDate(vh.getAdapterPosition()));
                mContext.startActivity(intent);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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