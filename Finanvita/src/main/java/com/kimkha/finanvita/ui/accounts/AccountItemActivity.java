package com.kimkha.finanvita.ui.accounts;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.kimkha.finanvita.R;
import com.kimkha.finanvita.ui.ItemActivity;
import com.kimkha.finanvita.ui.ItemFragment;

public class AccountItemActivity extends ItemActivity
{
    public static void startItem(Context context, long itemId, View expandFrom)
    {
        final Intent intent = makeIntent(context, AccountItemActivity.class, itemId);
        start(context, intent, expandFrom);
    }

    @Override
    protected ItemFragment createItemFragment(long itemId)
    {
        return AccountItemFragment.newInstance(itemId);
    }

    @Override
    protected String getActivityTitle()
    {
        return getString(R.string.account);
    }
}
