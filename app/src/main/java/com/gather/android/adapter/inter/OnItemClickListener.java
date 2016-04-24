package com.gather.android.adapter.inter;

import android.view.View;

/**
 * Created by Levi on 2015/7/23.
 */
public abstract class OnItemClickListener<E> implements View.OnClickListener{
    private E model;
    private int position;

    public OnItemClickListener(int p, E m){
        this.model = m;
        this.position = p;
    }


    @Override
    public void onClick(View view) {
        onItemClick(position, model);
    }

    public abstract void onItemClick(int position, E e);
}
