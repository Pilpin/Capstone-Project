package com.sylvainautran.nanodegree.capstoneproject.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        vh.present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPresent(true, getItemId(vh.getAdapterPosition()), vh.absent, vh.present, vh.leaving, getOption(vh.getAdapterPosition()));
            }
        });

        vh.absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAbsent(true, getItemId(vh.getAdapterPosition()), vh.absent, vh.present, vh.leaving);
            }
        });

        vh.leaving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLeft(true, getItemId(vh.getAdapterPosition()), vh.absent, vh.present, vh.leaving);
            }
        });
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

        if(!mCursor.isNull(CallsLoader.Query.COLUMN_IS_PRESENT)) {
            if(mCursor.getInt(CallsLoader.Query.COLUMN_IS_PRESENT) == 1){
                if(!mCursor.isNull(CallsLoader.Query.COLUMN_LEAVING_TIME)){
                    setLeft(false, mCursor.getLong(CallsLoader.Query._ID), holder.absent, holder.present, holder.leaving);
                }else {
                    setPresent(false, mCursor.getLong(CallsLoader.Query._ID), holder.absent, holder.present, holder.leaving, mCursor.getInt(CallsLoader.Query.COLUMN_LEAVING_TIME_OPTION));
                }
            }else{
                setAbsent(false, mCursor.getLong(CallsLoader.Query._ID), holder.absent, holder.present, holder.leaving);
            }
        }else{
            holder.leaving.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.options) public ImageView options;
        @BindView(R.id.name) public TextView name;
        @BindView(R.id.age_grade) public TextView age_grade;
        @BindView(R.id.call_buttons) public LinearLayout button_container;
        @BindView(R.id.absent) public Button absent;
        @BindView(R.id.present) public Button present;
        @BindView(R.id.leaving_time) public Button leaving;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private int getOption(int position){
        mCursor.moveToPosition(position);
        return mCursor.getInt(CallsLoader.Query.COLUMN_LEAVING_TIME_OPTION);
    }

    private void setAbsent(boolean animate, long id, Button absent, Button present, Button leaving){
        if(animate){
            if(absent.getVisibility() == View.GONE) {
                absent.setAlpha(0f);
                absent.setVisibility(View.VISIBLE);
                absent.animate()
                        .alpha(1f)
                        .setDuration(500)
                        .setListener(null);
            }

            present.animate()
                    .alpha(0f)
                    .setDuration(500)
                    .setListener(null);
            leaving.animate()
                    .alpha(0f)
                    .setDuration(500)
                    .setListener(null);
        }
        present.setVisibility(View.GONE);
        leaving.setVisibility(View.GONE);
        absent.setAlpha(.5f);
        absent.setClickable(false);
    }

    private void setPresent(boolean animate, long id, Button absent, Button present, Button leaving, int option){
        if(animate){
            switch(option){
                case 1:
                    if(leaving.getVisibility() == View.GONE) {
                        leaving.setAlpha(0f);
                        leaving.setVisibility(View.VISIBLE);
                        leaving.animate()
                                .alpha(1f)
                                .setDuration(500)
                                .setListener(null);
                    }

                    present.animate()
                            .alpha(0f)
                            .setDuration(500)
                            .setListener(null);
                    absent.animate()
                            .alpha(0f)
                            .setDuration(500)
                            .setListener(null);
                    break;
                case 0:
                    if(present.getVisibility() == View.GONE) {
                        present.setAlpha(0f);
                        present.setVisibility(View.VISIBLE);
                        present.animate()
                                .alpha(1f)
                                .setDuration(500)
                                .setListener(null);
                    }

                    leaving.animate()
                            .alpha(0f)
                            .setDuration(500)
                            .setListener(null);
                    absent.animate()
                            .alpha(0f)
                            .setDuration(500)
                            .setListener(null);
                    break;
            }

        }
        switch(option){
            case 1:
                absent.setVisibility(View.GONE);
                present.setVisibility(View.GONE);
                leaving.setVisibility(View.VISIBLE);
                break;
            case 0:
                absent.setVisibility(View.GONE);
                present.setAlpha(.5f);
                present.setClickable(false);
                leaving.setVisibility(View.GONE);
        }
    }

    private void setLeft(boolean animate, long id, Button absent, Button present, Button leaving){
        if(animate){
            if(leaving.getVisibility() == View.GONE) {
                absent.setAlpha(0f);
                absent.setVisibility(View.VISIBLE);
                absent.animate()
                        .alpha(1f)
                        .setDuration(500)
                        .setListener(null);
            }

            present.animate()
                    .alpha(0f)
                    .setDuration(500)
                    .setListener(null);
            absent.animate()
                    .alpha(0f)
                    .setDuration(500);
        }
        absent.setVisibility(View.GONE);
        present.setVisibility(View.GONE);
        leaving.setAlpha(.5f);
        leaving.setClickable(false);
    }
}