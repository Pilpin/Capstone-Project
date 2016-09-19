package com.sylvainautran.nanodegree.capstoneproject;

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

import java.text.DateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallsDetailsActivity extends AppCompatActivity {
    private final String LOG_CAT = this.getClass().getSimpleName();
    private static final String CALL_DETAILS = "call_details";
    public static final String CLASS_NAME = "class_name";
    public static final String CALL_DATE = "call_date";

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
        setContentView(R.layout.activity_calls_details);

        String className = "Unknown Class";
        long callDate = 0;
        if(getIntent() != null){
            className = getIntent().getStringExtra(CLASS_NAME);
            callDate = getIntent().getLongExtra(CALL_DATE, 0);

            long callId = AppelContract.CallStudentLinkEntry.getCallId(getIntent().getData());
            long classId = AppelContract.CallStudentLinkEntry.getClassId(getIntent().getData());

            if(savedInstanceState == null) {
                CallsDetailsFragment fragment = CallsDetailsFragment.newInstance(callId, classId);
                getFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, fragment, CALL_DETAILS)
                        .commit();
            }
        }

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(callDate);
            DateFormat df = DateFormat.getDateInstance();
            actionBar.setTitle(className + " - " + df.format(cal.getTime()));
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
