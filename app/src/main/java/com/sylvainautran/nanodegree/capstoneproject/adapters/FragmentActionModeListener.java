package com.sylvainautran.nanodegree.capstoneproject.adapters;

import android.support.v7.view.ActionMode;

import java.util.HashMap;

public interface FragmentActionModeListener extends ActionMode.Callback {

    public void addSelectedItem(int position, long id);

    public void removeSelectedItem(int position, long id);

    public void clearSelectedItems();
}
