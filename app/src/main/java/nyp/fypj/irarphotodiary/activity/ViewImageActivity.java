package nyp.fypj.irarphotodiary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.nostra13.universalimageloader.core.ImageLoader;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.application.BootstrapApplication;
import nyp.fypj.irarphotodiary.dto.ImageProfile;

public class ViewImageActivity extends FragmentActivity {

    private ImageProfile imageProfile;
    private KenBurnsView viewStorySingleImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_view_image);
        viewStorySingleImage = (KenBurnsView) findViewById(R.id.viewStorySingleImage);
        // getActionBar().setBackgroundDrawable(null);
        Intent intent = getIntent();

        imageProfile = intent.getExtras().getParcelable("imageProfile");
        ImageLoader.getInstance().displayImage("http://res.cloudinary.com/" + BootstrapApplication.CLOUDINARY_CLOUD_NAME + "/image/upload/w_300,h_400/" + imageProfile.getFilename() + "." + imageProfile.getExtension(), viewStorySingleImage);

    }
    @Override
    public void onResume() {
        super.onResume();
        // Set title
        getActionBar()
                .setTitle(imageProfile.getTitle());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // DEBUG image problem: Log.e("onSaveInstanceState", "image: "+imageProfile.getUri());


        outState.putParcelable("imageProfile", imageProfile);

        super.onSaveInstanceState(outState);
    }
   /* @Override
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
        /*    case R.id.viewStoryARMode:
                Intent i = new Intent(ViewStoryActivity.this, ARActivity.class);
                i.putParcelableArrayListExtra("imageProfiles", imageProfiles);
                startActivity(i);
                break;

            case R.id.editMode:

                Intent intent = new Intent(ViewStoryActivity.this, EditStoryActivity.class);
                intent.putExtra("imageProfile", imageProfile);
                startActivity(intent); //TODO: make the request code final


                break;

          /*  case R.id.viewStoryMapMode:
                Intent j = new Intent(ViewStoryActivity.this, GoogleMapActivity.class);
                j.putParcelableArrayListExtra("imageProfiles", imageProfiles);
                startActivity(j);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
**/




}
