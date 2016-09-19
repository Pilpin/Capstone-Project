package com.sylvainautran.nanodegree.capstoneproject.loaders;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;

public class StudentsLoader extends CursorLoader {
    public static StudentsLoader getAllStudents(Context context) {
        return new StudentsLoader(context, AppelContract.StudentEntry.CONTENT_URI, Query.PROJECTION);
    }

    public static StudentsLoader getAllStudentsInClass(Context context, long classId) {
        return new StudentsLoader(context, AppelContract.ClassStudentLinkEntry.buildClassStudentLinkFromClassUri(classId), Query.PROJECTION_FROM_CLASS);
    }

    public static StudentsLoader getAllStudentsInClass(Context context, Uri uri) {
        return new StudentsLoader(context, uri, Query.PROJECTION_FROM_CLASS);
    }

    public static StudentsLoader getStudentForId(Context context, long id) {
        return new StudentsLoader(context, AppelContract.StudentEntry.buildStudentUri(id), Query.PROJECTION);
    }

    private StudentsLoader(Context context, Uri uri, String[] projection) {
        super(context, uri, projection, null, null, AppelContract.StudentEntry.DEFAULT_SORT);
    }

    public interface Query {
        String[] PROJECTION = {
                AppelContract.StudentEntry._ID,
                AppelContract.StudentEntry.COLUMN_FIRSTNAME,
                AppelContract.StudentEntry.COLUMN_LASTNAME,
                AppelContract.StudentEntry.COLUMN_BIRTHDATE
        };

        String[] PROJECTION_FROM_CLASS  = {
                AppelContract.ClassStudentLinkEntry.TABLE_NAME + "." + AppelContract.ClassStudentLinkEntry._ID,
                AppelContract.StudentEntry.COLUMN_FIRSTNAME,
                AppelContract.StudentEntry.COLUMN_LASTNAME,
                AppelContract.StudentEntry.COLUMN_BIRTHDATE,
                AppelContract.ClassStudentLinkEntry.COLUMN_GRADE
        };

        int _ID = 0;
        int COLUMN_FIRSTNAME = 1;
        int COLUMN_LASTNAME = 2;
        int COLUMN_BIRTHDATE = 3;
        int COLUMN_GRADE = 4;
    }
}
