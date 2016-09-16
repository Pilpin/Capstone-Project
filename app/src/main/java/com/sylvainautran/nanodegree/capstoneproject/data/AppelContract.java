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
    public static final String PATH_CALL = "call";
    public static final String PATH_STUDENTS_CALLS = "students_calls";
    public static final String PATH_STUDENTS_CLASSES = "students_classes";


    public static final class StudentEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_STUDENT).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STUDENT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STUDENT;

        public static final String TABLE_NAME = "student";

        public static final String COLUMN_FIRSTNAME = "first_name";
        public static final String COLUMN_LASTNAME = "last_name";
        public static final String COLUMN_BIRTHDATE = "birth_date";
        public static final String COLUMN_GRADE = "grade";

        public static Uri buildStudentUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ClassEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLASS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLASS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLASS;

        public static final String TABLE_NAME = "class";

        public static final String COLUMN_NAME = "classname";

        public static Uri buildClassUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class CallEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CALL).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CALL;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CALL;

        public static final String TABLE_NAME = "call";

        public static final String COLUMN_CLASS_ID = "class_id";
        public static final String COLUMN_DATETIME = "date_time";
        public static final String COLUMN_LEAVING_TIME_OPTION = "leaving_time_option";

        public static Uri buildCallUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ClassStudentLinkEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_STUDENTS_CLASSES).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STUDENTS_CLASSES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STUDENTS_CLASSES;

        public static final String TABLE_NAME = "class_student";

        public static final String COLUMN_CLASS_ID = "class_id";
        public static final String COLUMN_STUDENT_ID = "student_id";

        public static Uri buildClassStudentLinkUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class CallStudentLinkEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_STUDENTS_CALLS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STUDENTS_CALLS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STUDENTS_CALLS;

        public static final String TABLE_NAME = "call_student";

        public static final String COLUMN_CALL_ID = "call_id";
        public static final String COLUMN_CLASS_STUDENT_LINK_ID = "class_student_link_id";
        public static final String COLUMN_IS_PRESENT = "is_present";
        public static final String COLUMN_LEAVING_TIME = "leaving_time";

        public static Uri buildCallStudentLinkUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
