package com.kennyc.adapters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MenuRecyclerAdapter extends BaseRecyclerAdapter<MenuItem, MenuRecyclerAdapter.MenuHolder> {

    @Nullable
    private View.OnClickListener clickListener;

    public MenuRecyclerAdapter(@NonNull Context context, @MenuRes int menuRes, @Nullable View.OnClickListener clickListener) {
        super(context, null);
        MenuInflater inflater = new MenuInflater(context);
        Menu menu = new RecyclerMenu(context);
        inflater.inflate(menuRes, menu);
        ArrayList<MenuItem> items = new ArrayList<>(menu.size());

        for (int i = 0; i < menu.size(); i++) {
            items.add(menu.getItem(i));
        }

        addItems(items);
        this.clickListener = clickListener;
    }

    public MenuRecyclerAdapter(@NonNull Context context, @Nullable List<MenuItem> items, @Nullable View.OnClickListener clickListener) {
        super(context, items);
        this.clickListener = clickListener;
    }

    @Override
    public MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MenuHolder holder = new MenuHolder(inflateView(R.layout.rv_menu_item, parent));
        holder.itemView.setOnClickListener(clickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(MenuHolder holder, int position) {
        MenuItem item = getItem(position);
        if (item.getIcon() != null) holder.icon.setImageDrawable(item.getIcon());
        holder.title.setText(item.getTitle());
    }

    @Override
    public void onDestroy() {
        clickListener = null;
        super.onDestroy();
    }

    protected static class MenuHolder extends RecyclerView.ViewHolder {

        protected ImageView icon;

        protected TextView title;

        public MenuHolder(@NonNull View view) {
            super(view);
            icon = (ImageView) view.findViewById(android.R.id.icon);
            title = (TextView) view.findViewById(android.R.id.title);
        }
    }

    private static class RecyclerMenu implements Menu {
        private Context mContext;

        private boolean mIsQwerty;

        private ArrayList<RecyclerMenuItem> mItems;

        public RecyclerMenu(Context context) {
            mContext = context;
            mItems = new ArrayList<>();
        }

        public Context getContext() {
            return mContext;
        }

        public MenuItem add(CharSequence title) {
            return add(0, 0, 0, title);
        }

        public MenuItem add(int titleRes) {
            return add(0, 0, 0, titleRes);
        }

        public MenuItem add(int groupId, int itemId, int order, int titleRes) {
            return add(groupId, itemId, order, mContext.getResources().getString(titleRes));
        }

        public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
            RecyclerMenuItem item = new RecyclerMenuItem(getContext(), groupId, itemId, 0, order, title);
            // TODO Order is ignored here.
            mItems.add(item);
            return item;
        }

        public int addIntentOptions(int groupId, int itemId, int order,
                                    ComponentName caller, Intent[] specifics, Intent intent, int flags,
                                    MenuItem[] outSpecificItems) {
            PackageManager pm = mContext.getPackageManager();
            final List<ResolveInfo> lri =
                    pm.queryIntentActivityOptions(caller, specifics, intent, 0);
            final int N = lri != null ? lri.size() : 0;

            if ((flags & FLAG_APPEND_TO_GROUP) == 0) {
                removeGroup(groupId);
            }

            for (int i = 0; i < N; i++) {
                final ResolveInfo ri = lri.get(i);
                Intent rintent = new Intent(
                        ri.specificIndex < 0 ? intent : specifics[ri.specificIndex]);
                rintent.setComponent(new ComponentName(
                        ri.activityInfo.applicationInfo.packageName,
                        ri.activityInfo.name));
                final MenuItem item = add(groupId, itemId, order, ri.loadLabel(pm))
                        .setIcon(ri.loadIcon(pm))
                        .setIntent(rintent);
                if (outSpecificItems != null && ri.specificIndex >= 0) {
                    outSpecificItems[ri.specificIndex] = item;
                }
            }

            return N;
        }

        public SubMenu addSubMenu(CharSequence title) {
            // TODO Implement submenus
            return null;
        }

        public SubMenu addSubMenu(int titleRes) {
            // TODO Implement submenus
            return null;
        }

        public SubMenu addSubMenu(int groupId, int itemId, int order,
                                  CharSequence title) {
            // TODO Implement submenus
            return null;
        }

        public SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes) {
            // TODO Implement submenus
            return null;
        }

        public void clear() {
            mItems.clear();
        }

        public void close() {
        }

        private int findItemIndex(int id) {
            final ArrayList<RecyclerMenuItem> items = mItems;
            final int itemCount = items.size();
            for (int i = 0; i < itemCount; i++) {
                if (items.get(i).getItemId() == id) {
                    return i;
                }
            }

            return -1;
        }

        public MenuItem findItem(int id) {
            return mItems.get(findItemIndex(id));
        }

        public MenuItem getItem(int index) {
            return mItems.get(index);
        }

        public boolean hasVisibleItems() {
            final ArrayList<RecyclerMenuItem> items = mItems;
            final int itemCount = items.size();

            for (int i = 0; i < itemCount; i++) {
                if (items.get(i).isVisible()) {
                    return true;
                }
            }

            return false;
        }

        private RecyclerMenuItem findItemWithShortcut(int keyCode, KeyEvent event) {
            // TODO Make this smarter.
            final boolean qwerty = mIsQwerty;
            final ArrayList<RecyclerMenuItem> items = mItems;
            final int itemCount = items.size();

            for (int i = 0; i < itemCount; i++) {
                RecyclerMenuItem item = items.get(i);
                final char shortcut = qwerty ? item.getAlphabeticShortcut() :
                        item.getNumericShortcut();
                if (keyCode == shortcut) {
                    return item;
                }
            }
            return null;
        }

        public boolean isShortcutKey(int keyCode, KeyEvent event) {
            return findItemWithShortcut(keyCode, event) != null;
        }

        public boolean performIdentifierAction(int id, int flags) {
            final int index = findItemIndex(id);
            if (index < 0) {
                return false;
            }

            return mItems.get(index).invoke();
        }

        public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
            RecyclerMenuItem item = findItemWithShortcut(keyCode, event);
            if (item == null) {
                return false;
            }

            return item.invoke();
        }

        public void removeGroup(int groupId) {
            final ArrayList<RecyclerMenuItem> items = mItems;
            int itemCount = items.size();
            int i = 0;
            while (i < itemCount) {
                if (items.get(i).getGroupId() == groupId) {
                    items.remove(i);
                    itemCount--;
                } else {
                    i++;
                }
            }
        }

        public void removeItem(int id) {
            mItems.remove(findItemIndex(id));
        }

        public void setGroupCheckable(int group, boolean checkable,
                                      boolean exclusive) {
            final ArrayList<RecyclerMenuItem> items = mItems;
            final int itemCount = items.size();

            for (int i = 0; i < itemCount; i++) {
                RecyclerMenuItem item = items.get(i);
                if (item.getGroupId() == group) {
                    item.setCheckable(checkable);
                    item.setExclusiveCheckable(exclusive);
                }
            }
        }

        public void setGroupEnabled(int group, boolean enabled) {
            final ArrayList<RecyclerMenuItem> items = mItems;
            final int itemCount = items.size();

            for (int i = 0; i < itemCount; i++) {
                RecyclerMenuItem item = items.get(i);
                if (item.getGroupId() == group) {
                    item.setEnabled(enabled);
                }
            }
        }

        public void setGroupVisible(int group, boolean visible) {
            final ArrayList<RecyclerMenuItem> items = mItems;
            final int itemCount = items.size();

            for (int i = 0; i < itemCount; i++) {
                RecyclerMenuItem item = items.get(i);
                if (item.getGroupId() == group) {
                    item.setVisible(visible);
                }
            }
        }

        public void setQwertyMode(boolean isQwerty) {
            mIsQwerty = isQwerty;
        }

        public int size() {
            return mItems.size();
        }
    }

    private static class RecyclerMenuItem implements MenuItem {
        private final int mId;

        private final int mGroup;

        private final int mCategoryOrder;

        private final int mOrdering;

        private CharSequence mTitle;

        private CharSequence mTitleCondensed;

        private Intent mIntent;

        private char mShortcutNumericChar;

        private char mShortcutAlphabeticChar;

        private Drawable mIconDrawable;

        private int mIconResId = NO_ICON;

        private Context mContext;

        private MenuItem.OnMenuItemClickListener mClickListener;

        private static final int NO_ICON = 0;

        private int mFlags = ENABLED;

        private static final int CHECKABLE = 0x00000001;

        private static final int CHECKED = 0x00000002;

        private static final int EXCLUSIVE = 0x00000004;

        private static final int HIDDEN = 0x00000008;

        private static final int ENABLED = 0x00000010;

        /**
         * Creates a MenuItem
         *
         * @param context       Context of the MenuItem
         * @param group         Group id of the MenuItem
         * @param id            Id of the MenuItem
         * @param categoryOrder Category order of the MenuItem
         * @param ordering      Ordering of the MenuItem
         * @param title         Title of the MenuItem
         */
        public RecyclerMenuItem(Context context, int group, int id, int categoryOrder, int ordering, CharSequence title) {
            mContext = context;
            mId = id;
            mGroup = group;
            mCategoryOrder = categoryOrder;
            mOrdering = ordering;
            mTitle = title;
        }

        @Override
        public ContextMenu.ContextMenuInfo getMenuInfo() {
            return null;
        }

        public char getAlphabeticShortcut() {
            return mShortcutAlphabeticChar;
        }

        public int getGroupId() {
            return mGroup;
        }

        public Drawable getIcon() {
            return mIconDrawable;
        }

        public Intent getIntent() {
            return mIntent;
        }

        public int getItemId() {
            return mId;
        }

        public char getNumericShortcut() {
            return mShortcutNumericChar;
        }

        public int getOrder() {
            return mOrdering;
        }

        public SubMenu getSubMenu() {
            return null;
        }

        public CharSequence getTitle() {
            return mTitle;
        }

        public CharSequence getTitleCondensed() {
            return mTitleCondensed != null ? mTitleCondensed : mTitle;
        }

        public boolean hasSubMenu() {
            return false;
        }

        public boolean isCheckable() {
            return (mFlags & CHECKABLE) != 0;
        }

        public boolean isChecked() {
            return (mFlags & CHECKED) != 0;
        }

        public boolean isEnabled() {
            return (mFlags & ENABLED) != 0;
        }

        public boolean isVisible() {
            return (mFlags & HIDDEN) == 0;
        }

        public MenuItem setAlphabeticShortcut(char alphaChar) {
            mShortcutAlphabeticChar = alphaChar;
            return this;
        }

        public MenuItem setCheckable(boolean checkable) {
            mFlags = (mFlags & ~CHECKABLE) | (checkable ? CHECKABLE : 0);
            return this;
        }

        public RecyclerMenuItem setExclusiveCheckable(boolean exclusive) {
            mFlags = (mFlags & ~EXCLUSIVE) | (exclusive ? EXCLUSIVE : 0);
            return this;
        }

        public MenuItem setChecked(boolean checked) {
            mFlags = (mFlags & ~CHECKED) | (checked ? CHECKED : 0);
            return this;
        }

        public MenuItem setEnabled(boolean enabled) {
            mFlags = (mFlags & ~ENABLED) | (enabled ? ENABLED : 0);
            return this;
        }

        public MenuItem setIcon(Drawable icon) {
            mIconDrawable = icon;
            mIconResId = NO_ICON;
            return this;
        }

        public MenuItem setIcon(int iconRes) {
            if (iconRes != NO_ICON) {
                mIconResId = iconRes;
                mIconDrawable = mContext.getResources().getDrawable(iconRes);
            }

            return this;
        }

        public MenuItem setIntent(Intent intent) {
            mIntent = intent;
            return this;
        }

        public MenuItem setNumericShortcut(char numericChar) {
            mShortcutNumericChar = numericChar;
            return this;
        }

        public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
            mClickListener = menuItemClickListener;
            return this;
        }

        public MenuItem setShortcut(char numericChar, char alphaChar) {
            mShortcutNumericChar = numericChar;
            mShortcutAlphabeticChar = alphaChar;
            return this;
        }

        public MenuItem setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        public MenuItem setTitle(int title) {
            mTitle = mContext.getResources().getString(title);
            return this;
        }

        public MenuItem setTitleCondensed(CharSequence title) {
            mTitleCondensed = title;
            return this;
        }

        public MenuItem setVisible(boolean visible) {
            mFlags = (mFlags & HIDDEN) | (visible ? 0 : HIDDEN);
            return this;
        }

        public boolean invoke() {
            if (mClickListener != null && mClickListener.onMenuItemClick(this)) {
                return true;
            }

            if (mIntent != null) {
                mContext.startActivity(mIntent);
                return true;
            }

            return false;
        }

        public void setShowAsAction(int show) {
            // Do nothing. ActionMenuItems always show as action buttons.
        }

        public MenuItem setActionView(View actionView) {
            throw new UnsupportedOperationException();
        }

        public View getActionView() {
            return null;
        }

        @Override
        public MenuItem setActionView(int resId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ActionProvider getActionProvider() {
            return null;
        }

        @Override
        public MenuItem setActionProvider(ActionProvider actionProvider) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MenuItem setShowAsActionFlags(int actionEnum) {
            setShowAsAction(actionEnum);
            return this;
        }

        @Override
        public boolean expandActionView() {
            return false;
        }

        @Override
        public boolean collapseActionView() {
            return false;
        }

        @Override
        public boolean isActionViewExpanded() {
            return false;
        }

        @Override
        public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
            // No need to save the listener; ActionMenuItem does not support collapsing items.
            return this;
        }
    }
}
