package com.sylvainautran.nanodegree.capstoneproject.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sylvainautran.nanodegree.capstoneproject.ClassStudentsListActivity;
import com.sylvainautran.nanodegree.capstoneproject.R;
import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;
import com.sylvainautran.nanodegree.capstoneproject.loaders.ClassesLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder> {
    private Cursor mCursor;
    private Context mContext;

    public ClassesAdapter(Context context, Cursor cursor) {
        mCursor = cursor;
        mContext = context;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(ClassesLoader.Query._ID);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_class, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, AppelContract.ClassStudentLinkEntry.buildClassStudentLinkFromClassUri(getItemId(vh.getAdapterPosition())));
                intent.putExtra(ClassStudentsListActivity.CLASS_NAME, vh.name.getText());
                mContext.startActivity(intent);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View dialog = inflater.inflate(R.layout.dialog_class_new, null);
                TextView title = ButterKnife.findById(dialog, R.id.title);
                title.setText(R.string.add_class);
                final TextInputEditText editText = ButterKnife.findById(dialog, R.id.name);
                editText.setText(vh.name.getText());

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppTheme_AlertDialog);
                builder.setView(dialog);
                builder.setPositiveButton(R.string.add_class, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ContentValues cv = new ContentValues();
                        cv.put(AppelContract.ClassEntry.COLUMN_NAME, editText.getText().toString());
                        mContext.getApplicationContext().getContentResolver().update(AppelContract.ClassEntry.CONTENT_URI, cv, AppelContract.ClassEntry._ID + " = ?", new String[] { Long.toString
                                (getItemId(vh.getAdapterPosition())) });
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);
                builder.show();
                return true;
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.name.setText(mCursor.getString(ClassesLoader.Query.COLUMN_NAME));
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) public TextView name;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}