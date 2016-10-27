package com.sylvainautran.nanodegree.capstoneproject;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;
import com.sylvainautran.nanodegree.capstoneproject.dialogs.ClassStudentsNewDialog;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClassStudentsListActivity extends AppCompatActivity {
    private final String LOG_TAG = this.getClass().getSimpleName();
    public static final String CLASS_NAME = "class_name";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private long classId;
    private String className;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_students_list);
        classId = 0;
        className = getString(R.string.unknown_class);
        if(getIntent() != null){
            classId = Long.parseLong(getIntent().getData().getLastPathSegment());
            className = getIntent().getStringExtra(CLASS_NAME);
        }
        ButterKnife.bind(this);

        if(savedInstanceState == null) {
            StudentsListFragment fragment = StudentsListFragment.newInstance(classId);
            getFragmentManager().beginTransaction()
                    .add(R.id.list_container, fragment, "students_list")
                    .commit();
        }

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(className);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.students_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.add_student:
                if(getResources().getBoolean(R.bool.tablet)){
                    FragmentManager fragmentManager = getFragmentManager();
                    ClassStudentsNewDialog classStudentsNewDialog = ClassStudentsNewDialog.newInstance(R.string.add_student_to_class, classId);
                    classStudentsNewDialog.show(fragmentManager, "dialog");
                }else {
                    FragmentManager fragmentManager = getFragmentManager();
                    ClassStudentsNewDialog classStudentsNewDialog = ClassStudentsNewDialog.newInstance(R.string.add_student_to_class, classId);

                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.add(R.id.container, classStudentsNewDialog, "dialog").addToBackStack(null).commit();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    public void fabOnClick(){
        startCall(AppelContract.CallEntry.RECORD_LEAVING_TIME);
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme_AlertDialog);
        builder.setTitle(R.string.start_call)
                .setItems(R.array.call_select_option, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startCall(which);
                    }
                });
        builder.create().show();*/
    }

    private void startCall(int option){
        Calendar cal = Calendar.getInstance();
        ContentValues cv = new ContentValues();
        cv.put(AppelContract.CallEntry.COLUMN_CLASS_ID, classId);
        cv.put(AppelContract.CallEntry.COLUMN_DATETIME, cal.getTimeInMillis());
        cv.put(AppelContract.CallEntry.COLUMN_LEAVING_TIME_OPTION, option);
        long callId = Long.parseLong(getContentResolver().insert(AppelContract.CallEntry.CONTENT_URI, cv).getLastPathSegment());
        Intent intent = new Intent(Intent.ACTION_VIEW, AppelContract.CallStudentLinkEntry.buildCallStudentLinkUriWithCallAndClass(classId, callId));
        intent.putExtra(getString(R.string.intent_extra_class_name), className);
        intent.putExtra(getString(R.string.intent_extra_call_date), cal.getTimeInMillis());
        intent.putExtra(getString(R.string.intent_extra_call_id), callId);
        intent.putExtra(getString(R.string.intent_extra_class_id), classId);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
