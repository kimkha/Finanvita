package com.kimkha.finanvita.ui.accounts;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import com.kimkha.finanvita.R;
import com.kimkha.finanvita.ui.ItemListActivity;
import com.kimkha.finanvita.ui.ItemListFragment;

@SuppressWarnings("UnusedDeclaration")
public class AccountListActivity extends ItemListActivity
{
    public static void startList(Context context)
    {
        Intent intent = makeIntent(context, AccountListActivity.class);
        context.startActivity(intent);
    }

    public static void startListSelection(Context context, Fragment fragment, int requestCode)
    {
        final Intent intent = makeIntent(context, AccountListActivity.class);
        startForSelect(fragment, intent, requestCode);
    }

    @Override
    protected ItemListFragment createListFragment(int selectionType, long[] itemIDs)
    {
        return AccountListFragment.newInstance(selectionType);
    }

    @Override
    protected String getActivityTitle()
    {
        return getString(R.string.accounts);
    }
}
