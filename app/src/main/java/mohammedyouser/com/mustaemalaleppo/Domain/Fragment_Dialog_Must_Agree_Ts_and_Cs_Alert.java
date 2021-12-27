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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.BUNDLE_KEY_DOWNLOAD_VPN;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.REQUEST_KEY_DOWNLOAD_VPN;

/**
 * Created by mohammed_youser on 4/12/2017.
 */

public class Fragment_Dialog_Must_Agree_Ts_and_Cs_Alert extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(getActivity(), R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.title_fragment_Agree_Ts_and_Cs_alert))
                .setMessage(getString(R.string.message_info_fragment_agree_ts_and_cs_alert))
                .setPositiveButton(getString(R.string.Got_IT), (dialog, which) -> {
                })
                .create();


    }


}
