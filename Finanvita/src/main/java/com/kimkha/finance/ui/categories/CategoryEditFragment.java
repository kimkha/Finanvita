package com.kimkha.finance.ui.categories;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.kimkha.finance.API;
import com.kimkha.finance.R;
import com.kimkha.finance.db.Tables;
import com.kimkha.finance.providers.CategoriesProvider;
import com.kimkha.finance.ui.BaseFragment;
import com.kimkha.finance.ui.ItemEditStepsFragment;
import com.kimkha.finance.utils.CategoriesUtils;

public class CategoryEditFragment extends ItemEditStepsFragment implements CategoryParentFragment.Callbacks
{
    private static final String ARG_CATEGORY_TYPE = CategoryEditFragment.class.getName() + ".ARG_CATEGORY_TYPE";
    // --------------------------------------------------------------------------------------------------------------------------------
    private static final String FRAGMENT_CURRENT = "FRAGMENT_CURRENT";
    // --------------------------------------------------------------------------------------------------------------------------------
    private int categoryType;

    public static CategoryEditFragment newInstance(long itemId, int categoryType)
    {
        final Bundle args = makeArgs(itemId);
        args.putInt(ARG_CATEGORY_TYPE, categoryType);

        CategoryEditFragment f = new CategoryEditFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get arguments
        final Bundle args = getArguments();
        categoryType = args.getInt(ARG_CATEGORY_TYPE, Tables.Categories.Type.EXPENSE);
    }

    @Override
    public int getStepsCount()
    {
        return itemId == 0 ? 2 : 1;
    }

    @Override
    public boolean onSave(Context context, long itemId)
    {
        final CategoryDetailsFragment f = (CategoryDetailsFragment) getChildFragmentManager().findFragmentByTag(FRAGMENT_CURRENT);
        final long parentId = f.getParentId();
        final String title = f.getTitle(true);
        final int color = f.getColor(true);
        final int level = f.getLevel();

        // Check values
        if (TextUtils.isEmpty(title) || color == 0)
            return false;


        ContentValues values = CategoriesUtils.getValues(parentId, title, level, categoryType, color);
        if (itemId == 0)
            API.createItem(CategoriesProvider.uriCategories(), values, new CategoriesUtils.OrderValuesUpdater());
        else
            API.updateItem(CategoriesProvider.uriCategories(), itemId, values, new CategoriesUtils.OrderValuesUpdater());

        return true;
    }

    @Override
    public boolean onDiscard()
    {
        return true;
    }

    @Override
    public void onParentCategorySelected(long id)
    {
        if (callbacks != null)
            callbacks.doNextOrSaveClick();
    }

    @Override
    protected void restoreOrInit(long itemId, Bundle savedInstanceState)
    {
        // Fragments
        BaseFragment currentFragment = (BaseFragment) getChildFragmentManager().findFragmentByTag(FRAGMENT_CURRENT);
        if (currentFragment == null)
        {
            currentFragment = getStepsCount() == 2 ? CategoryParentFragment.newInstance(categoryType) : CategoryDetailsFragment.newInstance(itemId, 0);
            getChildFragmentManager().beginTransaction().replace(R.id.container_V, currentFragment, FRAGMENT_CURRENT).commit();
        }

        if (currentFragment instanceof CategoryParentFragment)
            ((CategoryParentFragment) currentFragment).setCallbacks(this);
    }

    @Override
    protected int onNextStep()
    {
        final long parentId = ((CategoryParentFragment) getChildFragmentManager().findFragmentByTag(FRAGMENT_CURRENT)).getSelectedId();

        if (parentId <= 0)
            return -1;

        getChildFragmentManager().beginTransaction().replace(R.id.container_V, CategoryDetailsFragment.newInstance(itemId, parentId), FRAGMENT_CURRENT).addToBackStack(null).commit();
        return 1;
    }

    @Override
    protected int onPrevStep()
    {
        getChildFragmentManager().popBackStackImmediate();
        ((CategoryParentFragment) getChildFragmentManager().findFragmentByTag(FRAGMENT_CURRENT)).setCallbacks(this);
        return 0;
    }
}