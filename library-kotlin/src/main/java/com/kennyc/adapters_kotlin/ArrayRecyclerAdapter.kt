package com.kennyc.adapters_kotlin

import android.content.Context
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.util.*
import java.util.Collection

/**
 * [android.support.v7.widget.RecyclerView.Adapter] for rendering simple data like [ArrayAdapter]
 */
class ArrayRecyclerAdapter<T>
/**
 * Constructor for creating a [ArrayAdapter]

 * @param context            App context
 * *
 * @param layoutResource     The layout to use for the adapter
 * *
 * @param textViewResourceId The id of the [android.widget.TextView] in the layout
 * *
 * @param items              [Collection] of items to populate the adapter. Null safe
 * *
 * @param clickListener      Click Listener for receiving click events. Null safe
 */
(context: Context, @LayoutRes layoutResource: Int, @IdRes textViewResourceId: Int, items: kotlin.collections.Collection<T>?, clickListener: View.OnClickListener?) : RecyclerView.Adapter<ArrayRecyclerAdapter.SimpleTextViewHolder>() {
    protected val TAG = javaClass.simpleName

    private val listItems = ArrayList<T>()

    private var mInflater: LayoutInflater

    @IdRes
    private var mTextViewId = 0

    @LayoutRes
    private var mLayoutResource = 0

    private var mClickListener: View.OnClickListener? = null

    init {
        mInflater = LayoutInflater.from(context)
        mLayoutResource = layoutResource
        mTextViewId = textViewResourceId
        if (items != null && !items.isEmpty()) listItems.addAll(items)
        mClickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleTextViewHolder {
        val vh = SimpleTextViewHolder(mInflater.inflate(mLayoutResource, parent, false), mTextViewId)
        vh.itemView.setOnClickListener(mClickListener)
        return vh
    }

    override fun onBindViewHolder(holder: SimpleTextViewHolder, position: Int) {
        val item = getItem(position)

        if (item is CharSequence) {
            holder.textView.text = item
        } else {
            holder.textView.text = item.toString()
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    val isEmpty: Boolean
        get() = itemCount <= 0

    /**
     * Adds an item to the list, [.notifyItemRangeInserted] will be called

     * @param items Object to add to the adapter
     */
    fun addItem(items: T) {
        listItems.add(items)
        notifyItemInserted(listItems.size)
    }

    /**
     * Adds an item to the list at the given position, [.notifyItemRangeInserted] will be called

     * @param items   Object to add to the adapter
     * *
     * @param position Position to add the object
     */
    fun addItem(items: T, position: Int) {
        listItems.add(position, items)
        notifyItemRangeInserted(position, 1)
    }

    /**
     * Adds a list of items to the adapter list, [.notifyItemRangeInserted] will be called

     * @param items List of items to add to the adapter
     */
    fun addItems(items: List<T>?) {
        if (items == null || items.isEmpty()) {
            return
        }

        val startingSize = listItems.size
        listItems.addAll(items)
        val endSize = listItems.size
        notifyItemRangeInserted(startingSize, endSize)
    }

    /**
     * Adds a list of items to the adapter list at the given position, [.notifyItemRangeInserted] will be called

     * @param items    List of items to add to the adapter
     * *
     * @param position The position to add the items into the adapter
     */
    fun addItems(items: List<T>?, position: Int) {
        if (items == null || items.isEmpty()) {
            return
        }

        listItems.addAll(position, items)
        notifyItemRangeInserted(position, items.size)
    }

    /**
     * Removes an object from the list, [.notifyItemRangeRemoved] (int, int)} will be called

     * @param item The object to remove from the adapter
     * *
     * @return If the object was removed
     */
    fun removeItem(item: T): Boolean {
        val position = listItems.indexOf(item)
        return position >= 0 && removeItem(position) != null
    }

    /**
     * Removes an item at the given position, [.notifyItemRemoved] will be called

     * @param position The position to remove from the adapter
     * *
     * @return The item removed
     */
    fun removeItem(position: Int): T? {
        val removedItem = listItems.removeAt(position)
        notifyItemRemoved(position)
        return removedItem
    }

    /**
     * Removes a range of items from the adapter, [.notifyItemRangeRemoved] will be called

     * @param start Starting position of removal
     * *
     * @param end   Ending position of removal
     */
    fun removeItems(start: Int, end: Int) {
        listItems.subList(start, end).clear()
        notifyItemRangeRemoved(start, end - start)
    }

    /**
     * Returns the index of the item in regards to the backing list. If not found, [RecyclerView.NO_POSITION] will be returned

     * @param item The object to search for
     * *
     * @return The index of the item. [RecyclerView.NO_POSITION] will be returned if not found
     */
    fun indexOf(item: T): Int {
        return if (!listItems.isEmpty()) listItems.indexOf(item) else RecyclerView.NO_POSITION
    }

    /**
     * Removes all items from the list, [.notifyItemRangeRemoved] will be called
     */
    fun clear() {
        val size = listItems.size
        listItems.clear()
        notifyItemRangeRemoved(0, size)
    }

    /**
     * Returns the entire list. This is ***not*** a copy of the list. If a copy of the list is
     * needed, see [.retainItems]

     * @return The entire list of items in the adapter
     */
    protected val allItems: List<T>
        get() = listItems

    /**
     * Returns the object for the given position

     * @param position The position to return
     * *
     * @return The item at the given position
     */
    fun getItem(position: Int): T {
        return listItems[position]
    }

    /**
     * Returns an ArrayList of the items in the adapter, used for saving the items for configuration changes

     * @return A copy of the items in the adapter
     */
    fun retainItems(): ArrayList<T> {
        return ArrayList(listItems)
    }

    class SimpleTextViewHolder(view: View, @IdRes textViewId: Int) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            if (textViewId == 0) {
                if (view is TextView) {
                    textView = view
                } else {
                    throw IllegalArgumentException("View is not a TextView")
                }
            } else {
                val viewById: View = view.findViewById(textViewId)

                if (viewById is TextView) {
                    textView = viewById
                } else {
                    throw IllegalArgumentException("View is not a TextView")
                }
            }
        }
    }
}
