package com.sylvainautran.nanodegree.capstoneproject.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.sylvainautran.nanodegree.capstoneproject.CallsListActivity;
import com.sylvainautran.nanodegree.capstoneproject.ClassesListActivity;
import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.StudentsListActivity;

public class DrawerNavigationItemListener implements NavigationView.OnNavigationItemSelectedListener {
    private AppCompatActivity mActivity;
    private DrawerLayout mDrawerLayout;

    public DrawerNavigationItemListener(AppCompatActivity activity, DrawerLayout drawerLayout){
        mActivity = activity;
        mDrawerLayout = drawerLayout;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch(item.getItemId()){
            case R.id.navigation_classes:
                mDrawerLayout.closeDrawers();
                intent = new Intent(mActivity, ClassesListActivity.class);
                mActivity.startActivity(intent, ActivityOptionsCompat.makeCustomAnimation(mActivity, android.R.anim.fade_in, android.R.anim.fade_out).toBundle());
                break;
            case R.id.navigation_students:
                mDrawerLayout.closeDrawers();
                intent = new Intent(mActivity, StudentsListActivity.class);
                mActivity.startActivity(intent, ActivityOptionsCompat.makeCustomAnimation(mActivity, android.R.anim.fade_in, android.R.anim.fade_out).toBundle());
                break;
            case R.id.navigation_calls:
                mDrawerLayout.closeDrawers();
                intent = new Intent(mActivity, CallsListActivity.class);
                mActivity.startActivity(intent, ActivityOptionsCompat.makeCustomAnimation(mActivity, android.R.anim.fade_in, android.R.anim.fade_out).toBundle());
                break;
            default:
                break;
        }
        return false;
    }
}
