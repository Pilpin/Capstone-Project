package com.sylvainautran.nanodegree.capstoneproject.data.adapters;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.CallsLoader;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallStudentsAdapter extends RecyclerView.Adapter<CallStudentsAdapter.ViewHolder> implements SectionTitleProvider {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private Cursor mCursor;
    private Context mContext;
    private HashMap<Integer, String> mLetterToPosition;

    public CallStudentsAdapter(Context context, Cursor cursor, HashMap letterToPosition) {
        mCursor = cursor;
        mContext = context;
        mLetterToPosition = letterToPosition;
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
                setPresent(v);
            }
        });

        vh.absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAbsent(v);
            }
        });

        vh.leaving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLeft(v);
            }
        });

        vh.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext, v);
                popup.setOnMenuItemClickListener(new PopupMenuClickListener(v));
                if(v.getTag(R.id.options) != null){
                    switch((Integer) v.getTag(R.id.options)) {
                        case AppelContract.CallEntry.RECORD_LEAVING_TIME:
                            popup.inflate(R.menu.calls_students_edit_record);
                            break;
                        default:
                            popup.inflate(R.menu.calls_students_edit);
                    }
                }
                popup.show();
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
        String age_grade = mContext.getResources().getString(R.string.age_to_string_grade, grade, age);
        if(age < 2){
            age_grade = mContext.getResources().getString(R.string.age_to_string_singular_grade, grade, age);
        }
        holder.name.setText(name);
        holder.age_grade.setText(age_grade);
        holder.options.setContentDescription(mContext.getString(R.string.edit_call, name));

        Long leavingTime = null;
        Long callStudentId = null;
        long callDate = mCursor.getLong(CallsLoader.Query.COLUMN_DATETIME);
        long callId = mCursor.getLong(CallsLoader.Query.CALL_ID);
        long studentId = mCursor.getLong(CallsLoader.Query.STUDENT_ID);
        int option = mCursor.getInt(CallsLoader.Query.COLUMN_LEAVING_TIME_OPTION);

        if(!mCursor.isNull(CallsLoader.Query.CALL_STUDENT_ID)){
            callStudentId = mCursor.getLong(CallsLoader.Query.CALL_STUDENT_ID);
        }

        if(!mCursor.isNull(CallsLoader.Query.COLUMN_IS_PRESENT)) {
            View v = new View(mContext);
            if(mCursor.getInt(CallsLoader.Query.COLUMN_IS_PRESENT) == AppelContract.CallStudentLinkEntry.PRESENT){
                if(!mCursor.isNull(CallsLoader.Query.COLUMN_LEAVING_TIME)){
                    leavingTime = mCursor.getLong(CallsLoader.Query.COLUMN_LEAVING_TIME);
                    tagView(v, callId, studentId, callDate, leavingTime, callStudentId, option, holder);
                    holder.setLeft(leavingTime);
                }else {
                    tagView(v, callId, studentId, callDate, null, callStudentId, option, holder);
                    holder.setPresent(option);
                }
            }else{
                tagView(v, callId, studentId, callDate, null, callStudentId, option, holder);
                holder.setAbsent();
            }
        }else{
            holder.init();
        }

        tagView(holder.present, callId, studentId, callDate, leavingTime, callStudentId, option, holder);
        tagView(holder.absent, callId, studentId, callDate, leavingTime, callStudentId, option, holder);
        tagView(holder.leaving, callId, studentId, callDate, leavingTime, callStudentId, option, holder);
        tagView(holder.options, callId, studentId, callDate, leavingTime, callStudentId, option, holder);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    @Override
    public String getSectionTitle(int position) {
        return mLetterToPosition.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.options) ImageView options;
        @BindView(R.id.name) public TextView name;
        @BindView(R.id.age_grade) public TextView age_grade;
        @BindView(R.id.call_buttons) LinearLayout button_container;
        @BindView(R.id.absent) Button absent;
        @BindView(R.id.present) Button present;
        @BindView(R.id.leaving_time) Button leaving;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void init(){
            present.setAlpha(1f);
            present.setClickable(true);
            present.setVisibility(View.VISIBLE);
            absent.setAlpha(1f);
            absent.setClickable(true);
            absent.setVisibility(View.VISIBLE);
            leaving.setAlpha(1f);
            leaving.setClickable(true);
            leaving.setVisibility(View.GONE);
        }

        public void hideAll(){
            absent.setVisibility(View.GONE);
            present.setVisibility(View.GONE);
            leaving.setVisibility(View.GONE);
        }

        public void setPresent(int options){
            hideAll();
            switch(options){
                case AppelContract.CallEntry.RECORD_LEAVING_TIME:
                    leaving.setText(R.string.leaving);
                    leaving.setAlpha(1f);
                    leaving.setClickable(true);
                    leaving.setVisibility(View.VISIBLE);
                    break;
                case AppelContract.CallEntry.DO_NOT_RECORD_LEAVING_TIME:
                    present.setAlpha(.5f);
                    present.setClickable(false);
                    present.setVisibility(View.VISIBLE);
                    break;
            }
        }

        public void setAbsent(){
            hideAll();
            absent.setAlpha(.5f);
            absent.setClickable(false);
            absent.setVisibility(View.VISIBLE);
        }

        public void setLeft(long leavingTime){
            hideAll();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(leavingTime);
            DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
            leaving.setText(mContext.getString(R.string.leaving_time, df.format(cal.getTime())));
            leaving.setAlpha(.5f);
            leaving.setVisibility(View.VISIBLE);
        }
    }

    private class PopupMenuClickListener implements PopupMenu.OnMenuItemClickListener, TimePickerDialog.OnTimeSetListener {
        private View mView;

        PopupMenuClickListener(View v){
            mView = v;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.menu_present:
                    setPresent(mView);
                    return true;
                case R.id.menu_absent:
                    setAbsent(mView);
                    return true;
                case R.id.menu_leaving:
                    Calendar cal = Calendar.getInstance();
                    TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, this, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), android.text.format.DateFormat.is24HourFormat(mContext));
                    timePickerDialog.show();
                    break;
            }
            return false;
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis((Long) mView.getTag(R.id.key_call_datetime));
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cal.set(Calendar.MINUTE, minute);
            mView.setTag(R.id.key_leaving_time, cal.getTimeInMillis());
            setLeft(mView);
        }
    }

    private void tagView(View v, long callId, long studentId, long callDate, @Nullable Long leavingTime, @Nullable Long callStudentId, int option, ViewHolder vh){
        v.setTag(R.id.key_call_id, callId);
        v.setTag(R.id.key_student_id, studentId);
        v.setTag(R.id.key_call_datetime, callDate);
        v.setTag(R.id.key_leaving_time, leavingTime);
        v.setTag(R.id.key_call_student_id, callStudentId);
        v.setTag(R.id.options, option);
        v.setTag(R.id.view_holder, vh);
    }

    private void setAbsent(View v){
        ViewHolder vh = (ViewHolder) v.getTag(R.id.view_holder);
        animateAbsent(vh.absent, vh.present, vh.leaving);
        ContentValues cv = new ContentValues();
        cv.put(AppelContract.CallStudentLinkEntry.COLUMN_CALL_ID, (Long) v.getTag(R.id.key_call_id));
        cv.put(AppelContract.CallStudentLinkEntry.COLUMN_STUDENT_ID, (Long) v.getTag(R.id.key_student_id));
        cv.putNull(AppelContract.CallStudentLinkEntry.COLUMN_LEAVING_TIME);
        cv.put(AppelContract.CallStudentLinkEntry.COLUMN_IS_PRESENT, AppelContract.CallStudentLinkEntry.ABSENT);
        if(v.getTag(R.id.key_call_student_id) == null){
            mContext.getContentResolver().insert(AppelContract.CallStudentLinkEntry.CONTENT_URI, cv);
        }else {
            mContext.getContentResolver().update(AppelContract.CallStudentLinkEntry.CONTENT_URI, cv, AppelContract.CallStudentLinkEntry._ID + " = ? ", new String[]{Long.toString((Long) v.getTag
                    (R.id.key_call_student_id))});
        }
    }

    private void animateAbsent(Button absent, Button present, Button leaving){
        present.setVisibility(View.GONE);
        leaving.setVisibility(View.GONE);
        absent.setVisibility(View.GONE);
        absent.setAlpha(0f);
        absent.setClickable(false);
        absent.setVisibility(View.VISIBLE);

        absent.animate()
                .alpha(.5f)
                .setDuration(500);
    }

    private void setPresent(View v){
        ViewHolder vh = (ViewHolder) v.getTag(R.id.view_holder);
        animatePresent(vh.absent, vh.present, vh.leaving, (Integer) v.getTag(R.id.options));
        ContentValues cv = new ContentValues();
            cv.put(AppelContract.CallStudentLinkEntry.COLUMN_CALL_ID, (Long) v.getTag(R.id.key_call_id));
            cv.put(AppelContract.CallStudentLinkEntry.COLUMN_STUDENT_ID, (Long) v.getTag(R.id.key_student_id));
            cv.put(AppelContract.CallStudentLinkEntry.COLUMN_IS_PRESENT, AppelContract.CallStudentLinkEntry.PRESENT);
            cv.putNull(AppelContract.CallStudentLinkEntry.COLUMN_LEAVING_TIME);
            if(v.getTag(R.id.key_call_student_id) == null){
                mContext.getContentResolver().insert(AppelContract.CallStudentLinkEntry.CONTENT_URI, cv);
            }else {
                mContext.getContentResolver().update(AppelContract.CallStudentLinkEntry.CONTENT_URI, cv, AppelContract.CallStudentLinkEntry._ID + " = ? ", new String[]{Long.toString((Long) v.getTag(R.id.key_call_student_id))});
            }
    }

    private void animatePresent(Button absent, Button present, Button leaving, int options){
        present.setVisibility(View.GONE);
        leaving.setVisibility(View.GONE);
        absent.setVisibility(View.GONE);
        leaving.setText(R.string.leaving);

        switch(options){
            case AppelContract.CallEntry.RECORD_LEAVING_TIME:
                leaving.setClickable(true);
                leaving.setAlpha(0f);
                leaving.setVisibility(View.VISIBLE);

                leaving.animate()
                        .alpha(1f)
                        .setDuration(500);

                break;
            case AppelContract.CallEntry.DO_NOT_RECORD_LEAVING_TIME:
                present.setClickable(false);
                present.setAlpha(0f);
                present.setVisibility(View.VISIBLE);

                present.animate()
                        .alpha(.5f)
                        .setDuration(500);
                break;
        }
    }

    private void setLeft(View v){
        Calendar cal = Calendar.getInstance();
        if(v.getTag(R.id.key_leaving_time) != null){
            cal.setTimeInMillis((Long) v.getTag(R.id.key_leaving_time));
        }

        ViewHolder vh = (ViewHolder) v.getTag(R.id.view_holder);
        animateLeft(vh.absent, vh.present, vh.leaving, cal);

        ContentValues cv = new ContentValues();
        cv.put(AppelContract.CallStudentLinkEntry.COLUMN_CALL_ID, (Long) v.getTag(R.id.key_call_id));
        cv.put(AppelContract.CallStudentLinkEntry.COLUMN_STUDENT_ID, (Long) v.getTag(R.id.key_student_id));
        cv.put(AppelContract.CallStudentLinkEntry.COLUMN_LEAVING_TIME, cal.getTimeInMillis());
        cv.put(AppelContract.CallStudentLinkEntry.COLUMN_IS_PRESENT, AppelContract.CallStudentLinkEntry.PRESENT);
        if(v.getTag(R.id.key_call_student_id) == null){
            mContext.getContentResolver().insert(AppelContract.CallStudentLinkEntry.CONTENT_URI, cv);
        }else {
            mContext.getContentResolver().update(AppelContract.CallStudentLinkEntry.CONTENT_URI, cv, AppelContract.CallStudentLinkEntry._ID + " = ? ", new String[]{Long.toString((Long) v.getTag(R.id.key_call_student_id))});
        }
    }

    private void animateLeft(Button absent, Button present, Button leaving, Calendar cal){
        present.setVisibility(View.GONE);
        leaving.setVisibility(View.GONE);
        absent.setVisibility(View.GONE);
        leaving.setClickable(false);
        leaving.setAlpha(0f);
        leaving.setVisibility(View.VISIBLE);

        DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        leaving.setText(mContext.getString(R.string.leaving_time, df.format(cal.getTime())));

        leaving.animate()
                .alpha(.5f)
                .setDuration(500);
    }
}