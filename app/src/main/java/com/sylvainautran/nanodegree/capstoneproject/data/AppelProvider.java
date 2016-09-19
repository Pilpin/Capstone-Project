package com.sylvainautran.nanodegree.capstoneproject.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

public class AppelProvider extends ContentProvider {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private AppelDbHelper mOpenHelper;

    static final int CLASS = 100;
    static final int CLASSES = 101;
    static final int STUDENT = 200;
    static final int STUDENTS = 201;
    static final int CALL = 300;
    static final int CALLS = 301;
    static final int CLASS_STUDENT = 100200;
    static final int CLASS_STUDENTS = 101201;
    public static final int CLASS_STUDENT_FROM_CLASS = 1012010;
    static final int CLASS_STUDENT_FROM_STUDENT = 1012011;
    static final int CALL_STUDENT = 300200;
    static final int CALL_STUDENTS = 301201;
    static final int CALL_STUDENT_WITH_CLASS = 3012010;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AppelContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, AppelContract.PATH_CLASS, CLASSES);
        matcher.addURI(authority, AppelContract.PATH_CLASS + "/#", CLASS);
        matcher.addURI(authority, AppelContract.PATH_STUDENT, STUDENTS);
        matcher.addURI(authority, AppelContract.PATH_STUDENT + "/#", STUDENT);
        matcher.addURI(authority, AppelContract.PATH_CALL, CALLS);
        matcher.addURI(authority, AppelContract.PATH_CALL + "/#", CALL);
        matcher.addURI(authority, AppelContract.PATH_STUDENT_CLASS, CLASS_STUDENTS);
        matcher.addURI(authority, AppelContract.PATH_STUDENT_CLASS + "/#", CLASS_STUDENT);
        matcher.addURI(authority, AppelContract.PATH_STUDENT_CLASS_FROM_CLASS + "/#", CLASS_STUDENT_FROM_CLASS);
        matcher.addURI(authority, AppelContract.PATH_STUDENT_CLASS + "/" + AppelContract.PATH_STUDENT + "/#", CLASS_STUDENT_FROM_STUDENT);
        matcher.addURI(authority, AppelContract.PATH_STUDENT_CALL, CALL_STUDENTS);
        matcher.addURI(authority, AppelContract.PATH_STUDENT_CALL + "/#", CALL_STUDENT);
        matcher.addURI(authority, AppelContract.PATH_STUDENT_CALL + "/#/#", CALL_STUDENT_WITH_CLASS);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new AppelDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)){
            case CLASSES:
                builder.setTables(AppelContract.ClassEntry.TABLE_NAME);
                break;
            case CLASS:
                builder.setTables(AppelContract.ClassEntry.TABLE_NAME);
                builder.appendWhere(AppelContract.ClassEntry._ID + " = " + uri.getLastPathSegment());
                break;
            case STUDENTS:
                builder.setTables(AppelContract.StudentEntry.TABLE_NAME);
                break;
            case STUDENT:
                builder.setTables(AppelContract.StudentEntry.TABLE_NAME);
                builder.appendWhere(AppelContract.StudentEntry._ID + " = " + uri.getLastPathSegment());
                break;
            case CALLS:
                builder.setTables(AppelContract.CallEntry.TABLE_NAME +
                        " JOIN " + AppelContract.ClassEntry.TABLE_NAME + " ON " + AppelContract.CallEntry.COLUMN_CLASS_ID + " = " + AppelContract.ClassEntry.TABLE_NAME + "." + AppelContract
                        .ClassEntry._ID);
                break;
            case CALL:
                builder.setTables(AppelContract.CallEntry.TABLE_NAME);
                builder.appendWhere(AppelContract.CallEntry._ID + " = " + uri.getLastPathSegment());
                break;
            case CALL_STUDENT_WITH_CLASS:
                builder.setTables(AppelContract.ClassStudentLinkEntry.TABLE_NAME +
                        " JOIN " + AppelContract.StudentEntry.TABLE_NAME + " ON " + AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID + " = " + AppelContract.StudentEntry.TABLE_NAME + "." +
                        AppelContract.StudentEntry._ID +
                        " JOIN " + AppelContract.CallEntry.TABLE_NAME + " ON " + AppelContract.CallEntry.COLUMN_CLASS_ID + " = " + AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID +
                        " LEFT JOIN " + AppelContract.CallStudentLinkEntry.TABLE_NAME +
                        " ON " + AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID + " = " + AppelContract.CallStudentLinkEntry.COLUMN_STUDENT_ID +
                        " AND " + AppelContract.CallEntry.TABLE_NAME + "." + AppelContract.CallEntry._ID + " = " + AppelContract.CallStudentLinkEntry.COLUMN_CALL_ID);
                builder.appendWhere(AppelContract.CallStudentLinkEntry.COLUMN_CALL_ID + " = " + AppelContract.CallStudentLinkEntry.getCallId(uri));
                builder.appendWhere(" AND " + AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID + " = " + AppelContract.CallStudentLinkEntry.getClassId(uri));
                break;
            case CLASS_STUDENT_FROM_CLASS:
                builder.setTables(AppelContract.ClassStudentLinkEntry.TABLE_NAME +
                        " JOIN " + AppelContract.StudentEntry.TABLE_NAME + " ON " + AppelContract.StudentEntry.TABLE_NAME + "." + AppelContract.StudentEntry._ID + " = " +
                        AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID);
                builder.appendWhere(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID + " = " + uri.getLastPathSegment());
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Log.w(LOG_TAG, builder.buildQuery(projection, selection, null, null, sortOrder, null));
        Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)){
            case CLASSES:
                return AppelContract.ClassEntry.CONTENT_TYPE;
            case CLASS:
                return AppelContract.ClassEntry.CONTENT_ITEM_TYPE;
            case STUDENTS:
                return AppelContract.StudentEntry.CONTENT_TYPE;
            case STUDENT:
                return AppelContract.StudentEntry.CONTENT_ITEM_TYPE;
            case CALLS:
                return AppelContract.CallEntry.CONTENT_TYPE;
            case CALL:
                return AppelContract.CallEntry.CONTENT_ITEM_TYPE;
            case CALL_STUDENTS:
                return AppelContract.CallStudentLinkEntry.CONTENT_TYPE;
            case CALL_STUDENT:
                return AppelContract.CallStudentLinkEntry.CONTENT_ITEM_TYPE;
            case CALL_STUDENT_WITH_CLASS:
                return AppelContract.CallStudentLinkEntry.CONTENT_TYPE;
            case CLASS_STUDENTS:
                return AppelContract.ClassStudentLinkEntry.CONTENT_TYPE;
            case CLASS_STUDENT:
                return AppelContract.ClassStudentLinkEntry.CONTENT_ITEM_TYPE;
            case CLASS_STUDENT_FROM_CLASS:
                return AppelContract.ClassStudentLinkEntry.CONTENT_TYPE;
            case CLASS_STUDENT_FROM_STUDENT:
                return AppelContract.ClassStudentLinkEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long id;

        switch (sUriMatcher.match(uri)) {
            case CLASSES:
                id = db.insert(AppelContract.ClassEntry.TABLE_NAME, null, contentValues);
                break;
            case STUDENTS:
                id = db.insert(AppelContract.StudentEntry.TABLE_NAME, null, contentValues);
                break;
            case CALLS:
                id = db.insert(AppelContract.CallEntry.TABLE_NAME, null, contentValues);
                break;
            case CALL_STUDENTS:
                id = db.insert(AppelContract.CallStudentLinkEntry.TABLE_NAME, null, contentValues);
                break;
            case CLASS_STUDENTS:
                id = db.insert(AppelContract.ClassStudentLinkEntry.TABLE_NAME, null, contentValues);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (id <= 0 ) {
            throw new android.database.SQLException("Failed to insert row into " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;

        if ( null == s ) s = "1";
        switch (sUriMatcher.match(uri)) {
            case CLASSES:
                rowsDeleted = db.delete(AppelContract.ClassEntry.TABLE_NAME, s, strings);
                break;
            case STUDENTS:
                rowsDeleted = db.delete(AppelContract.StudentEntry.TABLE_NAME, s, strings);
                break;
            case CALLS:
                rowsDeleted = db.delete(AppelContract.CallEntry.TABLE_NAME, s, strings);
                break;
            case CALL_STUDENTS:
                rowsDeleted = db.delete(AppelContract.CallStudentLinkEntry.TABLE_NAME, s, strings);
                break;
            case CLASS_STUDENTS:
                rowsDeleted = db.delete(AppelContract.ClassStudentLinkEntry.TABLE_NAME, s, strings);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;
        ArrayList<Uri> urisToNotify = new ArrayList<Uri>();
        urisToNotify.add(uri);

        switch (sUriMatcher.match(uri)) {
            case CLASSES:
                rowsUpdated = db.update(AppelContract.ClassEntry.TABLE_NAME, contentValues, s, strings);
                break;
            case STUDENTS:
                rowsUpdated = db.update(AppelContract.StudentEntry.TABLE_NAME, contentValues, s, strings);
                urisToNotify.add(AppelContract.ClassStudentLinkEntry.CONTENT_URI);
                urisToNotify.add(AppelContract.CallStudentLinkEntry.CONTENT_URI);
                break;
            case CALLS:
                rowsUpdated = db.update(AppelContract.CallEntry.TABLE_NAME, contentValues, s, strings);
                break;
            case CALL_STUDENTS:
                rowsUpdated = db.update(AppelContract.CallStudentLinkEntry.TABLE_NAME, contentValues, s, strings);
                break;
            case CLASS_STUDENTS:
                rowsUpdated = db.update(AppelContract.ClassStudentLinkEntry.TABLE_NAME, contentValues, s, strings);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            int i = 0;
            while(!urisToNotify.isEmpty()){
                getContext().getContentResolver().notifyChange(urisToNotify.remove(i), null);
            }
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case STUDENTS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(AppelContract.StudentEntry.TABLE_NAME, null, value);
                        if (id != -1) {
                            count++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            case CALL_STUDENTS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(AppelContract.CallStudentLinkEntry.TABLE_NAME, null, value);
                        if (id != -1) {
                            count++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
