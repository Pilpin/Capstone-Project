package com.sylvainautran.nanodegree.capstoneproject.data.loaders;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;

public class ExportLoader extends CursorLoader {

    public static ExportLoader getCallDatesForExport(Context context, long classId, long startDate, long endDate){
        return new ExportLoader(
                context,
                AppelContract.CallEntry.buildExportCalls(),
                Query.CALL_DATES,
                AppelContract.CallEntry.COLUMN_CLASS_ID + " = ? AND " + AppelContract.CallEntry.COLUMN_DATETIME +" > ? AND " + AppelContract.CallEntry.COLUMN_DATETIME + " < ?",
                new String[] { Long.toString(classId), Long.toString(startDate), Long.toString(endDate) },
                AppelContract.CallEntry.COLUMN_DATETIME + " ASC");
    }

    public static ExportLoader getStudentsInfoForExport(Context context, long classId, long startDate, long endDate){
        return new ExportLoader(
                context,
                AppelContract.CallEntry.buildExportStudents(),
                Query.STUDENTS_INFO,
                AppelContract.CallEntry.COLUMN_CLASS_ID + " = ? AND " + AppelContract.CallEntry.COLUMN_DATETIME +" > ? AND " + AppelContract.CallEntry.COLUMN_DATETIME + " < ?",
                new String[] { Long.toString(classId), Long.toString(startDate), Long.toString(endDate) },
                AppelContract.StudentEntry.DEFAULT_SORT);
    }

    public static ExportLoader getCallsInfoForStudent(Context context, long classId, long startDate, long endDate, long studentId, long callId){
        return new ExportLoader(
                context,
                AppelContract.CallEntry.buildExportCallsStudents(),
                Query.CALLS_INFO,
                AppelContract.CallEntry.COLUMN_CLASS_ID + " = ? AND " + AppelContract.CallEntry.COLUMN_DATETIME +" > ? AND " + AppelContract.CallEntry.COLUMN_DATETIME +
                        " < ? AND " + AppelContract.CallStudentLinkEntry.COLUMN_STUDENT_ID + " = ? AND " + AppelContract.CallEntry.TABLE_NAME + "." + AppelContract.CallEntry._ID + " = ?",
                new String[] { Long.toString(classId), Long.toString(startDate), Long.toString(endDate), Long.toString(studentId), Long.toString(callId) },
                AppelContract.CallEntry.COLUMN_DATETIME + " ASC");
    }

    private ExportLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
        super(context, uri, projection, selection, selectionArgs, sort);
    }

    public interface Query {
        String[] CALL_DATES = {
                AppelContract.CallEntry.TABLE_NAME + "." + AppelContract.CallEntry._ID,
                AppelContract.CallEntry.COLUMN_DATETIME
        };

        String[] STUDENTS_INFO = {
                AppelContract.StudentEntry.TABLE_NAME + "." + AppelContract.StudentEntry._ID,
                AppelContract.StudentEntry.COLUMN_FIRSTNAME,
                AppelContract.StudentEntry.COLUMN_LASTNAME,
                AppelContract.ClassStudentLinkEntry.COLUMN_GRADE
        };

        String[] CALLS_INFO = {
                AppelContract.CallStudentLinkEntry.COLUMN_IS_PRESENT,
                AppelContract.CallStudentLinkEntry.COLUMN_LEAVING_TIME
        };

        int CALL_ID = 0, STUDENT_ID = 0, IS_PRESENT = 0;
        int DATETIME = 1, FIRSTNAME = 1, LEAVING_TIME = 1;
        int LASTNAME = 2;
        int GRADE = 3;
    }
}
