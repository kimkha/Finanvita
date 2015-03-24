package com.kimkha.finanvita.ui.tutorial;

import android.content.Context;

import com.kimkha.finanvita.ui.ItemEditActivity;
import com.kimkha.finanvita.ui.ItemEditFragment;

public class TutorialActivity extends ItemEditActivity {

    public static void startTutorial(Context context, long itemId)
    {
        start(context, makeIntent(context, TutorialActivity.class, itemId), null);
    }

    @Override
    protected ItemEditFragment createItemEditFragment(long itemId) {
        return TutorialFragment.newInstance(itemId);
    }
}
