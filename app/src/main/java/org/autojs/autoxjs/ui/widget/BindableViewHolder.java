package org.autojs.autoxjs.ui.widget;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Stardust on 2017/4/8.
 */

public abstract class BindableViewHolder<DataType> extends RecyclerView.ViewHolder{


    public BindableViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(DataType data, int position);
}
