package com.kimkha.finance;

import android.app.Application;
import android.content.Context;
import com.kimkha.finance.utils.PrefsHelper;

public class App extends Application
{
    private static Context appContext;

    public static Context getAppContext()
    {
        return appContext;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        appContext = this;
        PrefsHelper.getDefault(this).onAppStart();
    }
}
