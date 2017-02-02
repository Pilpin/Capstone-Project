package com.sylvainautran.nanodegree.capstoneproject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallsDetailsActivity extends AppCompatActivity {
    private final String LOG_CAT = this.getClass().getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_students_list);

        ButterKnife.bind(this);

        String className = getString(R.string.unknown_class);
        long callDate = 0;
        if(getIntent() != null){
            className = getIntent().getStringExtra(getString(R.string.intent_extra_class_name));
            callDate = getIntent().getLongExtra(getString(R.string.intent_extra_call_date), 0);

            long callId = getIntent().getLongExtra(getString(R.string.intent_extra_call_id), 0);
            long classId = getIntent().getLongExtra(getString(R.string.intent_extra_class_id), 0);

            PagerAdapter pagerAdapter = new PagerAdapter(getFragmentManager(), callId, classId);
            viewPager.setAdapter(pagerAdapter);
        }

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(callDate);
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
            actionBar.setTitle(className);
            actionBar.setSubtitle(df.format(cal.getTime()));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> fragments;
        ArrayList<CharSequence> fragmentsTitle;

        public PagerAdapter(FragmentManager fm, long callId, long classId) {
            super(fm);
            fragments = new ArrayList<>(3);
            fragmentsTitle = new ArrayList<>(3);
            fragments.add(CallsDetailsFragment.newInstance(callId, classId, CallsDetailsFragment.SELECT_ALL_STUDENTS));
            fragmentsTitle.add("Tous les élèves");
            fragments.add(CallsDetailsFragment.newInstance(callId, classId, CallsDetailsFragment.SELECT_MATERNELLE));
            fragmentsTitle.add("Maternelle");
            fragments.add(CallsDetailsFragment.newInstance(callId, classId, CallsDetailsFragment.SELECT_PRIMAIRE));
            fragmentsTitle.add("Primaire");
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentsTitle.get(position);
        }
    }
}
