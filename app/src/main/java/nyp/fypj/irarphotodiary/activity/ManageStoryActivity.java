package nyp.fypj.irarphotodiary.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.jfeinstein.jazzyviewpager.JazzyViewPager;
import com.viewpagerindicator.CirclePageIndicator;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.dto.Album;
import nyp.fypj.irarphotodiary.dto.ImageProfile;
import nyp.fypj.irarphotodiary.fragment.ViewStorySingleFragment;
public class ManageStoryActivity extends FragmentActivity {
    private JazzyViewPager viewPager;
    private CirclePageIndicator circlePageIndicator;
    private ArrayList<ImageProfile> imageProfiles;
    private String albumTitle;
    private String albumDes;
    private String albumId;
    private String albumDateUploaded;
    private Album album;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_story);
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        viewPager = (JazzyViewPager) findViewById(R.id.viewPagerE);
        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicatorE);
        imageProfiles = getIntent().getParcelableArrayListExtra("imageProfiles");
        //get the updated info
        if(getIntent().getParcelableExtra("imageProfile")!= null) {
            ImageProfile imageProfile = getIntent().getParcelableExtra("imageProfile");
            imageProfiles.set(viewPager.getCurrentItem(), imageProfile);
        }
        albumTitle = getIntent().getStringExtra("albumTitle");
        albumDes = getIntent().getStringExtra("albumDescription");
        albumId = getIntent().getStringExtra("albumId");
        albumDateUploaded= getIntent().getStringExtra("albumDateUploaded");
        album = new Album();
        album.set_id(albumId);
        album.setTitle(albumTitle);
        album.setDescription(albumDes);
        album.setImageProfiles(imageProfiles);
        album.setDateUploaded(albumDateUploaded);
        ViewStoryPagerAdapter viewStoryPagerAdapter = new ViewStoryPagerAdapter(ManageStoryActivity.this.getSupportFragmentManager(), imageProfiles);
        viewPager.setAdapter(viewStoryPagerAdapter);
        viewPager.setTransitionEffect(JazzyViewPager.TransitionEffect.Accordion);
        circlePageIndicator.setViewPager(viewPager);
    }
    @Override
    public void onResume() {
        super.onResume();
        // Set title
        if(albumTitle == null){
            getActionBar()
                    .setTitle(imageProfiles.get(viewPager.getCurrentItem()).getTitle());}
        else{
            getActionBar()
                    .setTitle(albumTitle);}
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_story_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.editStory:
               // Toast.makeText(getApplicationContext(), "test: "  +  imageProfiles.get(viewPager.getCurrentItem()).getTitle(), Toast.LENGTH_LONG).show();
                Intent i = new Intent(ManageStoryActivity.this, UpdateImageActivity.class);
                i.putExtra("imageProfile", imageProfiles.get(viewPager.getCurrentItem()));
                i.putParcelableArrayListExtra("imageProfiles", imageProfiles);
                startActivityForResult(i, 1);
                break;
            case R.id.removeStory:
                new AlertDialog.Builder(this)
                        .setTitle("Delete Photo")
                        .setMessage("Are you sure you want to delete this photo?")
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_action_discard)
                        .show();
                break;
            case R.id.new_album:
                Intent a = new Intent(ManageStoryActivity.this, CreateStoryListActivity.class);
                startActivity(a);
                break;
            case R.id.update_album:
                Intent intent = new Intent(ManageStoryActivity.this, UpdateStoryListActivity.class);
                intent.putParcelableArrayListExtra("imageProfiles", imageProfiles);
                intent.putExtra("albumTitle", albumTitle);
                intent.putExtra("albumDescription", albumDes);
                startActivity(intent);
                break;
            case R.id.delete_album:
                new AlertDialog.Builder(this)
                        .setTitle("Delete Album")
                        .setMessage("Are you sure you want to delete this album?")
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                //    String imgProfileId = imageProfiles.get(viewPager.getCurrentItem()).getFilename();
                                // make a httpclient call with uri provided by kang fei to remove this image profile based on its filename (unique key).


                                AsyncTask<Void, Integer, Void> task = new AsyncTask<Void, Integer, Void>() {
                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        Log.e("Album Delete", "Before delete");
                                        // continue with delete
                                        //    String imgProfileId = imageProfiles.get(viewPager.getCurrentItem()).getFilename();
                                        // make a httpclient call with uri provided by kang fei to remove this image profile based on its filename (unique key).
                                        Gson gson = new Gson();
                                        String albumJson = gson.toJson(album);

                                        try {
                                            HttpClient httpClient = new DefaultHttpClient();
                                            HttpPost httpPost = new HttpPost("http://fypj-124465r.rhcloud.com/delete/album");
                                            httpPost.setHeader("Content-Type", "application/json");
                                            httpPost.setEntity(new StringEntity(albumJson));
                                            HttpResponse httpResponse = httpClient.execute(httpPost); //TODO: not used?
                                            Log.e("AlbumJson", albumJson);
                                        }catch (Exception ex){
                                            ex.printStackTrace();
                                        }
                                        Log.e("Album Delete", "after delete");
                                        return null;
                                    }
                                };
                                task.execute();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_action_discard)
                        .show();

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