package com.sylvainautran.nanodegree.capstoneproject.data.loaders;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;

public class CallsLoader extends CursorLoader {
    public static CallsLoader getAllCalls(Context context) {
        return new CallsLoader(context, AppelContract.CallEntry.CONTENT_URI, Query.PROJECTION, null, null, AppelContract.CallEntry.MULTIPLE_SORT);
    }

    public static CallsLoader getAllCallDetails(Context context, long callId, long classId){
        return new CallsLoader(context, AppelContract.CallStudentLinkEntry.buildCallStudentLinkUriWithCallAndClass(classId, callId), Query.PROJECTION_DETAILS, null, null,
                AppelContract.StudentEntry.DEFAULT_SORT);
    }

    public static CallsLoader getAllCallMonthsFromClass(Context context, long classId){
        return new CallsLoader(context, AppelContract.CallEntry.buildExportMonths(classId), Query.PROJECTION_EXPORT_MONTHS, AppelContract.CallEntry.COLUMN_CLASS_ID + " = ?",
                new String[] { Long.toString(classId) }, AppelContract.CallEntry.DEFAULT_SORT);
    }

    private CallsLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
        super(context, uri, projection, selection, selectionArgs, sort);
    }

    public interface Query {
        String[] PROJECTION = {
                AppelContract.CallEntry.TABLE_NAME + "." + AppelContract.CallEntry._ID,
                AppelContract.CallEntry.COLUMN_LEAVING_TIME_OPTION,
                AppelContract.CallEntry.COLUMN_CLASS_ID,
                AppelContract.CallEntry.COLUMN_DATETIME,
                AppelContract.ClassEntry.COLUMN_NAME
        };

        String[] PROJECTION_DETAILS = {
                AppelContract.StudentEntry.TABLE_NAME + "." + AppelContract.StudentEntry._ID,
                AppelContract.CallEntry.COLUMN_LEAVING_TIME_OPTION,
                AppelContract.StudentEntry.COLUMN_FIRSTNAME,
                AppelContract.StudentEntry.COLUMN_LASTNAME,
                AppelContract.StudentEntry.COLUMN_BIRTHDATE,
                AppelContract.CallStudentLinkEntry.COLUMN_IS_PRESENT,
                AppelContract.CallStudentLinkEntry.COLUMN_LEAVING_TIME,
                AppelContract.ClassStudentLinkEntry.COLUMN_GRADE,
                AppelContract.CallStudentLinkEntry.TABLE_NAME + "." + AppelContract.CallStudentLinkEntry._ID,
                AppelContract.StudentEntry.TABLE_NAME + "." + AppelContract.StudentEntry._ID,
                AppelContract.CallEntry.TABLE_NAME + "." + AppelContract.CallEntry._ID,
                AppelContract.CallEntry.COLUMN_DATETIME
        };

        String[] PROJECTION_EXPORT_MONTHS = {
                AppelContract.CallEntry.TABLE_NAME + "." + AppelContract.CallEntry._ID,
                "strftime('%m', " + AppelContract.CallEntry.COLUMN_DATETIME + "/1000, 'unixepoch', 'localtime') - 1 AS " + AppelContract.CallEntry.AS_COLUMN_MONTH,
                "strftime('%Y', " + AppelContract.CallEntry.COLUMN_DATETIME + "/1000, 'unixepoch', 'localtime') AS " + AppelContract.CallEntry.AS_COLUMN_YEAR
        };

        int _ID = 0;
        int COLUMN_LEAVING_TIME_OPTION = 1, MONTH = 1;
        int COLUMN_CLASS_ID = 2, YEAR = 2;
        int COLUMN_FIRSTNAME = 2;
        int COLUMN_DATETIME = 3;
        int COLUMN_LASTNAME = 3;
        int COLUMN_CLASS_NAME = 4;
        int COLUMN_BIRTHDATE = 4;
        int COLUMN_IS_PRESENT = 5;
        int COLUMN_LEAVING_TIME = 6;
        int COLUMN_GRADE = 7;
        int CALL_STUDENT_ID = 8;
        int STUDENT_ID = 9;
        int CALL_ID = 10;
        int CALL_DATE = 11;
    }
}
