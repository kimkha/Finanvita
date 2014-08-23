package com.kimkha.finance.ui.currencies;

import android.content.Context;
import com.kimkha.finance.ui.ItemEditActivity;
import com.kimkha.finance.ui.ItemEditFragment;

public class CurrencyEditActivity extends ItemEditActivity
{
    public static void startItemEdit(Context context, long itemId)
    {
        start(context, makeIntent(context, CurrencyEditActivity.class, itemId), null);
    }

    @Override
    protected ItemEditFragment createItemEditFragment(long itemId)
    {
        return CurrencyEditFragment.newInstance(itemId);
    }
}
