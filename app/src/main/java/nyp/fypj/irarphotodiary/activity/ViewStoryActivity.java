package nyp.fypj.irarphotodiary.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jfeinstein.jazzyviewpager.JazzyViewPager;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.dto.ImageProfile;
import nyp.fypj.irarphotodiary.fragment.DashboardHomeFragment;
import nyp.fypj.irarphotodiary.fragment.ViewStorySingleFragment;

public class ViewStoryActivity extends FragmentActivity {

    private JazzyViewPager viewPager;
    private CirclePageIndicator circlePageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_story);

        viewPager = (JazzyViewPager) findViewById(R.id.viewPager);
        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);

        AsyncTask<Void,Void,Void> task = new AsyncTask<Void,Void,Void>() {

            private ArrayList<ImageProfile> imageProfiles;

            @Override
            protected Void doInBackground(Void... voids) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response;
                String responseString = null;
                try {
                    response = httpclient.execute(new HttpGet("https://fypj-124465r.rhcloud.com/images/"));
                    StatusLine statusLine = response.getStatusLine();
                    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        out.close();
                        responseString = out.toString();
                    } else{
                        //Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Gson gson = new Gson();
                imageProfiles = gson.fromJson(responseString, new TypeToken<ArrayList<ImageProfile>>(){}.getType());

                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                ViewStoryPagerAdapter viewStoryPagerAdapter = new ViewStoryPagerAdapter(ViewStoryActivity.this.getSupportFragmentManager(), imageProfiles);
                viewPager.setAdapter(viewStoryPagerAdapter);
                viewPager.setTransitionEffect(JazzyViewPager.TransitionEffect.Accordion);

                circlePageIndicator.setViewPager(viewPager);
            }
        }; // AsyncTask

        task.execute();
    }

    private class ViewStoryPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<ImageProfile> imageProfiles;

        public ViewStoryPagerAdapter(FragmentManager fm, ArrayList<ImageProfile> imageProfiles){
            super(fm);
            this.imageProfiles = imageProfiles;
        }

        public ViewStoryPagerAdapter(FragmentManager fm) {
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