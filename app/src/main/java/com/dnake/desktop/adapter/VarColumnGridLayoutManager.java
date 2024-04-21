package com.dnake.desktop.adapter;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VarColumnGridLayoutManager extends GridLayoutManager {

    private int spancount;

    public int getSpancount() {
        return spancount;
    }

    public void setSpancount(int spancount) {
        this.spancount = spancount;
        updateSpanCount();
    }

    public VarColumnGridLayoutManager(Context context, int spancount) {
        super(context, 1);
        this.spancount = spancount;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        updateSpanCount();
        super.onLayoutChildren(recycler, state);
    }

    private void updateSpanCount() {
        if (spancount < 1) {
            spancount = 1;
        }
        this.setSpanCount(spancount);
    }
}
