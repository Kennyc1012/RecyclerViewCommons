package com.kennyc.adapters

import android.content.Context
import android.content.res.Resources
import android.support.annotation.*
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.*


abstract class BaseRecyclerAdapter<T, VH : RecyclerView.ViewHolder>

/**
 * Simple constructor for creating a BaseRecyclerAdapter

 * @param context    The context the adapter is running in
 * *
 * @param collection A list of items to populate the adapter with, can be null.
 */
(context: Context, val collection: MutableList<T>?) : RecyclerView.Adapter<VH>() {
    private val items: ArrayList<T> = ArrayList()

    private var inflater: LayoutInflater?

    protected var resources: Resources?

    init {
        inflater = LayoutInflater.from(context)
        resources = context.resources
        if (collection != null && !collection.isEmpty()) items.addAll(collection)
    }

    /**
     * Adds an item to the list, [.notifyItemRangeInserted] will be called

     * @param item Object to add to the adapter
     */
    fun addItem(item: T) {
        items.add(item)
        notifyItemInserted(items.size)
    }

    /**
     * Adds an item to the list at the given position, [.notifyItemRangeInserted] will be called

     * @param item   Object to add to the adapter
     * *
     * @param position Position to add the object
     */
    fun addItem(item: T, position: Int) {
        items.add(position, item)
        notifyItemRangeInserted(position, 1)
    }

    /**
     * Adds a list of items to the adapter list, [.notifyItemRangeInserted] will be called

     * @param itemsToAdd List of items to add to the adapter
     */
    fun addItems(itemsToAdd: MutableList<T>?) {
        if (itemsToAdd == null || itemsToAdd.isEmpty()) {
            return
        }

        val startingSize = items.size
        items.addAll(itemsToAdd)
        val endSize = items.size
        notifyItemRangeInserted(startingSize, endSize)
    }

    /**
     * Adds a list of items to the adapter list at the given position, [.notifyItemRangeInserted] will be called

     * @param itemsToAdd    List of items to add to the adapter
     * *
     * @param position The position to add the items into the adapter
     */
    fun addItems(itemsToAdd: List<T>?, position: Int) {
        if (itemsToAdd == null || itemsToAdd.isEmpty()) {
            return
        }

        items.addAll(position, itemsToAdd)
        notifyItemRangeInserted(position, itemsToAdd.size)
    }

    /**
     * Removes an object from the list, [.notifyItemRangeRemoved] (int, int)} will be called

     * @param item The object to remove from the adapter
     * *
     * @return If the object was removed
     */
    fun removeItem(item: T): Boolean {
        val position = items.indexOf(item)
        return position >= 0 && removeItem(position) != null
    }

    /**
     * Removes an item at the given position, [.notifyItemRemoved] will be called

     * @param position The position to remove from the adapter
     * *
     * @return The item removed
     */
    fun removeItem(position: Int): T {
        val removedItem = items.removeAt(position)
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
        items.subList(start, end).clear()
        notifyItemRangeRemoved(start, end - start)
    }

    /**
     * Returns the index of the item in regards to the backing list. If not found, [RecyclerView.NO_POSITION] will be returned

     * @param item The object to search for
     * *
     * @return The index of the item. [RecyclerView.NO_POSITION] will be returned if not found
     */
    fun indexOf(item: T): Int {
        return if (!items.isEmpty()) items.indexOf(item) else RecyclerView.NO_POSITION
    }

    /**
     * Removes all items from the list, [.notifyItemRangeRemoved] will be called
     */
    fun clear() {
        val size = items.size
        items.clear()
        notifyItemRangeRemoved(0, size)
    }

    /**
     * Returns the entire list. This is ***not*** a copy of the list. If a copy of the list is
     * needed, see [.retainItems]

     * @return The entire list of items in the adapter
     */
    protected val allItems: List<T>
        get() = items

    /**
     * Returns the object for the given position

     * @param position The position to return
     * *
     * @return The item at the given position
     */
    fun getItem(position: Int): T {
        return items[position]
    }

    /**
     * Returns an ArrayList of the items in the adapter, used for saving the items for configuration changes

     * @return A copy of the items in the adapter
     */
    fun retainItems(): ArrayList<T> {
        return ArrayList(items)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * Returns if the adapter is empty

     * @return If the adapter is empty
     */
    val isEmpty: Boolean
        get() = itemCount <= 0

    /**
     * Returns the color for the given color resource

     * @param color Color resource id
     * *
     * @return
     */
    @ColorInt
    protected fun getColor(@ColorRes color: Int): Int {
        return resources!!.getColor(color)
    }

    /**
     * Returns the dimension for the given dimension resource

     * @param dimenRes
     * *
     * @return
     */
    protected fun getDimensionPixelSize(@DimenRes dimenRes: Int): Int {
        return resources!!.getDimensionPixelSize(dimenRes)
    }

    /**
     * Returns the [String] for the given string resource

     * @param string [String] resource
     * *
     * @return
     */
    protected fun getString(@StringRes string: Int): String {
        return resources!!.getString(string)
    }

    /**
     * Returns the [String] for the given string resource

     * @param string String resource
     * *
     * @param args   Format arguments for the [String]
     * *
     * @return
     */
    protected fun getString(@StringRes string: Int, vararg args: Any): String {
        return resources!!.getString(string, *args)
    }

    /**
     * Returns a [String] for a given plural resource

     * @param plural   Plural resource
     * *
     * @param quantity Quantity for the plural
     * *
     * @return
     */
    protected fun getQuanityString(@PluralsRes plural: Int, quantity: Int): String {
        return resources!!.getQuantityString(plural, quantity)
    }

    /**
     * Returns a [String] for a given plural resource

     * @param plural   Plural resource
     * *
     * @param quantity Quantity for the plural
     * *
     * @param args     Format arguments for the plural
     * *
     * @return
     */
    protected fun getQuanityString(@PluralsRes plural: Int, quantity: Int, vararg args: Any): String {
        return resources!!.getQuantityString(plural, quantity, *args)
    }

    /**
     * Inflates a view from the given layout resource

     * @param layoutId Layout resource to inflate
     * *
     * @param parent   Optional parent view
     * *
     * @return
     */
    protected fun inflateView(@LayoutRes layoutId: Int, parent: ViewGroup?): View {
        if (parent == null) {
            return inflater!!.inflate(layoutId, null)
        }

        return inflater!!.inflate(layoutId, parent, false)
    }

    /**
     * Frees up any resources tied to the adapter. Should be called in an activities onDestroy lifecycle method if needed
     */
    @CallSuper
    open fun onDestroy() {
        onDestroy(false)
    }

    /**
     * Frees up any resources tied to the adapter. Should be called in an activities onDestroy lifecycle method if needed

     * @param clearItems If the items in the adapter should be cleared
     */
    @CallSuper
    fun onDestroy(clearItems: Boolean) {
        resources = null
        inflater = null
        if (clearItems) items.clear()
    }
}