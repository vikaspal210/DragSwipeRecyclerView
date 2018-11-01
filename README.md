# DragSwipeRecyclerView
Drag and Swipe Recycler view using ItemTouchHelper, to rearrange items in list or dismiss item in list using swipe gesture in android.

![alt text](https://cdn-images-1.medium.com/max/800/1*FdJbZnF5I-iOw0wgiuVJGQ.gif)

## Setting up
First thing we need is a basic RecyclerView setup. If you haven’t already, update your build.gradle to include the RecyclerView dependency.
ItemTouchHelper will work with almost any RecyclerView.Adapter 

compile 'com.android.support:recyclerview-v7:22.2.0'

## Using ItemTouchHelper and ItemTouchHelper.Callback
In order to use ItemTouchHelper, you’ll create an ItemTouchHelper.Callback. This is the interface that allows you to listen for “move” and “swipe” events. It’s also where you are able to control the state of the view selected, and override the default animations. There’s a helper class that you can use if you want a basic implementation, SimpleCallback, but for the purposes of learning how it works, we’ll make our own.

The main callbacks that we must override to enable basic drag & drop and swipe-to-dismiss are:
```
getMovementFlags(RecyclerView, ViewHolder)
onMove(RecyclerView, ViewHolder, ViewHolder)
onSwiped(ViewHolder, int)
```
We’ll also use a couple of helpers:

```
isLongPressDragEnabled()
isItemViewSwipeEnabled()
```

ItemTouchHelper allows you to easily determine the direction of an event. You must override getMovementFlags() to specify which directions
of drags and swipes are supported. Use the helper ItemTouchHelper.makeMovementFlags(int, int) to build the returned flags. We’re enabling 
dragging and swiping in both directions here.
```
@Override
public int getMovementFlags(RecyclerView recyclerView, 
        RecyclerView.ViewHolder viewHolder) {
    int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
    int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
    return makeMovementFlags(dragFlags, swipeFlags);
}
```
ItemTouchHelper can be used for drag without swipe (or vice versa), so you must designate exactly what you wish to support. 
Implementations should return true from isLongPressDragEnabled() in order to support starting drag events from a long press on 
a RecyclerView item. Alternatively, ItemTouchHelper.startDrag(RecyclerView.ViewHolder) can be called to start a drag from a “handle.”
This will be explored further later.
```
@Override
public boolean isLongPressDragEnabled() {
    return true;
}
```
To enable swiping from touch events that start anywhere within the view, simply return true from isItemViewSwipeEnabled().
Alternatively, ItemTouchHelper.startSwipe(RecyclerView.ViewHolder) can be called to start a drag manually.
```
@Override
public boolean isItemViewSwipeEnabled() {
    return true;
}
```

The next two, onMove() and onSwiped() are needed to notify anything in charge of updating the underlying data.
So first we’ll create an interface that allows us to pass these event callbacks back up the chain.
###### Interface
```
public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
```
###### Implementation of interfaace
```
public class RecyclerListAdapter extends 
        RecyclerView.Adapter<ItemViewHolder> 
        implements ItemTouchHelperAdapter {
// ... code from gist
@Override
public void onItemDismiss(int position) {
    mItems.remove(position);
    notifyItemRemoved(position);
}

@Override
public boolean onItemMove(int fromPosition, int toPosition) {
    if (fromPosition < toPosition) {
        for (int i = fromPosition; i < toPosition; i++) {
            Collections.swap(mItems, i, i + 1);
        }
    } else {
        for (int i = fromPosition; i > toPosition; i--) {
            Collections.swap(mItems, i, i - 1);
        }
    }
    notifyItemMoved(fromPosition, toPosition);
    return true;
}
```

It’s very important to call notifyItemRemoved() and notifyItemMoved() so the Adapter is aware of the changes. 
It’s also important to note that we’re changing the position of the item every time the view is shifted to a new index, and not at 
the end of a “drop” event.

Now we can go back to building our SimpleItemTouchHelperCallback as we still must override onMove() and onSwiped(). 
First add a constructor and a field for the Adapter:
```
private final ItemTouchHelperAdapter mAdapter;

public SimpleItemTouchHelperCallback(
        ItemTouchHelperAdapter adapter) {
    mAdapter = adapter;
}
```
Then override the remaining events and notify the adapter:
```
@Override
public boolean onMove(RecyclerView recyclerView, 
        RecyclerView.ViewHolder viewHolder, 
        RecyclerView.ViewHolder target) {
    mAdapter.onItemMove(viewHolder.getAdapterPosition(), 
            target.getAdapterPosition());
    return true;
}
@Override
public void onSwiped(RecyclerView.ViewHolder viewHolder, 
        int direction) {
    mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
}
```

###### Final Callback class should look like this
```
public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
 
    private final ItemTouchHelperAdapter mAdapter;

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }
    
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, 
            ViewHolder target) {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

}
```

With our Callback ready, we can create our ItemTouchHelper and call attachToRecyclerView(RecyclerView) (e.g. in MainFragment.java):
```
ItemTouchHelper.Callback callback = 
    new SimpleItemTouchHelperCallback(adapter);
ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
touchHelper.attachToRecyclerView(recyclerView);
```


