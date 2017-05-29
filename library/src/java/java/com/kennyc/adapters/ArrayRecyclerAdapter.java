package com.kennyc.adapters;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * {@link android.support.v7.widget.RecyclerView.Adapter} for rendering simple data like {@link ArrayAdapter}
 */
public class ArrayRecyclerAdapter<T> extends RecyclerView.Adapter<ArrayRecyclerAdapter.SimpleTextViewHolder> {
    protected final String TAG = getClass().getSimpleName();
    private final List<T> mItems = new ArrayList<>();

    private LayoutInflater mInflater;

    @IdRes
    private int mTextViewId = 0;

    @LayoutRes
    private int mLayoutResource = 0;

    @Nullable
    private View.OnClickListener mClickListener = null;


    /**
     * Constructor for creating a {@link ArrayAdapter}
     *
     * @param context        App context
     * @param layoutResource The layout to use for the adapter
     * @param clickListener  Click Listener for receiving click events. Null safe
     */
    public ArrayRecyclerAdapter(@NonNull Context context, @LayoutRes int layoutResource, @Nullable View.OnClickListener clickListener) {
        this(context, layoutResource, 0, (Collection<T>) null, clickListener);
    }

    /**
     * Constructor for creating a {@link ArrayAdapter}
     *
     * @param context            App context
     * @param layoutResource     The layout to use for the adapter
     * @param textViewResourceId The id of the {@link android.widget.TextView} in the layout
     * @param clickListener      Click Listener for receiving click events. Null safe
     */
    public ArrayRecyclerAdapter(@NonNull Context context, @LayoutRes int layoutResource, @IdRes int textViewResourceId, @Nullable View.OnClickListener clickListener) {
        this(context, layoutResource, textViewResourceId, (Collection<T>) null, clickListener);
    }

    /**
     * Constructor for creating a {@link ArrayAdapter}
     *
     * @param context        App context
     * @param layoutResource The layout to use for the adapter
     * @param items          Array to populate the adapter. Null safe
     * @param clickListener  Click Listener for receiving click events. Null safe
     */
    public ArrayRecyclerAdapter(@NonNull Context context, @LayoutRes int layoutResource, T[] items, @Nullable View.OnClickListener clickListener) {
        this(context, layoutResource, 0, items != null && items.length > 0 ? Arrays.asList(items) : null, clickListener);
    }

    /**
     * Constructor for creating a {@link ArrayAdapter}
     *
     * @param context        App context
     * @param layoutResource The layout to use for the adapter
     * @param items          {@link Collection} of items to populate the adapter. Null safe
     * @param clickListener  Click Listener for receiving click events. Null safe
     */
    public ArrayRecyclerAdapter(@NonNull Context context, @LayoutRes int layoutResource, Collection<T> items, @Nullable View.OnClickListener clickListener) {
        this(context, layoutResource, 0, items, clickListener);
    }

    /**
     * Constructor for creating a {@link ArrayAdapter}
     *
     * @param context            App context
     * @param layoutResource     The layout to use for the adapter
     * @param textViewResourceId The id of the {@link android.widget.TextView} in the layout
     * @param items              Array to populate the adapter. Null safe
     * @param clickListener      Click Listener for receiving click events. Null safe
     */
    public ArrayRecyclerAdapter(@NonNull Context context, @LayoutRes int layoutResource, @IdRes int textViewResourceId, T[] items, @Nullable View.OnClickListener clickListener) {
        this(context, layoutResource, textViewResourceId, items != null && items.length > 0 ? Arrays.asList(items) : null, clickListener);
    }

    /**
     * Constructor for creating a {@link ArrayAdapter}
     *
     * @param context            App context
     * @param layoutResource     The layout to use for the adapter
     * @param textViewResourceId The id of the {@link android.widget.TextView} in the layout
     * @param items              {@link Collection} of items to populate the adapter. Null safe
     * @param clickListener      Click Listener for receiving click events. Null safe
     */
    public ArrayRecyclerAdapter(@NonNull Context context, @LayoutRes int layoutResource, @IdRes int textViewResourceId, Collection<T> items, @Nullable View.OnClickListener clickListener) {
        mInflater = LayoutInflater.from(context);
        mLayoutResource = layoutResource;
        mTextViewId = textViewResourceId;
        if (items != null && !items.isEmpty()) mItems.addAll(items);
        mClickListener = clickListener;
    }

    @Override
    public ArrayRecyclerAdapter.SimpleTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SimpleTextViewHolder vh = new SimpleTextViewHolder(mInflater.inflate(mLayoutResource, parent, false), mTextViewId);
        vh.itemView.setOnClickListener(mClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(ArrayRecyclerAdapter.SimpleTextViewHolder holder, int position) {
        T item = getItem(position);

        if (item instanceof CharSequence) {
            holder.textView.setText((CharSequence) item);
        } else {
            holder.textView.setText(item.toString());
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public boolean isEmpty() {
        return getItemCount() <= 0;
    }

    /**
     * Adds an item to the list, {@link #notifyItemRangeInserted(int, int)} will be called
     *
     * @param object Object to add to the adapter
     */
    public void addItem(T object) {
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
        startingSize = mItems.size();
        mItems.addAll(items);

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
        int position = mItems.indexOf(object);
        return position >= 0 && removeItem(position) != null;
    }

    /**
     * Removes an item at the given position, {@link #notifyItemRemoved(int)} will be called
     *
     * @param position The position to remove from the adapter
     * @return The item removed
     */
    public T removeItem(int position) {
        T removedItem = mItems.remove(position);
        notifyItemRemoved(position);
        return removedItem;
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
        return !mItems.isEmpty() ? mItems.indexOf(object) : RecyclerView.NO_POSITION;
    }

    /**
     * Removes all items from the list, {@link #notifyItemRangeRemoved(int, int)} will be called
     */
    public void clear() {
        int size = mItems.size();
        mItems.clear();
        notifyItemRangeRemoved(0, size);
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

    protected static class SimpleTextViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public SimpleTextViewHolder(View view, @IdRes int textViewId) {
            super(view);
            if (textViewId == 0) {
                textView = (TextView) view;
            } else {
                textView = (TextView) view.findViewById(textViewId);
            }

            if (textView == null) throw new IllegalArgumentException("View is not a TextView");
        }
    }
}
