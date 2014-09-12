package nyp.fypj.irarphotodiary;

import android.app.Application;

import com.cloudinary.Cloudinary;

/**
 * Created by L33533 on 9/12/2014.
 * To assist in initilizing constant global variables...sometimes singleton objects
 */
public class BootstrapApplication extends Application {
    private Cloudinary cloudinary;

    @Override
    public void onCreate() {
        super.onCreate();

        cloudinary = new Cloudinary(this.getApplicationContext());
    }

    public Cloudinary getCloudinary() {
        return cloudinary;
    }
}
