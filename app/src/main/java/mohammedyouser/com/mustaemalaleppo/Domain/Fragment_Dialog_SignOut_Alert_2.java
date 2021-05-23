package mohammedyouser.com.mustaemalaleppo.Domain;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import mohammedyouser.com.mustaemalaleppo.R;
import mohammedyouser.com.mustaemalaleppo.UI.Activity_Ineed_Ihave;
import mohammedyouser.com.mustaemalaleppo.UI.Activity_Ineed_Ihave_CurrentUser;

/**
 * Created by mohammed_youser on 4/12/2017.
 */

public class Fragment_Dialog_SignOut_Alert_2 extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.title_fragment_sign_out_confirm))
                .setMessage(getString(R.string.message_alert_sign_out_confirm))
                .setPositiveButton(getString(R.string.OK), (dialog, which) -> ((Activity_Ineed_Ihave_CurrentUser) getActivity()).SignOut())
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {})
                .create();


    }

}
