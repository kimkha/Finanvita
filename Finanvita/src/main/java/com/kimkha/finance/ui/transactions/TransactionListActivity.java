package com.kimkha.finance.ui.transactions;

import android.content.Context;
import android.content.Intent;
import com.kimkha.finance.R;
import com.kimkha.finance.ui.ItemListActivity;
import com.kimkha.finance.ui.ItemListFragment;
import com.kimkha.finance.ui.StickyListActivity;
import com.kimkha.finance.ui.StickyListFragment;

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
