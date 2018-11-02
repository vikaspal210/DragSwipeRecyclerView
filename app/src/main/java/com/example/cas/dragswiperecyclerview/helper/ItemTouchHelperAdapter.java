package com.example.cas.dragswiperecyclerview.helper;

public interface ItemTouchHelperAdapter {

    //called when item is moved to change position for reordering
    boolean onItemMove(int fromPosition, int toPosition);

    //called when item is dismissed by swipe
    void onItemDismiss(int position);
}
