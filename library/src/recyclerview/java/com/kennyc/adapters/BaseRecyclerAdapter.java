package com.kennyc.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseRecyclerAdapter<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<VH> {

    protected final String TAG = getClass().getSimpleName();

    private List<T> mItems;

    private LayoutInflater mInflater;

    private Resources mResources;

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
        mResources = context.getResources();
    }

    /**
     * Adds an item to the list, {@link #notifyItemRangeInserted(int, int)} will be called
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
     * Adds an item to the list at the given position, {@link #notifyItemRangeInserted(int, int)} will be called
     *
     * @param object   Object to add to the adapter
     * @param position Position to add the object
     */
    public void addItem(T object, int position) {
        // An exception is thrown instead of creating a List object since the type of list in unknown
        if (mItems == null) throw new NullPointerException("Adapter list has not been initialized");
        mItems.add(position, object);
        notifyItemRangeInserted(position, 1);
    }

    /**
     * Adds a list of items to the adapter list, {@link #notifyItemRangeInserted(int, int)} will be called
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
     * Adds a list of items to the adapter list at the given position, {@link #notifyItemRangeInserted(int, int)} will be called
     *
     * @param items    List of items to add to the adapter
     * @param position The position to add the items into the adapter
     */
    public void addItems(List<T> items, int position) {
        if (items == null || items.isEmpty()) {
            return;
        }

        mItems.addAll(position, items);
        notifyItemRangeInserted(position, items.size());
    }

    /**
     * Removes an object from the list, {@link #notifyItemRangeRemoved(int, int)} (int, int)} will be called
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
     * Removes an item at the given position, {@link #notifyItemRemoved(int)} will be called
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
     * Removes a range of items from the adapter, {@link #notifyItemRangeRemoved(int, int)} will be called
     *
     * @param start Starting position of removal
     * @param end   Ending position of removal
     */
    public void removeItems(int start, int end) {
        mItems.subList(start, end).clear();
        notifyItemRangeRemoved(start, end - start);
    }

    /**
     * Returns the index of the item in regards to the backing list. If not found, {@link RecyclerView#NO_POSITION} will be returned
     *
     * @param object The object to search for
     * @return The index of the item. {@link RecyclerView#NO_POSITION} will be returned if not found
     */
    public int indexOf(T object) {
        return mItems != null && !mItems.isEmpty() ? mItems.indexOf(object) : RecyclerView.NO_POSITION;
    }

    /**
     * Removes all items from the list, {@link #notifyItemRangeRemoved(int, int)} will be called
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
        return getItemCount() <= 0;
    }

    /**
     * Returns the color for the given color resource
     *
     * @param color Color resource id
     * @return
     */
    @ColorInt
    protected int getColor(@ColorRes int color) {
        return mResources.getColor(color);
    }

    /**
     * Returns the string for the given string resource
     *
     * @param string String resource
     * @return
     */
    protected String getString(@StringRes int string) {
        return mResources.getString(string);
    }

    /**
     * Inflates a view from the given layout resource
     *
     * @param layoutId Layout resource to inflate
     * @param parent   Optional parent view
     * @return
     */
    protected View inflateView(@LayoutRes int layoutId, @Nullable ViewGroup parent) {
        if (parent == null) {
            return mInflater.inflate(layoutId, null);
        }

        return mInflater.inflate(layoutId, parent, false);
    }

    /**
     * Frees up any resources tied to the adapter. Should be called in an activities onDestroy lifecycle method if needed
     */
    public void onDestroy() {
    }
}