package com.kimkha.finanvita.ui.transactions;

import android.content.Context;
import android.view.View;
import com.kimkha.finanvita.R;
import com.kimkha.finanvita.ui.ItemActivity;
import com.kimkha.finanvita.ui.ItemFragment;

public class TransactionItemActivity extends ItemActivity
{
    public static void startItem(Context context, long itemId, View expandFrom)
    {
        start(context, makeIntent(context, TransactionItemActivity.class, itemId), expandFrom);
    }

    @Override
    protected ItemFragment createItemFragment(long itemId)
    {
        return TransactionItemFragment.newInstance(itemId);
    }

    @Override
    protected String getActivityTitle()
    {
        return getString(R.string.transaction);
    }
}