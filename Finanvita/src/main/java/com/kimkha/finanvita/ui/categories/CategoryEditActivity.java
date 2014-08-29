package com.kimkha.finanvita.ui.categories;

import android.content.Context;
import android.content.Intent;
import com.kimkha.finanvita.db.Tables;
import com.kimkha.finanvita.ui.ItemEditActivity;
import com.kimkha.finanvita.ui.ItemEditFragment;

public class CategoryEditActivity extends ItemEditActivity
{
    private static final String EXTRA_CATEGORY_TYPE = CategoryEditActivity.class.getName() + ".EXTRA_CATEGORY_TYPE";
    private int categoryType;

    public static void startItemEdit(Context context, long itemId, int categoryType)
    {
        Intent intent = makeIntent(context, CategoryEditActivity.class, itemId);
        intent.putExtra(EXTRA_CATEGORY_TYPE, categoryType);
        context.startActivity(intent);
    }

    @Override
    protected ItemEditFragment createItemEditFragment(long itemId)
    {
        return CategoryEditFragment.newInstance(itemId, categoryType);
    }

    @Override
    protected int inflateView()
    {
        // Get extras
        final Intent extras = getIntent();
        categoryType = extras.getIntExtra(EXTRA_CATEGORY_TYPE, Tables.Categories.Type.EXPENSE);

        return super.inflateView();
    }
}