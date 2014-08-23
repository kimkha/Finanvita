package com.kimkha.finance.providers;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import com.kimkha.finance.db.Tables;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class BaseItemsProvider extends BaseProvider
{
    protected static final int OPERATION_INSERT = 1;
    protected static final int OPERATION_UPDATE = 2;
    protected static final int OPERATION_BULK_INSERT = 3;
    // -----------------------------------------------------------------------------------------------------------------
    private static final int URI_ITEMS = 1;
    private static final int URI_ITEMS_ID = 2;

    protected static void checkId(ContentValues values, String columnName, boolean required)
    {
        final Long value = values.getAsLong(columnName);
        if ((value != null && value <= 0) || (value == null && required))
            throw new IllegalArgumentException(columnName + " must be > 0.");
    }

    protected static void checkLong(ContentValues values, String columnName, boolean required)
    {
        if (required && values.getAsLong(columnName) == null)
            throw new IllegalArgumentException(columnName + " must not be empty.");
    }

    protected static void checkInt(ContentValues values, String columnName, boolean required)
    {
        if (required && values.getAsInteger(columnName) == null)
            throw new IllegalArgumentException(columnName + " must not be empty.");
    }

    protected static void checkInt(ContentValues values, String columnName, boolean required, int start, int end)
    {
        checkInt(values, columnName, required);

        final Integer value = values.getAsInteger(columnName);
        if (value != null && (value < start || value > end))
            throw new IllegalArgumentException(columnName + " must be " + start + " <= ? <= " + end);
    }

    protected static void checkDouble(ContentValues values, String columnName, boolean required)
    {
        if (required && values.getAsDouble(columnName) == null)
            throw new IllegalArgumentException(columnName + " must not be empty.");
    }

    protected static void checkDouble(ContentValues values, String columnName, boolean required, double start, double end)
    {
        checkDouble(values, columnName, required);

        final Double value = values.getAsDouble(columnName);
        if (value != null && (value < start || value > end))
            throw new IllegalArgumentException(columnName + " must be " + start + " <= ? <= " + end);
    }

    protected static void checkString(ContentValues values, String columnName, boolean required, boolean canBeEmpty)
    {
        final String value = values.getAsString(columnName);
        if (required && (value == null || (!canBeEmpty && value.length() == 0)))
            throw new IllegalArgumentException(columnName + " must not be empty.");
    }

    protected static void checkString(ContentValues values, String columnName, boolean required, String... possibleValues)
    {
        checkString(values, columnName, required, false);
        final String value = values.getAsString(columnName);
        if (!required && (possibleValues == null || possibleValues.length == 0 || value == null))
            return;

        boolean isOK = false;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < possibleValues.length; i++)
        {
            if (possibleValues[i].equals(value))
            {
                isOK = true;
                break;
            }
        }

        if (!isOK)
        {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < possibleValues.length; i++)
            {
                if (i > 0)
                    sb.append(", ");
                sb.append(possibleValues[i]);
            }

            throw new IllegalArgumentException(columnName + " must be equal to one of these values: " + sb.toString());
        }
    }

    @Override
    public boolean onCreate()
    {
        final boolean result = super.onCreate();
        final String authority = getAuthority(getContext(), getClass());
        final String table = getItemTable();
        uriMatcher.addURI(authority, table, URI_ITEMS);
        uriMatcher.addURI(authority, table + "/#", URI_ITEMS_ID);
        return result;
    }

    protected abstract String getItemTable();

    protected abstract Object onBeforeInsert(Uri uri, ContentValues values);

    protected abstract void onAfterInsert(Uri uri, ContentValues values, long newId, Object objectFromBefore);

    protected abstract Object onBeforeUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs);

    protected abstract void onAfterUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs, int updatedCount, Object objectFromBefore);

    protected abstract Object onBeforeDelete(Uri uri, String selection, String[] selectionArgs);

    protected abstract void onAfterDelete(Uri uri, String selection, String[] selectionArgs, int updatedCount, Object objectFromBefore);

    protected abstract Object onBeforeBulkInsert(Uri uri, ContentValues[] valuesArray);

    protected abstract void onAfterBulkInsert(Uri uri, ContentValues[] valuesArray, Object objectFromBefore);

    protected abstract void checkValues(ContentValues values, int operation);

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        Cursor c;

        final int uriId = uriMatcher.match(uri);
        switch (uriId)
        {
            case URI_ITEMS:
                c = queryItems(projection, selection, selectionArgs, sortOrder);
                break;

            case URI_ITEMS_ID:
                c = queryItem(uri, projection, selection, selectionArgs, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        //noinspection ConstantConditions
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public String getType(Uri uri)
    {
        switch (uriMatcher.match(uri))
        {
            case URI_ITEMS:
                return TYPE_LIST_BASE + getItemTable();
            case URI_ITEMS_ID:
                return TYPE_ITEM_BASE + getItemTable();
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        long newId;

        final int uriId = uriMatcher.match(uri);
        switch (uriId)
        {
            case URI_ITEMS:
                newId = insertItem(uri, values);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        return ContentUris.withAppendedId(uri, newId);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        int count;

        final int uriId = uriMatcher.match(uri);
        switch (uriId)
        {
            case URI_ITEMS:
                count = deleteItems(uri, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        int count;

        final int uriId = uriMatcher.match(uri);
        switch (uriId)
        {
            case URI_ITEMS:
                count = updateItems(uri, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, @SuppressWarnings("NullableProblems") ContentValues[] valuesArray)
    {
        int count;

        final int uriId = uriMatcher.match(uri);
        switch (uriId)
        {
            case URI_ITEMS:
                count = bulkInsertItems(uri, valuesArray);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        return count;
    }

    protected long insertItem(Uri uri, ContentValues values)
    {
        final String tableName = getItemTable();

        // Set default values
        String columnName = tableName + "_" + Tables.SUFFIX_SERVER_ID;
        if (!values.containsKey(columnName) || TextUtils.isEmpty(values.getAsString(columnName)))
            values.put(columnName, UUID.randomUUID().toString());

        values.put(tableName + "_" + Tables.SUFFIX_DELETE_STATE, Tables.DeleteState.NONE);

        long newId = 0;
        try
        {
            db.beginTransaction();

            checkValues(values, OPERATION_INSERT);
            final Object extras = onBeforeInsert(uri, values);
            newId = doUpdateOrInsert(db, getItemTable(), values, true);
            onAfterInsert(uri, values, newId, extras);

            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
        return newId;
    }

    protected int updateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        int count;
        try
        {
            db.beginTransaction();

            checkValues(values, OPERATION_UPDATE);
            final Object extras = onBeforeUpdate(uri, values, selection, selectionArgs);
            count = db.update(getItemTable(), values, selection, selectionArgs);
            onAfterUpdate(uri, values, selection, selectionArgs, count, extras);

            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
        return count;
    }

    protected int deleteItems(Uri uri, String selection, String[] selectionArgs)
    {
        int count;

        final String table = getItemTable();
        final ContentValues values = new ContentValues();
        values.put(table + "_" + Tables.SUFFIX_DELETE_STATE, Tables.DeleteState.DELETED);

        try
        {
            db.beginTransaction();

            final Object extras = onBeforeDelete(uri, selection, selectionArgs);
            count = db.update(table, values, selection, selectionArgs);
            onAfterDelete(uri, selection, selectionArgs, count, extras);

            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
        return count;
    }

    protected int bulkInsertItems(Uri uri, ContentValues[] valuesArray)
    {
        int count = 0;
        try
        {
            db.beginTransaction();

            final Object extras = onBeforeBulkInsert(uri, valuesArray);
            final String tableName = getItemTable();
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < valuesArray.length; i++)
            {
                final ContentValues values = valuesArray[i];
                checkValues(values, OPERATION_BULK_INSERT);
                doUpdateOrInsert(db, tableName, values, false);
                count++;
            }
            onAfterBulkInsert(uri, valuesArray, extras);

            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
        return count;
    }

    protected String getJoinedTables()
    {
        return "";
    }

    protected Cursor queryItems(String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(getItemTable() + getJoinedTables());

        return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    protected Cursor queryItem(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        final String table = getItemTable();
        qb.setTables(table + getJoinedTables());
        qb.appendWhere(table + "." + BaseColumns._ID + "=" + ContentUris.parseId(uri));

        return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    protected List<Long> getItemIDs(String selection, String[] selectionArgs)
    {
        final List<Long> itemIDs = new ArrayList<>();
        Cursor c = null;
        try
        {
            c = db.query(getItemTable(), new String[]{BaseColumns._ID}, selection, selectionArgs, null, null, null);
            if (c != null && c.moveToFirst())
            {
                do
                {
                    itemIDs.add(c.getLong(0));
                }
                while (c.moveToNext());
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }

        return itemIDs;
    }

    public static class InClause
    {
        private final String selection;
        private final String[] selectionArgs;

        public InClause(String selection, String[] selectionArgs)
        {
            this.selection = selection;
            this.selectionArgs = selectionArgs;
        }

        public static InClause getInClause(List<Long> itemIDs, String column)
        {
            final long[] itemIDsArray = new long[itemIDs.size()];
            for (int i = 0; i < itemIDs.size(); i++)
                itemIDsArray[i] = itemIDs.get(i);

            return getInClause(itemIDsArray, column);
        }

        public static InClause getInClause(long[] itemIDs, String column)
        {
            final StringBuilder selectionSB = new StringBuilder();
            final String[] selectionArgs = new String[itemIDs.length];

            for (int i = 0; i < itemIDs.length; i++)
            {
                if (selectionSB.length() > 0)
                    selectionSB.append(",");
                selectionSB.append("?");
                selectionArgs[i] = String.valueOf(itemIDs[i]);
            }

            selectionSB.insert(0, "(").insert(0, " in ").insert(0, column).append(")");

            return new InClause(selectionSB.toString(), selectionArgs);
        }

        public String getSelection()
        {
            return selection;
        }

        public String[] getSelectionArgs()
        {
            return selectionArgs;
        }
    }
}