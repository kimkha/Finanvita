package com.kimkha.finanvita.ui.tutorial;

import android.content.Context;
import android.os.Bundle;

import com.kimkha.finanvita.R;
import com.kimkha.finanvita.ui.BaseFragment;
import com.kimkha.finanvita.ui.ItemEditStepsFragment;

/**
 * @author kimkha
 * @version 0.2
 * @since 3/24/15
 */
public class TutorialFragment extends ItemEditStepsFragment {

    private static final String FRAGMENT_CURRENT = "FRAGMENT_CURRENT";

    public static TutorialFragment newInstance(long itemId)
    {
        final Bundle args = makeArgs(itemId);

        final TutorialFragment f = new TutorialFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public int getStepsCount() {
        return itemId == 0 ? 2 : 1;
    }

    @Override
    protected boolean onSave(Context context, long itemId) {
        // TODO Save status
        return true;
    }

    @Override
    protected boolean onDiscard() {
        // TODO Also save status
        return true;
    }

    @Override
    protected void restoreOrInit(long itemId, Bundle savedInstanceState) {
        if (getChildFragmentManager().findFragmentByTag(FRAGMENT_CURRENT) == null) {
            BaseFragment f = getStepsCount() == 2 ? TutorialLanguageFragment.newInstance() : TutorialCurrencyFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.container_V, f, FRAGMENT_CURRENT).commit();
        }
    }

    @Override
    protected int onNextStep() {
        // TODO
        BaseFragment f = TutorialCurrencyFragment.newInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.container_V, f, FRAGMENT_CURRENT).addToBackStack(null).commit();
        return 1;
    }

    @Override
    protected int onPrevStep() {
        // TODO
        getChildFragmentManager().popBackStack();
        return 0;
    }

}
