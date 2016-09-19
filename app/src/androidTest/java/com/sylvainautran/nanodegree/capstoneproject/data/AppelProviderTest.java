package com.sylvainautran.nanodegree.capstoneproject.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AppelProviderTest {
    private String LOG_TAG = AppelProviderTest.class.getSimpleName();
    private Context mContext;

    @Before
    public void setUp() throws Exception {
        mContext = InstrumentationRegistry.getTargetContext();
        deleteAllRecordsFromProvider();
    }

    @Test
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(), AppelProvider.class.getName());
        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            assertEquals("Error: provider registered with authority: " + providerInfo.authority + " instead of authority: " + AppelContract.CONTENT_AUTHORITY,
                    providerInfo.authority, AppelContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            assertTrue("Error: provider not registered at " + mContext.getPackageName(), false);
        }
    }

    @Test
    public void testGetType() throws Exception {
        String studentsType = mContext.getContentResolver().getType(AppelContract.StudentEntry.CONTENT_URI);
        assertEquals("Error: the StudentEntry CONTENT_URI should return StudentEntry.CONTENT_TYPE", AppelContract.StudentEntry.CONTENT_TYPE, studentsType);

        String classesType = mContext.getContentResolver().getType(AppelContract.ClassEntry.CONTENT_URI);
        assertEquals("Error: the ClassEntry CONTENT_URI should return ClassEntry.CONTENT_TYPE", AppelContract.ClassEntry.CONTENT_TYPE, classesType);

        String callsType = mContext.getContentResolver().getType(AppelContract.CallEntry.CONTENT_URI);
        assertEquals("Error: the CallEntry CONTENT_URI should return CallEntry.CONTENT_TYPE", AppelContract.CallEntry.CONTENT_TYPE, callsType);

        String studentType = mContext.getContentResolver().getType(AppelContract.StudentEntry.buildStudentUri(10));
        assertEquals("Error: the StudentEntry CONTENT_URI should return StudentEntry.CONTENT_TYPE", AppelContract.StudentEntry.CONTENT_ITEM_TYPE, studentType);

        String classType = mContext.getContentResolver().getType(AppelContract.ClassEntry.buildClassUri(20));
        assertEquals("Error: the ClassEntry CONTENT_URI should return ClassEntry.CONTENT_TYPE", AppelContract.ClassEntry.CONTENT_ITEM_TYPE, classType);

        String callType = mContext.getContentResolver().getType(AppelContract.CallEntry.buildCallUri(30));
        assertEquals("Error: the CallEntry CONTENT_URI should return CallEntry.CONTENT_TYPE", AppelContract.CallEntry.CONTENT_ITEM_TYPE, callType);

        String classStudentType = mContext.getContentResolver().getType(AppelContract.ClassStudentLinkEntry.buildClassStudentLinkFromClassUri(20));
        assertEquals("Error: the ClassStudentLinkEntry CONTENT_URI should return ClassStudentLinkEntry.CONTENT_TYPE", AppelContract.ClassStudentLinkEntry.CONTENT_TYPE, classStudentType);

        classStudentType = mContext.getContentResolver().getType(AppelContract.ClassStudentLinkEntry.buildClassStudentLinkFromStudentUri(20));
        assertEquals("Error: the ClassStudentLinkEntry CONTENT_URI should return ClassStudentLinkEntry.CONTENT_TYPE", AppelContract.ClassStudentLinkEntry.CONTENT_TYPE, classStudentType);

        classStudentType = mContext.getContentResolver().getType(AppelContract.ClassStudentLinkEntry.buildClassStudentLinkFromUri(20));
        assertEquals("Error: the ClassStudentLinkEntry CONTENT_URI should return ClassStudentLinkEntry.CONTENT_TYPE", AppelContract.ClassStudentLinkEntry.CONTENT_ITEM_TYPE, classStudentType);

        classStudentType = mContext.getContentResolver().getType(AppelContract.ClassStudentLinkEntry.CONTENT_URI);
        assertEquals("Error: the ClassStudentLinkEntry CONTENT_URI should return ClassStudentLinkEntry.CONTENT_TYPE", AppelContract.ClassStudentLinkEntry.CONTENT_TYPE, classStudentType);

        String callStudentType = mContext.getContentResolver().getType(AppelContract.CallStudentLinkEntry.buildCallStudentLinkUriWithCallAndClass(10, 20));
        assertEquals("Error: the CallStudentLinkEntry CONTENT_URI should return CallStudentLinkEntry.CONTENT_TYPE", AppelContract.CallStudentLinkEntry.CONTENT_TYPE, callStudentType);

        callStudentType = mContext.getContentResolver().getType(AppelContract.CallStudentLinkEntry.CONTENT_URI);
        assertEquals("Error: the CallStudentLinkEntry CONTENT_URI should return CallStudentLinkEntry.CONTENT_TYPE", AppelContract.CallStudentLinkEntry.CONTENT_TYPE, callStudentType);

        callStudentType = mContext.getContentResolver().getType(AppelContract.CallStudentLinkEntry.buildCallStudentLinkUri(10));
        assertEquals("Error: the CallStudentLinkEntry CONTENT_URI should return CallStudentLinkEntry.CONTENT_TYPE", AppelContract.CallStudentLinkEntry.CONTENT_ITEM_TYPE, callStudentType);
    }

    @Test
    public void testInsertReadClass() {
        insertClass(16);
    }

    @Test
    public void testInsertReadStudent() {
        insertStudent(1);
    }

    @Test
    public void testInsertReadCall() {
        insertCall(insertClass(17));
    }

    @Test
    public void testInsertReadClassStudent() {
        long classId = insertClass(18);
        long studentId = insertStudent(2);
        insertClassStudent(classId, studentId);
    }

    @Test
    public void testInsertReadCallStudent(){
        long classId = insertClass(19);
        long studentId = insertStudent(3);
        insertClassStudent(classId, studentId);
        long callId = insertCall(classId);
        insertCallStudent(callId, studentId, classId);
    }

    @Test
    public void testUpdateClass() {
        ContentValues values = TestUtilities.createClassValues(20);

        Uri classUri = mContext.getContentResolver().insert(AppelContract.ClassEntry.CONTENT_URI, values);
        long classId = ContentUris.parseId(classUri);

        assertTrue(classId != -1);
        Log.d(LOG_TAG, "New class row id: " + classId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(AppelContract.ClassEntry._ID, classId);
        updatedValues.put(AppelContract.ClassEntry.COLUMN_NAME, "Not the same name");

        Cursor classCursor = mContext.getContentResolver().query(AppelContract.ClassEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        classCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(AppelContract.ClassEntry.CONTENT_URI, updatedValues, AppelContract.ClassEntry._ID + "= ?", new String[] { Long.toString(classId)});
        assertEquals(count, 1);

        tco.waitForNotificationOrFail();

        classCursor.unregisterContentObserver(tco);
        classCursor.close();

        Cursor cursor = mContext.getContentResolver().query(
                AppelContract.ClassEntry.CONTENT_URI,
                null,   // projection
                AppelContract.ClassEntry._ID + " = " + classId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateClass.  Error validating class entry update.", cursor, updatedValues);

        cursor.close();
    }

    @Test
    public void testUpdateStudent() {
        ContentValues values = TestUtilities.createStudentValues(4);

        Uri studentUri = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, values);
        long studentId = ContentUris.parseId(studentUri);

        assertTrue(studentId != -1);
        Log.d(LOG_TAG, "New student row id: " + studentId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(AppelContract.StudentEntry._ID, studentId);
        updatedValues.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Not the same name");

        long classId = insertClass(21);
        insertClassStudent(classId, studentId);
        long callId = insertCall(classId);
        insertCallStudent(callId, studentId, classId);

        Cursor studentCursor = mContext.getContentResolver().query(AppelContract.StudentEntry.CONTENT_URI, null, null, null, null);
        Cursor classStudentCursor = mContext.getContentResolver().query(AppelContract.ClassStudentLinkEntry.buildClassStudentLinkFromClassUri(classId), null, null, null, null);
        Cursor callStudentCursor = mContext.getContentResolver().query(AppelContract.CallStudentLinkEntry.buildCallStudentLinkUriWithCallAndClass(classId, callId), null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        studentCursor.registerContentObserver(tco);
        classStudentCursor.registerContentObserver(tco);
        callStudentCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(AppelContract.StudentEntry.CONTENT_URI, updatedValues, AppelContract.StudentEntry._ID + "= ?", new String[] { Long.toString(studentId)});
        assertEquals(count, 1);

        tco.waitForNotificationOrFail();

        studentCursor.unregisterContentObserver(tco);
        classStudentCursor.unregisterContentObserver(tco);
        callStudentCursor.unregisterContentObserver(tco);
        studentCursor.close();
        classStudentCursor.close();
        callStudentCursor.close();

        Cursor cursor = mContext.getContentResolver().query(
                AppelContract.StudentEntry.CONTENT_URI,
                null,   // projection
                AppelContract.StudentEntry._ID + " = " + studentId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateStudent.  Error validating student entry update.", cursor, updatedValues);

        cursor.close();
    }

    @Test
    public void testUpdateCall() {
        ContentValues values = TestUtilities.createCallValues(insertClass(22));

        Uri callUri = mContext.getContentResolver().insert(AppelContract.CallEntry.CONTENT_URI, values);
        long callId = ContentUris.parseId(callUri);

        assertTrue(callId != -1);
        Log.d(LOG_TAG, "New call row id: " + callId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(AppelContract.CallEntry._ID, callId);
        updatedValues.put(AppelContract.CallEntry.COLUMN_DATETIME, Calendar.getInstance().getTimeInMillis());

        Cursor callCursor = mContext.getContentResolver().query(AppelContract.CallEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        callCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(AppelContract.CallEntry.CONTENT_URI, updatedValues, AppelContract.CallEntry._ID + "= ?", new String[] { Long.toString(callId)});
        assertEquals(count, 1);

        tco.waitForNotificationOrFail();

        callCursor.unregisterContentObserver(tco);
        callCursor.close();

        Cursor cursor = mContext.getContentResolver().query(
                AppelContract.CallEntry.CONTENT_URI,
                new String[] {AppelContract.CallEntry.TABLE_NAME + "." + AppelContract.CallEntry._ID, AppelContract.CallEntry.COLUMN_DATETIME, AppelContract.CallEntry.COLUMN_CLASS_ID, AppelContract
                        .CallEntry
                        .COLUMN_LEAVING_TIME_OPTION},
                AppelContract.CallEntry.TABLE_NAME + "." + AppelContract.CallEntry._ID + " = " + callId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateCall.  Error validating call entry update.", cursor, updatedValues);

        cursor.close();
    }

    @Test
    public void testUpdateClassStudent() {
        long classId = insertClass(23);
        ContentValues values = TestUtilities.createClassStudentValues(insertStudent(5), classId);

        Uri classStudentUri = mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, values);
        long classStudentId = ContentUris.parseId(classStudentUri);

        assertTrue(classStudentId != -1);
        Log.d(LOG_TAG, "New class_student row id: " + classStudentId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(AppelContract.ClassStudentLinkEntry._ID, classStudentId);
        updatedValues.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "Not the same grade");

        Cursor classStudentCursor = mContext.getContentResolver().query(AppelContract.ClassStudentLinkEntry.buildClassStudentLinkFromClassUri(classId), null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        classStudentCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(AppelContract.ClassStudentLinkEntry.CONTENT_URI, updatedValues, AppelContract.ClassStudentLinkEntry._ID + "= ?", new String[] { Long.toString(classStudentId)});
        assertEquals(count, 1);

        tco.waitForNotificationOrFail();

        classStudentCursor.unregisterContentObserver(tco);
        classStudentCursor.close();

        Cursor cursor = mContext.getContentResolver().query(
                AppelContract.ClassStudentLinkEntry.buildClassStudentLinkFromClassUri(classId),
                null,   // projection
                null,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateClassStudent.  Error validating class_student entry update.", cursor, updatedValues);

        cursor.close();
    }

    @Test
    public void testUpdateCallStudent() {
        // Create a new map of values, where column names are the keys
        long classId = insertClass(24);
        long callId = insertCall(classId);
        long studentId = insertStudent(6);
        insertClassStudent(classId, studentId);
        ContentValues values = TestUtilities.createCallStudentValues(callId, studentId);

        Uri callStudentUri = mContext.getContentResolver().insert(AppelContract.CallStudentLinkEntry.CONTENT_URI, values);
        long callStudentId = ContentUris.parseId(callStudentUri);

        assertTrue(callStudentId != -1);
        Log.d(LOG_TAG, "New call_student row id: " + callStudentId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(AppelContract.CallStudentLinkEntry._ID, callStudentId);
        updatedValues.put(AppelContract.CallStudentLinkEntry.COLUMN_IS_PRESENT, 0);

        Cursor callStudentCursor = mContext.getContentResolver().query(AppelContract.CallStudentLinkEntry.buildCallStudentLinkUriWithCallAndClass(classId, callId), null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        callStudentCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(AppelContract.CallStudentLinkEntry.CONTENT_URI, updatedValues, AppelContract.CallStudentLinkEntry._ID + "= ?", new String[] { Long.toString(callStudentId)});
        assertEquals(count, 1);

        tco.waitForNotificationOrFail();

        callStudentCursor.unregisterContentObserver(tco);
        callStudentCursor.close();

        Cursor cursor = mContext.getContentResolver().query(
                AppelContract.CallStudentLinkEntry.buildCallStudentLinkUriWithCallAndClass(classId, callId),
                null,   // projection
                null,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateClass.  Error validating call_student entry update.", cursor, updatedValues);

        cursor.close();
    }

    @Test
    public void testDeleteRecords() {
        long classId = insertClass(25);
        long studentId = insertStudent(7);
        long classStudentId = insertClassStudent(classId, studentId);
        long callId = insertCall(classId);
        insertCallStudent(callId, studentId, classId);

        TestUtilities.TestContentObserver classObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(AppelContract.ClassEntry.CONTENT_URI, true, classObserver);

        TestUtilities.TestContentObserver studentObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(AppelContract.StudentEntry.CONTENT_URI, true, studentObserver);

        TestUtilities.TestContentObserver callObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(AppelContract.CallEntry.CONTENT_URI, true, callObserver);

        TestUtilities.TestContentObserver classStudentObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(AppelContract.ClassStudentLinkEntry.buildClassStudentLinkFromClassUri(classId), true, classStudentObserver);

        TestUtilities.TestContentObserver callStudentObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(AppelContract.CallStudentLinkEntry.buildCallStudentLinkUriWithCallAndClass(classId, callId), true, callStudentObserver);

        deleteAllRecordsFromProvider();

        classObserver.waitForNotificationOrFail();
        studentObserver.waitForNotificationOrFail();
        callObserver.waitForNotificationOrFail();
        classStudentObserver.waitForNotificationOrFail();
        callStudentObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(classObserver);
        mContext.getContentResolver().unregisterContentObserver(studentObserver);
        mContext.getContentResolver().unregisterContentObserver(callObserver);
        mContext.getContentResolver().unregisterContentObserver(classStudentObserver);
        mContext.getContentResolver().unregisterContentObserver(callStudentObserver);
    }

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                AppelContract.StudentEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                AppelContract.ClassEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                AppelContract.CallEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                AppelContract.ClassStudentLinkEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                AppelContract.CallStudentLinkEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                AppelContract.StudentEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from " +  AppelContract.StudentEntry.TABLE_NAME + " table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                AppelContract.ClassEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from " +  AppelContract.ClassEntry.TABLE_NAME + " table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                AppelContract.CallEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from " +  AppelContract.CallEntry.TABLE_NAME + " table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                AppelContract.ClassStudentLinkEntry.buildClassStudentLinkFromClassUri(10),
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from " +  AppelContract.ClassStudentLinkEntry.TABLE_NAME + " table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                AppelContract.CallStudentLinkEntry.buildCallStudentLinkUriWithCallAndClass(10, 20),
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from " +  AppelContract.CallStudentLinkEntry.TABLE_NAME + " table during delete", 0, cursor.getCount());
        cursor.close();
    }

    private long insertClass(int x) {
        ContentValues cv = TestUtilities.createClassValues(x);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(AppelContract.ClassEntry.CONTENT_URI, true, tco);
        Uri classUri = mContext.getContentResolver().insert(AppelContract.ClassEntry.CONTENT_URI, cv);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long classId = ContentUris.parseId(classUri);

        assertTrue(classId != -1);

        Log.d(LOG_TAG, "New class row id: " + classId);

        Cursor cursor = mContext.getContentResolver().query(
                AppelContract.ClassEntry.buildClassUri(classId),
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("insertClass. Error validating ClassEntry.", cursor, cv);

        return classId;
    }

    private long insertStudent(int x) {
        ContentValues cv = TestUtilities.createStudentValues(x);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(AppelContract.StudentEntry.CONTENT_URI, true, tco);
        Uri studentUri = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long studentId = ContentUris.parseId(studentUri);

        assertTrue(studentId != -1);

        Log.d(LOG_TAG, "New student row id: " + studentId);

        Cursor cursor = mContext.getContentResolver().query(
                AppelContract.StudentEntry.buildStudentUri(studentId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("insertStudent. Error validating StudentEntry.", cursor, cv);

        return studentId;
    }

    private long insertCall(long classId){
        ContentValues cv = TestUtilities.createCallValues(classId);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(AppelContract.CallEntry.CONTENT_URI, true, tco);
        Uri callUri = mContext.getContentResolver().insert(AppelContract.CallEntry.CONTENT_URI, cv);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long callId = ContentUris.parseId(callUri);

        assertTrue(callId != -1);

        Log.d(LOG_TAG, "New call row id: " + callId);

        Cursor cursor = mContext.getContentResolver().query(
                AppelContract.CallEntry.buildCallUri(callId),
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("insertCall. Error validating CallEntry.", cursor, cv);

        return callId;
    }

    private long insertClassStudent(long classId, long studentId){
        ContentValues cv = TestUtilities.createClassStudentValues(studentId, classId);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(AppelContract.ClassStudentLinkEntry.buildClassStudentLinkFromClassUri(classId), true, tco);
        Uri classStudentUri = mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long classStudentId = ContentUris.parseId(classStudentUri);

        assertTrue(classStudentId != -1);

        Log.d(LOG_TAG, "New class_student row id: " + classStudentId);

        Cursor cursor = mContext.getContentResolver().query(
                AppelContract.ClassStudentLinkEntry.buildClassStudentLinkFromClassUri(classId),
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("insertClassStudent. Error validating ClassStudentLinkEntry.", cursor, cv);

        return classStudentId;
    }

    private long insertCallStudent(long callId, long studentId, long classId){
        ContentValues cv = TestUtilities.createCallStudentValues(callId, studentId);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(AppelContract.CallStudentLinkEntry.buildCallStudentLinkUriWithCallAndClass(classId, callId), true, tco);
        Uri callStudentUri = mContext.getContentResolver().insert(AppelContract.CallStudentLinkEntry.CONTENT_URI, cv);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long callStudentId = ContentUris.parseId(callStudentUri);

        assertTrue(callStudentId != -1);

        Log.d(LOG_TAG, "New call_student row id: " + callStudentId);

        Cursor cursor = mContext.getContentResolver().query(
                AppelContract.CallStudentLinkEntry.buildCallStudentLinkUriWithCallAndClass(classId, callId),
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("insertCallStudent. Error validating CallStudentLinkEntry.", cursor, cv);

        return callStudentId;
    }
}