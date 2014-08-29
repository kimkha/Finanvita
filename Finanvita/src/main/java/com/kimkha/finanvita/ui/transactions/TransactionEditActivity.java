package com.kimkha.finanvita.ui.transactions;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.kimkha.finanvita.ui.ItemEditActivity;
import com.kimkha.finanvita.ui.ItemEditFragment;

public class TransactionEditActivity extends ItemEditActivity
{
    public static void startItemEdit(Context context, long itemId, View expandFromView)
    {
        final Intent intent = makeIntent(context, TransactionEditActivity.class, itemId);
        start(context, intent, expandFromView);
    }

    @Override
    protected ItemEditFragment createItemEditFragment(long itemId)
    {
        return TransactionEditFragment.newInstance(itemId);
    }
}