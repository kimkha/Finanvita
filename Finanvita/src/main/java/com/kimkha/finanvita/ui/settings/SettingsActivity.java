package com.kimkha.finanvita.ui.settings;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.text.SpannableStringBuilder;
import android.text.style.TypefaceSpan;
import android.view.MenuItem;
import android.widget.Toast;

import com.kimkha.finanvita.R;
import com.kimkha.finanvita.ui.AboutActivity;
import com.kimkha.finanvita.ui.backup.YourDataActivity;
import com.kimkha.finanvita.ui.categories.CategoryListActivity;
import com.kimkha.finanvita.ui.currencies.CurrencyListActivity;
import com.kimkha.finanvita.ui.dialogs.ProgressDialog2;
import com.kimkha.finanvita.ui.settings.donate.DonateActivity;
import com.kimkha.finanvita.ui.settings.lock.LockActivity;
import com.kimkha.finanvita.ui.tutorial.TutorialActivity;
import com.kimkha.finanvita.utils.ExchangeRatesHelper;
import com.kimkha.finanvita.utils.LanguageHelper;
import com.kimkha.finanvita.utils.PeriodHelper;
import com.kimkha.finanvita.utils.PrefsHelper;
import com.kimkha.finanvita.utils.SecurityHelper;

@SuppressWarnings({"ConstantConditions", "deprecation"})
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener, OnPreferenceClickListener, Preference.OnPreferenceChangeListener
{
    public static final String PREF_PERIOD = "period";
    public static final String PREF_CURRENCIES = "currencies";
    public static final String PREF_UPDATE_EXCHANGE_RATES = "update_exchange_rates";
    public static final String PREF_CATEGORIES = "categories";
    public static final String PREF_FOCUS_CATEGORIES_SEARCH = "focus_categories_search";
    public static final String PREF_LANGUAGES = "languages";
    public static final String PREF_APP_LOCK = "app_lock";
    public static final String PREF_APP_LOCK_PATTERN = "app_lock_pattern";
    public static final String PREF_APP_LOCK_NONE = "app_lock_none";
    public static final String PREF_DONATE = "donate";
    public static final String PREF_RATE_APP = "rate_app";
    public static final String PREF_CHANGE_LOG = "change_log";
    public static final String PREF_SETUP_GUIDE = "setup_guide";
    public static final String PREF_YOUR_DATA = "your_data";
    // -----------------------------------------------------------------------------------------------------------------
    private ListPreference period_P;
    private Preference appLock_P;
    private Preference changeLog_P;
    private ListPreference updateExchangeRates_P;
    private ListPreference languages_P;

    public static void startSettings(Context context)
    {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // Setup ActionBar
        final String title = getString(R.string.settings);
        SpannableStringBuilder ssb = new SpannableStringBuilder(title);
        ssb.setSpan(new TypefaceSpan("sans-serif-light"), 0, title.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        getActionBar().setTitle(ssb);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Get preferences
        period_P = (ListPreference) findPreference(PREF_PERIOD);
        updateExchangeRates_P = (ListPreference) findPreference(PREF_UPDATE_EXCHANGE_RATES);
        appLock_P = findPreference(PREF_APP_LOCK);
        changeLog_P = findPreference(PREF_CHANGE_LOG);
        languages_P = (ListPreference) findPreference(PREF_LANGUAGES);

        // Set OnPreferenceClickListener
        findPreference(PREF_CURRENCIES).setOnPreferenceClickListener(this);
        findPreference(PREF_CATEGORIES).setOnPreferenceClickListener(this);
        findPreference(PREF_APP_LOCK_PATTERN).setOnPreferenceClickListener(this);
        findPreference(PREF_APP_LOCK_NONE).setOnPreferenceClickListener(this);
        findPreference(PREF_DONATE).setOnPreferenceClickListener(this);
        findPreference(PREF_RATE_APP).setOnPreferenceClickListener(this);
        findPreference(PREF_YOUR_DATA).setOnPreferenceClickListener(this);
        findPreference(PREF_SETUP_GUIDE).setOnPreferenceClickListener(this);
        changeLog_P.setOnPreferenceClickListener(this);

        // Set OnPreferenceChangeListener
        period_P.setOnPreferenceChangeListener(this);
        updateExchangeRates_P.setOnPreferenceChangeListener(this);
        findPreference(PREF_FOCUS_CATEGORIES_SEARCH).setOnPreferenceChangeListener(this);
        languages_P.setOnPreferenceChangeListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        updatePreferencesWithContentChanged();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause()
    {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        updatePreferences();

        // Need restart activity
        if (LanguageHelper.getDefault(this).isChanged()) {
            LanguageHelper.getDefault(this).startNewLanguage();
            restartActivity();
        }
    }

    private void restartActivity() {
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onPreferenceClick(Preference preference)
    {
        if (preference.getKey().equals(PREF_CURRENCIES))
        {
            CurrencyListActivity.startList(SettingsActivity.this);
            return true;
        }
        else if (preference.getKey().equals(PREF_CATEGORIES))
        {
            CategoryListActivity.startList(SettingsActivity.this);
            return true;
        }
        else if (preference.getKey().equals(PREF_APP_LOCK_PATTERN))
        {
            LockActivity.startLockNewPattern(SettingsActivity.this);
            return true;
        }
        else if (preference.getKey().equals(PREF_APP_LOCK_NONE))
        {
            LockActivity.startLockClear(SettingsActivity.this);
            return true;
        }
        else if (preference.getKey().equals(PREF_DONATE))
        {
            DonateActivity.startDonate(SettingsActivity.this);
            return true;
        }
        else if (preference.getKey().equals(PREF_RATE_APP))
        {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            try
            {
                startActivity(goToMarket);
            }
            catch (ActivityNotFoundException e)
            {
                Toast.makeText(SettingsActivity.this, "Couldn't launch the market", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        else if (preference.getKey().equals(PREF_CHANGE_LOG))
        {
            AboutActivity.startAbout(SettingsActivity.this);
            return true;
        }
        else if (preference.getKey().equals(PREF_YOUR_DATA))
        {
            YourDataActivity.start(SettingsActivity.this);
            return true;
        }
        else if (preference.getKey().equals(PREF_SETUP_GUIDE))
        {
            TutorialActivity.startTutorial(SettingsActivity.this, 0);
            return true;
        }

        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        if (preference.getKey().equals(PREF_PERIOD))
        {
            PeriodHelper.getDefault(SettingsActivity.this).setType(Integer.parseInt((String) newValue));
            return true;
        }
        else if (preference.getKey().equals(PREF_UPDATE_EXCHANGE_RATES))
        {
            ExchangeRatesHelper.getDefault(SettingsActivity.this).setExchangeRatesValue((String) newValue);
            return true;
        }
        else if (preference.getKey().equals(PREF_FOCUS_CATEGORIES_SEARCH))
        {
            PrefsHelper.getDefault(this).setFocusCategoriesSearch((Boolean) newValue);
            return true;
        } else if (preference.getKey().equals(PREF_LANGUAGES))
        {
            ProgressDialog2.showDialog(getFragmentManager(), getString(R.string.please_wait));
            LanguageHelper.getDefault(this).setLangCode((String) newValue);
            return true;
        }
        return false;
    }

    private void updatePreferencesWithContentChanged()
    {
        // Period
        switch (PeriodHelper.getDefault(this).getType())
        {
            case PeriodHelper.TYPE_YEAR:
                period_P.setSummary(R.string.year);
                break;

            case PeriodHelper.TYPE_MONTH:
                period_P.setSummary(R.string.month);
                break;

            case PeriodHelper.TYPE_WEEK:
                period_P.setSummary(R.string.week);
                break;

            case PeriodHelper.TYPE_DAY:
                period_P.setSummary(R.string.day);
                break;
        }

        // App lock
        final int appLock = SecurityHelper.getDefault(this).getAppLockCode();
        switch (appLock)
        {
            case SecurityHelper.APP_LOCK_PATTERN:
                appLock_P.setSummary(R.string.pattern);
                break;

            default:
                appLock_P.setSummary(R.string.none);
                break;
        }

        // Change log
        try
        {
            changeLog_P.setSummary("v" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        }
        catch (NameNotFoundException e)
        {
            // Ignore
        }

        updatePreferences();
        onContentChanged();
    }

    private void updatePreferences()
    {
        // Update exchange rates summary
        updateExchangeRates_P.setSummary(updateExchangeRates_P.getEntry());
        // Update summary for period
        period_P.setSummary(period_P.getEntry());
        // Update summary for language
        languages_P.setSummary(languages_P.getEntry());
    }
}