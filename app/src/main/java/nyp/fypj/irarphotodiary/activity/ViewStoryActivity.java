package nyp.fypj.irarphotodiary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.jfeinstein.jazzyviewpager.JazzyViewPager;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.dto.ImageProfile;
import nyp.fypj.irarphotodiary.fragment.ViewStorySingleFragment;

public class ViewStoryActivity extends FragmentActivity {

    private JazzyViewPager viewPager;
    private CirclePageIndicator circlePageIndicator;
    private ArrayList<ImageProfile> imageProfiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_story);

        viewPager = (JazzyViewPager) findViewById(R.id.viewPager);
        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);

        imageProfiles = getIntent().getParcelableArrayListExtra("imageProfiles");

        ViewStoryPagerAdapter viewStoryPagerAdapter = new ViewStoryPagerAdapter(ViewStoryActivity.this.getSupportFragmentManager(), imageProfiles);
        viewPager.setAdapter(viewStoryPagerAdapter);
        viewPager.setTransitionEffect(JazzyViewPager.TransitionEffect.Accordion);

        circlePageIndicator.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_story_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.viewStoryARMode:
                Intent i = new Intent(ViewStoryActivity.this, ARActivity.class);
                i.putParcelableArrayListExtra("imageProfiles", imageProfiles);
                startActivity(i);
                break;
            case R.id.viewStoryMapMode:
                Intent j = new Intent(ViewStoryActivity.this, GoogleMapActivity.class);
                j.putParcelableArrayListExtra("imageProfiles", imageProfiles);
                startActivity(j);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ViewStoryPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<ImageProfile> imageProfiles;

        public ViewStoryPagerAdapter(FragmentManager fm, ArrayList<ImageProfile> imageProfiles) {
            super(fm);
            this.imageProfiles = imageProfiles;
        }

        public ViewStoryPagerAdapter(FragmentManager fm) {
            // required by default
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ViewStorySingleFragment.newInstance(imageProfiles.get(position));
        }

        @Override
        public int getCount() {
            return imageProfiles.size();
        }

        // required for JazzyViewPager
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            Object obj = super.instantiateItem(container, position);
            viewPager.setObjectForPosition(obj, position);
            return obj;
        }
    }
}
