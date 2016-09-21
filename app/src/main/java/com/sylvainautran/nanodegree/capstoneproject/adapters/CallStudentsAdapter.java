package com.sylvainautran.nanodegree.capstoneproject.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.CallsLoader;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallStudentsAdapter extends RecyclerView.Adapter<CallStudentsAdapter.ViewHolder> {
    private Cursor mCursor;
    private Context mContext;

    public CallStudentsAdapter(Context context, Cursor cursor) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_cardview_call, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mCursor.getLong(CallsLoader.Query.COLUMN_BIRTHDATE));
        Calendar today = Calendar.getInstance();
        String name = mCursor.getString(CallsLoader.Query.COLUMN_FIRSTNAME) + " " + mCursor.getString(CallsLoader.Query.COLUMN_LASTNAME);
        int age = today.get(Calendar.YEAR) - cal.get(Calendar.YEAR);
        String grade = mCursor.getString(CallsLoader.Query.COLUMN_GRADE);
        grade = grade.isEmpty() ? grade : "- " + grade;
        String age_grade = mContext.getResources().getString(R.string.age_to_string, age, grade);
        if(age < 2){
            age_grade = mContext.getResources().getString(R.string.age_to_string_singular, age, grade);
        }
        holder.name.setText(name);
        holder.age_grade.setText(age_grade);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) public TextView name;
        @BindView(R.id.age_grade) public TextView age_grade;
        @BindView(R.id.absent) public Button absent;
        @BindView(R.id.present) public Button present;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}