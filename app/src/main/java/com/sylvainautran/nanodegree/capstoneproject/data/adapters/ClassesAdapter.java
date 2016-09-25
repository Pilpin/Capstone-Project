package com.sylvainautran.nanodegree.capstoneproject.data.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.ClassesLoader;

import java.util.HashMap;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClassesAdapter extends BaseAdapter<ClassesAdapter.ViewHolder> {
    private HashMap<Long, Character> mHeaders;

    public ClassesAdapter(Context context, Cursor cursor, @Nullable Set<Integer> selectedItems, View.OnClickListener onClickListener, @Nullable View.OnLongClickListener onLongClickListener, HashMap<Long,
            Character>
            headers) {
        super(context, cursor, selectedItems, onClickListener, onLongClickListener);
        mHeaders = headers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_class, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        vh.itemView.setOnLongClickListener(mOnLongClickListener);
        vh.itemView.setOnClickListener(mOnClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        if(mHeaders != null && mHeaders.containsKey(mCursor.getLong(ClassesLoader.Query._ID))){
            holder.first_letter.setText(Character.toString(mHeaders.get(mCursor.getLong(ClassesLoader.Query._ID))));
        }else{
            holder.first_letter.setText("");
        }

        holder.name.setText(mCursor.getString(ClassesLoader.Query.COLUMN_NAME));
        holder.itemView.setActivated(mSelectedItems.contains(position));

        tagView(holder.itemView, position, mCursor.getLong(ClassesLoader.Query._ID), mCursor.getString(ClassesLoader.Query.COLUMN_NAME));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.first_letter) public TextView first_letter;
        @BindView(R.id.name) public TextView name;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private void tagView(View v, int position, long classId, String className){
        v.setTag(R.id.key_position, position);
        v.setTag(R.id.key_class_id, classId);
        v.setTag(R.id.key_class_name, className);
    }
}