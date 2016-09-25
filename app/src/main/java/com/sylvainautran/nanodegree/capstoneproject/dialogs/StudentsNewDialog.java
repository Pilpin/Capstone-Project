package com.sylvainautran.nanodegree.capstoneproject.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StudentsNewDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private final String LOG_TAG = this.getClass().getSimpleName();
    public static final String TITLE_RES_ID = "title_res_id";
    public static final String STUDENT_ID = "student_id";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String BIRTH_DATE = "birth_date";

    @BindView(R.id.first_name)
    TextInputEditText first_name;
    @BindView(R.id.last_name)
    TextInputEditText last_name;
    @BindView(R.id.first_name_container)
    TextInputLayout first_name_container;
    @BindView(R.id.last_name_container)
    TextInputLayout last_name_container;
    @BindView(R.id.birth_date)
    Button birthdate;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.close)
    ImageView close;
    @BindView(R.id.save)
    TextView save;

    private Calendar cal = Calendar.getInstance();
    private DateFormat df;

    public StudentsNewDialog(){
        df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    public static StudentsNewDialog newInstance(int titleResId) {
        StudentsNewDialog fragment = new StudentsNewDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(TITLE_RES_ID, titleResId);
        fragment.setArguments(bundle);
        return fragment;
    }


    public static StudentsNewDialog newInstance(int titleResId, long student_id, String first_name, String last_name, long birth_date) {
        StudentsNewDialog fragment = new StudentsNewDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(TITLE_RES_ID, titleResId);
        bundle.putLong(STUDENT_ID, student_id);
        bundle.putString(FIRST_NAME, first_name);
        bundle.putString(LAST_NAME, last_name);
        bundle.putLong(BIRTH_DATE, birth_date);
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_student_new, container, false);
        ButterKnife.bind(this, view);

        if(getArguments() != null){
            title.setText(getArguments().getInt(TITLE_RES_ID, R.string.add_student));
            first_name.setText(getArguments().getString(FIRST_NAME, ""));
            last_name.setText(getArguments().getString(LAST_NAME, ""));
            cal.setTimeInMillis(getArguments().getLong(BIRTH_DATE, cal.getTimeInMillis()));
        }
        birthdate.setText(getString(R.string.student_birth_date, df.format(cal.getTime())));

        return view;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
        cal.set(year, month, date);
        birthdate.setText(getString(R.string.student_birth_date, df.format(cal.getTime())));
    }

    @OnClick({R.id.close, R.id.save, R.id.birth_date})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.close:
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
                dismiss();
                break;
            case R.id.save:
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
                if(first_name.getText().length() > 0 && last_name.getText().length() > 0) {
                    ContentValues cv = new ContentValues();
                    cv.put(AppelContract.StudentEntry.COLUMN_FIRSTNAME, first_name.getText().toString());
                    cv.put(AppelContract.StudentEntry.COLUMN_LASTNAME, last_name.getText().toString());
                    cv.put(AppelContract.StudentEntry.COLUMN_BIRTHDATE, cal.getTimeInMillis());

                    if (getArguments() != null && getArguments().containsKey(STUDENT_ID)) {
                        getActivity().getContentResolver().update(AppelContract.StudentEntry.CONTENT_URI, cv, AppelContract.StudentEntry._ID + " = ?", new String[]{Long.toString(getArguments().getLong
                                (STUDENT_ID))});
                    } else {
                        getActivity().getContentResolver().insert(AppelContract.StudentEntry.CONTENT_URI, cv);
                    }
                    dismiss();
                }else{
                    if(first_name.getText().length() < 1){
                        first_name_container.setError(getString(R.string.first_name_missing));
                    }
                    if(last_name.getText().length() < 1){
                        last_name_container.setError(getString(R.string.last_name_missing));
                    }
                }

                break;
            case R.id.birth_date:
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), StudentsNewDialog.this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;
        }
    }
}
