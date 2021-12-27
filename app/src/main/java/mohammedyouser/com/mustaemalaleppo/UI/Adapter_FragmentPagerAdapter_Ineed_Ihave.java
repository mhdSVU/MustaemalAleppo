package mohammedyouser.com.mustaemalaleppo.UI;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

/**
 * Created by mohammed_youser on 11/4/2017.
 */

public class Adapter_FragmentPagerAdapter_Ineed_Ihave extends FragmentStatePagerAdapter {
    private final ArrayList<String> tabs;
    private  ArrayList<Fragment> fragments=new ArrayList<>();
    private int tapCount;

    public Adapter_FragmentPagerAdapter_Ineed_Ihave(FragmentManager fm, int tapCount, ArrayList<String> tabs) {
        super(fm);
        this.tapCount = tapCount;
        this.tabs = tabs;

    }

    @NonNull
    @Override
    public Fragment getItem(int tab_position) {

        switch (tab_position) {

            case 0:

                return new Fragment_Ineed();
            case 1:

                return new Fragment_Ihave();


            default: {

                throw new NullPointerException("Null fragment");
            }
        }


    }

    @Override
    public int getCount() {
        return tabs.size();
    }


    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final Fragment fragment = (Fragment) super.instantiateItem(container, position);

        //  instansiatedFragments.put(position, new WeakReference<>(fragment));
        fragments.add(fragment);


        return fragment;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
       // instansiatedFragments.remove(position);
        if(position<fragments.size())
        fragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getFragment(final int position) {
        try {
            return fragments.get(position);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TAG", "getFragment: ");
        }
        return new Fragment_Ineed();

    }

    @Override
    public CharSequence getPageTitle(int position) {

        return tabs.get(position);
    }
}
