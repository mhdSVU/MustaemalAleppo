package mohammedyouser.com.mustaemalaleppo.UI;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by mohammed_youser on 10/2/2017.
 */

public class My_FragmentPageAdapter_Auth extends FragmentStatePagerAdapter {
    private  int tapCount;

    public My_FragmentPageAdapter_Auth(FragmentManager fm, int tapCount) {
        super(fm);
        this.tapCount=tapCount;
    }

    @Override
    public Fragment getItem(int tab_position) {

        switch (tab_position){

            case 0:

                return new SignIn_fragment();
            case 1:

                return new SignUp_fragment();




default: return null;
        }




    }

    @Override
    public int getCount() {
        return tapCount;
    }
}
