package com.kimkha.finance.services;

import android.database.Cursor;

import com.kimkha.finance.R;
import com.kimkha.finance.db.Tables;
import com.kimkha.finance.providers.AccountsProvider;
import com.kimkha.finance.ui.MainActivity;
import com.kimkha.finance.utils.AmountUtils;
import com.kimkha.finance.utils.CurrencyHelper;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

public class DashClockService extends DashClockExtension
{
    @Override
    protected void onInitialize(boolean isReconnect)
    {
        super.onInitialize(isReconnect);
        addWatchContentUris(new String[]{AccountsProvider.uriAccounts().toString()});
    }

    @Override
    protected void onUpdateData(int reason)
    {
        final String[] projection = new String[]{Tables.Accounts.BALANCE};
        final String selection = Tables.Accounts.ORIGIN + "<>? and " + Tables.Accounts.DELETE_STATE + "<>? and " + Tables.Accounts.SHOW_IN_TOTALS + "=?";
        final String[] selectionArgs = new String[]{String.valueOf(Tables.Categories.Origin.SYSTEM), String.valueOf(Tables.DeleteState.DELETED), "1"};
        double balance = 0.0;
        Cursor c = null;
        try
        {
            c = getContentResolver().query(AccountsProvider.uriAccounts(), projection, selection, selectionArgs, null);

            if (c != null && c.moveToFirst())
            {
                final int iBalance = c.getColumnIndex(Tables.Accounts.BALANCE);
                do
                {
                    balance += c.getDouble(iBalance);
                }
                while (c.moveToNext());
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }

        // Publish the extension data update.
        publishUpdate(new ExtensionData().visible(true).icon(R.drawable.ic_launcher).status(AmountUtils.formatAmount(balance))
                                         .expandedTitle(getResources().getString(R.string.balance) + ": " + AmountUtils.formatAmount(CurrencyHelper.get().getMainCurrencyId(), balance))
                                         .clickIntent(MainActivity.makeIntent(this)));
    }
}