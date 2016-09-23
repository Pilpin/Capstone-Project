package com.sylvainautran.nanodegree.capstoneproject.data;

import android.content.Context;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract.StudentEntry;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract.ClassEntry;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract.CallEntry;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract.CallStudentLinkEntry;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract.ClassStudentLinkEntry;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.sylvainautran.nanodegree.capstoneproject.data.AppelContract.PUBLIC;

public class AppelDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "appel.db";

    public AppelDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_STUDENT_TABLE = "CREATE TABLE " + StudentEntry.TABLE_NAME + " (" +
                StudentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                StudentEntry.COLUMN_FIRSTNAME + " TEXT NOT NULL, " +
                StudentEntry.COLUMN_LASTNAME + " TEXT NOT NULL, " +
                StudentEntry.COLUMN_BIRTHDATE + " INTEGER NOT NULL, " +
                StudentEntry.COLUMN_DELETED + " INTEGER DEFAULT " + PUBLIC + ");";

        final String SQL_CREATE_CLASS_TABLE = "CREATE TABLE " + ClassEntry.TABLE_NAME + " (" +
                ClassEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ClassEntry.COLUMN_NAME + " TEXT NOT NULL," +
                ClassEntry.COLUMN_DELETED + " INTEGER DEFAULT " + PUBLIC + ");";

        final String SQL_CREATE_CALL_TABLE = "CREATE TABLE " + CallEntry.TABLE_NAME + " (" +
                CallEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CallEntry.COLUMN_CLASS_ID + " INTEGER NOT NULL, " +
                CallEntry.COLUMN_DATETIME + " INTEGER NOT NULL, " +
                CallEntry.COLUMN_LEAVING_TIME_OPTION + " INTEGER DEFAULT 0, " +
                CallEntry.COLUMN_DELETED + " INTEGER DEFAULT " + PUBLIC + ", " +

                "FOREIGN KEY (" + CallEntry.COLUMN_CLASS_ID + ") REFERENCES " + ClassEntry.TABLE_NAME + "(" + ClassEntry._ID  + "));";

        final String SQL_CREATE_CLASS_STUDENT_TABLE = "CREATE TABLE " + ClassStudentLinkEntry.TABLE_NAME + " (" +
                ClassStudentLinkEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ClassStudentLinkEntry.COLUMN_CLASS_ID + " INTEGER NOT NULL, " +
                ClassStudentLinkEntry.COLUMN_STUDENT_ID + " INTEGER NOT NULL, " +
                ClassStudentLinkEntry.COLUMN_GRADE + " TEXT, " +
                ClassStudentLinkEntry.COLUMN_DELETED + " INTEGER DEFAULT " + PUBLIC + ", " +

                " FOREIGN KEY (" + ClassStudentLinkEntry.COLUMN_CLASS_ID + ") REFERENCES " + ClassEntry.TABLE_NAME + "(" + ClassEntry._ID + "), " +
                " FOREIGN KEY (" + ClassStudentLinkEntry.COLUMN_STUDENT_ID + ") REFERENCES " + StudentEntry.TABLE_NAME + "(" + StudentEntry._ID + "));";

        final String SQL_CREATE_CALL_STUDENT_TABLE = "CREATE TABLE " + CallStudentLinkEntry.TABLE_NAME + " (" +
                CallStudentLinkEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CallStudentLinkEntry.COLUMN_CALL_ID + " INTEGER NOT NULL, " +
                CallStudentLinkEntry.COLUMN_STUDENT_ID + " INTEGER NOT NULL, " +
                CallStudentLinkEntry.COLUMN_IS_PRESENT +  " INTEGER NOT NULL, " +
                CallStudentLinkEntry.COLUMN_LEAVING_TIME + " INTEGER, " +

                " FOREIGN KEY (" + CallStudentLinkEntry.COLUMN_CALL_ID + ") REFERENCES " + CallEntry.TABLE_NAME + "(" + CallEntry._ID + "), " +
                " FOREIGN KEY (" + CallStudentLinkEntry.COLUMN_STUDENT_ID + ") REFERENCES " + StudentEntry.TABLE_NAME + "(" + StudentEntry._ID + "));";

        sqLiteDatabase.execSQL(SQL_CREATE_STUDENT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CLASS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CALL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CLASS_STUDENT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CALL_STUDENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CallStudentLinkEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ClassStudentLinkEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CallEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ClassEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + StudentEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
