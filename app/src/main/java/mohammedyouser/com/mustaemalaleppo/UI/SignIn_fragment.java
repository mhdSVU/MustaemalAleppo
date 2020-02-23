package mohammedyouser.com.mustaemalaleppo.UI;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.SignInButton;

import mohammedyouser.com.mustaemalaleppo.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class
SignIn_fragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private Button mButton_Phone;
    private Button mButton__EmlPass;
    private SignInButton mButton_Google;
    private View fragment_view;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentInteractionListener)
        {
            mListener=(OnFragmentInteractionListener) context;
        }
        else {
            throw new ClassCastException(context.toString() + " must implement OnFragmentInteractionListener.");
        }


    }

    public SignIn_fragment() {
        // Required empty public constructor

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
 fragment_view=inflater.inflate(R.layout.fragment_sign_in,container,false);

        return fragment_view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onViewClickInFragment(View v) {
        if (mListener != null) {
            mListener.onFragmentInteraction(fragment_view,v.getId());
        }
    }

    @Override
    public void onClick(View v) {
        onViewClickInFragment(v);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mButton_Phone=(Button) view.findViewById(R.id.sign_in_button_Phone);
        mButton__EmlPass=(Button) view.findViewById(R.id.sign_in_button_EmlPass);
        mButton_Google=(SignInButton) view.findViewById(R.id.button_sign_in_Google);
        mButton_Phone.setOnClickListener(this);
        mButton__EmlPass.setOnClickListener(this);
        mButton_Google.setOnClickListener(this);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(View view,int viewId);
    }
}
