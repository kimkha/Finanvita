package com.kimkha.finance.ui.transactions;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.kimkha.finance.R;
import com.kimkha.finance.adapters.AbstractCursorAdapter;
import com.kimkha.finance.adapters.TransactionsAdapter;
import com.kimkha.finance.db.Tables;
import com.kimkha.finance.providers.TransactionsProvider;
import com.kimkha.finance.ui.ItemListFragment;
import com.kimkha.finance.ui.MainActivity;
import com.kimkha.finance.utils.FilterHelper;

import org.joda.time.format.DateTimeFormatterBuilder;

import de.greenrobot.event.EventBus;

public class TransactionListFragment extends ItemListFragment implements MainActivity.NavigationContentFragment, AbsListView.OnScrollListener {
    private TextView month_TV;
    private TransactionsAdapter transactionAdapter;
    private View header_month_V;
    private View separator_V;

    public static TransactionListFragment newInstance(int selectionType)
    {
        final TransactionListFragment f = new TransactionListFragment();
        f.setArguments(makeArgs(selectionType, null));
        return f;
    }

    public static Loader<Cursor> createItemsLoader(Context context)
    {
        final Uri uri = TransactionsProvider.uriTransactions();
        final String[] projection = new String[]
                {
                        Tables.Transactions.T_ID, Tables.Transactions.DATE, Tables.Transactions.AMOUNT, Tables.Transactions.NOTE, Tables.Transactions.STATE, Tables.Transactions.EXCHANGE_RATE,
                        Tables.Transactions.ACCOUNT_FROM_ID, Tables.Accounts.AccountFrom.S_TITLE, Tables.Accounts.AccountFrom.S_CURRENCY_ID, Tables.Currencies.CurrencyFrom.S_EXCHANGE_RATE,
                        Tables.Transactions.ACCOUNT_TO_ID, Tables.Accounts.AccountTo.S_TITLE, Tables.Accounts.AccountTo.S_CURRENCY_ID, Tables.Currencies.CurrencyTo.S_EXCHANGE_RATE,
                        Tables.Transactions.CATEGORY_ID, Tables.Categories.CategoriesChild.S_TITLE, Tables.Categories.CategoriesChild.S_TYPE, Tables.Categories.CategoriesChild.S_COLOR
                };

        final FilterHelper filterHelper = FilterHelper.getDefault(context);
        final long startDate = filterHelper.getPeriodStart();
        final long endDate = filterHelper.getPeriodEnd();

        final String selection = Tables.Transactions.DELETE_STATE + "=?" + (startDate > 0 ? " and " + Tables.Transactions.DATE + " >=?" : "") + (endDate > 0 ? " and " + Tables.Transactions.DATE + " <=?" : "");
        final String[] selectionArgs = new String[1 + (startDate > 0 ? 1 : 0) + (endDate > 0 ? 1 : 0)];
        selectionArgs[0] = String.valueOf(Tables.DeleteState.NONE);
        if (startDate > 0)
            selectionArgs[1] = String.valueOf(startDate);
        if (endDate > 0)
            selectionArgs[1 + (startDate > 0 ? 1 : 0)] = String.valueOf(endDate);
        final String sortOrder = Tables.Transactions.STATE + " desc, " + Tables.Transactions.DATE + " desc";

        return new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Register events
        EventBus.getDefault().register(this, FilterHelper.FilterChangedEvent.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_transaction_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        month_TV = (TextView) view.findViewById(R.id.month_TV);
        header_month_V = view.findViewById(R.id.header_month_V);
        separator_V = view.findViewById(R.id.separator_V);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        transactionAdapter = (TransactionsAdapter) adapter;

        list_V.setOnScrollListener(this);
        onScroll(list_V, 0, 1, list_V.getChildCount());
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        // Unregister events
        EventBus.getDefault().unregister(this, FilterHelper.FilterChangedEvent.class);
    }

    @Override
    public String getTitle()
    {
        return getString(R.string.transactions);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(FilterHelper.FilterChangedEvent event)
    {
        getLoaderManager().restartLoader(LOADER_ITEMS, null, this);
    }

    @Override
    protected AbstractCursorAdapter createAdapter(Context context)
    {
        return new TransactionsAdapter(context);
    }

    @Override
    protected Loader<Cursor> createItemsLoader()
    {
        return createItemsLoader(getActivity());
    }

    @Override
    protected void onItemSelected(long itemId, AbstractCursorAdapter adapter, Cursor c, Bundle outExtras)
    {
        // Ignore. There will be no transaction selection for now.
    }

    @Override
    protected void startItemDetails(Context context, long itemId, int position, AbstractCursorAdapter adapter, Cursor c, View view)
    {
        TransactionItemActivity.startItem(context, itemId, view);
    }

    @Override
    protected void startItemCreate(Context context, View view)
    {
        TransactionEditActivity.startItemEdit(context, 0, view);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int position, int visibleItemCount, int totalItemCount) {
        if (position < 0) {
            header_month_V.setVisibility(View.INVISIBLE);
            return;
        }

        if (position < totalItemCount - 1 && transactionAdapter.getItemViewType(position + 1) == TransactionsAdapter.TYPE_HEADER) {
            header_month_V.setVisibility(View.VISIBLE);
            return;
        }

        header_month_V.setVisibility(View.VISIBLE);

        String title = transactionAdapter.getSessionTitle(position);
        if (!TextUtils.isEmpty(title)) {
            month_TV.setText(title);
        }
    }
}