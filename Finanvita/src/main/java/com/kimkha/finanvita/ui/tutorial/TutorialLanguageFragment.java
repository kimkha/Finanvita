package com.kimkha.finanvita.ui.tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kimkha.finanvita.R;
import com.kimkha.finanvita.ui.BaseFragment;

/**
 * @author kimkha
 * @version 0.2
 * @since 3/24/15
 */
public class TutorialLanguageFragment extends BaseFragment {

    public static TutorialLanguageFragment newInstance() {
        return new TutorialLanguageFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tutorial_language, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO Get views
    }

}
