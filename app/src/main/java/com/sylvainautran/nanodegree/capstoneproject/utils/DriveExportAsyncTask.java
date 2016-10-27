package com.sylvainautran.nanodegree.capstoneproject.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.ExportLoader;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DriveExportAsyncTask extends ApiClientAsyncTask<DriveFile, Void, Boolean> {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private final String MATERNELLE = "PS MS GS ps ms gs Ps Ms Gs pS Ms Gs";
    public static int SUCCESS = 1;
    public static int FAIL = 0;
    private Context mContext;
    private Long classId;
    private Long startDate;
    private Long endDate;
    private String monthYear;
    private TaskCallback listener;

    public DriveExportAsyncTask(Context context, TaskCallback listener, long classId, long startDate, long endDate, String monthYear){
        super(context);
        mContext = context;
        this.listener = listener;
        this.classId = classId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.monthYear = monthYear;
    }

    @Override
    protected Boolean doInBackgroundConnected(DriveFile... args) {
        String separator = ",";
        String emptyLine = "";
        String jumpLine = "\n";

        DriveFile file = args[0];
        DriveApi.DriveContentsResult driveContentsResult = file.open(getGoogleApiClient(), DriveFile.MODE_WRITE_ONLY, null).await();

        if (!driveContentsResult.getStatus().isSuccess()) {
            Log.e(LOG_TAG, mContext.getString(R.string.drive_contents_fail, driveContentsResult.getStatus().getStatusMessage()));
            return false;
        }

        Charset charset = Charset.forName("UTF-8");
        DriveContents driveContents = driveContentsResult.getDriveContents();
        OutputStream outputStream = driveContents.getOutputStream();

        Looper.prepare();
        Cursor callDatesCursor = ExportLoader.getCallDatesForExport(mContext, classId, startDate, endDate).loadInBackground();
        Cursor studentsInfoCursor = ExportLoader.getStudentsInfoForExport(mContext, classId, startDate, endDate).loadInBackground();

        int totalMaternelle = 0;
        int totalPrimaire = 0;
        int total = studentsInfoCursor.getCount();

        int[] callMaternellesCount = new int[callDatesCursor.getCount()];
        int[] callPrimairesCount = new int[callDatesCursor.getCount()];
        int[] callTotalCount = new int[callDatesCursor.getCount()];

        try {
            Calendar calendar = Calendar.getInstance();
            outputStream.write((monthYear + separator).getBytes(charset));
            while(callDatesCursor.moveToNext()){
                calendar.setTimeInMillis(callDatesCursor.getLong(ExportLoader.Query.DATETIME));
                outputStream.write((separator + (calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())).substring(0,1).toUpperCase() +
                        " " + calendar.get(Calendar.DATE)).getBytes(charset));
            }

            outputStream.write(jumpLine.getBytes(charset));
            outputStream.flush();

            for (int i = 0; i < callDatesCursor.getCount() + 2; i++) {
                emptyLine += separator;
            }

            outputStream.write(emptyLine.getBytes(charset));
            outputStream.write(jumpLine.getBytes(charset));
            outputStream.flush();

            String name;
            String grade;
            String callState;
            Cursor callInfo;
            Long studentId;
            Long callId;
            while(studentsInfoCursor.moveToNext()){
                studentId = studentsInfoCursor.getLong(ExportLoader.Query.STUDENT_ID);
                name = studentsInfoCursor.getString(ExportLoader.Query.FIRSTNAME) + " " + studentsInfoCursor.getString(ExportLoader.Query.LASTNAME).toUpperCase(Locale.getDefault());
                grade = studentsInfoCursor.getString(ExportLoader.Query.GRADE);
                outputStream.write((name + separator + grade).getBytes(charset));

                if(MATERNELLE.contains(grade)){
                    totalMaternelle++;
                }else {
                    totalPrimaire++;
                }

                for (callDatesCursor.moveToFirst(); !callDatesCursor.isAfterLast(); callDatesCursor.moveToNext()) {
                    callId = callDatesCursor.getLong(ExportLoader.Query.CALL_ID);
                    callInfo = ExportLoader.getCallsInfoForStudent(mContext, classId, startDate, endDate, studentId, callId).loadInBackground();

                    outputStream.write(separator.getBytes(charset));
                    if(callInfo.moveToFirst()){
                        if(callInfo.getInt(ExportLoader.Query.IS_PRESENT) == AppelContract.CallStudentLinkEntry.PRESENT){

                            callTotalCount[callDatesCursor.getPosition()]++;
                            if(MATERNELLE.contains(grade)){
                                callMaternellesCount[callDatesCursor.getPosition()]++;
                            }else{
                                callPrimairesCount[callDatesCursor.getPosition()]++;
                            }

                            if(callInfo.isNull(ExportLoader.Query.LEAVING_TIME)){
                                callState = mContext.getString(R.string.present);
                            }else{
                                DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
                                callState = df.format(new Date(callInfo.getLong(ExportLoader.Query.LEAVING_TIME)));
                            }


                        }else{
                            callState = "";
                        }
                        outputStream.write(callState.getBytes(charset));
                    }

                    callInfo.close();
                }

                outputStream.write(jumpLine.getBytes(charset));
                outputStream.flush();
            }
            outputStream.flush();
            outputStream.write(emptyLine.getBytes(charset));
            outputStream.write(jumpLine.getBytes(charset));
            outputStream.flush();

            outputStream.write(("Total Maternelle" + separator + totalMaternelle).getBytes(charset));
            for (callDatesCursor.moveToFirst(); !callDatesCursor.isAfterLast(); callDatesCursor.moveToNext()) {
                outputStream.write((separator + callMaternellesCount[callDatesCursor.getPosition()]).getBytes(charset));
            }

            outputStream.write(jumpLine.getBytes(charset));
            outputStream.flush();

            outputStream.write(("Total Primaire" + separator + totalPrimaire).getBytes(charset));
            for (callDatesCursor.moveToFirst(); !callDatesCursor.isAfterLast(); callDatesCursor.moveToNext()) {
                outputStream.write((separator + callPrimairesCount[callDatesCursor.getPosition()]).getBytes(charset));
            }

            outputStream.write(jumpLine.getBytes(charset));
            outputStream.flush();

            outputStream.write(("Total" + separator + total).getBytes(charset));
            for (callDatesCursor.moveToFirst(); !callDatesCursor.isAfterLast(); callDatesCursor.moveToNext()) {
                outputStream.write((separator + callTotalCount[callDatesCursor.getPosition()]).getBytes(charset));
            }

            } catch (IOException e) {
            Log.e(LOG_TAG, mContext.getString(R.string.io_exception), e);
        } finally {
            callDatesCursor.close();
            studentsInfoCursor.close();
            try {
                if (outputStream != null) outputStream.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        com.google.android.gms.common.api.Status status = driveContents.commit(getGoogleApiClient(), null).await();
        return status.isSuccess();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if(result) {
            listener.done(SUCCESS);
        }else{
            listener.done(FAIL);
        }
    }

    public interface TaskCallback{
        void done(int status);
    }
}
