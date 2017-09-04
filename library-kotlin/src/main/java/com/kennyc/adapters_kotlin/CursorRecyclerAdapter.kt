package com.kennyc.adapters_kotlin

import android.content.Context
import android.database.Cursor
import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by Kenny-PC on 9/4/2017.
 */
abstract class CursorRecyclerAdapter<VH : RecyclerView.ViewHolder>(val context: Context, var dataCursor: Cursor) : RecyclerView.Adapter<VH>() {

    var layoutInflater: LayoutInflater? = LayoutInflater.from(context)
    var cursor: Cursor? = dataCursor

    override fun getItemCount(): Int {
        if (cursor != null) return cursor!!.count
        return 0
    }

    /**
     * Replaces the cursor of the adapter. Null is allowed
     *
     * @param newCursor
     */
    fun swapCursor(newCursor: Cursor) {
        if (newCursor === cursor) return

        val oldCursor = cursor
        cursor = newCursor
        oldCursor?.close()
        notifyDataSetChanged()
    }

    /**
     * Returns if the adapter is empty
     *
     * @return
     */
    fun isEmpty(): Boolean {
        return itemCount <= 0
    }

    /**
     * Moves the cursor to the given position
     *
     * @param position
     * @return If the cursor was successfully moved to the desired position
     */
    protected fun moveToPosition(position: Int): Boolean {
        return if (cursor != null) {
            cursor!!.moveToPosition(position)
        } else false

    }

    /**
     * Inflates a view from the given layout resource
     *
     * @param layoutId Layout resource to inflate
     * @param parent   Optional parent view
     * @return
     */
    protected fun inflateView(@LayoutRes layoutId: Int, parent: ViewGroup?): View {
        return if (parent == null) {
            layoutInflater!!.inflate(layoutId, null)
        } else layoutInflater!!.inflate(layoutId, parent, false)

    }

    @CallSuper
    fun onDestroy() {
        layoutInflater = null
    }
}