package mohammedyouser.com.mustaemalaleppo.UI;

import android.app.FragmentManager;
import android.content.Intent;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import mohammedyouser.com.mustaemalaleppo.ConnectionReceiverListener;
import mohammedyouser.com.mustaemalaleppo.Device.ConnectionReceiver;
import mohammedyouser.com.mustaemalaleppo.Domain.VPN_AlertDialogFragment;
import mohammedyouser.com.mustaemalaleppo.R;
import mohammedyouser.com.mustaemalaleppo.R.*;

import static mohammedyouser.com.mustaemalaleppo.Device.NetworkUtil.CheckNetworkConnectionStatus;
import static mohammedyouser.com.mustaemalaleppo.Device.NetworkUtil.isConnected;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;


public class AuthActivity extends AppCompatActivity implements
        TabLayout.OnTabSelectedListener,
        SignIn_fragment.OnFragmentInteractionListener,
        SignUp_fragment.OnFragmentInteractionListener,
        ConnectionReceiverListener  {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private My_FragmentPageAdapter_Auth myFragmentStatePagerViewAdapter;
    private Intent sign_in_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        setUpAuthTabLayout();

        Toolbar toolbar = (Toolbar) findViewById(id.toolBar);
        setSupportActionBar(toolbar);

        alertVPN();

    }
    public void setConnectionListener(ConnectionReceiverListener listener) {
        ConnectionReceiver.connectionReceiverListener = listener;

    }
    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        setConnectionListener(this);

        CheckNetworkConnectionStatus();
    }

    private void setUpAuthTabLayout() {
        tabLayout=(TabLayout) findViewById(R.id.tabLayout);

        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(color.selcted_tab));
        tabLayout.setTabTextColors(getResources().getColor(R.color.normal),getResources().getColor(R.color.selcted_tab));

        myFragmentStatePagerViewAdapter =new My_FragmentPageAdapter_Auth(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager= (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(myFragmentStatePagerViewAdapter);

        tabLayout.addOnTabSelectedListener(this);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);
        try {
            tabLayout.getTabAt(0).setText(getString(R.string.sign_in));
            tabLayout.getTabAt(1).setText(getString(R.string.sign_up));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }


    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabSelected(final TabLayout.Tab tab) {

        viewPager.setCurrentItem(tab.getPosition());


    }



    @Override
    public void onFragmentInteraction(View view, int viewId) {

        switch (viewId)
        {
            case id.sign_in_button_Phone :
            {
                sign_in_intent=new Intent(this,SignInActivity_Phone.class);
                startActivity(sign_in_intent);

            }
            break;
            case id.sign_in_button_EmlPass:
            {
                sign_in_intent=new Intent(this,SignInActivity_EmlPass.class);
                startActivity(sign_in_intent);

            }
            break;
            case id.button_sign_in_Google:
            {
                sign_in_intent=new Intent(this,SignInActivity_Google.class);
                sign_in_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(sign_in_intent);


            }
            break;



        }


    }

    @Override
    public void onBackPressed() {

            moveTaskToBack(true);

    }

    private void alertVPN() {
        FragmentManager fragmentManager = getFragmentManager();


        android.app.Fragment fragment = fragmentManager.findFragmentByTag("VPN_Alert_Fragment");
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }


        VPN_AlertDialogFragment vpn_alertDialogFragment = new VPN_AlertDialogFragment();
        vpn_alertDialogFragment.show(fragmentManager, "VPN_Alert_Fragment");


    }

    @Override
    public  void onNetworkConnectionStatusChanged() {
        CheckNetworkConnectionStatus();

        if(!isConnected) {

            //show a No Internet Alert or Dialog
            showProgressDialog(this,getString(R.string.title_network_con_not_found),getString(R.string.message_info_network_con_check));

        }else{

            // dismiss the dialog or refresh the activity
            hideProgressDialog();
        }
    }



}
