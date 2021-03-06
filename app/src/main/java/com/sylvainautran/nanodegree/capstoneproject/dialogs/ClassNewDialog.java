package com.sylvainautran.nanodegree.capstoneproject.dialogs;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClassNewDialog extends DialogFragment {
    private final String LOG_TAG = this.getClass().getSimpleName();
    public static final String CLASS_ID = "class_id";
    public static final String CLASS_NAME = "class_name";

    @BindView(R.id.name)
    TextInputEditText name;
    @BindView(R.id.name_container)
    TextInputLayout name_container;

    public ClassNewDialog(){
    }

    public static ClassNewDialog newInstance(long class_id, String class_name) {
        ClassNewDialog fragment = new ClassNewDialog();
        Bundle bundle = new Bundle();
        bundle.putString(CLASS_ID, Long.toString(class_id));
        bundle.putString(CLASS_NAME, class_name);
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_AlertDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_class_new, null);
        ButterKnife.bind(this, view);

        builder.setView(view)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ClassNewDialog.this.dismiss();
                    }
                });

        builder.setTitle(R.string.add_class);
        if(getArguments() != null && getArguments().containsKey(CLASS_ID)){
            builder.setTitle(R.string.edit_class);
            name.setText(getArguments().getString(CLASS_NAME));
        }

        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog dialog = (AlertDialog) getDialog();

        if (dialog != null) {
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    save();
                }
            });
        }
    }

    public void save(){
        if(getArguments() != null && getArguments().containsKey(CLASS_ID)) {
            if(name.getText().length() < 1){
                name_container.setError(getString(R.string.class_name_missing));
            }else {
                ContentValues cv = new ContentValues();
                cv.put(AppelContract.ClassEntry.COLUMN_NAME, name.getText().toString());
                getActivity().getApplicationContext().getContentResolver().update(AppelContract.ClassEntry.CONTENT_URI, cv, AppelContract.ClassEntry._ID + " = ?", new String[] { getArguments().getString(CLASS_ID) });
                dismiss();
            }
        }else{
            if(name.getText().length() < 1){
                name_container.setError(getString(R.string.class_name_missing));
            }else {
                ContentValues cv = new ContentValues();
                cv.put(AppelContract.ClassEntry.COLUMN_NAME, name.getText().toString());
                getActivity().getApplicationContext().getContentResolver().insert(AppelContract.ClassEntry.CONTENT_URI, cv);
                dismiss();
            }
        }
    }
}
