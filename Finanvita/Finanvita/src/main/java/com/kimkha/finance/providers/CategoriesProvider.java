package com.kimkha.finance.providers;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.kimkha.finance.App;
import com.kimkha.finance.db.DBUpgrade;
import com.kimkha.finance.db.Tables;

import java.util.ArrayList;
import java.util.List;

public class CategoriesProvider extends BaseItemsProvider
{

    public static Uri uriCategories()
    {
        return Uri.parse(CONTENT_URI_BASE + getAuthority(App.getAppContext(), CategoriesProvider.class) + "/" + Tables.Categories.TABLE_NAME);
    }

    public static Uri uriCategory(long categoryId)
    {
        return ContentUris.withAppendedId(uriCategories(), categoryId);
    }

    @Override
    protected String getItemTable()
    {
        return Tables.Categories.TABLE_NAME;
    }

    @Override
    protected Object onBeforeInsert(Uri uri, ContentValues values)
    {
        return null;
    }

    @Override
    protected void onAfterInsert(Uri uri, ContentValues values, long newId, Object objectFromBefore)
    {
        // Update order
        DBUpgrade.updateCategoriesOrder(db);

        notifyURIs(uriCategories());
    }

    @Override
    protected Object onBeforeUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        if (values.containsKey(Tables.Categories.PARENT_ID))
        {
            //noinspection ConstantConditions
            final long parentId = values.getAsLong(Tables.Categories.PARENT_ID);
            Cursor c = db.query(Tables.Categories.TABLE_NAME, new String[]{Tables.Categories.PARENT_ID}, selection, selectionArgs, null, null, null);
            try
            {
                if (c != null && c.moveToFirst())
                {
                    do
                    {
                        if (parentId != c.getLong(0))
                            throw new IllegalArgumentException("Cannot change " + Tables.Categories.PARENT_ID + ". Create new category instead.");
                    }
                    while (c.moveToNext());
                }
            }
            finally
            {
                if (c != null && !c.isClosed())
                    c.close();
            }
        }
        return null;
    }

    @Override
    protected void onAfterUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs, int updatedCount, Object objectFromBefore)
    {
        // Check if color was changed
        if (values.containsKey(Tables.Categories.COLOR))
        {
            // Update color for subcategories
            Cursor c = null;
            try
            {
                final String realSelection = Tables.Categories.LEVEL + "=1" + (selection != null ? " and (" + selection + ")" : "");
                c = queryItems(new String[]{Tables.Categories.T_ID}, realSelection, selectionArgs, null);
                if (c != null && c.moveToFirst())
                {
                    do
                    {
                        ContentValues tempValues = new ContentValues();
                        tempValues.put(Tables.Categories.COLOR, values.getAsInteger(Tables.Categories.COLOR));
                        db.update(Tables.Categories.TABLE_NAME, tempValues, Tables.Categories.PARENT_ID + "=?", new String[]{String.valueOf(c.getLong(0))});
                    }
                    while (c.moveToNext());
                }
            }
            finally
            {
                if (c != null && !c.isClosed())
                    c.close();
            }
        }

        // Update order
        DBUpgrade.updateCategoriesOrder(db);

        // Notify
        notifyURIs(CategoriesProvider.uriCategories(), TransactionsProvider.uriTransactions());
    }

    @Override
    protected Object onBeforeDelete(Uri uri, String selection, String[] selectionArgs)
    {
        return getItemIDs(selection, selectionArgs);
    }

    @Override
    protected void onAfterDelete(Uri uri, String selection, String[] selectionArgs, int updatedCount, Object objectFromBefore)
    {
        // Find all subcategories for deleted categories
        //noinspection unchecked
        final List<Long> itemIDs = (List<Long>) objectFromBefore;
        final List<Long> subcategoriesIDs = new ArrayList<Long>();
        InClause inClause = InClause.getInClause(itemIDs, Tables.Categories.PARENT_ID);
        Cursor c = null;
        try
        {
            //noinspection ConstantConditions
            c = getContext().getContentResolver().query(uriCategories(), new String[]{Tables.Categories.T_ID}, inClause.getSelection(), inClause.getSelectionArgs(), null);
            if (c != null && c.moveToFirst())
            {
                Long id;
                do
                {
                    id = c.getLong(0);
                    if (!itemIDs.contains(id))
                        itemIDs.add(id);
                    if (!subcategoriesIDs.contains(id))
                        subcategoriesIDs.add(id);
                }
                while (c.moveToNext());
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }

        // Delete subcategories
        if (subcategoriesIDs.size() > 0)
        {
            inClause = InClause.getInClause(subcategoriesIDs, Tables.Categories.ID);
            getContext().getContentResolver().delete(uriCategories(), inClause.getSelection(), inClause.getSelectionArgs());
        }

        // Delete Transactions
        if (itemIDs != null && itemIDs.size() > 0)
        {
            inClause = InClause.getInClause(itemIDs, Tables.Transactions.CATEGORY_ID);
            getContext().getContentResolver().delete(TransactionsProvider.uriTransactions(), inClause.getSelection(), inClause.getSelectionArgs());
        }

        // Update order
        DBUpgrade.updateCategoriesOrder(db);

        // Notify
        notifyURIs(CategoriesProvider.uriCategories(), TransactionsProvider.uriTransactions());
    }

    @Override
    protected Object onBeforeBulkInsert(Uri uri, ContentValues[] valuesArray)
    {
        return null;
    }

    @Override
    protected void onAfterBulkInsert(Uri uri, ContentValues[] valuesArray, Object objectFromBefore)
    {
        // Update order
        DBUpgrade.updateCategoriesOrder(db);

        // Notify
        notifyURIs(CategoriesProvider.uriCategories(), TransactionsProvider.uriTransactions());
    }

    @Override
    protected void checkValues(ContentValues values, int operation)
    {
        final boolean required = operation == OPERATION_INSERT || operation == OPERATION_BULK_INSERT;
        checkId(values, Tables.Categories.PARENT_ID, required);
        checkString(values, Tables.Categories.TITLE, required, false);
        checkInt(values, Tables.Categories.COLOR, required);
        checkInt(values, Tables.Categories.LEVEL, required, 0, Integer.MAX_VALUE);
        checkInt(values, Tables.Categories.TYPE, required, 0, 2);
        if (required && !values.containsKey(Tables.Categories.ORIGIN))
            values.put(Tables.Categories.ORIGIN, Tables.Categories.Origin.USER);
        if (required && !values.containsKey(Tables.Categories.ORDER))
            values.put(Tables.Categories.ORDER, 0);
    }
}