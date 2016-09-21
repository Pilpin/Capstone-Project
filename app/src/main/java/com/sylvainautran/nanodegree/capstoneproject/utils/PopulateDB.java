package com.sylvainautran.nanodegree.capstoneproject.utils;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;

import java.util.Calendar;

public class PopulateDB extends AsyncTask<Void, Void, Void> {
    Context mContext;

    public PopulateDB(Context context){
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        ContentValues cv = new ContentValues();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        String studentId;

        cv.put(AppelContract.ClassEntry.COLUMN_NAME, "Garderie 2016-17");
        String classId = mContext.getContentResolver().insert(AppelContract.ClassEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();

        cal.set(2008,3,14);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "ADJED");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Enzo");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CE2");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2011,4,11);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "ALBRETCH");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Mathéis");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "GS");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2011,2,22);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "ANDRE");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Lohan");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "GS");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2011,6,15);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "BASANO");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Clara");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "GS");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2011,3,16);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "BERTHET");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Cléa");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CP");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2012,7,29);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "CABRAL");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Ethan");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "MS");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2011,0,22);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "CARATTI");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "louise");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "GS");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();


        cal.set(2008,6,15);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "CENCIAI");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Manuella");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CE2");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2011,6,7);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "CENCIAI");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Maëva");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "GS");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2007,7,30);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "COUGNARD");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Joris");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CM1");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2007,6,21);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "COUVREUR");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Ben");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CM1");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2009,11,2);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "COUVREUR");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Léo");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CE1");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2008,7,22);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "DOTOU");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Elisa");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CE2");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2012,7,11);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "DUPRE");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Maxime");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "MS");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2009,10,11);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "FABRE");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Maxence");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CE1");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2008,3,15);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "FRANKIAS");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Tom");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CE2");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2010,8,23);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "GLACET");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Ewann");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CP");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2012,6,6);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "GLACET");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Loucas");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "MS");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2010,3,20);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "HUBERT");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Alexandre");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CP");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2011,9,10);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "HUBERT");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Maxime");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "GS");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2006,0,18);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "JACQUIN");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Evan");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CM2");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2009,3,3);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "JACQUIN");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Fidji");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CE1");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2012,10,27);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "JACQUIN");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Timéo");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "MS");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2012,2,3);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "L'HUILLIER");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Lorenzo");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "MS");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2009,1,19);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "LOMBARD");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Milan");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CE1");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2011,11,15);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "LOMBARD");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Anouk");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "GS");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2009,0,21);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "MANGANO");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Ethan");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CE1");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2006,5,18);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "MANGANO");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Zoé");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CE2");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2010,9,28);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "MILLO");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Lilian");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CP");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2008,11,16);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "MONVOISIN");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "LiLy-Rose");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CE2");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2007,1,26);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "MONVOISIN");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Louis");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CM1");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2006,7,17);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "MOUZET");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Nolan");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CM1");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2010,8,9);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "OLLAGNIER");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Manon");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CP");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2011,3,2);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "OLLIVIER");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Louane");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "GS");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2009,10,29);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "PARISI");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Nathan");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CE1");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2011,2,30);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "PASTOR");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Anjali");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "GS");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2009,2,4);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "PASTOR");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Noé");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CE2");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2009,6,15);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "PESCI");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Lucas");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CE1");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2009,6,15);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "PESCI");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Alysson");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CE1");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2012,4,10);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "PINDELAUMENIE");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "raphaël");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "MS");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2013,6,29);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "PY");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Loukas");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "PS");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2012,4,31);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "PY");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Giulian");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "MS");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2012,3,17);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "RICHER");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Eliott");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "MS");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2011,5,21);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "RIZZO");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Mathis");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "GS");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2009,1,9);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "ROPARS");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Luisa");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CE1");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2007,7,18);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "ROPARS");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Esteban");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CM1");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2006,1,19);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "ROVERE");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Luca");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CM2");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2007,6,24);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "RUSSO");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Hugo");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CM1");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2013,10,16);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "SCHEMITT");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Maël");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "PS");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2007,6,31);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "VADEZ");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Vincent");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CM1");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2008,10,29);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "VADEZ");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Baptiste");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CE2");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2008,0,24);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "XICLUNA");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Thimothée");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CE2");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        cv.clear();

        cal.set(2009,11,10);
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "XICLUNA");
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Bastien");
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = mContext.getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv.clear();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CE1");
        mContext.getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);
        return null;
    }
}
