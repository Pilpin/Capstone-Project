package com.sylvainautran.nanodegree.capstoneproject.adapters;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.ClassesLoader;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.StudentsLoader;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClassStudentsAdapter extends RecyclerView.Adapter<ClassStudentsAdapter.ViewHolder> implements AdapterActionModeListener {
    public static final String GRADE = "grade";

    private Cursor mCursor;
    private AppCompatActivity mActivity;
    private ActionMode mActionMode;
    private FragmentActionModeListener mActionModeListener;
    private HashMap<Integer, Long> mSelectedStudents;
    private HashMap<Long, Character> mHeaders;

    public ClassStudentsAdapter(AppCompatActivity activity, Cursor cursor, FragmentActionModeListener actionModeListener, HashMap headers) {
        mCursor = cursor;
        mActivity = activity;
        mActionModeListener = actionModeListener;
        mSelectedStudents = new HashMap<>();
        mHeaders = headers;
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
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mActionMode != null){
                    if(!removeSelectedItem(vh.getAdapterPosition(), getItemId(vh.getAdapterPosition()))){
                        addSelectedItem(vh.getAdapterPosition(), getItemId(vh.getAdapterPosition()));
                    }
                }
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(mActionMode == null) {
                    Log.d("TEST", "SIZE : " + mSelectedStudents.size());
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
        holder.itemView.setActivated(mSelectedStudents.keySet().contains(position));
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
        String grade = mCursor.getString(StudentsLoader.Query.COLUMN_GRADE);
        grade = grade.isEmpty() ? grade : "- " + grade;
        String age_grade = mActivity.getResources().getString(R.string.age_to_string, age, grade);
        if(age < 2){
            age_grade = mActivity.getResources().getString(R.string.age_to_string_singular, age, grade);
        }
        holder.name.setText(name);
        holder.age_grade.setText(age_grade);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    @Override
    public void clearSelectedItems() {
        Iterator it = mSelectedStudents.keySet().iterator();
        while(it.hasNext()){
            notifyItemChanged((Integer) it.next());
        }
        mSelectedStudents.clear();
        mActionMode = null;
    }

    @Override
    public HashMap getValues(int position){
        mCursor.moveToPosition(position);
        HashMap<String, String> values = new HashMap<>();
        values.put(GRADE, mCursor.getString(StudentsLoader.Query.COLUMN_GRADE));
        return values;
    }

    public void addSelectedItem(int position, long id){
        mActionModeListener.addSelectedItem(position, id);
        mSelectedStudents.put(position, id);
        notifyItemChanged(position);
        mActionMode.setTitle(Integer.toString(mSelectedStudents.size()));
        if(mSelectedStudents.size() == 2){
            mActionMode.invalidate();
        }
    }

    public boolean removeSelectedItem(int position, long id){
        mActionModeListener.removeSelectedItem(position, id);
        if(mSelectedStudents.remove(position) != null){
            mActionMode.setTitle(Integer.toString(mSelectedStudents.size()));
            if(mSelectedStudents.size() == 1){
                mActionMode.invalidate();
            }
            notifyItemChanged(position);
            return true;
        }
        return false;
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
}