package org.autojs.autoxjs.ui.widget;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Stardust on 2017/3/27.
 */

public interface OnItemClickListener {

    void onItemClick(RecyclerView parent, View item, int position);
}
