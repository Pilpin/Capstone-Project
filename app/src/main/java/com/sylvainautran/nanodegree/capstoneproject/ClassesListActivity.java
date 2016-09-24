package com.sylvainautran.nanodegree.capstoneproject;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.sylvainautran.nanodegree.capstoneproject.dialogs.ClassNewDialog;
import com.sylvainautran.nanodegree.capstoneproject.utils.PopulateDB;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClassesListActivity extends AppCompatActivity {
    private final String CLASSES_LIST = "classes_list";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view) NavigationView navigationView;
    ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_list);
        ButterKnife.bind(this);

        if(savedInstanceState == null) {
            ClassesListFragment fragment = ClassesListFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment, CLASSES_LIST)
                    .commit();
        }

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.classes);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer);
        drawerLayout.addDrawerListener(mDrawerToggle);
        navigationView.setNavigationItemSelectedListener(new DrawerNavigationItemListener(this));
        navigationView.setCheckedItem(R.id.navigation_classes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.classes_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.add_class){
            FragmentManager fm = getSupportFragmentManager();
            ClassNewDialog frag = new ClassNewDialog();
            frag.show(fm, "dialog");
        }else if(item.getItemId() == R.id.add_dummy_values){
            PopulateDB pop = new PopulateDB(this);
            pop.execute();
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
