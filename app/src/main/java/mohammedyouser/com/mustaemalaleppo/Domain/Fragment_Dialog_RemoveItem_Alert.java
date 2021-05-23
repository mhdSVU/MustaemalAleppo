package mohammedyouser.com.mustaemalaleppo.Domain;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import mohammedyouser.com.mustaemalaleppo.R;

/**
 * Created by mohammed_youser on 4/12/2017.
 */

public class Fragment_Dialog_RemoveItem_Alert extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.title_fragment_confirm_delete))
                .setMessage(getString(R.string.message_alert_delete_item))
                .setPositiveButton(getString(R.string.OK), (dialog, which) -> ((Activity_Display_Modify_Remove_Item) getActivity()).manage_removeItem_state())
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {})
                .create();


    }

}
