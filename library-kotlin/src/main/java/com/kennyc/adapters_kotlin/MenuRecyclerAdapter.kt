package com.kennyc.adapters_kotlin

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.annotation.MenuRes
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by Kenny-PC on 9/4/2017.
 */
class MenuRecyclerAdapter(context: Context, @MenuRes menuRes: Int, clickListener: View.OnClickListener?) : BaseRecyclerAdapter<MenuItem, MenuRecyclerAdapter.MenuHolder>(context, null) {
    private var clickListener: View.OnClickListener? = null

    init {
        val inflater = MenuInflater(context)
        val menu = RecyclerMenu(context)
        inflater.inflate(menuRes, menu)
        val items = ArrayList<MenuItem>(menu.size())

        for (i in 0..menu.size() - 1) {
            items.add(menu.getItem(i))
        }

        addItems(items)
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MenuHolder {
        val holder = MenuHolder(inflateView(R.layout.rv_menu_item, parent))
        holder.itemView.setOnClickListener(clickListener)
        return holder
    }

    override fun onBindViewHolder(holder: MenuHolder, position: Int) {
        val item = getItem(position)
        holder.icon?.setImageDrawable(item.icon)
        holder.title.setText(item.title)
    }

    override fun onDestroy() {
        clickListener = null
        super.onDestroy()
    }

    class MenuHolder(view: View) : RecyclerView.ViewHolder(view) {
        var icon: ImageView? = view.findViewById<View>(android.R.id.icon) as ImageView

        val title = view.findViewById<View>(android.R.id.title) as TextView
    }

    private class RecyclerMenu(val context: Context) : Menu {

        private var mIsQwerty: Boolean = false

        private val mItems: java.util.ArrayList<RecyclerMenuItem>

        init {
            mItems = java.util.ArrayList()
        }

        override fun add(title: CharSequence): MenuItem {
            return add(0, 0, 0, title)
        }

        override fun add(titleRes: Int): MenuItem {
            return add(0, 0, 0, titleRes)
        }

        override fun add(groupId: Int, itemId: Int, order: Int, titleRes: Int): MenuItem {
            return add(groupId, itemId, order, context.resources.getString(titleRes))
        }

        override fun add(groupId: Int, itemId: Int, order: Int, title: CharSequence): MenuItem {
            val item = RecyclerMenuItem(context, groupId, itemId, 0, order, title)
            // TODO Order is ignored here.
            mItems.add(item)
            return item
        }

        override fun addIntentOptions(groupId: Int, itemId: Int, order: Int,
                                      caller: ComponentName, specifics: Array<Intent>, intent: Intent, flags: Int,
                                      outSpecificItems: Array<MenuItem>?): Int {
            val pm = context.packageManager
            val lri = pm.queryIntentActivityOptions(caller, specifics, intent, 0)
            val N = lri?.size ?: 0

            if (flags and Menu.FLAG_APPEND_TO_GROUP == 0) {
                removeGroup(groupId)
            }

            for (i in 0..N - 1) {
                val ri = lri!![i]
                val rintent = Intent(
                        if (ri.specificIndex < 0) intent else specifics[ri.specificIndex])
                rintent.component = ComponentName(
                        ri.activityInfo.applicationInfo.packageName,
                        ri.activityInfo.name)
                val item = add(groupId, itemId, order, ri.loadLabel(pm))
                        .setIcon(ri.loadIcon(pm))
                        .setIntent(rintent)
                if (outSpecificItems != null && ri.specificIndex >= 0) {
                    outSpecificItems[ri.specificIndex] = item
                }
            }

            return N
        }

        override fun addSubMenu(title: CharSequence): SubMenu? {
            // TODO Implement submenus
            return null
        }

        override fun addSubMenu(titleRes: Int): SubMenu? {
            // TODO Implement submenus
            return null
        }

        override fun addSubMenu(groupId: Int, itemId: Int, order: Int,
                                title: CharSequence): SubMenu? {
            // TODO Implement submenus
            return null
        }

        override fun addSubMenu(groupId: Int, itemId: Int, order: Int, titleRes: Int): SubMenu? {
            // TODO Implement submenus
            return null
        }

        override fun clear() {
            mItems.clear()
        }

        override fun close() {}

        private fun findItemIndex(id: Int): Int {
            val items = mItems
            val itemCount = items.size
            for (i in 0..itemCount - 1) {
                if (items[i].itemId == id) {
                    return i
                }
            }

            return -1
        }

        override fun findItem(id: Int): MenuItem {
            return mItems[findItemIndex(id)]
        }

        override fun getItem(index: Int): MenuItem {
            return mItems[index]
        }

        override fun hasVisibleItems(): Boolean {
            val items = mItems
            val itemCount = items.size

            for (i in 0..itemCount - 1) {
                if (items[i].isVisible) {
                    return true
                }
            }

            return false
        }

        private fun findItemWithShortcut(keyCode: Int, event: KeyEvent): RecyclerMenuItem? {
            // TODO Make this smarter.
            val qwerty = mIsQwerty
            val items = mItems
            val itemCount = items.size

            for (i in 0..itemCount - 1) {
                val item = items[i]
                val shortcut = if (qwerty)
                    item.alphabeticShortcut
                else
                    item.numericShortcut
                if (keyCode == shortcut.toInt()) {
                    return item
                }
            }
            return null
        }

        override fun isShortcutKey(keyCode: Int, event: KeyEvent): Boolean {
            return findItemWithShortcut(keyCode, event) != null
        }

        override fun performIdentifierAction(id: Int, flags: Int): Boolean {
            val index = findItemIndex(id)
            return if (index < 0) {
                false
            } else mItems[index].invoke()

        }

        override fun performShortcut(keyCode: Int, event: KeyEvent, flags: Int): Boolean {
            val item = findItemWithShortcut(keyCode, event) ?: return false

            return item.invoke()
        }

        override fun removeGroup(groupId: Int) {
            val items = mItems
            var itemCount = items.size
            var i = 0
            while (i < itemCount) {
                if (items[i].groupId == groupId) {
                    items.removeAt(i)
                    itemCount--
                } else {
                    i++
                }
            }
        }

        override fun removeItem(id: Int) {
            mItems.removeAt(findItemIndex(id))
        }

        override fun setGroupCheckable(group: Int, checkable: Boolean,
                                       exclusive: Boolean) {
            val items = mItems
            val itemCount = items.size

            for (i in 0..itemCount - 1) {
                val item = items[i]
                if (item.groupId == group) {
                    item.isCheckable = checkable
                    item.setExclusiveCheckable(exclusive)
                }
            }
        }

        override fun setGroupEnabled(group: Int, enabled: Boolean) {
            val items = mItems
            val itemCount = items.size

            for (i in 0..itemCount - 1) {
                val item = items[i]
                if (item.groupId == group) {
                    item.isEnabled = enabled
                }
            }
        }

        override fun setGroupVisible(group: Int, visible: Boolean) {
            val items = mItems
            val itemCount = items.size

            for (i in 0..itemCount - 1) {
                val item = items[i]
                if (item.groupId == group) {
                    item.isVisible = visible
                }
            }
        }

        override fun setQwertyMode(isQwerty: Boolean) {
            mIsQwerty = isQwerty
        }

        override fun size(): Int {
            return mItems.size
        }
    }

    private class RecyclerMenuItem
    (private val mContext: Context, private val mGroup: Int, private val mId: Int, private val mCategoryOrder: Int, private val mOrdering: Int, private var mTitle: CharSequence?) : MenuItem {

        private var mTitleCondensed: CharSequence? = null

        private var mIntent: Intent? = null

        private var mShortcutNumericChar: Char = ' '

        private var mShortcutAlphabeticChar: Char = ' '

        private var mIconDrawable: Drawable? = null

        private var mIconResId = NO_ICON

        private var mClickListener: MenuItem.OnMenuItemClickListener? = null

        private var mFlags = ENABLED

        override fun getMenuInfo(): ContextMenu.ContextMenuInfo? {
            return null
        }

        override fun getAlphabeticShortcut(): Char {
            return mShortcutAlphabeticChar
        }

        override fun getGroupId(): Int {
            return mGroup
        }

        override fun getIcon(): Drawable? {
            return mIconDrawable
        }

        override fun getIntent(): Intent? {
            return mIntent
        }

        override fun getItemId(): Int {
            return mId
        }

        override fun getNumericShortcut(): Char {
            return mShortcutNumericChar
        }

        override fun getOrder(): Int {
            return mOrdering
        }

        override fun getSubMenu(): SubMenu? {
            return null
        }

        override fun getTitle(): CharSequence? {
            return mTitle
        }

        override fun getTitleCondensed(): CharSequence? {
            return if (mTitleCondensed != null) mTitleCondensed else mTitle
        }

        override fun hasSubMenu(): Boolean {
            return false
        }

        override fun isCheckable(): Boolean {
            return mFlags and CHECKABLE != 0
        }

        override fun isChecked(): Boolean {
            return mFlags and CHECKED != 0
        }

        override fun isEnabled(): Boolean {
            return mFlags and ENABLED != 0
        }

        override fun isVisible(): Boolean {
            return mFlags and HIDDEN == 0
        }

        override fun setAlphabeticShortcut(alphaChar: Char): MenuItem {
            mShortcutAlphabeticChar = alphaChar
            return this
        }

        override fun setCheckable(checkable: Boolean): MenuItem {
            mFlags = mFlags and CHECKABLE.inv() or if (checkable) CHECKABLE else 0
            return this
        }

        fun setExclusiveCheckable(exclusive: Boolean): RecyclerMenuItem {
            mFlags = mFlags and EXCLUSIVE.inv() or if (exclusive) EXCLUSIVE else 0
            return this
        }

        override fun setChecked(checked: Boolean): MenuItem {
            mFlags = mFlags and CHECKED.inv() or if (checked) CHECKED else 0
            return this
        }

        override fun setEnabled(enabled: Boolean): MenuItem {
            mFlags = mFlags and ENABLED.inv() or if (enabled) ENABLED else 0
            return this
        }

        override fun setIcon(icon: Drawable): MenuItem {
            mIconDrawable = icon
            mIconResId = NO_ICON
            return this
        }

        override fun setIcon(iconRes: Int): MenuItem {
            if (iconRes != NO_ICON) {
                mIconResId = iconRes
                mIconDrawable = mContext.resources.getDrawable(iconRes)
            }

            return this
        }

        override fun setIntent(intent: Intent): MenuItem {
            mIntent = intent
            return this
        }

        override fun setNumericShortcut(numericChar: Char): MenuItem {
            mShortcutNumericChar = numericChar
            return this
        }

        override fun setOnMenuItemClickListener(menuItemClickListener: MenuItem.OnMenuItemClickListener): MenuItem {
            mClickListener = menuItemClickListener
            return this
        }

        override fun setShortcut(numericChar: Char, alphaChar: Char): MenuItem {
            mShortcutNumericChar = numericChar
            mShortcutAlphabeticChar = alphaChar
            return this
        }

        override fun setTitle(title: CharSequence): MenuItem {
            mTitle = title
            return this
        }

        override fun setTitle(title: Int): MenuItem {
            mTitle = mContext.resources.getString(title)
            return this
        }

        override fun setTitleCondensed(title: CharSequence?): MenuItem {
            mTitleCondensed = title
            return this
        }

        override fun setVisible(visible: Boolean): MenuItem {
            mFlags = mFlags and HIDDEN or if (visible) 0 else HIDDEN
            return this
        }

        operator fun invoke(): Boolean {
            if (mClickListener != null && mClickListener!!.onMenuItemClick(this)) {
                return true
            }

            if (mIntent != null) {
                mContext.startActivity(mIntent)
                return true
            }

            return false
        }

        override fun setShowAsAction(show: Int) {
            // Do nothing. ActionMenuItems always show as action buttons.
        }

        override fun setActionView(actionView: View): MenuItem {
            throw UnsupportedOperationException()
        }

        override fun getActionView(): View? {
            return null
        }

        override fun setActionView(resId: Int): MenuItem {
            throw UnsupportedOperationException()
        }

        override fun getActionProvider(): ActionProvider? {
            return null
        }

        override fun setActionProvider(actionProvider: ActionProvider): MenuItem {
            throw UnsupportedOperationException()
        }

        override fun setShowAsActionFlags(actionEnum: Int): MenuItem {
            setShowAsAction(actionEnum)
            return this
        }

        override fun expandActionView(): Boolean {
            return false
        }

        override fun collapseActionView(): Boolean {
            return false
        }

        override fun isActionViewExpanded(): Boolean {
            return false
        }

        override fun setOnActionExpandListener(listener: MenuItem.OnActionExpandListener): MenuItem {
            // No need to save the listener; ActionMenuItem does not support collapsing items.
            return this
        }

        companion object {

            private val NO_ICON = 0

            private val CHECKABLE = 0x00000001

            private val CHECKED = 0x00000002

            private val EXCLUSIVE = 0x00000004

            private val HIDDEN = 0x00000008

            private val ENABLED = 0x00000010
        }
    }
}