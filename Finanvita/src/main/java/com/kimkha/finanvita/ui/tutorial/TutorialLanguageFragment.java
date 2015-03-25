package com.kimkha.finanvita.ui.tutorial;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kimkha.finanvita.R;
import com.kimkha.finanvita.ui.BaseFragment;
import com.kimkha.finanvita.utils.LanguageHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author kimkha
 * @version 0.2
 * @since 3/24/15
 */
public class TutorialLanguageFragment extends BaseFragment {

    private AlertDialog.Builder dialogBuilder;

    private List<String> languageValues = new ArrayList<>();

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

        buildDialog();

        Button btn = (Button) view.findViewById(R.id.select_language_B);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.show();
            }
        });
    }

    private void buildDialog() {
        dialogBuilder = new AlertDialog.Builder(getActivity()).setIcon(R.drawable.ic_launcher).setTitle(R.string.change_language);
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        languageValues = Arrays.asList(getResources().getStringArray(R.array.language_values));
        String currentLanguage = LanguageHelper.getDefault(getActivity()).getLangCode();
        int pos = 0;
        for (int i=0; i<languageValues.size(); i++) {
            if (languageValues.get(i).equals(currentLanguage)) {
                pos = i;
            }
        }

        dialogBuilder.setSingleChoiceItems(R.array.languages, pos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LanguageHelper.getDefault(getActivity()).setLangCode(languageValues.get(which));
                dialog.dismiss();
            }
        });
    }

}
