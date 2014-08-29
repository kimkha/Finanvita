package com.kimkha.finanvita.ui.accounts;

import android.content.Context;
import com.kimkha.finanvita.ui.ItemEditActivity;
import com.kimkha.finanvita.ui.ItemEditFragment;

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