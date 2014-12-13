package com.kimkha.finanvita.utils;

import android.app.Activity;
import android.content.Context;
import com.kimkha.finanvita.billing.Purchase;
import com.kimkha.finanvita.ui.settings.donate.DonateActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import java.util.UUID;

public class Tracking
{
    public static void startTracking(Activity activity)
    {
        EasyTracker.getInstance(activity).activityStart(activity);
    }

    public static void stopTracking(Activity activity)
    {
        EasyTracker.getInstance(activity).activityStop(activity);
    }

    public static void onPurchaseCompleted(Context context, Purchase purchase, DonateActivity.Product product)
    {
        String productName = "Donate";
        double totalRevenue = product.getPriceAmount();
        productName += totalRevenue;

        final String transactionId = UUID.randomUUID().toString();

        final EasyTracker easyTracker = EasyTracker.getInstance(context);
        easyTracker.send(MapBuilder
                .createTransaction(transactionId,       // (String) Transaction ID
                        "In-app Product",               // (String) Affiliation
                        totalRevenue,                   // (Double) Order revenue
                        0.3d,                           // (Double) Tax
                        0.0d,                           // (Double) Shipping
                        "GBP")                          // (String) Currency code
                .build()
        );

        easyTracker.send(MapBuilder
                .createItem(transactionId,              // (String) Transaction ID
                        productName,                    // (String) Product name
                        purchase.getSku(),              // (String) Product SKU
                        "Donate",                       // (String) Product category
                        totalRevenue,                   // (Double) Product price
                        1L,                             // (Long) Product quantity
                        "GBP")                          // (String) Currency code
                .build()
        );
    }
}
