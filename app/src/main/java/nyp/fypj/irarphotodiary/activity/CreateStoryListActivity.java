package nyp.fypj.irarphotodiary.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudinary.Cloudinary;
import com.google.gson.Gson;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.application.BootstrapApplication;
import nyp.fypj.irarphotodiary.dto.ImageProfile;
import nyp.fypj.irarphotodiary.util.ColorProfiler;
import nyp.fypj.irarphotodiary.util.ColorThief;

public class CreateStoryListActivity extends FragmentActivity {
    private DragSortListView createStoryList;
    private CreateStoryListAdapter createStoryListAdapter;
    private ImageSize thumbnailSize = new ImageSize(128,128);
    private ArrayList<ImageProfile> imageProfiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story_list);

        if(savedInstanceState !=null){
            imageProfiles = savedInstanceState.getParcelableArrayList("imageProfiles");
        }else{
            ImageProfile imageProfile1 = new ImageProfile();
            imageProfile1.setTitle("New stuff coming up soon!");
            imageProfile1.setDescription("How about adding some interesting description to this image?");

            ImageProfile imageProfile2 = new ImageProfile();
            imageProfile2.setTitle("New stuff coming up soon!");
            imageProfile2.setDescription("How about adding some interesting description to this image?");

            imageProfiles = new ArrayList<ImageProfile>();
            imageProfiles.add(imageProfile1);
            imageProfiles.add(imageProfile2);
        }

        createStoryListAdapter = new CreateStoryListAdapter(this, imageProfiles);

        //http://stackoverflow.com/questions/14813882/bauerca-drag-sort-listview-simple-example
        //android:id="@android:id/list"
        createStoryList = (DragSortListView) findViewById(R.id.createStoryDragSortListView);
        createStoryList.setAdapter(createStoryListAdapter);
        createStoryList.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                if(from != to){
                    createStoryListAdapter.reorder(from, to);
                }
            }
        });

        DragSortController dragSortController = new DragSortController(createStoryList);
        dragSortController.setRemoveEnabled(false);
        dragSortController.setSortEnabled(true);
        dragSortController.setDragInitMode(DragSortController.ON_DOWN);
        dragSortController.setDragHandleId(R.id.createStoryItemPosition);

        createStoryList.setFloatViewManager(dragSortController);
        createStoryList.setOnTouchListener(dragSortController);
        createStoryList.setDragEnabled(true);

        createStoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ImageProfile imageProfile = (ImageProfile) createStoryListAdapter.getItem(position);

                Intent intent = new Intent(CreateStoryListActivity.this, CreateStoryActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("imageProfile", imageProfile);
                startActivityForResult(intent, 1); //TODO: make the request code final
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                int position = intent.getIntExtra("position", -1);
                ImageProfile imageProfile = intent.getExtras().getParcelable("imageProfile");
                createStoryListAdapter.set(position, imageProfile);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_story_list_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.createStoryListAddNew:
                ImageProfile imageProfile = new ImageProfile();
                imageProfile.setTitle("New stuff coming up soon!");
                imageProfile.setDescription("How about adding some interesting description to this image?");
                createStoryListAdapter.add(imageProfile);
                break;
            case R.id.createStoryListUpload:
                final String albumId = UUID.randomUUID().toString().toLowerCase();

                ///// async task
                AsyncTask<Void,Integer,Void> task = new AsyncTask<Void,Integer,Void>() {
                    private List<ImageProfile> imageProfiles = createStoryListAdapter.imageProfiles;
                    private NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    private NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(CreateStoryListActivity.this);

                    @Override
                    protected Void doInBackground(Void... voids) {

                        // For each of the imageProfile
                        //for (final ImageProfile imageProfile : imageProfiles) {
                        for(int i = 0; i< imageProfiles.size();i++){
                            publishProgress(i+1, imageProfiles.size());

                            final ImageProfile imageProfile = imageProfiles.get(i);
                            imageProfile.setOrder(i); // IMPORTANT: the final position/index/order is only saved to the entity just before uploading to avoid confusion

                            ImageSize imageSize = new ImageSize(320,480);

                            // load the bitmap image from disk cache
                            // IMPORTANT: Use loadImageSync so that the async task will not spawn additional threads, which will cause out of memory error if too many concurrent upload exist.
                            Bitmap loadedImage = ImageLoader.getInstance().loadImageSync(imageProfile.getUri(), imageSize);
                            try {
                                // Compute dominant colors from the bitmap
                                List<int[]> rgbColors = ColorThief.compute(loadedImage, 5); //TODO: THE MAX NUMBER!! FINAL CONSTANT

                                // convert rgb to lab color space
                                List<double[]> labColors = new ArrayList<double[]>(5);
                                for (int[] rgbColor : rgbColors) {
                                    labColors.add(ColorProfiler.RGBtoLAB(rgbColor));
                                }
                                // set the color profiles to the image POJO
                                imageProfile.setRgbColors(rgbColors);
                                imageProfile.setLabColors(labColors);

                                // Upload image to cloudinary
                                // Get instance from application constant DO NOT INITIALIZE ANOTHER.
                                File file = new File(imageProfile.getUri().substring(7));

                                Cloudinary cloudinary = ((BootstrapApplication) CreateStoryListActivity.this.getApplication()).getCloudinary();
                                JSONObject uploadResult = cloudinary.uploader().upload(file, Cloudinary.emptyMap());

                                // set the format and public url from cloudinary uplaod response
                                // if the key is not present in the upload result (meaning upload failed), a JSONException will be thrown
                                imageProfile.setFilename(uploadResult.get("public_id").toString());
                                imageProfile.setExtension(uploadResult.get("format").toString());
                                imageProfile.setAlbumId(albumId);

                                // flatten imageProfile to json
                                Gson gson = new Gson();
                                String imageProfileJson = gson.toJson(imageProfile);

                                // Upload json to databasesss
                                HttpClient httpClient = new DefaultHttpClient();
                                HttpPost httpPost = new HttpPost("http://fypj-124465r.rhcloud.com/images");
                                httpPost.setHeader("Content-Type", "application/json");
                                httpPost.setEntity(new StringEntity(imageProfileJson));
                                HttpResponse httpResponse = httpClient.execute(httpPost); //TODO: not used?

                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        }
                        return null;
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();

                        // start the notification progress
                        notificationCompat.setSmallIcon(R.drawable.ic_launcher);
                        notificationCompat.setContentTitle("Upload Album");
                        notificationCompat.setProgress(0, 0, true);
                        notificationManager.notify(1, notificationCompat.build());
                    }

                    @Override
                    protected void onProgressUpdate(Integer... progress) {
                        super.onProgressUpdate(progress);

                        int count = progress[0];
                        int total = progress[1];

                        notificationCompat.setContentText("Uploading " + count + " of " + total+"...");
                        notificationCompat.setProgress(0, 0, true);
                        notificationManager.notify(1, notificationCompat.build());
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);

                        notificationCompat.setContentText("Upload completed.");
                        notificationCompat.setProgress(0, 0, false);
                        notificationManager.notify(1, notificationCompat.build());
                    }
                };

                task.execute();
                ///// end of async task
                break;
            case R.id.createStoryListCancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("imageProfiles", imageProfiles);

        super.onSaveInstanceState(outState);
    }

    private class CreateStoryListAdapter extends BaseAdapter {

        private List<ImageProfile> imageProfiles;
        private LayoutInflater layoutInflater;

        private CreateStoryListAdapter(Context context, List<ImageProfile> imageProfiles) {
            this.imageProfiles = imageProfiles;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return imageProfiles.size();
        }

        @Override
        public Object getItem(int position) {
            return imageProfiles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            final ViewHolder viewHolder;

            if(convertView == null){
                view = layoutInflater.inflate(R.layout.adapter_activity_create_story_list_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.createStoryItemThumbnail = (ImageView) view.findViewById(R.id.createStoryItemThumbnail);
                viewHolder.createStoryItemTitle = (TextView) view.findViewById(R.id.createStoryItemTitle);
                viewHolder.createStoryItemDescription = (TextView) view.findViewById(R.id.createStoryItemDescription);
                viewHolder.createStoryItemPosition = (TextView) view.findViewById(R.id.createStoryItemPosition);

                view.setTag(viewHolder);

            }else{
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            ImageProfile imageProfile = imageProfiles.get(position);
            viewHolder.createStoryItemTitle.setText(imageProfile.getTitle());
            viewHolder.createStoryItemDescription.setText(imageProfile.getDescription());
            viewHolder.createStoryItemPosition.setText("#"+position);
            if(imageProfile.getUri() != "" || imageProfile.getUri() != null){
                viewHolder.createStoryItemThumbnail.setImageBitmap(null);
                ImageLoader.getInstance().loadImage(imageProfile.getUri(), thumbnailSize, new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        //DEBUG: Log.e("TADAH", "image"+": "+imageUri);

                        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(loadedImage, 128, 128);
                        viewHolder.createStoryItemThumbnail.setImageBitmap(thumbnail);

                        Animation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
                        fadeIn.setDuration(300);
                        fadeIn.setFillAfter(true);
                        viewHolder.createStoryItemThumbnail.startAnimation(fadeIn);
                    }

                });
            }

            return view;
        }

        public void add(ImageProfile imageProfile){
            imageProfiles.add(imageProfile);
            notifyDataSetChanged();
        }

        public void add(int position, ImageProfile imageProfile){
            imageProfiles.add(position, imageProfile);
            notifyDataSetChanged();
        }

        public void set(int position, ImageProfile imageProfile){
            imageProfiles.set(position, imageProfile);
            notifyDataSetChanged();
        }

        public void remove(int position){
            imageProfiles.remove(position);
            notifyDataSetChanged();
        }

        public void reorder(int from, int to){
            ImageProfile imageProfile = imageProfiles.get(from);
            imageProfiles.remove(from);
            imageProfiles.add(to,imageProfile);
            notifyDataSetChanged();
        }

        public void swap(int from, int to){
            Collections.swap(imageProfiles, from, to);
            notifyDataSetChanged();
        }

        private class ViewHolder{
            public ImageView createStoryItemThumbnail;
            public TextView createStoryItemTitle;
            public TextView createStoryItemDescription;
            public TextView createStoryItemPosition;
        }
    }
}
