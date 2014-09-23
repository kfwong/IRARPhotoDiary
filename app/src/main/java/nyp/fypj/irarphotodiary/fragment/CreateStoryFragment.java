package nyp.fypj.irarphotodiary.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import nyp.fypj.irarphotodiary.R;

public class CreateStoryFragment extends Fragment {

    private ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_story, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.createStoryFragmentViewPager);
        viewPager.setAdapter(new CreateStoryPagerAdapter(getChildFragmentManager()));

        final CirclePageIndicator circlePageIndicator = (CirclePageIndicator) view.findViewById(R.id.createStoryFragmentCirclePageIndicator);
        circlePageIndicator.setViewPager(viewPager);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.create_story_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        CreateStoryPagerAdapter createStoryPagerAdapter =(CreateStoryPagerAdapter)viewPager.getAdapter();
        switch (item.getItemId()){
            case R.id.createStoryCanvasInsertCanvas:
                // Add new canvas base on viewpager's current index/position
                createStoryPagerAdapter.data.add(viewPager.getCurrentItem(), new CreateStoryCanvasFragment());
                createStoryPagerAdapter.notifyDataSetChanged();
                break;
            case R.id.createStoryCanvasRemoveCanvas:
                createStoryPagerAdapter.data.remove(viewPager.getCurrentItem());
                createStoryPagerAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //http://stackoverflow.com/questions/18747975/difference-between-fragmentpageradapter-and-fragmentstatepageradapter
    private class CreateStoryPagerAdapter extends FragmentStatePagerAdapter {

        private List<CreateStoryCanvasFragment> data;

        private CreateStoryPagerAdapter(FragmentManager fm) {
            super(fm);
            this.data = new ArrayList<CreateStoryCanvasFragment>();
        }

        @Override
        public Fragment getItem(int i) {
            return data.get(i);
        }

        @Override
        public int getCount() {
            return data.size();
        }


        //http://stackoverflow.com/questions/10396321/remove-fragment-page-from-viewpager-in-android
        @Override
        public int getItemPosition(Object object){
            return PagerAdapter.POSITION_NONE;
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
