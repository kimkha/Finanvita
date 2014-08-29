package com.kimkha.finanvita.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Nguyen Kim Kha on 8/23/14.
 *
 * @author Nguyen Kim Kha
 */
public abstract class AbstractStickyCursorAdapter extends AbstractCursorAdapter implements StickyListHeadersAdapter
{
    private List<String> allItems;

    private DataSetObserver mDataSetObserver = new DataSetObserver()
    {
        public void onChanged()
        {
            prepareIndexer(mCursor);
        }

        public void onInvalidated()
        {
            prepareIndexer(mCursor);
        }
    };

    public AbstractStickyCursorAdapter(Context context, Cursor c) {
        super(context, c);

        this.allItems = new ArrayList<String>();

        if (c != null)
        {
            c.registerDataSetObserver(mDataSetObserver);
            findIndexes(c);
        }
        prepareIndexer(c);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor)
    {
        if (getCursor() != null)
            getCursor().unregisterDataSetObserver(mDataSetObserver);

        final Cursor oldCursor = super.swapCursor(newCursor);

        if (newCursor != null)
        {
            findIndexes(newCursor);
            newCursor.registerDataSetObserver(mDataSetObserver);
        }
        prepareIndexer(newCursor);

        return oldCursor;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        View v;
        if (convertView == null) {
            v = newHeaderView(mContext, mCursor, parent);
        } else {
            v = convertView;
        }
        bindHeaderView(v, mContext, mCursor);
        return v;
    }

    @Override
    public long getHeaderId(int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        return getSectionUniqueId(mCursor);
    }

    protected abstract String getIndexColumnValue(Cursor c);

    protected abstract long getSectionUniqueId(Cursor c);

    protected abstract View newHeaderView(Context context, Cursor c, ViewGroup root);

    protected abstract void bindHeaderView(View view, Context context, Cursor c);

    private void prepareIndexer(Cursor c) {
        allItems.clear();

        if (c == null || !c.moveToFirst())
            return;

        String val;
        do {
            val = getIndexColumnValue(c);
            if (TextUtils.isEmpty(val))
                val = "";

            allItems.add(val);
        } while (c.moveToNext());
    }

}
