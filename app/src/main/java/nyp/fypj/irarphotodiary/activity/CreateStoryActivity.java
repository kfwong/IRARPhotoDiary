package nyp.fypj.irarphotodiary.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.dto.ImageProfile;
import nyp.fypj.irarphotodiary.util.BitmapUtils;

public class CreateStoryActivity extends FragmentActivity {
    private int position; // keeping track current entry in the parent list view position
    private ImageProfile imageProfile;
    private KenBurnsView createStoryImageView;
    private TextView createStoryTitle;
    private TextView createStoryDescription;
    private boolean isFadedIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);

        getActionBar().setBackgroundDrawable(null);

        Intent intent = getIntent();

        position = intent.getIntExtra("position", -1);
        imageProfile = (ImageProfile) intent.getExtras().getParcelable("imageProfile");

        createStoryTitle = (TextView)findViewById(R.id.createStoryTitle);
        createStoryTitle.setText(imageProfile.getTitle());

        createStoryDescription = (TextView) findViewById(R.id.createStoryDescription);
        createStoryDescription.setText(imageProfile.getDescription());

        createStoryImageView = (KenBurnsView) findViewById(R.id.createStoryImageView);
        createStoryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(isFadedIn){
                Animation fadeOut = new AlphaAnimation(1.0f , 0.0f);
                fadeOut.setDuration(300);
                fadeOut.setFillAfter(true);
                createStoryTitle.startAnimation(fadeOut);
                createStoryDescription.startAnimation(fadeOut);
                isFadedIn = false;
            }else{
                Animation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
                fadeIn.setDuration(300);
                fadeIn.setFillAfter(true);
                createStoryTitle.startAnimation(fadeIn);
                createStoryDescription.startAnimation(fadeIn);
                isFadedIn = true;
            }
            }
        });
        if(imageProfile.getUri() != "" || imageProfile.getUri() != null){
            ImageLoader.getInstance().displayImage(imageProfile.getUri(), createStoryImageView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_story, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.createStoryChooseFromGallery:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);//TODO: LOOK AT THAT UGLY REQUEST CODE!!!
                break;
            case R.id.createStoryEditTitle:
                final EditText editTitle = new EditText(this);
                editTitle.setSingleLine(true);

                new AlertDialog.Builder(this)
                        .setTitle("Edit Title")
                        .setMessage("E.g: Who are they? Where are you? What is that?")
                        .setView(editTitle)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                imageProfile.setTitle(editTitle.getText().toString());
                                createStoryTitle.setText(editTitle.getText());
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
                break;
            case R.id.createStoryEditDescription:
                final EditText editDescription = new EditText(this);
                editDescription.setSingleLine(false);
                editDescription.setHeight(300);
                editDescription.setGravity(Gravity.BOTTOM | Gravity.LEFT);

                new AlertDialog.Builder(this)
                        .setTitle("Edit Description")
                        .setMessage("Tell others your experience!")
                        .setView(editDescription)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                imageProfile.setDescription(editDescription.getText().toString());
                                createStoryDescription.setText(editDescription.getText());
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
                break;
            case R.id.createStorySave:

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

        switch(requestCode) {
            case 1: //TODO
                if(resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    imageProfile.setUri(uri.toString());
                    ImageLoader.getInstance().displayImage(imageProfile.getUri(), createStoryImageView);
                }
        }
    }
}