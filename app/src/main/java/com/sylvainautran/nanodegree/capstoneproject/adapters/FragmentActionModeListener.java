package com.sylvainautran.nanodegree.capstoneproject.adapters;

import android.support.v7.view.ActionMode;

public interface FragmentActionModeListener extends ActionMode.Callback {

    void addSelectedItem(int position, String[] values);

    void removeSelectedItem(int position);

}
