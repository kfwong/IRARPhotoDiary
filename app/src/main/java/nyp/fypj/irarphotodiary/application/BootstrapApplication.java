package nyp.fypj.irarphotodiary.application;

import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.location.Location;
import android.view.Display;
import android.view.WindowManager;

import com.cloudinary.Cloudinary;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;
import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.dto.Album;

/**
 * Created by L33533 on 9/12/2014.
 * To assist in initilizing constant global variables...sometimes singleton objects
 */
public class BootstrapApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "juDsKi29m7D3kYwfEePKAkaN1";
    private static final String TWITTER_SECRET = "SnkMQrRcuxgSp871Cv2B8cA8io7Qn86ufHrg6uMRnwISEnZqih";

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.

    public static final String CLOUDINARY_CLOUD_NAME = "dxspdhqz3";
    public static int DEVICE_WIDTH;
    public static int DEVICE_HEIGHT;
    public static Location LAST_KNOWN_LOCATION;
    private Cloudinary cloudinary;
    public static ArrayList<Album> albums;
    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

       // final TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);

       // Fabric.with(this, new Twitter(authConfig));

        // Cloudinary API Services Initialization
        cloudinary = new Cloudinary(this.getApplicationContext());

        // Android Universal ImageLoder initialization
        // IMPORTANT: Enable disk cache for bitmaps!!! Using memory cache is not recommended in this application!!!
        // Unless you prefer Out of Memory Error blah blah blah
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .displayer(new FadeInBitmapDisplayer(2000)) //milliseconds
                .showImageForEmptyUri(R.drawable.placeholder)
                .showImageOnFail(R.drawable.placeholder)
                .resetViewBeforeLoading(true)
                .build();

        ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(displayImageOptions)
                .build();
        ImageLoader.getInstance().init(imageLoaderConfiguration);

        // Screen size
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        DEVICE_WIDTH = size.x;
        DEVICE_HEIGHT = size.y;
    }

    public Cloudinary getCloudinary() {
        return cloudinary;
    }
}
