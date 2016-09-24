package com.sylvainautran.nanodegree.capstoneproject;

import android.content.ContentValues;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;

import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;
import com.sylvainautran.nanodegree.capstoneproject.dialogs.StudentsNewDialog;

import java.sql.SQLException;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentsListActivity extends AppCompatActivity {
    private static final String STUDENTS_LIST = "students_list";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    private ActionBarDrawerToggle mDrawerToggle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_list);
        ButterKnife.bind(this);

        if(savedInstanceState == null) {
            StudentsListFragment studentsListFragment = StudentsListFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, studentsListFragment, STUDENTS_LIST)
                    .commit();
        }

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.students);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer);
        drawerLayout.addDrawerListener(mDrawerToggle);
        navigationView.setNavigationItemSelectedListener(new DrawerNavigationItemListener(this));
        navigationView.setCheckedItem(R.id.navigation_students);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.students_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.add_student){
            FragmentManager fragmentManager = getSupportFragmentManager();
            StudentsNewDialog newFragment = StudentsNewDialog.newInstance(R.string.add_student);

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.add(R.id.drawer_layout, newFragment, "dialog").addToBackStack(null).commit();
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


}
