package com.sylvainautran.nanodegree.capstoneproject;

import android.content.ContentValues;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallsListActivity extends AppCompatActivity {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private final String CALLS_LIST = "calls_list";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_list);
        ButterKnife.bind(this);

        getContentResolver().delete(AppelContract.ClassEntry.CONTENT_URI, null, null);
        getContentResolver().delete(AppelContract.CallEntry.CONTENT_URI, null, null);
        getContentResolver().delete(AppelContract.StudentEntry.CONTENT_URI, null, null);
        ContentValues cv = new ContentValues();
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Sylvain");
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "Autran");
        Calendar cal = Calendar.getInstance();
        cal.set(1987, 11, 9 , 0, 0, 0);
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        String studentId = getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv = new ContentValues();
        cv.put(AppelContract.ClassEntry.COLUMN_NAME, "Garderie 2016-17");
        String classId = getContentResolver().insert(AppelContract.ClassEntry.CONTENT_URI, cv).getLastPathSegment();
        cv = new ContentValues();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "CM2");
        String classStudentId = getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv).getLastPathSegment();
        cv = new ContentValues();
        cv.put(AppelContract.CallEntry.COLUMN_DATETIME, Calendar.getInstance().getTimeInMillis());
        cv.put(AppelContract.CallEntry.COLUMN_LEAVING_TIME_OPTION, 1);
        cv.put(AppelContract.CallEntry.COLUMN_CLASS_ID, classId);
        String callId = getContentResolver().insert(AppelContract.CallEntry.CONTENT_URI, cv).getLastPathSegment();
        cv = new ContentValues();
        cv.put(AppelContract.CallStudentLinkEntry.COLUMN_CALL_ID, callId);
        cv.put(AppelContract.CallStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        String callStudentId = getContentResolver().insert(AppelContract.CallStudentLinkEntry.CONTENT_URI, cv).getLastPathSegment();
        Log.e(LOG_TAG, "Student ID : " + studentId + ", Class Id : " + classId + ", ClassStudent Id : " + classStudentId + ", Call Id : " + callId + ", CallStudent Id : " + callStudentId);

        cv = new ContentValues();
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Lee");
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "Autran");
        cal = Calendar.getInstance();
        cal.set(2013, 2, 25 , 0, 0, 0);
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        studentId = getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv = new ContentValues();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "MS");
        classStudentId = getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv).getLastPathSegment();
        Log.e(LOG_TAG, "Student ID : " + studentId + ", Class Id : " + classId + ", ClassStudent Id : " + classStudentId + ", Call Id : " + callId + ", CallStudent Id : " + callStudentId);

        if(savedInstanceState == null) {
            CallsListFragment fragment = CallsListFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment, CALLS_LIST)
                    .commit();
        }

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.calls);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer);
        drawerLayout.addDrawerListener(mDrawerToggle);
        navigationView.setNavigationItemSelectedListener(new DrawerNavigationItemListener(this));
        navigationView.setCheckedItem(R.id.navigation_calls);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
