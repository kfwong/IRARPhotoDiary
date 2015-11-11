package sg.edu.nyp.irarphotodiary.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Ion;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

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
import java.util.Date;
import java.util.concurrent.ExecutionException;

import sg.edu.nyp.irarphotodiary.R;
import sg.edu.nyp.irarphotodiary.application.BootstrapApplication;
import sg.edu.nyp.irarphotodiary.dto.Album;
import sg.edu.nyp.irarphotodiary.dto.ImageProfile;
import sg.edu.nyp.irarphotodiary.dto.Tag;

public class CreateStoryListActivity extends FragmentActivity {
    private DragSortListView createStoryList;
    private CreateStoryListAdapter createStoryListAdapter;
    private ImageSize thumbnailSize = new ImageSize(128, 128);
    private ArrayList<ImageProfile> imageProfiles;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private FloatLabeledEditText albumTitle;
    private FloatLabeledEditText albumDescription;

    @Override
    protected void onStart() {
        super.onStart();

        locationListener = new CreateStoryLocationListener();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story_list);

        albumTitle = (FloatLabeledEditText) findViewById(R.id.albumTitle);
        albumDescription = (FloatLabeledEditText) findViewById(R.id.albumDescription);

        if (savedInstanceState != null) {
            imageProfiles = savedInstanceState.getParcelableArrayList("imageProfiles");
        } else {
            albumTitle.setText("My Album Title");
            albumDescription.setText("These are some collections of images I have!");

            ImageProfile imageProfile1 = new ImageProfile();
            imageProfile1.setTitle("Tap here to edit this content!");
            imageProfile1.setDescription("Select options on top right corner to Add new content to this album.");

            imageProfiles = new ArrayList<ImageProfile>();
            imageProfiles.add(imageProfile1);
        }

        createStoryListAdapter = new CreateStoryListAdapter(this, imageProfiles);

        //http://stackoverflow.com/questions/14813882/bauerca-drag-sort-listview-simple-example
        //android:id="@android:id/list"
        createStoryList = (DragSortListView) findViewById(R.id.createStoryDragSortListView);
        createStoryList.setAdapter(createStoryListAdapter);
        createStoryList.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                if (from != to) {
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
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
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

        switch (item.getItemId()) {
            case R.id.createStoryListAddNew:
                ImageProfile imageProfile = new ImageProfile();
                imageProfile.setTitle("New stuff coming up soon!");
                imageProfile.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed nisi eros, varius vitae quam quis, mattis dignissim elit. Ut ipsum orci, consequat vitae nisi nec, vulputate commodo mi. Ut eros lectus, malesuada a velit nec, posuere pharetra massa. Vestibulum feugiat egestas orci, sit amet volutpat ligula pretium a. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Vestibulum congue urna at varius feugiat. Etiam mattis pellentesque odio, elementum dapibus mauris placerat eu. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Duis nulla nulla, eleifend vitae lectus ac, scelerisque fringilla metus. Proin aliquam in felis eget convallis. Maecenas a est quis eros dapibus facilisis. Nunc ut massa feugiat, suscipit tellus ut, congue arcu. ");
                createStoryListAdapter.add(imageProfile);
                break;
            case R.id.createStoryListUpload:
                ///// async task
                AsyncTask<Void, Integer, Void> task = new AsyncTask<Void, Integer, Void>() {
                    private Album album = new Album();
                    private ArrayList<ImageProfile> imageProfiles = createStoryListAdapter.imageProfiles;
                    private NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    private NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(CreateStoryListActivity.this);

                    @Override
                    protected Void doInBackground(Void... voids) {

                        // Upload images
                        // For each of the imageProfile
                        //for (final ImageProfile imageProfile : imageProfiles) {
                        for (int i = 0; i < imageProfiles.size(); i++) {
                            publishProgress(i + 1, imageProfiles.size());

                            final ImageProfile imageProfile = imageProfiles.get(i);
                            imageProfile.setOrder(i); // IMPORTANT: the final position/index/order is only saved to the entity just before uploading to avoid confusion

                            //ImageSize imageSize = new ImageSize(320,480);

                            // load the bitmap image from disk cache
                            // IMPORTANT: Use loadImageSync so that the async task will not spawn additional threads, which will cause out of memory error if too many concurrent upload exist.
                            //Bitmap loadedImage = ImageLoader.getInstance().loadImageSync(imageProfile.getUri(), imageSize);
                            try {
                                // Compute dominant colors from the bitmap
                                /* NO LONGER USED
                                List<int[]> rgbColors = ColorThief.compute(loadedImage, 5); //TODO: THE MAX NUMBER!! FINAL CONSTANT

                                // convert rgb to lab color space
                                List<double[]> labColors = new ArrayList<double[]>(5);
                                for (int[] rgbColor : rgbColors) {
                                    labColors.add(ColorProfiler.RGBtoLAB(rgbColor));
                                }
                                // set the color profiles to the image POJO
                                imageProfile.setRgbColors(rgbColors);
                                imageProfile.setLabColors(labColors);
                                */

                                // Upload image to cloudinary
                                // Get instance from application constant DO NOT INITIALIZE ANOTHER.
                                File file = new File(imageProfile.getUri().substring(7));
                                Log.e("TADAH", "uploading image");
                                Cloudinary cloudinary = ((BootstrapApplication) CreateStoryListActivity.this.getApplication()).getCloudinary();
                                JSONObject uploadResult = cloudinary.uploader().upload(file, Cloudinary.emptyMap());

                                Log.e("TADAH", "uploaded: " + uploadResult);

                                // autotagging
                                /*
                                Host: api.imagga.com

                                Accept-Language: en-US,en;q=0.5
                                Accept-Encoding: gzip, deflate
                                DNT: 1
                                Authorization: Basic YWNjXzMxZGU3NjJlNDA3YTZhMzowMDczZDQ2NDMxY2Y5MjQ4YjM0ODE0ZTIxNjY1YWVhMA==
                                Pragma: no-cache
                                Cache-Control: no-cache
                                Referer: http://api.imagga.com/v1/tagging?api_key=acc_31de762e407a6a3&url=https://s3.amazonaws.com/images.seroundtable.com/google-sandy-hook-ribbon-1356095082.jpg
                                Cookie: imagga_session=eyJpdiI6ImZtSXBXd0ZyVnJyYTFXbURhRGVoZ0ZmXC94Zkg3bGpxTVdlS1hhRVRqZEZvPSIsInZhbHVlIjoiOFFlRWthNWUxYlwvK2prMmJqWkdxOVJNYTZ5VGlaZk83VEhqNG1uZStQWVBPT1RPTVF1QmwyMFc3WUFLcktwRXZycFNXUjhkS3BxWDNRWm9ETWRWWENnPT0iLCJtYWMiOiJlNDRhMDU3Y2QxMTgwZDZkYmRhZGQxZjE5NjE4Yjg5YTkxNzM3ZmYxNTJmOTM2OGEyNDhmYjYwODU0OTI2YzllIn0%3D
                                Connection: keep-alive

                                api_key=acc_31de762e407a6a3
                                url=https://s3.amazonaws.com/images.seroundtable.com/google-sandy-hook-ribbon-1356095082.jpg

                                 */
                                Log.e("TADAH", "processing autotagging");
                                JsonObject jsonObject = Ion.with(CreateStoryListActivity.this)
                                        .load("http://api.imagga.com/v1/tagging?api_key=acc_31de762e407a6a3&url=" + uploadResult.getString("url"))
                                        .addHeader("Authorization", "Basic YWNjXzMxZGU3NjJlNDA3YTZhMzowMDczZDQ2NDMxY2Y5MjQ4YjM0ODE0ZTIxNjY1YWVhMA==")
                                        .asJsonObject()
                                        .get();

                                Log.e("TADAH", "done autotagging: " + jsonObject.getAsJsonArray("results").get(0).getAsJsonObject().get("tags"));

                                ArrayList<Tag> tags = new Gson().fromJson(jsonObject.getAsJsonArray("results").get(0).getAsJsonObject().get("tags"), new TypeToken<ArrayList<Tag>>() {
                                }.getType());

                                Log.e("TADAH", "done conversion to entity");

                                //set to image's tags
                                imageProfile.setTags(tags);

                                // set the format and public url from cloudinary uplaod response
                                // if the key is not present in the upload result (meaning upload failed), a JSONException will be thrown
                                imageProfile.setFilename(uploadResult.get("public_id").toString());
                                imageProfile.setExtension(uploadResult.get("format").toString());

                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            } catch (ExecutionException ex) {
                                ex.printStackTrace();
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }

                        }// end for loop

                        // Upload imageProfiles
                        try {

                            album.setTitle(albumTitle.getTextString());
                            album.setDescription(albumDescription.getTextString());
                            album.setDateUploaded(new Date());
                            album.setImageProfiles(imageProfiles);

                            // flatten imageProfile to json
                            Gson gson = new Gson();
                            String albumJson = gson.toJson(album);

                            // Upload json to databasesss
                            HttpClient httpClient = new DefaultHttpClient();
                            HttpPost httpPost = new HttpPost("http://app-irarphotodiary.rhcloud.com/albums");
                            httpPost.setHeader("Content-Type", "application/json");
                            httpPost.setEntity(new StringEntity(albumJson));
                            HttpResponse httpResponse = httpClient.execute(httpPost); //TODO: not used?

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        return null;
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();

                        // start the notification progress
                        notificationCompat.setSmallIcon(R.drawable.ic_launcher);
                        notificationCompat.setContentTitle("Uploading Album");
                        notificationCompat.setProgress(0, 0, true);
                        notificationManager.notify(1, notificationCompat.build());
                    }

                    @Override
                    protected void onProgressUpdate(Integer... progress) {
                        super.onProgressUpdate(progress);

                        int count = progress[0];
                        int total = progress[1];

                        notificationCompat.setContentText("Uploading image profiles " + count + " of " + total + "...");
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

        private ArrayList<ImageProfile> imageProfiles;
        private LayoutInflater layoutInflater;

        private CreateStoryListAdapter(Context context, ArrayList<ImageProfile> imageProfiles) {
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

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.adapter_activity_create_story_list_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.createStoryItemThumbnail = (ImageView) view.findViewById(R.id.createStoryItemThumbnail);
                viewHolder.createStoryItemTitle = (TextView) view.findViewById(R.id.createStoryItemTitle);
                viewHolder.createStoryItemDescription = (TextView) view.findViewById(R.id.createStoryItemDescription);
                viewHolder.createStoryItemPosition = (TextView) view.findViewById(R.id.createStoryItemPosition);

                view.setTag(viewHolder);

            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            ImageProfile imageProfile = imageProfiles.get(position);
            viewHolder.createStoryItemTitle.setText(imageProfile.getTitle());
            viewHolder.createStoryItemDescription.setText(imageProfile.getDescription());
            viewHolder.createStoryItemPosition.setText("#" + position);
            if (imageProfile.getUri() != "" && imageProfile.getUri() != null) {
                viewHolder.createStoryItemThumbnail.setImageBitmap(null);
                ImageLoader.getInstance().loadImage(imageProfile.getUri(), thumbnailSize, new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(loadedImage, 128, 128);
                        viewHolder.createStoryItemThumbnail.setImageBitmap(thumbnail);

                        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                        fadeIn.setDuration(300);
                        fadeIn.setFillAfter(true);
                        viewHolder.createStoryItemThumbnail.startAnimation(fadeIn);
                    }

                });
            } else {
                viewHolder.createStoryItemThumbnail.setImageResource(R.drawable.placeholder);
            }

            return view;
        }

        public void add(ImageProfile imageProfile) {
            imageProfiles.add(imageProfile);
            notifyDataSetChanged();
        }

        public void add(int position, ImageProfile imageProfile) {
            imageProfiles.add(position, imageProfile);
            notifyDataSetChanged();
        }

        public void set(int position, ImageProfile imageProfile) {
            imageProfiles.set(position, imageProfile);
            notifyDataSetChanged();
        }

        public void remove(int position) {
            imageProfiles.remove(position);
            notifyDataSetChanged();
        }

        public void reorder(int from, int to) {
            ImageProfile imageProfile = imageProfiles.get(from);
            imageProfiles.remove(from);
            imageProfiles.add(to, imageProfile);
            notifyDataSetChanged();
        }

        public void swap(int from, int to) {
            Collections.swap(imageProfiles, from, to);
            notifyDataSetChanged();
        }

        private class ViewHolder {
            public ImageView createStoryItemThumbnail;
            public TextView createStoryItemTitle;
            public TextView createStoryItemDescription;
            public TextView createStoryItemPosition;
        }
    }

    private class CreateStoryLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            BootstrapApplication.LAST_KNOWN_LOCATION = loc;
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
}
