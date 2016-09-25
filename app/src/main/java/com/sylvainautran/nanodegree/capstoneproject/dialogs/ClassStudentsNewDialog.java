package com.sylvainautran.nanodegree.capstoneproject.dialogs;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.data.adapters.BaseAdapter;
import com.sylvainautran.nanodegree.capstoneproject.data.adapters.StudentsAdapter;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.StudentsLoader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClassStudentsNewDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private final String SAVE_SELECTED_ITEMS = "selected_items";
    public static final String TITLE_RES_ID = "title_res_id";
    public static final String CLASS_ID = "class_id";
    public static final String CLASS_STUDENT_ID = "class_student_id";
    public static final String GRADE = "grade";
    public static final String STUDENT_ID = "student_id";


    @BindView(R.id.grade)
    TextInputEditText grade;
    @BindView(R.id.grade_container)
    TextInputLayout grade_container;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.list_container)
    LinearLayout list_container;
    @BindView(R.id.list_description)
    TextView list_description;
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

    private BaseAdapter adapter;
    private HashMap<Integer, Bundle> selectedStudents;

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SAVE_SELECTED_ITEMS, selectedStudents);
        super.onSaveInstanceState(outState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog;
        if(getArguments() != null && getArguments().containsKey(CLASS_STUDENT_ID)){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_AlertDialog);
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_class_student_new, null, false);
            ButterKnife.bind(this, view);
            save.setVisibility(View.GONE);
            close.setVisibility(View.GONE);
            list_container.setVisibility(View.GONE);
            toolbar.setVisibility(View.GONE);
            grade.append(getArguments().getString(GRADE));

            builder.setView(view)
                    .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {}
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ClassStudentsNewDialog.this.dismiss();
                        }
                    });
            builder.setTitle(getArguments().getInt(TITLE_RES_ID));
            dialog = builder.create();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }else{
            dialog = super.onCreateDialog(savedInstanceState);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getArguments() != null){
            if(getArguments().containsKey(CLASS_STUDENT_ID)){
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
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;

        if(getArguments() != null && !getArguments().containsKey(CLASS_STUDENT_ID)){
            view = inflater.inflate(R.layout.dialog_class_student_new, container, false);
            selectedStudents = new HashMap<>();
            if(savedInstanceState != null){
                selectedStudents = (HashMap<Integer, Bundle>) savedInstanceState.getSerializable(SAVE_SELECTED_ITEMS);
            }

            ButterKnife.bind(this, view);
            mRecyclerView.setAdapter(new BaseAdapter(getActivity(), null, null, null, null));
            title.setText(getArguments().getInt(TITLE_RES_ID, R.string.add_student_to_class));
            list_container.setVisibility(View.VISIBLE);
            getLoaderManager().restartLoader(0, null, this);
        }

        return view;
    }

    @Override
    public CursorLoader onCreateLoader(int id, Bundle args) {
        return StudentsLoader.getAllStudentsNotInClass(getActivity(), getArguments().getLong(CLASS_ID));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        HashMap<Long, Character> headers = null;
        if(cursor != null && cursor.moveToFirst()){
            headers = new HashMap<>();
            char first_letter = Character.toUpperCase(cursor.getString(StudentsLoader.Query.COLUMN_LASTNAME).charAt(0));
            headers.put(cursor.getLong(StudentsLoader.Query._ID), first_letter);
            while(cursor.moveToNext()){
                char new_first_letter = Character.toUpperCase(cursor.getString(StudentsLoader.Query.COLUMN_LASTNAME).charAt(0));
                if(first_letter != new_first_letter){
                    first_letter = new_first_letter;
                    headers.put(cursor.getLong(StudentsLoader.Query._ID), first_letter);
                }
            }

        }

        Set<Integer> selectedPositions = new HashSet<>();
        if(!selectedStudents.isEmpty()){
            selectedPositions = selectedStudents.keySet();
        }
        adapter = new StudentsAdapter(getActivity(), cursor, selectedPositions, this, null, headers);

        if(adapter.getItemCount() > 0){
            emptyView.setVisibility(View.GONE);
        }else{
            emptyView.setVisibility(View.VISIBLE);
        }

        adapter.setHasStableIds(true);
        mRecyclerView.swapAdapter(adapter, false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    @Override
    @OnClick({R.id.close, R.id.save})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.close:
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
                dismiss();
                break;
            case R.id.save:
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
                save();
                break;
            default:
                int position = (Integer) view.getTag(R.id.key_position);
                if(!removeSelectedItem(position)){
                    addSelectedItem(position, view);
                }
        }
    }

    public void addSelectedItem(int position, View view){
        Bundle values = new Bundle();
        values.putLong(STUDENT_ID, (Long) view.getTag(R.id.key_student_id));
        selectedStudents.put(position, values);
        adapter.addItem(position);
    }

    public boolean removeSelectedItem(int position){
        if(selectedStudents.remove(position) != null){
            adapter.removeItem(position);
            return true;
        }
        return false;
    }

    public void save(){
        if(getArguments() != null) {
            if (getArguments().containsKey(CLASS_STUDENT_ID)) {
                if(grade.getText().length() < 1){
                    grade_container.setError(getString(R.string.grade_missing));
                }else {
                    ContentValues cv = new ContentValues();
                    cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, grade.getText().toString());
                    getActivity().getContentResolver().update(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cv, AppelContract.ClassStudentLinkEntry._ID + " = ?",
                            new String[]{Long.toString(getArguments().getLong(CLASS_STUDENT_ID))});
                    dismiss();
                }
            } else if (getArguments().containsKey(CLASS_ID)) {
                if(selectedStudents.size() > 0 && grade.getText().length() > 0) {
                    ContentValues[] cvs = new ContentValues[selectedStudents.size()];
                    Iterator<Integer> iterator = selectedStudents.keySet().iterator();
                    Bundle values;
                    for(int i = 0; iterator.hasNext(); i++){
                        values = selectedStudents.get(iterator.next());
                        ContentValues cv = new ContentValues();
                        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, getArguments().getLong(CLASS_ID));
                        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, values.getLong(STUDENT_ID));
                        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, grade.getText().toString());
                        cvs[i] = cv;
                    }
                    getActivity().getContentResolver().bulkInsert(AppelContract.ClassStudentLinkEntry.CONTENT_URI, cvs);
                    dismiss();
                }
                if(selectedStudents.size() < 1){
                    Integer colorFrom = list_description.getCurrentTextColor();
                    Integer colorTo = getResources().getColor(android.R.color.holo_red_light);
                    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            list_description.setTextColor((Integer)animator.getAnimatedValue());
                        }
                    });
                    colorAnimation.start();
                }
                if(grade.getText().length() < 1){
                    grade_container.setError(getString(R.string.grade_missing));
                }
            }
        }
    }
}
