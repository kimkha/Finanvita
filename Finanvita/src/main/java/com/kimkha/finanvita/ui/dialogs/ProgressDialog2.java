package com.kimkha.finanvita.ui.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;

public class ProgressDialog2 extends DialogFragment
{
    private static final String FRAGMENT_PROGRESS = ProgressDialog2.class.getName() + "FRAGMENT_PROGRESS";
    // -----------------------------------------------------------------------------------------------------------------
    private static final String ARG_MESSAGE = "ARG_MESSAGE";

    public static void showDialog(FragmentManager fm, String message)
    {
        dismissDialog(fm);

        final Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);

        final ProgressDialog2 f = new ProgressDialog2();
        f.setArguments(args);

        f.show(fm, FRAGMENT_PROGRESS);
    }

    public static void dismissDialog(FragmentManager fm)
    {
        ProgressDialog2 f = (ProgressDialog2) fm.findFragmentByTag(FRAGMENT_PROGRESS);
        if (f != null)
            f.dismiss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Bundle args = getArguments();
        final android.app.ProgressDialog dialog = new android.app.ProgressDialog(getActivity());
        dialog.setMessage(args.getString(ARG_MESSAGE));
        dialog.setIndeterminate(true);
        return dialog;
    }

}