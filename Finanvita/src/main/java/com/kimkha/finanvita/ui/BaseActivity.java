package com.kimkha.finanvita.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.content.LocalBroadcastManager;
import android.text.SpannableStringBuilder;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.kimkha.finanvita.App;
import com.kimkha.finanvita.R;
import com.kimkha.finanvita.ui.dialogs.ProgressDialog2;
import com.kimkha.finanvita.ui.settings.SettingsActivity;
import com.kimkha.finanvita.ui.settings.lock.LockActivity;
import com.kimkha.finanvita.utils.SecurityHelper;

import java.util.HashMap;

/**
 * @author kimkha
 * @since 25/05/13
 * @version 0.2
 */
public abstract class BaseActivity extends FragmentActivity
{
    protected static final String STATE_FORCE_SECURITY = BaseActivity.class.getName() + ".STATE_FORCE_SECURITY";
    protected final BroadcastReceiver killReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            finish();
        }
    };
    protected boolean forceSecurity = false;

    protected static void start(Context context, Intent intent, View expandFromView)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && expandFromView != null &&context instanceof Activity)
        {
            final Bundle options = ActivityOptionsCompat.makeScaleUpAnimation(expandFromView, 0, 0, expandFromView.getWidth(), expandFromView.getHeight()).toBundle();
            ActivityCompat.startActivity((Activity) context, intent, options);
        }
        else
        {
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Register broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(killReceiver, new IntentFilter(LockActivity.ACTION_KILL));

        // Setup ActionBar
        //noinspection ConstantConditions
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Restore state
        if (savedInstanceState != null)
            forceSecurity = savedInstanceState.getBoolean(STATE_FORCE_SECURITY, false);

        getTracker(TrackerName.GLOBAL_TRACKER);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Ask for unlock if necessary
        SecurityHelper.getDefault(this).checkSecurity(this, forceSecurity);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        // Update unlock timestamp
        SecurityHelper.getDefault(this).updateUnlockTimestamp();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        // Unregister broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(killReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putBoolean(STATE_FORCE_SECURITY, forceSecurity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                TaskStackBuilder tsb = TaskStackBuilder.create(this);
                setupParentActivities(tsb);
                final int intentCount = tsb.getIntentCount();
                if (intentCount > 0)
                {
                    Intent upIntent = tsb.getIntents()[intentCount - 1];
                    if (NavUtils.shouldUpRecreateTask(this, upIntent))
                    {
                        // This activity is not part of the application's task, so create a new task with a synthesized back stack.
                        tsb.startActivities();
                        finish();
                    }
                    else
                    {
                        // This activity is part of the application's task, so simply navigate up to the hierarchical parent activity.
                        NavUtils.navigateUpTo(this, upIntent);
                    }
                }
                else
                {
                    onBackPressed();
                }
                return true;
            }

            case R.id.action_settings:
                SettingsActivity.startSettings(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setForceSecurity(boolean forceSecurity)
    {
        this.forceSecurity = forceSecurity;
    }

    /**
     * Override this method and add activities to {@link TaskStackBuilder}. If you don't do that, "Up" action will behave as back button.
     *
     * @param tsb Task stack builder.
     */
    protected void setupParentActivities(TaskStackBuilder tsb)
    {
    }

    protected void setActionBarTitle(int resId)
    {
        setActionBarTitle(getString(resId));
    }

    protected void setActionBarTitle(String title)
    {
        SpannableStringBuilder ssb = new SpannableStringBuilder(title);
        ssb.setSpan(new TypefaceSpan("sans-serif-light"), 0, title.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        getActionBar().setTitle(ssb);
    }

    public void showProgressDialog() {
        ProgressDialog2.showDialog(getFragmentManager(), getString(R.string.please_wait));
    }
    public void dismissProgressDialog() {
        ProgressDialog2.dismissDialog(getFragmentManager());
    }

    public void showShortToast(String msg) {
        Toast.makeText(App.getAppContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Enum used to identify the tracker that needs to be used for tracking.
     *
     * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
     * storing them all in Application object helps ensure that they are created only once per
     * application instance.
     */
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    private String PROPERTY_ID = null;

    protected synchronized Tracker getTracker(TrackerName trackerId) {
        if (PROPERTY_ID == null) {
            PROPERTY_ID = getString(R.string.ga_trackingId);
        }
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
                    : analytics.newTracker(R.xml.ecommerce_tracker);

            // Enable Advertising Features.
            t.enableAdvertisingIdCollection(true);

            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }
}