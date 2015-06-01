package com.kennyc.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kcampagna on 5/29/15.
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected final String TAG = getClass().getSimpleName();

    private List<T> mItems;

    protected LayoutInflater mInflater;

    /**
     * Simple constructor for creating a BaseRecyclerAdapter
     *
     * @param context    The context the adapter is running in
     * @param collection A list of items to populate the adapter with, can be null. If passing a null list,
     *                   {@link #addItem(Object)} will throw an exception as the list type is undefined. The list
     *                   needs to be created first with {@link #addItems(List)}
     */
    public BaseRecyclerAdapter(Context context, List<T> collection) {
        mItems = collection;
        mInflater = LayoutInflater.from(context);
    }

    /**
     * Adds an item to the list, {@link #notifyDataSetChanged()} will be called
     *
     * @param object Object to add to the adapter
     */
    public void addItem(T object) {
        // An exception is thrown instead of creating a List object since the type of list in unknown
        if (mItems == null) throw new NullPointerException("Adapter list has not been initialized");
        mItems.add(object);
        notifyItemInserted(mItems.size());
    }

    /**
     * Adds a list of items to the adapter list, {@link #notifyDataSetChanged()} will be called
     *
     * @param items List of items to add to the adapter
     */
    public void addItems(List<T> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        int startingSize = 0;
        int endSize = 0;

        if (mItems == null) {
            mItems = items;
        } else {
            startingSize = mItems.size();
            mItems.addAll(items);
        }

        endSize = mItems.size();
        notifyItemRangeInserted(startingSize, endSize);
    }

    /**
     * Removes an object from the list
     *
     * @param object The object to remove from the adapter
     * @return If the object was removed
     */
    public boolean removeItem(T object) {
        if (mItems != null) {
            int position = mItems.indexOf(object);
            return position >= 0 && removeItem(position) != null;
        }

        return false;
    }

    /**
     * Removes an item at the given position, {@link #notifyDataSetChanged()} will be called
     *
     * @param position The position to remove from the adapter
     * @return The item removed
     */
    public T removeItem(int position) {
        if (mItems != null) {
            T removedItem = mItems.remove(position);
            notifyItemRemoved(position);
            return removedItem;
        }

        return null;
    }

    /**
     * Removes all items from the list, {@link #notifyDataSetChanged()} will be called
     */
    public void clear() {
        if (mItems != null) {
            int size = mItems.size();
            mItems.clear();
            notifyItemRangeRemoved(0, size);
        }
    }

    /**
     * Returns the entire list. This is <b><i>not</i></b> a copy of the list. If a copy of the list is
     * needed, see {@link #retainItems()}
     *
     * @return The entire list of items in the adapter
     */
    protected List<T> getAllItems() {
        return mItems;
    }

    /**
     * Returns the object for the given position
     *
     * @param position The position to return
     * @return The item at the given position
     */
    public T getItem(int position) {
        return mItems.get(position);
    }

    /**
     * Returns an ArrayList of the items in the adapter, used for saving the items for configuration changes
     *
     * @return A copy of the items in the adapter
     */
    public ArrayList<T> retainItems() {
        return new ArrayList<>(mItems);
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    /**
     * Returns if the adapter is empty
     *
     * @return If the adapter is empty
     */
    public boolean isEmpty() {
        return mItems == null || mItems.size() <= 0;
    }
}
