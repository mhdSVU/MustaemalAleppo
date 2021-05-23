package mohammedyouser.com.mustaemalaleppo.Domain;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.BUNDLE_KEY_DOWNLOAD_VPN;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.BUNDLE_KEY_REQUEST_LOCATION_UPDATE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.REQUEST_KEY_DOWNLOAD_VPN;

/**
 * Created by mohammed_youser on 4/12/2017.
 */

public class Fragment_Dialog_VPN_Alert extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.title_fragment_VPN_alert))
                .setMessage(getString(R.string.message_info_fragment_VPN_alert))
                .setPositiveButton(getString(R.string.Download_VPN_OK), (dialog, which) -> {
                    Bundle result = new Bundle();
                    result.putBoolean(BUNDLE_KEY_DOWNLOAD_VPN, true);
                    // The child fragment needs to still set the result on its parent fragment manager
                    getParentFragmentManager().setFragmentResult(REQUEST_KEY_DOWNLOAD_VPN, result);
                    downloadVPN();})
                .setNegativeButton(getString(R.string.Download_VPN_NO), (dialog, which) -> {
                    Bundle result = new Bundle();
                    result.putBoolean(BUNDLE_KEY_DOWNLOAD_VPN, false);
                    // The child fragment needs to still set the result on its parent fragment manager
                    getParentFragmentManager().setFragmentResult(REQUEST_KEY_DOWNLOAD_VPN, result);
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
