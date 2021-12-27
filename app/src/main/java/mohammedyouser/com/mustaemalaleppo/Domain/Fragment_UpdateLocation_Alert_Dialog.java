package mohammedyouser.com.mustaemalaleppo.Domain;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.BUNDLE_KEY_REQUEST_LOCATION_UPDATES;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.BUNDLE_KEY_REQUEST_LOCATION_UPDATE;

/**
 * Created by mohammed_youser on 4/12/2017.
 */

public class Fragment_UpdateLocation_Alert_Dialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(getActivity(), R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.title_fragment_update_location_confirm))
                .setMessage(getString(R.string.message_alert_update_location_confirm))
                .setPositiveButton(getString(R.string.btn_confirm_update_location), (dialog, which) -> {
                    Bundle result = new Bundle();
                    result.putBoolean(BUNDLE_KEY_REQUEST_LOCATION_UPDATE, true);
                    // The child fragment needs to still set the result on its parent fragment manager
                    getParentFragmentManager().setFragmentResult(BUNDLE_KEY_REQUEST_LOCATION_UPDATE, result);
                })

                .setNegativeButton(getString(R.string.btn_preserve_location), (dialog, which) -> {

                    Bundle result = new Bundle();
                    result.putBoolean(BUNDLE_KEY_REQUEST_LOCATION_UPDATE, false);
                    // The child fragment needs to still set the result on its parent fragment manager
                    getParentFragmentManager().setFragmentResult(BUNDLE_KEY_REQUEST_LOCATION_UPDATE, result);

                    Toast.makeText(getActivity(), getString(R.string.message_info_loc_preserved), Toast.LENGTH_SHORT).show();
                })
                .create();


    }

}
