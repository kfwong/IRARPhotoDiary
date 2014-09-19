package nyp.fypj.irarphotodiary.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import nyp.fypj.irarphotodiary.R;

public class DashboardFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new DashboardPagerAdapter(getChildFragmentManager()));

        return view;
    }

    private class DashboardPagerAdapter extends FragmentPagerAdapter {

        public DashboardPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0: return new DashboardHomeFragment();
                case 1: return new SecondFragment();
                case 2: return ThirdFragment.newInstance("ThirdFragment, Instance 1");
                case 3: return new SecondFragment();
                case 4: return new SecondFragment();
                default: return new SecondFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return("Recent Stories").toUpperCase();
                case 1: return ("Second").toUpperCase();
                case 2: return ("Third").toUpperCase();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 5;
        }
    }

    // Dumb bug fix for calling nested fragments onActivityResult
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // notifying nested fragments (support library bug fix)
        final FragmentManager childFragmentManager = getChildFragmentManager();

        if (childFragmentManager != null) {
            final List<Fragment> nestedFragments = childFragmentManager.getFragments();

            if (nestedFragments == null || nestedFragments.size() == 0) return;

            for (Fragment childFragment : nestedFragments) {
                //TODO: need to prevent double executing while attaching same fragment
                if (childFragment != null && !childFragment.isDetached() && !childFragment.isRemoving()) {
                    Log.v(childFragment.getClass().getName(), childFragment.getClass().getName());
                    childFragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }
}