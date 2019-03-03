package mohammedyouser.com.mustaemalaleppo.Domain;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import mohammedyouser.com.mustaemalaleppo.R;

/**
 * Created by mohammed_youser on 4/12/2017.
 */

public class RemoveItem_AlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.title_fragment_confirm_delete))
                .setMessage(getString(R.string.message_info_error_no_email))
                .setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Display_and_Manage_SingleItem_Activity)getActivity()).removeItem_From_Corresponding_DB_Refs();

                    }
                })

                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();


    }

}
