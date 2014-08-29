package com.kimkha.finanvita.ui.transactions;

import android.content.Context;
import android.content.Intent;
import com.kimkha.finanvita.R;
import com.kimkha.finanvita.ui.StickyListActivity;
import com.kimkha.finanvita.ui.StickyListFragment;

public class TransactionListActivity extends StickyListActivity
{
    public static void start(Context context)
    {
        Intent intent = new Intent(context, TransactionListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected StickyListFragment createListFragment(int selectionType, long[] itemIDs)
    {
        return TransactionListFragment.newInstance(selectionType);
    }

    @Override
    protected String getActivityTitle()
    {
        return getString(R.string.transactions);
    }
}
