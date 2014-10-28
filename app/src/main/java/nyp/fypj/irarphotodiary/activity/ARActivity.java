package nyp.fypj.irarphotodiary.activity;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.UUID;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.application.BootstrapApplication;
import nyp.fypj.irarphotodiary.dto.ImageProfile;

public class ARActivity extends FragmentActivity {

    private BeyondarFragmentSupport mBeyondarFragment;
    private World mWorld;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_ar);

        // Get imageProfiles from intent
        ArrayList<ImageProfile> imageProfiles = getIntent().getParcelableArrayListExtra("imageProfiles");

        mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(
                R.id.beyondarFragment);

        // We create the world and fill it ...
        mWorld = new World(this);

        // The user can set the default bitmap. This is useful if you are
        // loading images form Internet and the connection get lost
        mWorld.setDefaultImage(R.drawable.placeholder);

        // User position (you can change it using the GPS listeners form Android
        // API)
        mWorld.setGeoPosition(1.37912487, 103.84926296);

        // Loop through each imageProfiles, create GeoObjects
        for(ImageProfile imageProfile : imageProfiles){
            GeoObject geoObject = new GeoObject();
            geoObject.setGeoPosition(imageProfile.getLatitude(), imageProfile.getLongitude());
            geoObject.setName(imageProfile.getTitle());

            Log.e("TADAH","imageProfile:"+imageProfile.getTitle()+":"+imageProfile.getLatitude()+":"+imageProfile.getLongitude());

            //Bitmap loadedImage = ImageLoader.getInstance().loadImageSync("http://res.cloudinary.com/" + BootstrapApplication.CLOUDINARY_CLOUD_NAME + "/image/upload/w_0.1/" + imageProfile.getFilename() + "." + imageProfile.getExtension(), tinySize);
            //geoObject.setImageUri("http://res.cloudinary.com/" + BootstrapApplication.CLOUDINARY_CLOUD_NAME + "/image/upload/w_100,h_100,c_thumb/w_250,h_100,c_pad,g_south_west,bo_3px_solid_rgb:00000090,b_rgb:3399aa/l_text:arial_11_bold_underline:Lorem ipsum,g_north_west,x_113,y_15,c_fit,w_130,h_65,co_white/l_text:arial_11:Lorem ipsum dolor sit amet consectetur adipisicing elit sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.,g_north_west,x_113,y_35,c_fit,w_130,h_65,co_white/" + imageProfile.getFilename() + "." + imageProfile.getExtension());
            //geoObject.setImageResource(R.drawable.placeholder);
            geoObject.setImageUri("http://res.cloudinary.com/dxspdhqz3/image/upload/w_100,h_100,c_thumb/w_250,h_100,c_pad,g_south_west,bo_3px_solid_rgb:00000090,b_rgb:3399aa/mkd1fftrv89qsxxbejuj.jpg");

            // Add the GeoObjects to the world
            mWorld.addBeyondarObject(geoObject, 1);
        }

        // ... and send it to the fragment
        mBeyondarFragment.setWorld(mWorld);

    }

}