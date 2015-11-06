package sg.edu.nyp.irarphotodiary.application;

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

import sg.edu.nyp.irarphotodiary.R;

/**
 * Created by L33533 on 9/12/2014.
 * To assist in initilizing constant global variables...sometimes singleton objects
 */
public class BootstrapApplication extends Application {
    public static final String CLOUDINARY_CLOUD_NAME = "dxspdhqz3";
    public static int DEVICE_WIDTH;
    public static int DEVICE_HEIGHT;
    public static Location LAST_KNOWN_LOCATION;
    private Cloudinary cloudinary;

    @Override
    public void onCreate() {
        super.onCreate();

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
