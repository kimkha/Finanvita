package com.kimkha.finanvita.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.kimkha.finanvita.R;
import com.kimkha.finanvita.utils.AmountUtils;

public class AccountView extends LinearLayout
{
    private TextView title_TV;
    private TextView balance_TV;

    public AccountView(Context context)
    {
        this(context, null);
    }

    public AccountView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initAll(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public AccountView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initAll(context);
    }

    private void initAll(Context context) {
        inflate(context, R.layout.v_account, this);

        // Get views
        title_TV = (TextView) findViewById(R.id.title_TV);
        balance_TV = (TextView) findViewById(R.id.balance_TV);
    }

    public void bind(String title, double balance, long currencyId)
    {
        title_TV.setText(title);
        balance_TV.setText(AmountUtils.formatAmount(currencyId, balance));
        balance_TV.setTextColor(AmountUtils.getBalanceColor(getContext(), balance));
    }
}
