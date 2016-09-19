package com.sylvainautran.nanodegree.capstoneproject.loaders;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;

public class CallsLoader extends CursorLoader {
    public static CallsLoader getAllCalls(Context context) {
        return new CallsLoader(context, AppelContract.CallEntry.CONTENT_URI, Query.MULTIPLE_PROJECTION, AppelContract.CallEntry.MULTIPLE_SORT);
    }

    public static CallsLoader getCallForId(Context context, long id) {
        return new CallsLoader(context, AppelContract.CallEntry.buildCallUri(id), Query.PROJECTION, AppelContract.CallEntry.DEFAULT_SORT);
    }

    public static CallsLoader getAllCallsFromClass(Context context, Uri uri){
        return new CallsLoader(context, uri, Query.PROJECTION_DETAILS, AppelContract.StudentEntry.DEFAULT_SORT);
    }

    public static CallsLoader getAllCallsFromClass(Context context, long callId, long classId){
        return new CallsLoader(context, AppelContract.CallStudentLinkEntry.buildCallStudentLinkUriWithCallAndClass(classId, callId), Query.PROJECTION_DETAILS, AppelContract.StudentEntry.DEFAULT_SORT);
    }

    private CallsLoader(Context context, Uri uri, String[] projection, String sort) {
        super(context, uri, projection, null, null, sort);
    }

    public interface Query {
        String[] PROJECTION = {
                AppelContract.CallEntry._ID,
                AppelContract.CallEntry.COLUMN_LEAVING_TIME_OPTION,
                AppelContract.CallEntry.COLUMN_CLASS_ID,
                AppelContract.CallEntry.COLUMN_DATETIME
        };

        String[] MULTIPLE_PROJECTION = {
                AppelContract.CallEntry.TABLE_NAME + "." + AppelContract.CallEntry._ID,
                AppelContract.CallEntry.COLUMN_LEAVING_TIME_OPTION,
                AppelContract.CallEntry.COLUMN_CLASS_ID,
                AppelContract.CallEntry.COLUMN_DATETIME,
                AppelContract.ClassEntry.COLUMN_NAME
        };

        String[] PROJECTION_DETAILS = {
                AppelContract.CallStudentLinkEntry.TABLE_NAME + "." + AppelContract.CallStudentLinkEntry._ID,
                AppelContract.CallEntry.COLUMN_LEAVING_TIME_OPTION,
                AppelContract.StudentEntry.COLUMN_FIRSTNAME,
                AppelContract.StudentEntry.COLUMN_LASTNAME,
                AppelContract.StudentEntry.COLUMN_BIRTHDATE,
                AppelContract.CallStudentLinkEntry.COLUMN_IS_PRESENT,
                AppelContract.CallStudentLinkEntry.COLUMN_LEAVING_TIME,
                AppelContract.ClassStudentLinkEntry.COLUMN_GRADE
        };

        int _ID = 0;
        int COLUMN_LEAVING_TIME_OPTION = 1;
        int COLUMN_CLASS_ID = 2;
        int COLUMN_FIRSTNAME = 2;
        int COLUMN_DATETIME = 3;
        int COLUMN_LASTNAME = 3;
        int COLUMN_CLASS_NAME = 4;
        int COLUMN_BIRTHDATE = 4;
        int COLUMN_IS_PRESENT = 5;
        int COLUMN_LEAVING_TIME = 6;
        int COLUMN_GRADE = 7;
    }
}
