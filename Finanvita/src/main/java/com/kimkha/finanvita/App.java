package com.kimkha.finanvita;

import android.app.Application;
import android.content.Context;
import com.kimkha.finanvita.utils.PrefsHelper;

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
