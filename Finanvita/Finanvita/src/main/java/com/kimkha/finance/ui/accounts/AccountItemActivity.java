package com.kimkha.finance.ui.accounts;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.kimkha.finance.R;
import com.kimkha.finance.ui.ItemActivity;
import com.kimkha.finance.ui.ItemFragment;

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
