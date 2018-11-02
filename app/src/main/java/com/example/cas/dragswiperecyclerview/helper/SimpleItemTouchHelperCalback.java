package com.example.cas.dragswiperecyclerview.helper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class SimpleItemTouchHelperCalback extends ItemTouchHelper.Callback {

    //constants
    private final ItemTouchHelperAdapter mAdapter;

    //constructor
    public SimpleItemTouchHelperCalback(ItemTouchHelperAdapter adapter) {
        mAdapter=adapter;
    }

    //required to be overridden
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //to determine direction of event add flags
        int dragFlags=ItemTouchHelper.UP|ItemTouchHelper.DOWN;
        int swipeFlags=ItemTouchHelper.START|ItemTouchHelper.END;
        return makeMovementFlags(dragFlags,swipeFlags);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        mAdapter.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition())
        return true;
    }

    //helpers
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
}
