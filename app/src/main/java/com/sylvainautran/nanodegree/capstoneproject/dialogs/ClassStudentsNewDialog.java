package com.sylvainautran.nanodegree.capstoneproject.dialogs;

import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sylvainautran.nanodegree.capstoneproject.DividerItemDecoration;
import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.adapters.AdapterActionModeListener;
import com.sylvainautran.nanodegree.capstoneproject.adapters.StudentsPickerAdapter;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.StudentsLoader;


import java.util.Collection;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClassStudentsNewDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>  {
    private final String LOG_TAG = this.getClass().getSimpleName();
    public static final String TITLE_RES_ID = "title_res_id";
    public static final String CLASS_ID = "class_id";
    public static final String CLASS_STUDENT_ID = "class_student_id";
    public static final String GRADE = "grade";

    @BindView(R.id.grade)
    TextInputEditText grade;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.list_container)
    LinearLayout list_container;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.close)
    ImageView close;
    @BindView(R.id.save)
    TextView save;

    public ClassStudentsNewDialog(){
    }

    public static ClassStudentsNewDialog newInstance(int titleResId, long class_id) {
        ClassStudentsNewDialog fragment = new ClassStudentsNewDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(TITLE_RES_ID, titleResId);
        bundle.putLong(CLASS_ID, class_id);
        fragment.setArguments(bundle);
        return fragment;
    }


    public static ClassStudentsNewDialog newInstance(int titleResId, long class_student_id, String grade) {
        ClassStudentsNewDialog fragment = new ClassStudentsNewDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(TITLE_RES_ID, titleResId);
        bundle.putLong(CLASS_STUDENT_ID, class_student_id);
        bundle.putString(GRADE, grade);
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog;
        if(getArguments() != null && getArguments().containsKey(CLASS_STUDENT_ID)){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_class_student_new, null, false);
            ButterKnife.bind(this, view);
            save.setVisibility(View.GONE);
            close.setVisibility(View.GONE);
            list_container.setVisibility(View.GONE);
            title.setText(getArguments().getInt(TITLE_RES_ID));
            grade.append(getArguments().getString(GRADE));

            builder.setView(view)
                    .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            save();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ClassStudentsNewDialog.this.dismiss();
                        }
                    });
            dialog = builder.create();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            return dialog;
        }else{
            dialog = super.onCreateDialog(savedInstanceState);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = view = inflater.inflate(R.layout.dialog_class_student_new, container, false);

        if(getArguments() != null){
            if(getArguments().containsKey(CLASS_STUDENT_ID)){
                return null;
            }
            ButterKnife.bind(this, view);
            title.setText(getArguments().getInt(TITLE_RES_ID, R.string.add_student_to_class));
            list_container.setVisibility(View.VISIBLE);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
            getActivity().getLoaderManager().initLoader(0, null, this);

        }

        return view;
    }

    @OnClick({R.id.close, R.id.save})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.close:
                dismiss();
                break;
            case R.id.save:
                save();
                break;
        }
    }

    @Override
    public CursorLoader onCreateLoader(int id, Bundle args) {
        return StudentsLoader.getAllStudentsNotInClass(getActivity(), getArguments().getLong(CLASS_ID));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        RecyclerView.Adapter adapter = new StudentsPickerAdapter(getActivity(), cursor);

        if(cursor != null && cursor.getCount() > 0){
            emptyView.setVisibility(View.GONE);
        }else{
            emptyView.setVisibility(View.VISIBLE);
        }

        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    public void save(){
        if(getArguments() != null) {
            if (getArguments().containsKey(CLASS_STUDENT_ID)) {
                ContentValues cv = new ContentValues();
                cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, grade.getText().toString());
                getActivity().getContentResolver().update(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv, AppelContract.ClassStudentLinkEntry._ID + " = ?",
                        new String[]{Long.toString(getArguments().getLong(CLASS_STUDENT_ID))});
                dismiss();
            } else if (getArguments().containsKey(CLASS_ID)) {
                AdapterActionModeListener adapter = (AdapterActionModeListener) mRecyclerView.getAdapter();
                if (adapter != null) {
                    Collection values = adapter.getValues(0).values();
                    Log.d(LOG_TAG, "Getting there " + values.size());
                    if(values.size() > 0) {
                        Iterator it = values.iterator();
                        ContentValues[] cvs = new ContentValues[values.size()];
                        int i = 0;
                        while (it.hasNext()) {
                            ContentValues cv = new ContentValues();
                            cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, getArguments().getLong(CLASS_ID));
                            cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, (Long) it.next());
                            cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, grade.getText().toString());
                            cvs[i++] = cv;
                        }
                        getActivity().getContentResolver().bulkInsert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cvs);
                        dismiss();
                    }else{
                        Toast.makeText(getActivity(), "You need to select students to add to the class", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }
}
