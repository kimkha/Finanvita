package com.kimkha.finanvita.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

import de.greenrobot.event.EventBus;

/**
 * @author kimkha
 * @version 0.2
 * @since 3/22/15
 */
public class LanguageHelper {
    public static final String PREFIX = "language_helper_";
    public static final String DEFAULT_LANGUAGE = "none";
    // -----------------------------------------------------------------------------------------------------------------
    private static LanguageHelper instance;
    // -----------------------------------------------------------------------------------------------------------------
    private Context context;
    private String langCode;
    private boolean isChanged;

    private LanguageHelper(Context context) {
        this.context = context.getApplicationContext();

        final SharedPreferences prefs = PrefsHelper.getPrefs(context);
        langCode = prefs.getString(PREFIX + "langCode", DEFAULT_LANGUAGE);
    }

    public static LanguageHelper getDefault(Context context) {
        if (instance == null) {
            instance = new LanguageHelper(context);
        }
        return instance;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        if (this.langCode.equalsIgnoreCase(langCode)) {
            return;
        }
        this.setChanged(true);

        if (langCode == null || DEFAULT_LANGUAGE.equalsIgnoreCase(langCode)) {
            this.langCode = DEFAULT_LANGUAGE;
            PrefsHelper.getPrefs(context).edit().remove(PREFIX + "langCode").apply();
        } else {
            this.langCode = langCode;
            PrefsHelper.getPrefs(context).edit().putString(PREFIX + "langCode", langCode).apply();
        }

        EventBus.getDefault().post(new LanguageChangedEvent());
    }

    public void startNewLanguage() {
        Resources res = context.getResources();
        Configuration config = res.getConfiguration();
        if (this.langCode == null || DEFAULT_LANGUAGE.equalsIgnoreCase(this.langCode)) {
            config.locale = null;
        } else {
            config.locale = new Locale(getLangCode());
        }
        res.updateConfiguration(config, res.getDisplayMetrics());

        this.setChanged(false);
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }

    public static class LanguageChangedEvent {

    }
}
