package mohammedyouser.com.mustaemalaleppo.Domain;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import mohammedyouser.com.mustaemalaleppo.R;
import mohammedyouser.com.mustaemalaleppo.UI.Activity_Ineed_Ihave;

/**
 * Created by mohammed_youser on 4/12/2017.
 */

public class Fragment_Dialog_SignOut_Alert extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(getActivity(), R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.title_fragment_sign_out_confirm))
                .setMessage(getString(R.string.message_alert_sign_out_confirm))
                .setPositiveButton(getString(R.string.OK), (dialog, which) -> ((Activity_Ineed_Ihave) getActivity()).SignOut())
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {})
                .create();


    }

}
