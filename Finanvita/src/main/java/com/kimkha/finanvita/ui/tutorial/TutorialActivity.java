package com.kimkha.finanvita.ui.tutorial;

import android.content.Context;
import android.content.Intent;

import com.kimkha.finanvita.ui.ItemEditActivity;
import com.kimkha.finanvita.ui.ItemEditFragment;
import com.kimkha.finanvita.utils.LanguageHelper;

public class TutorialActivity extends ItemEditActivity {

    public static void startTutorial(Context context, long itemId)
    {
        start(context, makeIntent(context, TutorialActivity.class, itemId), null);
    }

    @Override
    protected ItemEditFragment createItemEditFragment(long itemId) {
        return TutorialFragment.newInstance(itemId);
    }

    public void restartActivity() {
        LanguageHelper.getDefault(this).startNewLanguage();
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
