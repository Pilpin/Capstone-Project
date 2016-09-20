package com.sylvainautran.nanodegree.capstoneproject;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClassStudentsListActivity extends AppCompatActivity {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private static final String STUDENTS_LIST = "students_list";
    public static final String CLASS_NAME = "class_name";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    ActionBarDrawerToggle mDrawerToggle;

    private long classId;
    private String className;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_students_list);
        classId = 0;
        className = "Unknown Class";
        if(getIntent() != null){
            classId = Long.parseLong(getIntent().getData().getLastPathSegment());
            className = getIntent().getStringExtra(CLASS_NAME);
        }
        ButterKnife.bind(this);

        getContentResolver().delete(AppelContract.StudentEntry.CONTENT_URI, null, null);
        getContentResolver().delete(AppelContract.ClassStudentLinkEntry.CONTENT_URI, null, null);
        ContentValues cv = new ContentValues();
        cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, "Sylvain");
        cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, "Autran");
        Calendar cal = Calendar.getInstance();
        cal.set(1987, 11, 9 , 0, 0, 0);
        cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());
        String studentId = getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv).getLastPathSegment();
        cv = new ContentValues();
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, studentId);
        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, "MS");
        getContentResolver().insert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv);

        if(savedInstanceState == null) {
            StudentsListFragment fragment = StudentsListFragment.newInstance(classId);
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment, STUDENTS_LIST)
                    .commit();
        }

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(className);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer);
        drawerLayout.addDrawerListener(mDrawerToggle);
        navigationView.setNavigationItemSelectedListener(new DrawerNavigationItemListener(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.students_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.add_student){
            Toast.makeText(this, getString(R.string.add_student), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
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

    @OnClick(R.id.fab)
    public void fabOnClick(){
        Log.d(LOG_TAG, "startCall");

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme_AlertDialog);
        builder.setTitle(R.string.start_call)
                .setItems(R.array.call_select_option, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startCall(which);

                    }
                });
        builder.create().show();
    }

    private void startCall(int option){
        Calendar cal = Calendar.getInstance();
        ContentValues cv = new ContentValues();
        cv.put(AppelContract.CallEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.CallEntry.COLUMN_DATETIME, cal.getTimeInMillis());
        cv.put(AppelContract.CallEntry.COLUMN_LEAVING_TIME_OPTION, option);
        long callId = Long.parseLong(getContentResolver().insert(AppelContract.CallEntry.CONTENT_URI, cv).getLastPathSegment());
        Intent intent = new Intent(Intent.ACTION_VIEW, AppelContract.CallStudentLinkEntry.buildCallStudentLinkUriWithCallAndClass(classId, callId));
        intent.putExtra(CallsDetailsActivity.CLASS_NAME, className);
        intent.putExtra(CallsDetailsActivity.CALL_DATE, cal.getTimeInMillis());
        startActivity(intent);
    }
}
