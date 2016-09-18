package com.sylvainautran.nanodegree.capstoneproject.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AppelDbHelperTest {

    @Before
    public void setUp() throws Exception {
        InstrumentationRegistry.getTargetContext().deleteDatabase(AppelDbHelper.DATABASE_NAME);
    }

    @Test
    public void testOnCreate() throws Exception {
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(AppelContract.StudentEntry.TABLE_NAME);
        tableNameHashSet.add(AppelContract.ClassEntry.TABLE_NAME);
        tableNameHashSet.add(AppelContract.CallEntry.TABLE_NAME);
        tableNameHashSet.add(AppelContract.ClassStudentLinkEntry.TABLE_NAME);
        tableNameHashSet.add(AppelContract.CallStudentLinkEntry.TABLE_NAME);

        SQLiteDatabase db = new AppelDbHelper(InstrumentationRegistry.getTargetContext()).getWritableDatabase();
        db.setForeignKeyConstraintsEnabled(true);
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: The database has not been created correctly", c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );
        assertTrue("Error: Your database was created without all the tables", tableNameHashSet.isEmpty());

        final HashSet<String> columnHashSet = new HashSet<String>();

        c = db.rawQuery("PRAGMA table_info(" + AppelContract.StudentEntry.TABLE_NAME + ")", null);
        assertTrue("Error: We were unable to query the database for " + AppelContract.StudentEntry.TABLE_NAME + " table information.", c.moveToFirst());

        columnHashSet.add(AppelContract.StudentEntry._ID);
        columnHashSet.add(AppelContract.StudentEntry.COLUMN_LASTNAME);
        columnHashSet.add(AppelContract.StudentEntry.COLUMN_FIRSTNAME);
        columnHashSet.add(AppelContract.StudentEntry.COLUMN_BIRTHDATE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            columnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required student entry columns", columnHashSet.isEmpty());


        c = db.rawQuery("PRAGMA table_info(" + AppelContract.ClassEntry.TABLE_NAME + ")", null);
        assertTrue("Error: We were unable to query the database for " + AppelContract.ClassEntry.TABLE_NAME + " table information.", c.moveToFirst());

        columnHashSet.clear();
        columnHashSet.add(AppelContract.ClassEntry._ID);
        columnHashSet.add(AppelContract.ClassEntry.COLUMN_NAME);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            columnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required class entry columns", columnHashSet.isEmpty());


        c = db.rawQuery("PRAGMA table_info(" + AppelContract.CallEntry.TABLE_NAME + ")", null);
        assertTrue("Error: We were unable to query the database for " + AppelContract.CallEntry.TABLE_NAME + " table information.", c.moveToFirst());

        columnHashSet.clear();
        columnHashSet.add(AppelContract.CallEntry._ID);
        columnHashSet.add(AppelContract.CallEntry.COLUMN_CLASS_ID);
        columnHashSet.add(AppelContract.CallEntry.COLUMN_DATETIME);
        columnHashSet.add(AppelContract.CallEntry.COLUMN_LEAVING_TIME_OPTION);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            columnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required call entry columns", columnHashSet.isEmpty());


        c = db.rawQuery("PRAGMA table_info(" + AppelContract.ClassStudentLinkEntry.TABLE_NAME + ")", null);
        assertTrue("Error: We were unable to query the database for " + AppelContract.ClassStudentLinkEntry.TABLE_NAME + " table information.", c.moveToFirst());

        columnHashSet.clear();
        columnHashSet.add(AppelContract.ClassStudentLinkEntry._ID);
        columnHashSet.add(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID);
        columnHashSet.add(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID);
        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            columnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required student_class entry columns", columnHashSet.isEmpty());


        c = db.rawQuery("PRAGMA table_info(" + AppelContract.CallStudentLinkEntry.TABLE_NAME + ")", null);
        assertTrue("Error: We were unable to query the database for " + AppelContract.CallStudentLinkEntry.TABLE_NAME + " table information.", c.moveToFirst());

        columnHashSet.clear();
        columnHashSet.add(AppelContract.CallStudentLinkEntry._ID);
        columnHashSet.add(AppelContract.CallStudentLinkEntry.COLUMN_CALL_ID);
        columnHashSet.add(AppelContract.CallStudentLinkEntry.COLUMN_STUDENT_ID);
        columnHashSet.add(AppelContract.CallStudentLinkEntry.COLUMN_IS_PRESENT);
        columnHashSet.add(AppelContract.CallStudentLinkEntry.COLUMN_LEAVING_TIME);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            columnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required student_call entry columns", columnHashSet.isEmpty());
        db.close();

    }

    @Test
    public void testStudentTable() {
        insertStudent();
    }

    @Test
    public void testClassTable() {
        insertClass();
    }

    @Test
    public void testCallTable() {
        insertCall(insertClass());
    }

    @Test
    public void testClassStudentTable() {
        long classId = insertClass();
        long studentIt = insertStudent();
        insertClassStudent(classId, studentIt);
    }

    @Test
    public void testCallStudentTable() {
        long studentIt = insertStudent();
        long callId = insertCall(insertClass());
        insertCallStudent(callId, studentIt);
    }

    private long insertClass(){
        AppelDbHelper dbHelper = new AppelDbHelper(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.setForeignKeyConstraintsEnabled(true);

        ContentValues testValues = new ContentValues();
        testValues.put(AppelContract.ClassEntry.COLUMN_NAME, "6b");

        long id = db.insert(AppelContract.ClassEntry.TABLE_NAME, null, testValues);

        assertTrue(id != -1);

        Cursor cursor = db.query(AppelContract.ClassEntry.TABLE_NAME, null, null, null, null, null, null);

        assertTrue( "Error: No Records returned from " + AppelContract.ClassEntry.TABLE_NAME + " query", cursor.moveToFirst() );
        String error = "Error: " + AppelContract.ClassEntry.TABLE_NAME + " query validation failed";
        Set<Map.Entry<String, Object>> valueSet = testValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = cursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() + "' did not match the expected value '" + expectedValue + "'. " + error, expectedValue, cursor.getString(idx));
        }

        assertFalse( "Error: More than one record returned from " + AppelContract.ClassEntry.TABLE_NAME + " query", cursor.moveToNext() );

        cursor.close();
        db.close();

        return id;
    }

    private long insertStudent(){
        AppelDbHelper dbHelper = new AppelDbHelper(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.setForeignKeyConstraintsEnabled(true);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2013);
        cal.set(Calendar.MONTH, 2);
        cal.set(Calendar.DATE, 25);

        ContentValues testValues = new ContentValues();
        testValues.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Lee");
        testValues.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "Autran");
        testValues.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());

        long id = db.insert(AppelContract.StudentEntry.TABLE_NAME, null, testValues);

        assertTrue(id != -1);

        Cursor cursor = db.query(AppelContract.StudentEntry.TABLE_NAME, null, null, null, null, null, null);

        assertTrue( "Error: No Records returned from " + AppelContract.StudentEntry.TABLE_NAME + " query", cursor.moveToFirst() );
        String error = "Error: " + AppelContract.StudentEntry.TABLE_NAME + " query validation failed";
        Set<Map.Entry<String, Object>> valueSet = testValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = cursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() + "' did not match the expected value '" + expectedValue + "'. " + error, expectedValue, cursor.getString(idx));
        }

        assertFalse( "Error: More than one record returned from " + AppelContract.StudentEntry.TABLE_NAME + " query", cursor.moveToNext() );

        cursor.close();
        db.close();

        return id;
    }

    private long insertCall(long classId){
        AppelDbHelper dbHelper = new AppelDbHelper(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.setForeignKeyConstraintsEnabled(true);

        Calendar cal = Calendar.getInstance();

        ContentValues testValues = new ContentValues();
        testValues.put(AppelContract.CallEntry.COLUMN_CLASS_ID, classId);
        testValues.put(AppelContract.CallEntry.COLUMN_DATETIME, cal.getTimeInMillis());
        testValues.put(AppelContract.CallEntry.COLUMN_LEAVING_TIME_OPTION, 0);

        long id =  db.insert(AppelContract.CallEntry.TABLE_NAME, null, testValues);

        assertTrue(id != -1);

        Cursor cursor = db.query(AppelContract.CallEntry.TABLE_NAME, null, null, null, null, null, null);

        assertTrue( "Error: No Records returned from " + AppelContract.CallEntry.TABLE_NAME + " query", cursor.moveToFirst() );
        String error = "Error: " + AppelContract.CallEntry.TABLE_NAME + " query validation failed";
        Set<Map.Entry<String, Object>> valueSet = testValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = cursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() + "' did not match the expected value '" + expectedValue + "'. " + error, expectedValue, cursor.getString(idx));
        }

        assertFalse( "Error: More than one record returned from " + AppelContract.CallEntry.TABLE_NAME + " query", cursor.moveToNext() );

        cursor.close();
        db.close();

        return id;
    }

    private long insertClassStudent(long classId, long studentId){
        AppelDbHelper dbHelper = new AppelDbHelper(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.setForeignKeyConstraintsEnabled(true);

        ContentValues testValues = new ContentValues();
        testValues.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        testValues.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);

        long id =  db.insert(AppelContract.ClassStudentLinkEntry.TABLE_NAME, null, testValues);

        assertTrue(id != -1);

        Cursor cursor = db.query(AppelContract.ClassStudentLinkEntry.TABLE_NAME, null, null, null, null, null, null);

        assertTrue( "Error: No Records returned from " + AppelContract.ClassStudentLinkEntry.TABLE_NAME + " query", cursor.moveToFirst() );
        String error = "Error: " + AppelContract.ClassStudentLinkEntry.TABLE_NAME + " query validation failed";
        Set<Map.Entry<String, Object>> valueSet = testValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = cursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() + "' did not match the expected value '" + expectedValue + "'. " + error, expectedValue, cursor.getString(idx));
        }

        assertFalse( "Error: More than one record returned from " + AppelContract.ClassStudentLinkEntry.TABLE_NAME + " query", cursor.moveToNext() );

        cursor.close();
        db.close();

        return id;
    }

    private long insertCallStudent(long callId, long studentId){
        AppelDbHelper dbHelper = new AppelDbHelper(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.setForeignKeyConstraintsEnabled(true);

        ContentValues testValues = new ContentValues();
        testValues.put(AppelContract.CallStudentLinkEntry.COLUMN_CALL_ID, callId);
        testValues.put(AppelContract.CallStudentLinkEntry.COLUMN_STUDENT_ID, studentId);

        long id =  db.insert(AppelContract.CallStudentLinkEntry.TABLE_NAME, null, testValues);

        assertTrue(id != -1);

        Cursor cursor = db.query(AppelContract.CallStudentLinkEntry.TABLE_NAME, null, null, null, null, null, null);

        assertTrue( "Error: No Records returned from " + AppelContract.CallStudentLinkEntry.TABLE_NAME + " query", cursor.moveToFirst() );
        String error = "Error: " + AppelContract.CallStudentLinkEntry.TABLE_NAME + " query validation failed";
        Set<Map.Entry<String, Object>> valueSet = testValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = cursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() + "' did not match the expected value '" + expectedValue + "'. " + error, expectedValue, cursor.getString(idx));
        }

        assertFalse( "Error: More than one record returned from " + AppelContract.CallStudentLinkEntry.TABLE_NAME + " query", cursor.moveToNext() );

        cursor.close();
        db.close();

        return id;
    }
}