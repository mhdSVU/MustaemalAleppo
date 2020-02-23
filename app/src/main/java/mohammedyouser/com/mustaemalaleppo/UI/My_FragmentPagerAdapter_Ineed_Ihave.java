package mohammedyouser.com.mustaemalaleppo.UI;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import android.util.SparseArray;
import android.view.ViewGroup;
import java.lang.ref.WeakReference;

/**
 * Created by mohammed_youser on 11/4/2017.
 */

public class My_FragmentPagerAdapter_Ineed_Ihave extends FragmentStatePagerAdapter {
    private SparseArray<WeakReference<Fragment>> instansiatedFragments=new SparseArray<>();
    private final ArrayList<String> tabs;
    private  int tapCount;

    public My_FragmentPagerAdapter_Ineed_Ihave(FragmentManager fm, int tapCount, ArrayList<String> tabs) {
        super(fm);
        this.tapCount=tapCount;
        this.tabs=tabs;
    }

    @Override
    public Fragment getItem(int tab_position) {

        switch (tab_position){

            case 0:

                return new Ineed_Fragment();
            case 1:

                return new Ihave_Fragment();




            default: return null;
        }




    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    @Override
    public Object instantiateItem(final ViewGroup container,final int position) {
       final Fragment fragment= (Fragment) super.instantiateItem(container,position);

        instansiatedFragments.put(position,new WeakReference<>(fragment));


        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        instansiatedFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getFragment(final int position) {

       final WeakReference<Fragment> wr=instansiatedFragments.get(position);
        if(wr!=null)
        {
            return wr.get();
        }
        else {return null;}

    }

    @Override
    public CharSequence getPageTitle(int position) {

        return tabs.get(position);
    }
}
