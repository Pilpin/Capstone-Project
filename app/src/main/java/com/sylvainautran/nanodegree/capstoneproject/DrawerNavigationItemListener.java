package com.sylvainautran.nanodegree.capstoneproject;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;

public class DrawerNavigationItemListener implements NavigationView.OnNavigationItemSelectedListener {
    private Context mContext;

    public DrawerNavigationItemListener(Context context){
        mContext = context;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch(item.getItemId()){
            case R.id.navigation_classes:
                intent = new Intent(mContext, ClassesListActivity.class);
                mContext.startActivity(intent);
                break;
            case R.id.navigation_students:
                intent = new Intent(mContext, StudentsListActivity.class);
                mContext.startActivity(intent);
                break;
            case R.id.navigation_calls:
                intent = new Intent(mContext, CallsListActivity.class);
                mContext.startActivity(intent);
                break;
            default:
                break;
        }
        return false;
    }
}
