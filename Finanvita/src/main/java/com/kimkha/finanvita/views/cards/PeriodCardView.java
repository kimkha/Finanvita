package com.kimkha.finanvita.views.cards;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import com.kimkha.finanvita.utils.PeriodHelper;

@SuppressWarnings("UnusedDeclaration")
public class PeriodCardView extends TitleCardView
{
    public PeriodCardView(Context context)
    {
        this(context, null);
    }

    public PeriodCardView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public PeriodCardView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        // Setup
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getSecondaryTitleView().setAllCaps(true);
        }
    }

    @Override
    public void setCardInfo(CardInfo cardInfo)
    {
        super.setCardInfo(cardInfo);
        setSecondaryTitle(((PeriodCardInfo) cardInfo).getPeriodName());
    }

    public static class PeriodCardInfo extends TitleCardInfo
    {
        final protected Context context;
        protected int periodType;
        protected long periodStart;
        protected long periodEnd;

        public PeriodCardInfo(Context context, long id)
        {
            super(id);
            this.context = context;
        }

        public PeriodCardInfo setPeriod(int periodType, long periodStart, long periodEnd)
        {
            this.periodType = periodType;
            this.periodStart = periodStart;
            this.periodEnd = periodEnd;
            return this;
        }

        public int getPeriodType()
        {
            return periodType;
        }

        public long getPeriodStart()
        {
            return periodStart;
        }

        public long getPeriodEnd()
        {
            return periodEnd;
        }

        public String getPeriodName()
        {
            return PeriodHelper.getPeriodShortTitle(context, getPeriodType(), getPeriodStart(), getPeriodEnd());
        }
    }
}