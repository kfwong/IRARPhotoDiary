package sg.edu.nyp.irarphotodiary.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import java.io.File;
import java.io.IOException;

import sg.edu.nyp.irarphotodiary.R;
import sg.edu.nyp.irarphotodiary.application.BootstrapApplication;
import sg.edu.nyp.irarphotodiary.dto.ImageProfile;

public class CreateStoryActivity extends FragmentActivity {
    private int position; // keeping track current entry in the parent list view position
    private ImageProfile imageProfile;
    private KenBurnsView createStoryImageView;
    private FloatLabeledEditText createStoryTitle;
    private FloatLabeledEditText createStoryDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);

        getActionBar().setBackgroundDrawable(null);

        createStoryImageView = (KenBurnsView) findViewById(R.id.createStoryImageView);
        createStoryTitle = (FloatLabeledEditText) findViewById(R.id.createStoryTitle);
        createStoryDescription = (FloatLabeledEditText) findViewById(R.id.createStoryDescription);

        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position", position);
            imageProfile = savedInstanceState.getParcelable("imageProfile");

        } else {
            Intent intent = getIntent();

            position = intent.getIntExtra("position", -1);
            imageProfile = intent.getExtras().getParcelable("imageProfile");

            createStoryTitle.setText(imageProfile.getTitle());
            createStoryDescription.setText(imageProfile.getDescription());

            if (imageProfile.getUri() != "" || imageProfile.getUri() != null) {
                // DEBUG image problem: Log.e("onCreate", "image-isnull: "+imageProfile.getUri());
                ImageLoader.getInstance().displayImage(imageProfile.getUri(), createStoryImageView);
            }

        }
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
                imageProfile.setTitle(createStoryTitle.getTextString());
                imageProfile.setDescription(createStoryDescription.getTextString());

                Intent intent = new Intent();
                intent.putExtra("position", position);
                intent.putExtra("imageProfile", imageProfile);

                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.createStoryCancel:
                setResult(RESULT_CANCELED);
                finish();
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

        outState.putInt("position", position);
        outState.putParcelable("imageProfile", imageProfile);

        super.onSaveInstanceState(outState);
    }
}