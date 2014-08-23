package com.kimkha.finance.ui.reports;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.kimkha.finance.R;
import com.kimkha.finance.ui.BaseActivity;

public class CategoriesReportActivity extends BaseActivity
{
    public static void startCategoriesReport(Context context)
    {
        Intent intent = new Intent(context, CategoriesReportActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Setup ActionBar
        setActionBarTitle(R.string.categories_report);

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, CategoriesReportFragment.newInstance()).commit();
    }
}