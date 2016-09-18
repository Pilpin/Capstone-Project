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

public class AppelProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private AppelDbHelper mOpenHelper;

    static final int CLASS = 100;
    static final int CLASSES = 101;
    static final int STUDENT = 200;
    static final int STUDENTS = 201;
    static final int CALL = 300;
    static final int CALLS = 301;
    static final int CLASS_STUDENT_LINKS = 101201;
    static final int CALL_STUDENT_LINKS = 301201;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AppelContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, AppelContract.PATH_CLASS, CLASSES);
        matcher.addURI(authority, AppelContract.PATH_CLASS + "/#", CLASS);
        matcher.addURI(authority, AppelContract.PATH_STUDENT, STUDENTS);
        matcher.addURI(authority, AppelContract.PATH_STUDENT + "/#", STUDENT);
        matcher.addURI(authority, AppelContract.PATH_CALL, CALLS);
        matcher.addURI(authority, AppelContract.PATH_CALL + "/#", CALL);
        matcher.addURI(authority, AppelContract.PATH_STUDENT_CLASS + "/#", CLASS_STUDENT_LINKS);
        matcher.addURI(authority, AppelContract.PATH_STUDENT_CALL + "/#", CALL_STUDENT_LINKS);

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
            case STUDENTS:
                builder.setTables(AppelContract.StudentEntry.TABLE_NAME);
                break;
            case STUDENT:
                builder.setTables(AppelContract.StudentEntry.TABLE_NAME);
                builder.appendWhere(AppelContract.StudentEntry._ID + " = " + uri.getLastPathSegment());
                break;
            case CALLS:
                builder.setTables(AppelContract.CallEntry.TABLE_NAME);
                break;
            case CALL_STUDENT_LINKS:
                builder.setTables(AppelContract.CallStudentLinkEntry.TABLE_NAME +
                        " JOIN " + AppelContract.StudentEntry.TABLE_NAME + " ON " + AppelContract.StudentEntry.TABLE_NAME + "." + AppelContract.StudentEntry._ID + " = " +
                        AppelContract.CallStudentLinkEntry.COLUMN_STUDENT_ID);
                builder.appendWhere(AppelContract.CallStudentLinkEntry.COLUMN_CALL_ID + " = " + uri.getLastPathSegment());
                break;
            case CLASS_STUDENT_LINKS:
                builder.setTables(AppelContract.ClassStudentLinkEntry.TABLE_NAME +
                        " JOIN " + AppelContract.StudentEntry.TABLE_NAME + " ON " + AppelContract.StudentEntry.TABLE_NAME + "." + AppelContract.StudentEntry._ID + " = " +
                        AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID);
                builder.appendWhere(AppelContract.CallStudentLinkEntry.COLUMN_CALL_ID + " = " + uri.getLastPathSegment());
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
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
            case CALL_STUDENT_LINKS:
                return AppelContract.CallStudentLinkEntry.CONTENT_TYPE;
            case CLASS_STUDENT_LINKS:
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
                long classId = contentValues.getAsLong(AppelContract.CallEntry.COLUMN_CLASS_ID);
                String[] projection = {AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID};
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(AppelContract.ClassStudentLinkEntry.TABLE_NAME);
                builder.appendWhere(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID + " = " + classId);
                Cursor cursor = builder.query(db, projection, null, null, null, null, null);
                ContentValues[] cvs = new ContentValues[cursor.getCount()];
                int i = 0;
                while(cursor.moveToNext()){
                    ContentValues cv = new ContentValues();
                    cv.put(AppelContract.CallStudentLinkEntry.COLUMN_CALL_ID, id);
                    cv.put(AppelContract.CallStudentLinkEntry.COLUMN_STUDENT_ID, cursor.getColumnName(cursor.getColumnIndex(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID)));
                    cvs[i] = cv;
                }
                bulkInsert(AppelContract.CallStudentLinkEntry.CONTENT_URI, cvs);
                break;
            case CALL_STUDENT_LINKS:
                id = db.insert(AppelContract.CallStudentLinkEntry.TABLE_NAME, null, contentValues);
                break;
            case CLASS_STUDENT_LINKS:
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
            case CALL_STUDENT_LINKS:
                rowsDeleted = db.delete(AppelContract.CallStudentLinkEntry.TABLE_NAME, s, strings);
                break;
            case CLASS_STUDENT_LINKS:
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

            switch (sUriMatcher.match(uri)) {
                case CLASSES:
                    rowsUpdated = db.update(AppelContract.ClassEntry.TABLE_NAME, contentValues, s, strings);
                    break;
                case STUDENTS:
                    rowsUpdated = db.update(AppelContract.StudentEntry.TABLE_NAME, contentValues, s, strings);
                    break;
                case CALLS:
                    rowsUpdated = db.update(AppelContract.CallEntry.TABLE_NAME, contentValues, s, strings);
                    break;
                case CALL_STUDENT_LINKS:
                    rowsUpdated = db.update(AppelContract.CallStudentLinkEntry.TABLE_NAME, contentValues, s, strings);
                    break;
                case CLASS_STUDENT_LINKS:
                    rowsUpdated = db.update(AppelContract.ClassStudentLinkEntry.TABLE_NAME, contentValues, s, strings);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }

            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CALL_STUDENT_LINKS:
                db.beginTransaction();
                int count = 0;
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
