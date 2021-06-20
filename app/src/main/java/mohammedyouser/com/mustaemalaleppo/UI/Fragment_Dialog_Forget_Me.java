package mohammedyouser.com.mustaemalaleppo.UI;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import mohammedyouser.com.mustaemalaleppo.R;

import static android.content.Context.MODE_PRIVATE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;
import static mohammedyouser.com.mustaemalaleppo.UI.SessionManager.SHP_KEY_ISREMEMBERME;
import static mohammedyouser.com.mustaemalaleppo.UI.SessionManager.SHP_KEY_PASSWORD;
import static mohammedyouser.com.mustaemalaleppo.UI.SessionManager.SHP_KEY_PHONE_NUMBER;

public class Fragment_Dialog_Forget_Me extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.title_fragment_forget_me))
                .setMessage(getString(R.string.message_info_fragment_forget_me))
                .setPositiveButton(getString(R.string.btn_forget_me), (dialog, which) -> {
                    Bundle result = new Bundle();
                    result.putBoolean(BUNDLE_KEY_FORGET_ME, true);
                    // The child fragment needs to still set the result on its parent fragment manager
                    getParentFragmentManager().setFragmentResult(REQUEST_KEY_FORGET_ME, result);
                    forgetMe();
                })
                .setNegativeButton(getString(R.string.btn_keep_remember_me), (dialog, which) -> {
                    Bundle result = new Bundle();
                    result.putBoolean(BUNDLE_KEY_FORGET_ME, false);
                    // The child fragment needs to still set the result on its parent fragment manager
                    getParentFragmentManager().setFragmentResult(REQUEST_KEY_FORGET_ME, result);
                    Toast.makeText(getActivity(), getActivity().getString(R.string.message_info_not_forget_me), Toast.LENGTH_SHORT).show();

                })
                .create();


    }

    private void forgetMe() {

        SessionManager sessionManager = new SessionManager(getActivity(), SessionManager.SESSION_NAME_REMEMBER_ME);
        SharedPreferences shpref_user_session = getActivity().getSharedPreferences(SessionManager.SESSION_NAME_REMEMBER_ME, MODE_PRIVATE);

        SharedPreferences.Editor editor = shpref_user_session.edit();

        editor.putBoolean(SHP_KEY_ISREMEMBERME, true);
        editor.putString(SHP_KEY_PHONE_NUMBER, "");
        editor.putString(SHP_KEY_PASSWORD, "");
        editor.apply();
        Toast.makeText(getActivity(), getActivity().getString(R.string.message_info_forget_me), Toast.LENGTH_SHORT).show();

    }
}
