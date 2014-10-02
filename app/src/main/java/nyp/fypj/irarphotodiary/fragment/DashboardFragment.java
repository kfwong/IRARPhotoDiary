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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jfeinstein.jazzyviewpager.JazzyViewPager;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.UnderlinePageIndicator;

import java.util.List;

import nyp.fypj.irarphotodiary.R;

public class DashboardFragment extends Fragment {

    private JazzyViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        viewPager = (JazzyViewPager) view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new DashboardPagerAdapter(getChildFragmentManager()));
        viewPager.setTransitionEffect(JazzyViewPager.TransitionEffect.FlipHorizontal);

        final TitlePageIndicator titlePageIndicator = (TitlePageIndicator) view.findViewById(R.id.indicator);
        titlePageIndicator.setViewPager(viewPager);

        return view;
    }

    private class DashboardPagerAdapter extends FragmentPagerAdapter {

        public DashboardPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {

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
            return("Recent Stories").toUpperCase();
        }

        @Override
        public int getCount() {
            return 5;
        }

        // required for JazzyViewPager
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            Object obj = super.instantiateItem(container, position);
            viewPager.setObjectForPosition(obj, position);
            return obj;
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
                    childFragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }
}