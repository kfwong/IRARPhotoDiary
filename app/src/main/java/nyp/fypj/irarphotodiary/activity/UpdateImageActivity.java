package nyp.fypj.irarphotodiary.activity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.application.BootstrapApplication;
import nyp.fypj.irarphotodiary.dto.ImageProfile;
import nyp.fypj.irarphotodiary.dto.Tag;

public class UpdateImageActivity extends FragmentActivity {
    private int position; // keeping track current entry in the parent list view position
    private ImageProfile imageProfile;
    private ArrayList<ImageProfile> imageProfiles;
    private KenBurnsView createStoryImageView;
    private FloatLabeledEditText createStoryTitle;
    private FloatLabeledEditText createStoryDescription;
    private TextView date;
    private TextView day;
    private TextView monthyear;
    private TextView time;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);

        getActionBar().setBackgroundDrawable(null);

        createStoryImageView = (KenBurnsView) findViewById(R.id.createStoryImageView);
        createStoryTitle = (FloatLabeledEditText) findViewById(R.id.createStoryTitle);
        createStoryDescription = (FloatLabeledEditText) findViewById(R.id.createStoryDescription);
//display time
        date = (TextView) findViewById(R.id.date);
        day = (TextView) findViewById(R.id.day);
        monthyear = (TextView) findViewById(R.id.monthyear);
        time = (TextView) findViewById(R.id.time);

        Intent intent = getIntent();

        imageProfile = intent.getExtras().getParcelable("imageProfile");
        imageProfiles = intent.getExtras().getParcelableArrayList("imageProfiles");

        createStoryTitle.setText(imageProfile.getTitle());
        createStoryDescription.setText(imageProfile.getDescription());
        String date1= imageProfile.getDateUploaded();


        List<String> arrayLists=new ArrayList<String>(Arrays.asList(date1.split(", ")));
        day.setText(arrayLists.get(0));
        date.setText(arrayLists.get(1));
        monthyear.setText(arrayLists.get(2));
        time.setText(arrayLists.get(3));

        // Uri myUri = Uri.parse(imageProfile.getUri());
        // createStoryImageView.setImageURI(myUri);
        ImageLoader.getInstance().displayImage("http://res.cloudinary.com/" + BootstrapApplication.CLOUDINARY_CLOUD_NAME + "/image/upload/" + imageProfile.getFilename() + "." + imageProfile.getExtension(), createStoryImageView);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_story_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.createStoryTakePhoto:
                try {
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File storage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    File image = null;

                    image = File.createTempFile("IRAR_" + System.currentTimeMillis(), ".jpg", storage);

                    imageProfile.setUri(Uri.fromFile(image).toString());

                    //gps
                    if (BootstrapApplication.LAST_KNOWN_LOCATION != null) {
                        imageProfile.setLatitude(BootstrapApplication.LAST_KNOWN_LOCATION.getLatitude());
                        imageProfile.setLongitude(BootstrapApplication.LAST_KNOWN_LOCATION.getLongitude());
                    }

                    if (image != null) {
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
                        startActivityForResult(takePhotoIntent, 2);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                break;
            case R.id.createStoryChooseFromGallery:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);//TODO: LOOK AT THAT UGLY REQUEST CODE!!!
                break;
            case R.id.createStorySave:
                new AlertDialog.Builder(this)
                        .setTitle("Update Photo")
                        .setMessage("Save Changes?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // update the image


                                imageProfile.setUri("http://res.cloudinary.com/" + BootstrapApplication.CLOUDINARY_CLOUD_NAME + "/image/upload/" + imageProfile.getFilename() + "." + imageProfile.getExtension());

                                ///// async task
                                AsyncTask<Void, Integer, Void> task = new AsyncTask<Void, Integer, Void>() {
                                    // private Album album = new Album();
                                    // private ArrayList<ImageProfile> imageProfiles = createStoryListAdapter.imageProfiles;
                                    private NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    private NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(UpdateImageActivity.this);

                                    @Override
                                    protected Void doInBackground(Void... voids) {

                                        // Upload images

                                        // IMPORTANT: the final position/index/order is only saved to the entity just before uploading to avoid confusion

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
                                            Cloudinary cloudinary = ((BootstrapApplication) UpdateImageActivity.this.getApplication()).getCloudinary();
                                            JSONObject uploadResult = cloudinary.uploader().upload(file, Cloudinary.emptyMap());

                                            Log.e("TADAH", "uploaded: " + uploadResult);

                                            // autotagging
                                            Log.e("TADAH", "processing autotagging");
                                            JsonObject jsonObject = Ion.with(UpdateImageActivity.this)
                                                    .load("http://api.imagga.com/draft/tags?api_key=acc_31de762e407a6a3&url=" + uploadResult.getString("url"))
                                                    .asJsonObject()
                                                    .get();

                                            Log.e("TADAH", "done autotagging: " + jsonObject.get("tags"));

                                            ArrayList<Tag> tags = new Gson().fromJson(jsonObject.get("tags"), new TypeToken<ArrayList<Tag>>() {
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
                                        }catch (NullPointerException ex){
                                            if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
                                                UpdateImageActivity.this.runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        Toast.makeText(UpdateImageActivity.this, "Please choose images to upload.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                finish();
                                            }
                                        }


                                        // end for loop

                                        // Upload imageProfiles
                                        try {
                                            SimpleDateFormat format = new SimpleDateFormat("EEEE, dd, MMM yyyy, hh:mm aaa");
                                            Date date1 = Calendar.getInstance().getTime();
                                            String today = format.format(date1);
                                            imageProfile.setTitle(createStoryTitle.getTextString());
                                            imageProfile.setDescription(createStoryDescription.getTextString());
                                            //imageProfile.setDateUploaded(today);


                                            // flatten imageProfile to json
                                            Gson gson = new Gson();
                                            String albumJson = gson.toJson(imageProfile);

                                            Log.e("this is the json", albumJson);

                                            // Upload json to databasesss
                                            HttpClient httpClient = new DefaultHttpClient();
                                            HttpPost httpPost = new HttpPost("http://fypj-124465r.rhcloud.com/update/image");
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
                                        notificationCompat.setContentTitle("Updating Image");
                                        notificationCompat.setProgress(0, 0, true);
                                        notificationManager.notify(1, notificationCompat.build());

                                    }


                                    @Override
                                    protected void onPostExecute(Void aVoid) {
                                        super.onPostExecute(aVoid);

                                        notificationCompat.setContentText("Update completed.");
                                        notificationCompat.setProgress(0, 0, false);
                                        notificationManager.notify(1, notificationCompat.build());
                                        Intent intent= new Intent(UpdateImageActivity.this.getApplicationContext(),ManageStoryActivity.class);
                                     //   intent.putParcelableArrayListExtra("imageProfiles",albumzz.getImageProfiles());
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                      //  intent.putExtra("albumTitle", albumzz.getTitle());
                                        //
                                        startActivityForResult(intent,1);
                                        UpdateImageActivity.this.finish();


                                    }
                                };

                                task.execute();
                                ///// end of async task

                                //finish();
                               // startActivity(getIntent());
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_action_save)
                        .show();

                //i.putExtra("imageProfile", imageProfile);


                break;
            case R.id.createStoryCancel:
                new AlertDialog.Builder(this)
                        .setTitle("Exit")
                        .setMessage("Discard changes?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent= new Intent(UpdateImageActivity.this.getApplicationContext(),ManageStoryActivity.class);
                                intent.putParcelableArrayListExtra("imageProfiles",imageProfiles);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_action_warning)
                        .show();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1: //TODO
                if (resultCode == RESULT_OK) {
                    Uri cachedUri = data.getData();

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(cachedUri,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    final String actualUri = cursor.getString(columnIndex);
                    cursor.close();

                    imageProfile.setUri("file://" + actualUri);

                    ImageLoader.getInstance().displayImage(imageProfile.getUri(), createStoryImageView);
                }
                break;
            case 2: //TODO
                if (resultCode == RESULT_OK) {
//                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                    File f = new File(imageProfile.getUri());
//                    Uri contentUri = Uri.fromFile(f);
//                    mediaScanIntent.setData(contentUri);
//                    this.sendBroadcast(mediaScanIntent);
                    // DEBUG image problem: Log.e("onActivityResult", "image: "+imageProfile.getUri());

                    ImageLoader.getInstance().displayImage(imageProfile.getUri(), createStoryImageView);
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // DEBUG image problem: Log.e("onSaveInstanceState", "image: "+imageProfile.getUri());


        outState.putParcelable("imageProfile", imageProfile);

        super.onSaveInstanceState(outState);
    }
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Discard Changes")
                .setMessage("Are you sure you want to leave this page?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // continue with exit

                        // deleteCache(getApplication());
                        Intent intent= new Intent(UpdateImageActivity.this.getApplicationContext(),ManageStoryActivity.class);
                           intent.putParcelableArrayListExtra("imageProfiles",imageProfiles);
                        startActivity(intent);
                        finish();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
