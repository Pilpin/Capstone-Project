package com.sylvainautran.nanodegree.capstoneproject;

import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.app.FragmentManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.sylvainautran.nanodegree.capstoneproject.dialogs.CallMonthsPickerDialog;
import com.sylvainautran.nanodegree.capstoneproject.dialogs.ClassPickerDialog;
import com.sylvainautran.nanodegree.capstoneproject.dialogs.DialogListener;
import com.sylvainautran.nanodegree.capstoneproject.utils.DrawerNavigationItemListener;
import com.sylvainautran.nanodegree.capstoneproject.utils.DriveExportAsyncTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallsListActivity extends AppCompatActivity implements DialogListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DriveExportAsyncTask.TaskCallback {
    private final String LOG_TAG = this.getClass().getSimpleName();

    protected static final int RESOLVE_CONNECTION_REQUEST_CODE = 10;
    protected static final int REQUEST_FILE_CREATION = 11;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    ActionBarDrawerToggle mDrawerToggle;

    private GoogleApiClient mGoogleApiClient;

    private long classId;
    private String className;
    private String monthName;
    private int year;
    private long startDate;
    private long endDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_list);
        ButterKnife.bind(this);

        if(savedInstanceState == null) {
            CallsListFragment fragment = CallsListFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .add(R.id.list_container, fragment, "calls_list")
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
        navigationView.setNavigationItemSelectedListener(new DrawerNavigationItemListener(this, drawerLayout));
        navigationView.setCheckedItem(R.id.navigation_calls);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calls_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.publish_call){
            FragmentManager fragmentManager = getFragmentManager();
            ClassPickerDialog classPickerDialog = new ClassPickerDialog();
            classPickerDialog.setListener(this);
            classPickerDialog.show(fragmentManager, "dialog");
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

    @Override
    public void onDismissDialog(Bundle bundle) {
        if(bundle.containsKey(ClassPickerDialog.CLASS_ID)){
            classId = bundle.getLong(ClassPickerDialog.CLASS_ID);
            className = bundle.getString(ClassPickerDialog.CLASS_NAME);
            FragmentManager fragmentManager = getFragmentManager();
            CallMonthsPickerDialog callMonthsPickerDialog = CallMonthsPickerDialog.newInstance(classId);
            callMonthsPickerDialog.setListener(this);
            callMonthsPickerDialog.show(fragmentManager, "dialog");
        }

        if(bundle.containsKey(CallMonthsPickerDialog.CALLS_START_DATE)){
            monthName = bundle.getString(CallMonthsPickerDialog.MONTH_NAME);
            monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
            year = bundle.getInt(CallMonthsPickerDialog.YEAR);
            startDate = bundle.getLong(CallMonthsPickerDialog.CALLS_START_DATE);
            endDate = bundle.getLong(CallMonthsPickerDialog.CALLS_END_DATE);
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(driveContentsCallback);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(LOG_TAG, getString(R.string.google_api_client_fail, result.toString()));
        if (!result.hasResolution()) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        try {
            result.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            Snackbar.make(toolbar, e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            Log.e(LOG_TAG, getString(R.string.google_api_resolution_fail), e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_FILE_CREATION:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    DriveFile file = driveId.asDriveFile();
                    DriveExportAsyncTask driveExportAsyncTask = new DriveExportAsyncTask(this, this, classId, startDate, endDate, monthName + " " + year);
                    driveExportAsyncTask.execute(file);
                }
                break;
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    final ResultCallback<DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                    MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                            .setTitle(year + " " + monthName + " - " + className)
                            .setMimeType("text/csv").build();
                    IntentSender intentSender = Drive.DriveApi
                            .newCreateFileActivityBuilder()
                            .setInitialMetadata(metadataChangeSet)
                            .setInitialDriveContents(result.getDriveContents())
                            .setActivityTitle(getString(R.string.publish_call))
                            .build(mGoogleApiClient);
                    try {
                        startIntentSenderForResult(
                                intentSender, REQUEST_FILE_CREATION, null, 0, 0, 0);
                    } catch (SendIntentException e) {
                        Log.e(LOG_TAG, getString(R.string.send_intent_fail), e);
                    }
                }
            };

    @Override
    public void done(int status) {
        String message;
        int length;
        if(status == DriveExportAsyncTask.SUCCESS){
            message = getString(R.string.drive_export_success);
            length = Snackbar.LENGTH_SHORT;
        }else{
            message = getString(R.string.drive_export_fail);
            length = Snackbar.LENGTH_LONG;
        }

        Snackbar.make(toolbar,message,length).show();
    }
}
