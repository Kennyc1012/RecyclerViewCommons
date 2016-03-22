package com.kennyc.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kcampagna on 3/16/16.
 */
public abstract class CursorRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected final String TAG = getClass().getSimpleName();

    private LayoutInflater mInflater;

    @Nullable
    private Cursor mCursor;

    public CursorRecyclerAdapter(@NonNull Context context, @Nullable Cursor cursor) {
        mInflater = LayoutInflater.from(context);
        mCursor = cursor;
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    /**
     * Replaces the cursor of the adapter. Null is allowed
     *
     * @param newCursor
     */
    public void swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) return;

        Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if (oldCursor != null) oldCursor.close();
    }

    public boolean isEmpty() {
        return getItemCount() <= 0;
    }

    /**
     * Moves the cursor to the given position
     *
     * @param position
     * @return If the cursor was successfully moved to the desired position
     */
    protected boolean moveToPosition(int position) {
        if (mCursor != null) {
            return mCursor.moveToPosition(position);
        }

        return false;
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
}
