package com.sylvainautran.nanodegree.capstoneproject.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View.OnLongClickListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.sylvainautran.nanodegree.capstoneproject.data.loaders.StudentsLoader;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class BaseAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    Context mContext;
    Cursor mCursor;
    ArrayList<Integer> mSelectedItems;
    OnClickListener mOnClickListener;
    OnLongClickListener mOnLongClickListener;

    public BaseAdapter(Context context, Cursor cursor, Set<Integer> set, OnClickListener onClickListener, OnLongClickListener onLongClickListener){
        mContext = context;
        mCursor = cursor;
        mOnClickListener = onClickListener;
        mOnLongClickListener = onLongClickListener;
        mSelectedItems = new ArrayList<>();
        Iterator<Integer> it = set.iterator();
        while(it.hasNext()){
            int i = it.next();
            mSelectedItems.add(i);
        }
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(StudentsLoader.Query._ID);
    }

    public void addItem(Integer position) {
        mSelectedItems.add(position);
        notifyItemChanged(position);

    }

    public void removeItem(Integer position) {
        mSelectedItems.remove(position);
        notifyItemChanged(position);
    }

    public void clearSelectedItems() {
        ArrayList<Integer> items = (ArrayList<Integer>) mSelectedItems.clone();
        mSelectedItems.clear();
        for(Iterator<Integer> iterator = items.iterator(); iterator.hasNext(); ){
            notifyItemChanged(iterator.next());
        }
    }
}
