package com.sylvainautran.nanodegree.capstoneproject.adapters;

import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.StudentsNewDialog;
import com.sylvainautran.nanodegree.capstoneproject.loaders.ClassesLoader;
import com.sylvainautran.nanodegree.capstoneproject.loaders.StudentsLoader;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.ViewHolder> {
    private Cursor mCursor;
    private AppCompatActivity mActivity;

    public StudentsAdapter(AppCompatActivity activity, Cursor cursor) {
        mCursor = cursor;
        mActivity = activity;

    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(ClassesLoader.Query._ID);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.list_item_student, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                StudentsNewDialog newFragment = StudentsNewDialog.newInstance(R.string.edit_student, getItemId(vh.getAdapterPosition()), getLastName(vh.getAdapterPosition()), getFirstName(vh
                        .getAdapterPosition()), getBirthDate(vh.getAdapterPosition()));

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(R.id.container, newFragment, "dialog").addToBackStack(null).commit();
                return true;
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mCursor.getLong(StudentsLoader.Query.COLUMN_BIRTHDATE));
        Calendar today = Calendar.getInstance();
        String name = mCursor.getString(StudentsLoader.Query.COLUMN_FIRSTNAME) + " " + mCursor.getString(StudentsLoader.Query.COLUMN_LASTNAME);
        int age_grade = today.get(Calendar.YEAR) - cal.get(Calendar.YEAR);
        holder.name.setText(name);
        holder.age_grade.setText(mActivity.getResources().getString(R.string.age_to_string, age_grade));
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) public TextView name;
        @BindView(R.id.age_grade) public TextView age_grade;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private String getLastName(int position){
        mCursor.moveToPosition(position);
        return mCursor.getString(StudentsLoader.Query.COLUMN_LASTNAME);
    }

    private String getFirstName(int position){
        mCursor.moveToPosition(position);
        return mCursor.getString(StudentsLoader.Query.COLUMN_FIRSTNAME);
    }

    private long getBirthDate(int position){
        mCursor.moveToPosition(position);
        return mCursor.getLong(StudentsLoader.Query.COLUMN_BIRTHDATE);
    }
}