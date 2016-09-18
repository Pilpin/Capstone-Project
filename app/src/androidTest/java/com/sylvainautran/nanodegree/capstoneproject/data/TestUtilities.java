package com.sylvainautran.nanodegree.capstoneproject.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.test.runner.AndroidJUnit4;

import com.sylvainautran.nanodegree.capstoneproject.utils.PollingCheck;

import static org.junit.Assert.*;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Map;
import java.util.Set;

@RunWith(AndroidJUnit4.class)
public class TestUtilities {

    static ContentValues createClassValues(int x) {
        ContentValues cv = new ContentValues();
        cv.put(AppelContract.ClassEntry.COLUMN_NAME, "Garderie 2016-" + x);
        return cv;
    }
    static ContentValues createStudentValues(int x) {
        ContentValues cv = new ContentValues();
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "Autran");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Lee");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, getDate(x));
        return cv;
    }
    static ContentValues createCallValues(long classId) {
        ContentValues cv = new ContentValues();
        cv.put(AppelContract.CallEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.CallEntry.COLUMN_DATETIME, getDate(1000));
        cv.put(AppelContract.CallEntry.COLUMN_LEAVING_TIME_OPTION, 1);
        return cv;
    }
    static ContentValues createClassStudentValues(long studentId, long classId) {
        ContentValues cv = new ContentValues();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CP");
        return cv;
    }
    static ContentValues createCallStudentValues(long callId, long studentId) {
        ContentValues cv = new ContentValues();
        cv.put(AppelContract.CallStudentLinkEntry.COLUMN_CALL_ID, callId);
        cv.put(AppelContract.CallStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.CallStudentLinkEntry.COLUMN_IS_PRESENT, 1);
        cv.put(AppelContract.CallStudentLinkEntry.COLUMN_LEAVING_TIME, getDate(0));
        return cv;
    }

    static long getDate(int x){
        Calendar cal = Calendar.getInstance();
        cal.set(2010, 1, 1, 0, 0, 0);
        cal.add(Calendar.DAY_OF_MONTH, x);
        return cal.getTimeInMillis();
    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() + "' did not match the expected value '" + expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
