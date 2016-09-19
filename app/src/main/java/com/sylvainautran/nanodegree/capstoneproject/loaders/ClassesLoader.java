package com.sylvainautran.nanodegree.capstoneproject.loaders;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

import com.sylvainautran.nanodegree.capstoneproject.data.AppelContract;

public class ClassesLoader extends CursorLoader {
    public static ClassesLoader getAllClasses(Context context) {
        return new ClassesLoader(context, AppelContract.ClassEntry.CONTENT_URI);
    }

    public static ClassesLoader getClassForId(Context context, long id) {
        return new ClassesLoader(context, AppelContract.ClassEntry.buildClassUri(id));
    }

    private ClassesLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, AppelContract.ClassEntry.DEFAULT_SORT);
    }

    public interface Query {
        String[] PROJECTION = {
                AppelContract.ClassEntry._ID,
                AppelContract.ClassEntry.COLUMN_NAME
        };

        int _ID = 0;
        int COLUMN_NAME = 1;
    }
}
