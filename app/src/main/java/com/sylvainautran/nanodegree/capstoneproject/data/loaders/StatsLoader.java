package com.sylvainautran.nanodegree.capstoneproject.data.loaders;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;

public class StatsLoader extends CursorLoader {

    public static StatsLoader getStatsForOne(Context context, long callId, String selection){
        selection = selection == null ? "" : " AND " + selection;
        return new StatsLoader(
                context,
                AppelContract.CallEntry.buildCallStatsForOneUri(callId),
                Query.PROJECTION,
                AppelContract.CallEntry.TABLE_NAME + "." + AppelContract.CallEntry._ID + " = " + Long.toString(callId) + selection,
                null,
                AppelContract.CallEntry.COLUMN_DATETIME + " ASC");
    }

    public static StatsLoader getStatsForDates(Context context, long startDate, long endDate){
        return new StatsLoader(
                context,
                AppelContract.CallEntry.buildCallStats(startDate, endDate),
                Query.PROJECTION,
                AppelContract.CallEntry.COLUMN_DATETIME +" > ? AND " + AppelContract.CallEntry.COLUMN_DATETIME + " < ?",
                new String[] { Long.toString(startDate), Long.toString(endDate) },
                AppelContract.CallEntry.DEFAULT_SORT);
    }

    private StatsLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
        super(context, uri, projection, selection, selectionArgs, sort);
    }

    public interface Query {
        String[] PROJECTION = {
                "count(" + AppelContract.CallStudentLinkEntry.TABLE_NAME + "." + AppelContract.CallStudentLinkEntry._ID + ")",
                "sum(" + AppelContract.CallStudentLinkEntry.COLUMN_IS_PRESENT + ")",
                "count(" + AppelContract.CallStudentLinkEntry.COLUMN_LEAVING_TIME + ")",
                AppelContract.CallEntry.COLUMN_LEAVING_TIME_OPTION
        };

        int TOTAL = 0;
        int PRESENT = 1;
        int LEFT = 2;
        int OPTION = 3;
    }

    public interface QueryWidget {
        String[] PROJECTION = {
                AppelContract.CallEntry.TABLE_NAME + "." + AppelContract.CallEntry._ID,
                "count(" + AppelContract.CallStudentLinkEntry.TABLE_NAME + "." + AppelContract.CallStudentLinkEntry._ID + ")",
                "sum(" + AppelContract.CallStudentLinkEntry.COLUMN_IS_PRESENT + ")",
                AppelContract.ClassEntry.COLUMN_NAME,
                AppelContract.CallEntry.COLUMN_DATETIME,
                AppelContract.ClassEntry.TABLE_NAME + "." + AppelContract.ClassEntry._ID
        };

        int ID = 0;
        int TOTAL = 1;
        int PRESENT = 2;
        int CLASS_NAME = 3;
        int DATE_TIME = 4;
        int CLASS_ID = 5;
    }
}
