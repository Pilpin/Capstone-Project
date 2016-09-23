package com.sylvainautran.nanodegree.capstoneproject.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class AppelContract {

    public static final String CONTENT_AUTHORITY = "com.sylvainautran.nanodegree.capstoneproject";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_STUDENT = "student";
    public static final String PATH_CLASS = "class";
    public static final String PATH_NOT_CLASS = "not_class";
    public static final String PATH_CALL = "call";
    public static final String PATH_STUDENT_CALL = "student_call";
    public static final String PATH_STUDENT_CLASS = "student_class";
    public static final String PATH_STUDENT_CLASS_FROM_CLASS = PATH_STUDENT_CLASS + "/" + PATH_CLASS;
    public static final String PATH_STUDENT_CLASS_NOT_FROM_CLASS = PATH_STUDENT_CLASS + "/" + PATH_NOT_CLASS;

    public static final int DELETED = 1;
    public static final int PUBLIC = 0;


    public static final class StudentEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_STUDENT).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STUDENT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STUDENT;

        public static final String TABLE_NAME = "student";

        public static final String COLUMN_FIRSTNAME = "first_name";
        public static final String COLUMN_LASTNAME = "last_name";
        public static final String COLUMN_BIRTHDATE = "birth_date";
        public static final String COLUMN_DELETED = "student_deleted";

        public static final String DEFAULT_SORT = COLUMN_LASTNAME + " ASC, " + COLUMN_FIRSTNAME + " ASC";

        public static Uri buildStudentUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ClassEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLASS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLASS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLASS;

        public static final String TABLE_NAME = "class";

        public static final String COLUMN_NAME = "class_name";
        public static final String COLUMN_DELETED = "class_deleted";

        public static final String DEFAULT_SORT = COLUMN_NAME + " ASC";

        public static Uri buildClassUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class CallEntry implements BaseColumns {

        public static final int RECORD_LEAVING_TIME = 1;
        public static final int DO_NOT_RECORD_LEAVING_TIME = 0;

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CALL).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CALL;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CALL;

        public static final String TABLE_NAME = "call";

        public static final String COLUMN_CLASS_ID = "call_class_id";
        public static final String COLUMN_DATETIME = "date_time";
        public static final String COLUMN_LEAVING_TIME_OPTION = "leaving_time_option";
        public static final String COLUMN_DELETED = "call_deleted";

        public static final String DEFAULT_SORT = COLUMN_DATETIME + " DESC";
        public static final String MULTIPLE_SORT = COLUMN_DATETIME + " DESC, " + ClassEntry.COLUMN_NAME + " ASC";

        public static Uri buildCallUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ClassStudentLinkEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_STUDENT_CLASS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STUDENT_CLASS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STUDENT_CLASS;

        public static final String TABLE_NAME = "class_student";

        public static final String COLUMN_CLASS_ID = "class_student_class_id";
        public static final String COLUMN_STUDENT_ID = "class_student_student_id";
        public static final String COLUMN_GRADE = "grade";
        public static final String COLUMN_DELETED = "class_student_deleted";

        public static Uri buildClassStudentLinkFromUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildClassStudentLinkFromClassUri(long classId) {
            Uri.Builder builder = CONTENT_URI.buildUpon();
            builder.appendPath(PATH_CLASS);
            return ContentUris.withAppendedId(builder.build(), classId);
        }
        public static Uri buildClassStudentLinkFromStudentUri(long studentId) {
            Uri.Builder builder = CONTENT_URI.buildUpon();
            builder.appendPath(PATH_STUDENT);
            return ContentUris.withAppendedId(builder.build(), studentId);
        }
        public static Uri buildClassStudentLinkNotFromClassUri(long classId) {
            Uri.Builder builder = CONTENT_URI.buildUpon();
            builder.appendPath(PATH_NOT_CLASS);
            return ContentUris.withAppendedId(builder.build(), classId);
        }
    }

    public static final class CallStudentLinkEntry implements BaseColumns {

        public static final int ABSENT = 0;
        public static final int PRESENT = 1;

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_STUDENT_CALL).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STUDENT_CALL;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STUDENT_CALL;

        public static final String TABLE_NAME = "call_student";

        public static final String COLUMN_CALL_ID = "call_student_call_id";
        public static final String COLUMN_STUDENT_ID = "call_student_student_id";
        public static final String COLUMN_IS_PRESENT = "is_present";
        public static final String COLUMN_LEAVING_TIME = "leaving_time";

        public static Uri buildCallStudentLinkUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildCallStudentLinkUriWithCallAndClass(long classId, long callId) { return ContentUris.withAppendedId(ContentUris.withAppendedId(CONTENT_URI, classId), callId); }
        public static long getCallId(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(2));
        }
        public static long getClassId(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }
}
