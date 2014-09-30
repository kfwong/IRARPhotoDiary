package nyp.fypj.irarphotodiary.application;

import android.app.ActivityManager;
import android.app.Application;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import nyp.fypj.irarphotodiary.R;

/**
 * Created by L33533 on 9/12/2014.
 * To assist in initilizing constant global variables...sometimes singleton objects
 */
public class BootstrapApplication extends Application {
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
                .build();

        ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(displayImageOptions)
                .build();
        ImageLoader.getInstance().init(imageLoaderConfiguration);
    }

    public Cloudinary getCloudinary() {
        return cloudinary;
    }
}
