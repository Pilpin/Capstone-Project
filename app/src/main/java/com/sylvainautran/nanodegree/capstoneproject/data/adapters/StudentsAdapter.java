package com.sylvainautran.nanodegree.capstoneproject.data.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.StudentsLoader;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentsAdapter extends BaseAdapter<StudentsAdapter.ViewHolder> {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private HashMap<Long, Character> mHeaders;

    public StudentsAdapter(Context context, Cursor cursor, Set<Integer> selectedItems, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener, HashMap<Long, Character>
            headers) {
        super(context, cursor, selectedItems, onClickListener, onLongClickListener);
        mHeaders = headers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_student, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        vh.itemView.setOnLongClickListener(mOnLongClickListener);
        vh.itemView.setOnClickListener(mOnClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        if(mHeaders != null && mHeaders.containsKey(mCursor.getLong(StudentsLoader.Query._ID))){
            holder.first_letter.setText(Character.toString(mHeaders.get(mCursor.getLong(StudentsLoader.Query._ID))));
        }else{
            holder.first_letter.setText("");
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mCursor.getLong(StudentsLoader.Query.COLUMN_BIRTHDATE));
        Calendar today = Calendar.getInstance();
        String name = mCursor.getString(StudentsLoader.Query.COLUMN_FIRSTNAME) + " " + mCursor.getString(StudentsLoader.Query.COLUMN_LASTNAME);
        int age = today.get(Calendar.YEAR) - cal.get(Calendar.YEAR);
        String age_grade = mContext.getResources().getString(R.string.age_to_string, age);
        if(age < 2){
            age_grade = mContext.getResources().getString(R.string.age_to_string_singular, age);
        }

        holder.name.setText(name);
        holder.age_grade.setText(age_grade);
        holder.itemView.setActivated(mSelectedItems.contains(position));

        tagView(holder.itemView, position, mCursor.getLong(StudentsLoader.Query._ID), mCursor.getString(StudentsLoader.Query.COLUMN_FIRSTNAME), mCursor.getString(StudentsLoader
                .Query.COLUMN_LASTNAME), mCursor.getLong(StudentsLoader.Query.COLUMN_BIRTHDATE));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.first_letter) public TextView first_letter;
        @BindView(R.id.name) public TextView name;
        @BindView(R.id.age_grade) public TextView age_grade;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private void tagView(View v, int position, long studentId, String firstName, String lastName, long birthDate){
        v.setTag(R.id.key_position, position);
        v.setTag(R.id.key_student_id, studentId);
        v.setTag(R.id.key_first_name, firstName);
        v.setTag(R.id.key_last_name, lastName);
        v.setTag(R.id.key_birth_date, birthDate);
    }
}
