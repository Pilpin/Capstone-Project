package com.sylvainautran.nanodegree.capstoneproject.adapters;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sylvainautran.nanodegree.capstoneproject.ClassStudentsListActivity;
import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.ClassesLoader;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.StudentsLoader;

import java.util.HashMap;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder> implements AdapterActionModeListener {
    public static final String CLASS_NAME = "class_name";

    private Cursor mCursor;
    private AppCompatActivity mActivity;
    private ActionMode mActionMode;
    private FragmentActionModeListener mActionModeListener;
    private HashMap<Integer, Long> mSelectedClasses;
    private HashMap<Long, Character> mHeaders;

    public ClassesAdapter(AppCompatActivity activity, Cursor cursor, FragmentActionModeListener actionModeListener, HashMap headers) {
        mCursor = cursor;
        mActivity = activity;
        mActionModeListener = actionModeListener;
        mSelectedClasses = new HashMap<>();
        mHeaders = headers;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(ClassesLoader.Query._ID);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.list_item_class, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mActionMode != null){
                    if(!removeSelectedItem(vh.getAdapterPosition(), getItemId(vh.getAdapterPosition()))){
                        addSelectedItem(vh.getAdapterPosition(), getItemId(vh.getAdapterPosition()));
                    }
                }else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, AppelContract.ClassStudentLinkEntry.buildClassStudentLinkFromClassUri(getItemId(vh.getAdapterPosition())));
                    intent.putExtra(ClassStudentsListActivity.CLASS_NAME, vh.name.getText());
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
        holder.itemView.setActivated(mSelectedClasses.keySet().contains(position));
        mCursor.moveToPosition(position);

        if(mHeaders != null && mHeaders.containsKey(mCursor.getLong(ClassesLoader.Query._ID))){
            holder.first_letter.setText(Character.toString(mHeaders.get(mCursor.getLong(ClassesLoader.Query._ID))));
        }else{
            holder.first_letter.setText("");
        }

        holder.name.setText(mCursor.getString(ClassesLoader.Query.COLUMN_NAME));
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    @Override
    public void clearSelectedItems() {
        Iterator it = mSelectedClasses.keySet().iterator();
        while(it.hasNext()){
            notifyItemChanged((Integer) it.next());
        }
        mSelectedClasses.clear();
        mActionMode = null;
    }

    @Override
    public HashMap getValues(int position) {
        mCursor.moveToPosition(position);
        HashMap<String, String> values = new HashMap<>();
        values.put(CLASS_NAME, mCursor.getString(ClassesLoader.Query.COLUMN_NAME));
        return values;
    }

    public void addSelectedItem(int position, long id){
        mActionModeListener.addSelectedItem(position, id);
        mSelectedClasses.put(position, id);
        notifyItemChanged(position);
        mActionMode.setTitle(Integer.toString(mSelectedClasses.size()));
        if(mSelectedClasses.size() == 2){
            mActionMode.invalidate();
        }
    }

    public boolean removeSelectedItem(int position, long id){
        mActionModeListener.removeSelectedItem(position, id);
        if(mSelectedClasses.remove(position) != null){
            mActionMode.setTitle(Integer.toString(mSelectedClasses.size()));
            if(mSelectedClasses.size() == 1){
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

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}