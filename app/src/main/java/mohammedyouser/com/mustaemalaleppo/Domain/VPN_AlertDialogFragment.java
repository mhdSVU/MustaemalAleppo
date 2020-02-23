package mohammedyouser.com.mustaemalaleppo.Domain;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;

import mohammedyouser.com.mustaemalaleppo.R;

/**
 * Created by mohammed_youser on 4/12/2017.
 */

public class VPN_AlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.title_fragment_VPN_alert))
                .setMessage(getString(R.string.message_info_fragment_VPN_alert))
                .setPositiveButton(getString(R.string.Download_VPN_OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadVPN();

                    }
                })

                .setNegativeButton(getString(R.string.Download_VPN_NO), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();


    }

    private void downloadVPN() {
        Intent downloadVPNIntent =new Intent();
        downloadVPNIntent.setAction(Intent.ACTION_VIEW);
        downloadVPNIntent.setData(Uri.parse(getActivity().getString(R.string.default_val_vpn_android_link)));
        getActivity().startActivity(downloadVPNIntent);

    }

}
