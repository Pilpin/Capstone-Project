package com.sylvainautran.nanodegree.capstoneproject.dialogs;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
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
import android.support.annotation.StringDef;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sylvainautran.nanodegree.capstoneproject.DividerItemDecoration;
import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.adapters.AdapterActionModeListener;
import com.sylvainautran.nanodegree.capstoneproject.adapters.FragmentActionModeListener;
import com.sylvainautran.nanodegree.capstoneproject.adapters.StudentsPickerAdapter;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;
import com.sylvainautran.nanodegree.capstoneproject.data.loaders.StudentsLoader;
import com.sylvainautran.nanodegree.capstoneproject.utils.AdapterKeys;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClassStudentsNewDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>, FragmentActionModeListener {
    private final String LOG_TAG = this.getClass().getSimpleName();
    public static final String TITLE_RES_ID = "title_res_id";
    public static final String CLASS_ID = "class_id";

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

    private RecyclerView.Adapter adapter;
    private HashMap<Integer, String[]> selectedStudents;

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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog;
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_class_student_new, container, false);

        if(getArguments() != null){
            ButterKnife.bind(this, view);
            title.setText(getString(R.string.add_student_to_class));
            getActivity().getLoaderManager().restartLoader(0, null, this);
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
    public void onDismiss(DialogInterface dialog) {
        Log.d(LOG_TAG, "dismiss");
        super.onDismiss(dialog);
    }

    @Override
    public void onDestroyView() {
        Log.d(LOG_TAG, "destroy");
        super.onDestroyView();
    }

    @Override
    public CursorLoader onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "created");
        return StudentsLoader.getAllStudentsNotInClass(getActivity(), getArguments().getLong(CLASS_ID));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(LOG_TAG, "finished");
        HashMap<Long, Character> headers = null;
        if(cursor != null && cursor.moveToFirst()){
            headers = new HashMap<>();
            char first_letter = cursor.getString(StudentsLoader.Query.COLUMN_LASTNAME).charAt(0);
            headers.put(cursor.getLong(StudentsLoader.Query._ID), first_letter);
            while(cursor.moveToNext()){
                char new_first_letter = cursor.getString(StudentsLoader.Query.COLUMN_LASTNAME).charAt(0);
                if(first_letter != new_first_letter){
                    first_letter = new_first_letter;
                    headers.put(cursor.getLong(StudentsLoader.Query._ID), first_letter);
                }
            }

        }

        adapter = new StudentsPickerAdapter(getActivity(), cursor, this, selectedStudents, headers);

        if(adapter.getItemCount() > 0){
            emptyView.setVisibility(View.GONE);
        }else{
            emptyView.setVisibility(View.VISIBLE);
        }

        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG, "reset");
        mRecyclerView.setAdapter(null);
    }

    public void save(){
        if(getArguments() != null) {
            AdapterActionModeListener adapter = (AdapterActionModeListener) mRecyclerView.getAdapter();
            if (adapter != null) {
                if(selectedStudents.size() > 0 && grade.getText().length() > 0) {
                    Iterator it = selectedStudents.keySet().iterator();
                    ContentValues[] cvs = new ContentValues[selectedStudents.size()];
                    int i = 0;
                    while (it.hasNext()) {
                        ContentValues cv = new ContentValues();
                        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_CLASS_ID, getArguments().getLong(CLASS_ID));
                        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_STUDENT_ID, Long.parseLong( selectedStudents.get(it.next())[AdapterKeys.key_student_id] ));
                        cv.put(AppelContract.ClassStudentLinkEntry.COLUMN_GRADE, grade.getText().toString());
                        cvs[i++] = cv;
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

    @Override
    public void addSelectedItem(int position, String[] values) {
        selectedStudents.put(position, values);
    }

    @Override
    public void removeSelectedItem(int position) {
        selectedStudents.remove(position);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }
}
