package com.sylvainautran.nanodegree.capstoneproject.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sylvainautran.nanodegree.capstoneproject.CallsDetailsActivity;
import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.StatsLoader;

import java.text.DateFormat;
import java.util.Calendar;

public class AppelWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ViewFactory(getApplicationContext());
    }

    private class ViewFactory implements RemoteViewsService.RemoteViewsFactory {
        private Context mContext;
        private Cursor cursor;

        public ViewFactory(Context c){
            mContext = c;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            if(cursor != null){
                cursor.close();
            }

            final long token = Binder.clearCallingIdentity();
            Calendar endDate = Calendar.getInstance();
            Calendar startDate = Calendar.getInstance();
            startDate.add(Calendar.DAY_OF_MONTH, - 7);
            cursor = mContext.getContentResolver().query(
                    AppelContract.CallEntry.buildCallStats(startDate.getTimeInMillis(), endDate.getTimeInMillis()),
                    StatsLoader.QueryWidget.PROJECTION,
                    AppelContract.CallEntry.COLUMN_DATETIME + " > " + Long.toString(startDate.getTimeInMillis()) +
                            " AND " + AppelContract.CallEntry.COLUMN_DATETIME + " < " + Long.toString(endDate.getTimeInMillis()),
                    null,
                    AppelContract.CallEntry.DEFAULT_SORT);
            Binder.restoreCallingIdentity(token);
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            if(cursor != null){
                return cursor.getCount();
            }
            return 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if(position == AdapterView.INVALID_POSITION || cursor == null || !cursor.moveToPosition(position) ){
                return null;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(cursor.getLong(StatsLoader.QueryWidget.DATE_TIME));
            String date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(calendar.getTime());
            int presents = cursor.getInt(StatsLoader.QueryWidget.PRESENT);
            int total = cursor.getInt(StatsLoader.QueryWidget.TOTAL);
            int absents = total - presents;
            String className = cursor.getString(StatsLoader.QueryWidget.CLASS_NAME);

            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_stats_item);
            rv.setTextViewText(R.id.class_name, className);
            rv.setTextViewText(R.id.call_date, date);
            rv.setTextViewText(R.id.present, Integer.toString(presents));
            rv.setTextViewText(R.id.absent, Integer.toString(absents));

            final Intent fillInIntent = new Intent(mContext, CallsDetailsActivity.class);
            fillInIntent.putExtra(getString(R.string.intent_extra_class_name), className);
            fillInIntent.putExtra(getString(R.string.intent_extra_call_date), calendar.getTimeInMillis());
            fillInIntent.putExtra(getString(R.string.intent_extra_call_id), cursor.getLong(StatsLoader.QueryWidget.ID));
            fillInIntent.putExtra(getString(R.string.intent_extra_class_id), cursor.getLong(StatsLoader.QueryWidget.CLASS_ID));
            rv.setOnClickFillInIntent(R.id.appel_item, fillInIntent);

            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            if(cursor != null && cursor.moveToPosition(position)){
                return cursor.getInt(StatsLoader.QueryWidget.ID);
            }
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
