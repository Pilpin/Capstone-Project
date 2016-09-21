package com.sylvainautran.nanodegree.capstoneproject.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.ClassesLoader;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.StudentsLoader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentsPickerAdapter extends RecyclerView.Adapter<StudentsPickerAdapter.ViewHolder> implements AdapterActionModeListener {
        private Cursor mCursor;
    private Context mContext;
    private HashMap<Integer, Long> selectedStudents;

    public StudentsPickerAdapter(Context context, Cursor cursor) {
        mCursor = cursor;
        mContext = context;
        selectedStudents = new HashMap<>();
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(ClassesLoader.Query._ID);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_student, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!removeSelectedItem(vh.getAdapterPosition(), getItemId(vh.getAdapterPosition()))){
                    addSelectedItem(vh.getAdapterPosition(), getItemId(vh.getAdapterPosition()));
                }
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setActivated(selectedStudents.keySet().contains(position));
        mCursor.moveToPosition(position);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mCursor.getLong(StudentsLoader.Query.COLUMN_BIRTHDATE));
        Calendar today = Calendar.getInstance();
        String name = mCursor.getString(StudentsLoader.Query.COLUMN_FIRSTNAME) + " " + mCursor.getString(StudentsLoader.Query.COLUMN_LASTNAME);
        int age = today.get(Calendar.YEAR) - cal.get(Calendar.YEAR);
        String age_grade = mContext.getResources().getString(R.string.age_to_string, age, "");
        if(age < 2){
            age_grade = mContext.getResources().getString(R.string.age_to_string_singular, age, "");
        }
        holder.name.setText(name);
        holder.age_grade.setText(age_grade);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    @Override
    public void clearSelectedItems() { }

    @Override
    public HashMap getValues(int position){
        return selectedStudents;
    }

    public void addSelectedItem(int position, long id){
        selectedStudents.put(position, id);
        notifyItemChanged(position);
    }

    public boolean removeSelectedItem(int position, long id){
        selectedStudents.remove(position);
        notifyItemChanged(position);
        return false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) public TextView name;
        @BindView(R.id.age_grade) public TextView age_grade;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}