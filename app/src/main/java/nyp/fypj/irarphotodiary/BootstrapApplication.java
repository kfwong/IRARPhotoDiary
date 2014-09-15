package nyp.fypj.irarphotodiary;

import android.app.Application;

import com.cloudinary.Cloudinary;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
        ImageLoader.getInstance().init(config);
    }

    public Cloudinary getCloudinary() {
        return cloudinary;
    }
}
