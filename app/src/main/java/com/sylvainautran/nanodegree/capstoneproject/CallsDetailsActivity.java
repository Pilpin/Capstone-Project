package com.sylvainautran.nanodegree.capstoneproject;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewGroupCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;


import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;

import java.text.DateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallsDetailsActivity extends AppCompatActivity implements CallsDetailsFragment.UpdateUI {
    private final String LOG_CAT = this.getClass().getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bottomBar)
    ViewGroup bottombar;
    @BindView(R.id.total)
    TextView totalTV;
    @BindView(R.id.present)
    TextView presentTV;
    @BindView(R.id.absent)
    TextView absentTV;

    private int total, present, absent, left;
    private boolean option;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_students_list);

        String className = getString(R.string.unknown_class);
        long callDate = 0;
        if(getIntent() != null){
            className = getIntent().getStringExtra(getString(R.string.intent_extra_class_name));
            callDate = getIntent().getLongExtra(getString(R.string.intent_extra_call_date), 0);

            long callId = getIntent().getLongExtra(getString(R.string.intent_extra_call_id), 0);
            long classId = getIntent().getLongExtra(getString(R.string.intent_extra_class_id), 0);

            if(savedInstanceState == null) {
                CallsDetailsFragment fragment = CallsDetailsFragment.newInstance(callId, classId);
                getFragmentManager().beginTransaction()
                        .add(R.id.list_container, fragment, "call_details")
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

    @Override
    public void updateTotal(int total) {
        this.total = total;
        totalTV.setText(getString(R.string.stats_total, total));
        isCallEnded();
    }

    @Override
    public void updateCall(int present, int absent, int left, boolean option) {
        this.present = present;
        this.absent = absent;
        this.left = left;
        this.option = option;
        if (option) {
            presentTV.setText(getString(R.string.stats_present, present) + " [" + left + "]");
        }else {
            presentTV.setText(getString(R.string.stats_present, present));
        }
        absentTV.setText(getString(R.string.stats_absent, present));
        isCallEnded();
    }

    private void isCallEnded(){
        if(option){
            if(total != 0 && total == left + absent){
                animateBottomBar();
            }
        }else{
            if(total != 0 && total == present + absent){
                animateBottomBar();
            }
        }
    }

    private void animateBottomBar(){

        Integer colorFrom = getResources().getColor(R.color.colorPrimary);
        Integer colorTo = getResources().getColor(R.color.colorPresent);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                bottombar.setBackgroundColor((Integer) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }
}
