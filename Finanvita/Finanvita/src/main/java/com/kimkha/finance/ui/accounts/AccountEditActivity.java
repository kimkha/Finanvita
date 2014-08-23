package com.kimkha.finance.ui.accounts;

import android.content.Context;
import com.kimkha.finance.ui.ItemEditActivity;
import com.kimkha.finance.ui.ItemEditFragment;

public class AccountEditActivity extends ItemEditActivity
{
    public static void startItemEdit(Context context, long itemId)
    {
        start(context, makeIntent(context, AccountEditActivity.class, itemId), null);
    }

    @Override
    protected ItemEditFragment createItemEditFragment(long itemId)
    {
        return AccountEditFragment.newInstance(itemId);
    }
}