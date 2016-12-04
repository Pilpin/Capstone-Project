package com.sylvainautran.nanodegree.capstoneproject.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telecom.Call;
import android.util.Log;

import java.util.ArrayList;

import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract.ClassEntry;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract.StudentEntry;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract.CallEntry;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract.ClassStudentLinkEntry;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract.CallStudentLinkEntry;

public class AppelProvider extends ContentProvider {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private AppelDbHelper mOpenHelper;

    static final int CLASS = 1;
    static final int CLASSES = 2;
    static final int STUDENT = 3;
    static final int STUDENTS = 4;
    static final int CALL = 5;
    static final int CALLS = 6;
    static final int CLASS_STUDENT = 7;
    static final int CLASS_STUDENTS = 8;
    static final int CLASS_STUDENT_FROM_CLASS = 9;
    static final int CLASS_STUDENT_NOT_FROM_CLASS = 10;
    static final int CLASS_STUDENT_FROM_STUDENT = 11;
    static final int CALL_STUDENT = 12;
    static final int CALL_STUDENTS = 13;
    static final int CALL_STUDENT_WITH_CLASS = 14;

    static final int STATS = 15;
    static final int STATS_FOR_ONE = 16;

    static final int EXPORT_MONTH = 17;
    static final int EXPORT_CALLS = 18;
    static final int EXPORT_STUDENTS = 19;
    static final int EXPORT_CALLS_STUDENTS = 20;

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
        matcher.addURI(authority, AppelContract.PATH_STUDENT_CLASS_NOT_FROM_CLASS + "/#", CLASS_STUDENT_NOT_FROM_CLASS);
        matcher.addURI(authority, AppelContract.PATH_STUDENT_CLASS + "/" + AppelContract.PATH_STUDENT + "/#", CLASS_STUDENT_FROM_STUDENT);
        matcher.addURI(authority, AppelContract.PATH_STUDENT_CALL, CALL_STUDENTS);
        matcher.addURI(authority, AppelContract.PATH_STUDENT_CALL + "/#", CALL_STUDENT);
        matcher.addURI(authority, AppelContract.PATH_STUDENT_CALL + "/#/#", CALL_STUDENT_WITH_CLASS);

        matcher.addURI(authority, AppelContract.PATH_CALL + "/" + AppelContract.PATH_STATS + "/#", STATS_FOR_ONE);
        matcher.addURI(authority, AppelContract.PATH_CALL + "/" + AppelContract.PATH_STATS + "/#/#", STATS);

        matcher.addURI(authority, AppelContract.PATH_EXPORT_MONTHS + "/#", EXPORT_MONTH);
        matcher.addURI(authority, AppelContract.PATH_EXPORT_CALLS, EXPORT_CALLS);
        matcher.addURI(authority, AppelContract.PATH_EXPORT_STUDENTS, EXPORT_STUDENTS);
        matcher.addURI(authority, AppelContract.PATH_EXPORT_CALLS_STUDENTS, EXPORT_CALLS_STUDENTS);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new AppelDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        String groupBy = null;

        switch (sUriMatcher.match(uri)){
            case CLASSES:
                builder.setTables(ClassEntry.TABLE_NAME);
                builder.appendWhere(ClassEntry.COLUMN_DELETED + " = " + AppelContract.PUBLIC);
                break;
            case CLASS:
                builder.setTables(ClassEntry.TABLE_NAME);
                builder.appendWhere(ClassEntry._ID + " = " + uri.getLastPathSegment());
                builder.appendWhere(" AND " + ClassEntry.COLUMN_DELETED + " = " + AppelContract.PUBLIC);
                break;
            case STUDENTS:
                builder.setTables(StudentEntry.TABLE_NAME);
                builder.appendWhere(StudentEntry.COLUMN_DELETED + " = " + AppelContract.PUBLIC);
                break;
            case STUDENT:
                builder.setTables(StudentEntry.TABLE_NAME);
                builder.appendWhere(StudentEntry._ID + " = " + uri.getLastPathSegment());
                builder.appendWhere(" AND " + StudentEntry.COLUMN_DELETED + " = " + AppelContract.PUBLIC);
                break;
            case CALLS:
                builder.setTables(CallEntry.TABLE_NAME + " JOIN " + ClassEntry.TABLE_NAME + " ON " + CallEntry.COLUMN_CLASS_ID + " = " + ClassEntry.TABLE_NAME + "." + AppelContract.ClassEntry._ID);
                builder.appendWhere(CallEntry.COLUMN_DELETED + " = " + AppelContract.PUBLIC);
                builder.appendWhere(" AND " + ClassEntry.COLUMN_DELETED + " = " + AppelContract.PUBLIC);
                break;
            case CALL:
                builder.setTables(CallEntry.TABLE_NAME);
                builder.appendWhere(CallEntry._ID + " = " + uri.getLastPathSegment());
                builder.appendWhere(" AND " + CallEntry.COLUMN_DELETED + " = " + AppelContract.PUBLIC);
                break;
            case CALL_STUDENT_WITH_CLASS:
                builder.setTables(ClassStudentLinkEntry.TABLE_NAME +
                        " JOIN " + StudentEntry.TABLE_NAME + " ON " + ClassStudentLinkEntry.COLUMN_STUDENT_ID + " = " + StudentEntry.TABLE_NAME + "." +
                        StudentEntry._ID +
                        " JOIN " + CallEntry.TABLE_NAME + " ON " + CallEntry.COLUMN_CLASS_ID + " = " + ClassStudentLinkEntry.COLUMN_CLASS_ID +
                        " LEFT JOIN " + CallStudentLinkEntry.TABLE_NAME +
                        " ON " + ClassStudentLinkEntry.COLUMN_STUDENT_ID + " = " + CallStudentLinkEntry.COLUMN_STUDENT_ID +
                        " AND " + CallEntry.TABLE_NAME + "." + CallEntry._ID + " = " + CallStudentLinkEntry.COLUMN_CALL_ID);
                builder.appendWhere(CallEntry.TABLE_NAME + "." + CallEntry._ID + " = " + CallStudentLinkEntry.getCallId(uri));
                builder.appendWhere(" AND " + ClassStudentLinkEntry.COLUMN_CLASS_ID + " = " + CallStudentLinkEntry.getClassId(uri));
                builder.appendWhere(" AND " + ClassStudentLinkEntry.COLUMN_DELETED + " = " + AppelContract.PUBLIC);
                builder.appendWhere(" AND " + StudentEntry.COLUMN_DELETED + " = " + AppelContract.PUBLIC);
                builder.appendWhere(" AND " + CallEntry.COLUMN_DELETED + " = " + AppelContract.PUBLIC);
                break;
            case CLASS_STUDENT_FROM_CLASS:
                builder.setTables(ClassStudentLinkEntry.TABLE_NAME +
                        " JOIN " + StudentEntry.TABLE_NAME + " ON " + StudentEntry.TABLE_NAME + "." + StudentEntry._ID + " = " +
                        ClassStudentLinkEntry.COLUMN_STUDENT_ID);
                builder.appendWhere(ClassStudentLinkEntry.COLUMN_CLASS_ID + " = " + uri.getLastPathSegment());
                builder.appendWhere(" AND " + ClassStudentLinkEntry.COLUMN_DELETED + " = " + AppelContract.PUBLIC);
                builder.appendWhere(" AND " + StudentEntry.COLUMN_DELETED + " = " + AppelContract.PUBLIC);
                break;
            case CLASS_STUDENT_NOT_FROM_CLASS:
                String[] nestedProjection = new String[] {StudentEntry.TABLE_NAME + "." + StudentEntry._ID};
                SQLiteQueryBuilder nestedBuilder = new SQLiteQueryBuilder();
                nestedBuilder.setTables(ClassStudentLinkEntry.TABLE_NAME +
                        " JOIN " + StudentEntry.TABLE_NAME + " ON " + StudentEntry.TABLE_NAME + "." + StudentEntry._ID + " = " +
                        ClassStudentLinkEntry.COLUMN_STUDENT_ID);
                nestedBuilder.appendWhere(ClassStudentLinkEntry.COLUMN_CLASS_ID + " = " + uri.getLastPathSegment());
                nestedBuilder.appendWhere(" AND " + ClassStudentLinkEntry.COLUMN_DELETED + " = " + AppelContract.PUBLIC);
                nestedBuilder.appendWhere(" AND " + StudentEntry.COLUMN_DELETED + " = " + AppelContract.PUBLIC);

                String nestedQuery = nestedBuilder.buildQuery(nestedProjection, null, null, null, null, null);

                builder.setTables(StudentEntry.TABLE_NAME);
                builder.appendWhere(StudentEntry.COLUMN_DELETED + " = " + AppelContract.PUBLIC);
                builder.appendWhere(" AND " + StudentEntry.TABLE_NAME + "." + StudentEntry._ID + " NOT IN (" + nestedQuery + ")");
                break;
            case EXPORT_MONTH:
                builder.setTables(CallEntry.TABLE_NAME + " JOIN " + ClassEntry.TABLE_NAME + " ON " + ClassEntry.TABLE_NAME + "." + ClassEntry._ID);
                builder.appendWhere(CallEntry.COLUMN_DELETED + " = " + AppelContract.PUBLIC);
                groupBy = CallEntry.AS_COLUMN_MONTH;
                break;
            case EXPORT_CALLS:
                groupBy = CallEntry.COLUMN_DATETIME;
                sortOrder = CallEntry.COLUMN_DATETIME + " ASC";
                builder.setTables(CallEntry.TABLE_NAME +
                        " JOIN " + CallStudentLinkEntry.TABLE_NAME + " ON " + CallEntry.TABLE_NAME + "." + CallEntry._ID + " = " + CallStudentLinkEntry.COLUMN_CALL_ID +
                        " JOIN " + StudentEntry.TABLE_NAME + " ON " + CallStudentLinkEntry.COLUMN_STUDENT_ID + " = " + StudentEntry.TABLE_NAME + "." + StudentEntry._ID +
                        " JOIN " + ClassStudentLinkEntry.TABLE_NAME + " ON " + CallEntry.COLUMN_CLASS_ID + " = " + ClassStudentLinkEntry.COLUMN_CLASS_ID +
                        " AND " + CallStudentLinkEntry.COLUMN_STUDENT_ID + " = " + ClassStudentLinkEntry.COLUMN_STUDENT_ID);
                break;
            case EXPORT_STUDENTS:
                groupBy = StudentEntry.TABLE_NAME + "." + StudentEntry._ID;
                sortOrder = StudentEntry.COLUMN_LASTNAME + " ASC";
                builder.setTables(CallEntry.TABLE_NAME +
                        " JOIN " + CallStudentLinkEntry.TABLE_NAME + " ON " + CallEntry.TABLE_NAME + "." + CallEntry._ID + " = " + CallStudentLinkEntry.COLUMN_CALL_ID +
                        " JOIN " + StudentEntry.TABLE_NAME + " ON " + CallStudentLinkEntry.COLUMN_STUDENT_ID + " = " + StudentEntry.TABLE_NAME + "." + StudentEntry._ID +
                        " JOIN " + ClassStudentLinkEntry.TABLE_NAME + " ON " + CallEntry.COLUMN_CLASS_ID + " = " + ClassStudentLinkEntry.COLUMN_CLASS_ID +
                        " AND " + CallStudentLinkEntry.COLUMN_STUDENT_ID + " = " + ClassStudentLinkEntry.COLUMN_STUDENT_ID);
                break;
            case EXPORT_CALLS_STUDENTS:
                sortOrder = CallEntry.COLUMN_DATETIME + " ASC";
                builder.setTables(CallEntry.TABLE_NAME +
                        " JOIN " + CallStudentLinkEntry.TABLE_NAME + " ON " + CallEntry.TABLE_NAME + "." + CallEntry._ID + " = " + CallStudentLinkEntry.COLUMN_CALL_ID +
                        " JOIN " + StudentEntry.TABLE_NAME + " ON " + CallStudentLinkEntry.COLUMN_STUDENT_ID + " = " + StudentEntry.TABLE_NAME + "." + StudentEntry._ID +
                        " JOIN " + ClassStudentLinkEntry.TABLE_NAME + " ON " + CallEntry.COLUMN_CLASS_ID + " = " + ClassStudentLinkEntry.COLUMN_CLASS_ID +
                        " AND " + CallStudentLinkEntry.COLUMN_STUDENT_ID + " = " + ClassStudentLinkEntry.COLUMN_STUDENT_ID);
                break;
            case STATS:
                sortOrder = CallEntry.DEFAULT_SORT;
                groupBy = CallEntry.TABLE_NAME + "." + CallEntry._ID;
                builder.setTables(CallEntry.TABLE_NAME +
                        " JOIN " + CallStudentLinkEntry.TABLE_NAME + " ON " + CallEntry.TABLE_NAME + "." + CallEntry._ID + " = " + CallStudentLinkEntry.COLUMN_CALL_ID +
                        " JOIN " + ClassEntry.TABLE_NAME + " ON " + CallEntry.COLUMN_CLASS_ID + " = " + ClassEntry.TABLE_NAME + "." + ClassEntry._ID);
                break;
            case STATS_FOR_ONE:
                builder.setTables(CallEntry.TABLE_NAME +
                        " JOIN " + CallStudentLinkEntry.TABLE_NAME + " ON " + CallEntry.TABLE_NAME + "." + CallEntry._ID + " = " + CallStudentLinkEntry.COLUMN_CALL_ID +
                        " JOIN " + ClassEntry.TABLE_NAME + " ON " + CallEntry.COLUMN_CLASS_ID + " = " + ClassEntry.TABLE_NAME + "." + ClassEntry._ID +
                        " JOIN " + ClassStudentLinkEntry.TABLE_NAME + " ON " + CallEntry.COLUMN_CLASS_ID + " = " + ClassStudentLinkEntry.COLUMN_CLASS_ID +
                        " AND " + CallStudentLinkEntry.COLUMN_STUDENT_ID + " = " + ClassStudentLinkEntry.COLUMN_STUDENT_ID);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Log.e(LOG_TAG, builder.buildQuery(projection, selection, groupBy, null, sortOrder, null));
        Cursor cursor = builder.query(db, projection, selection, selectionArgs, groupBy, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)){
            case CLASSES:
                return ClassEntry.CONTENT_TYPE;
            case CLASS:
                return ClassEntry.CONTENT_ITEM_TYPE;
            case STUDENTS:
                return StudentEntry.CONTENT_TYPE;
            case STUDENT:
                return StudentEntry.CONTENT_ITEM_TYPE;
            case CALLS:
                return CallEntry.CONTENT_TYPE;
            case CALL:
                return CallEntry.CONTENT_ITEM_TYPE;
            case CALL_STUDENTS:
                return CallStudentLinkEntry.CONTENT_TYPE;
            case CALL_STUDENT:
                return CallStudentLinkEntry.CONTENT_ITEM_TYPE;
            case CALL_STUDENT_WITH_CLASS:
                return CallStudentLinkEntry.CONTENT_TYPE;
            case CLASS_STUDENTS:
                return ClassStudentLinkEntry.CONTENT_TYPE;
            case CLASS_STUDENT:
                return ClassStudentLinkEntry.CONTENT_ITEM_TYPE;
            case CLASS_STUDENT_FROM_CLASS:
                return ClassStudentLinkEntry.CONTENT_TYPE;
            case CLASS_STUDENT_NOT_FROM_CLASS:
                return ClassStudentLinkEntry.CONTENT_TYPE;
            case CLASS_STUDENT_FROM_STUDENT:
                return ClassStudentLinkEntry.CONTENT_TYPE;
            case EXPORT_MONTH:
                return CallEntry.CONTENT_TYPE;
            default:
                Log.e(LOG_TAG, getContext().getString(R.string.unknown_uri));
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long id;
        ArrayList<Uri> urisToNotify = new ArrayList<Uri>();
        urisToNotify.add(uri);

        switch (sUriMatcher.match(uri)) {
            case CLASSES:
                id = db.insert(ClassEntry.TABLE_NAME, null, contentValues);
                break;
            case STUDENTS:
                id = db.insert(StudentEntry.TABLE_NAME, null, contentValues);
                break;
            case CALLS:
                id = db.insert(CallEntry.TABLE_NAME, null, contentValues);
                break;
            case CALL_STUDENTS:
                id = db.insert(CallStudentLinkEntry.TABLE_NAME, null, contentValues);
                urisToNotify.add(CallEntry.STATS_CONTENT_URI);
                break;
            case CLASS_STUDENTS:
                id = db.insert(ClassStudentLinkEntry.TABLE_NAME, null, contentValues);
                break;
            default:
                Log.e(LOG_TAG, getContext().getString(R.string.unknown_uri));
                return null;
        }

        if(id > 0){
            for(int i = 0; !urisToNotify.isEmpty(); ){
                getContext().getContentResolver().notifyChange(urisToNotify.remove(i), null);
            }
        }
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] strings) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;
        ContentValues contentValues = new ContentValues();
        ArrayList<Uri> urisToNotify = new ArrayList<Uri>();
        urisToNotify.add(uri);

        if ( null == s ) s = "1";
        switch (sUriMatcher.match(uri)) {
            case CLASSES:
                contentValues.put(ClassEntry.COLUMN_DELETED, AppelContract.DELETED);
                rowsDeleted = db.update(ClassEntry.TABLE_NAME, contentValues, s, strings);
                break;
            case STUDENTS:
                contentValues.put(StudentEntry.COLUMN_DELETED, AppelContract.DELETED);
                rowsDeleted = db.update(StudentEntry.TABLE_NAME, contentValues, s, strings);
                urisToNotify.add(ClassStudentLinkEntry.CONTENT_URI);
                urisToNotify.add(CallStudentLinkEntry.CONTENT_URI);
                break;
            case CALLS:
                contentValues.put(CallEntry.COLUMN_DELETED, AppelContract.DELETED);
                rowsDeleted = db.update(CallEntry.TABLE_NAME, contentValues, s, strings);
                break;
            case CLASS_STUDENTS:
                contentValues.put(ClassStudentLinkEntry.COLUMN_DELETED, AppelContract.DELETED);
                rowsDeleted = db.update(ClassStudentLinkEntry.TABLE_NAME, contentValues, s, strings);
                break;
            default:
                Log.e(LOG_TAG, getContext().getString(R.string.unknown_uri));
                rowsDeleted = 0;
        }

        if (rowsDeleted != 0) {
            for(int i = 0; !urisToNotify.isEmpty(); ){
                getContext().getContentResolver().notifyChange(urisToNotify.remove(i), null);
            }
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;
        ArrayList<Uri> urisToNotify = new ArrayList<Uri>();
        urisToNotify.add(uri);

        switch (sUriMatcher.match(uri)) {
            case CLASSES:
                rowsUpdated = db.update(ClassEntry.TABLE_NAME, contentValues, s, strings);
                break;
            case STUDENTS:
                rowsUpdated = db.update(StudentEntry.TABLE_NAME, contentValues, s, strings);
                urisToNotify.add(ClassStudentLinkEntry.CONTENT_URI);
                urisToNotify.add(CallStudentLinkEntry.CONTENT_URI);
                break;
            case CALLS:
                rowsUpdated = db.update(CallEntry.TABLE_NAME, contentValues, s, strings);
                break;
            case CALL_STUDENTS:
                rowsUpdated = db.update(CallStudentLinkEntry.TABLE_NAME, contentValues, s, strings);
                urisToNotify.add(CallEntry.STATS_CONTENT_URI);
                break;
            case CLASS_STUDENTS:
                rowsUpdated = db.update(ClassStudentLinkEntry.TABLE_NAME, contentValues, s, strings);
                break;
            default:
                Log.e(LOG_TAG, getContext().getString(R.string.unknown_uri));
                rowsUpdated = 0;
        }

        if (rowsUpdated != 0) {
            for(int i = 0; !urisToNotify.isEmpty(); ){
                getContext().getContentResolver().notifyChange(urisToNotify.remove(i), null);
            }
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case STUDENTS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(StudentEntry.TABLE_NAME, null, value);
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
            case CLASS_STUDENTS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(ClassStudentLinkEntry.TABLE_NAME, null, value);
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
                        long id = db.insert(CallStudentLinkEntry.TABLE_NAME, null, value);
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
