package nyp.fypj.irarphotodiary.activity;


import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.view.BeyondarViewAdapter;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.application.BootstrapApplication;
import nyp.fypj.irarphotodiary.dto.ImageProfile;

public class ARActivity extends FragmentActivity implements OnClickBeyondarObjectListener, View.OnClickListener {

    float headingAngle;
    float pitchAngle;
    float rollAngle;
    private BeyondarFragmentSupport mBeyondarFragment;
    private World mWorld;
    private List<BeyondarObject> showViewOn;
    private ImageProfile imageProfile;
    private ArrayList<ImageProfile> imageProfiles;
    private SensorManager sensorManager;
    private int orientationSensor;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showViewOn = Collections.synchronizedList(new ArrayList<BeyondarObject>());

        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ar);
        mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(
                R.id.beyondarFragment);
        // We create the world and fill it ...
        // User position (you can change it using the GPS listeners form Android
        // API)
        mWorld = new World(this);
        mWorld.setGeoPosition(1.37912487, 103.84926296);

        mWorld.setDefaultImage(R.drawable.placeholder);

        imageProfiles = getIntent().getParcelableArrayListExtra("imageProfiles");

        // Loop through each imageProfiles, create GeoObjects
        for (ImageProfile imageProfile : imageProfiles) {
            GeoObject geoObject = new GeoObject();
            geoObject.setGeoPosition(imageProfile.getLatitude(), imageProfile.getLongitude());

            // because the beyondar has only setName function...we need to insert more details
            Gson gson = new Gson();
            String json = gson.toJson(imageProfile);
            geoObject.setName(json);

            //WORKING: geoObject.setImageUri("http://res.cloudinary.com/" + BootstrapApplication.CLOUDINARY_CLOUD_NAME + "/image/upload/w_512,h_512,c_thumb/" + imageProfile.getFilename() + "." + imageProfile.getExtension());
            //PARTIAL WORKING: geoObject.setImageUri("http://res.cloudinary.com/dxspdhqz3/image/upload/w_512,h_512,c_thumb/w_348,h_128,c_pad,g_south_west,bo_5px_solid_rgb:00000090,b_rgb:000000/l_text:arial_18_bold_underline:Title,g_north_west,x_138,y_15,c_fit,w_192,h_65,co_white/l_text:arial_16:Description.,g_north_west,x_138,y_40,c_fit,w_192,h_95,co_white/sfls6bydbraqv4im43n0.jpg");
            geoObject.setImageUri("http://res.cloudinary.com/" + BootstrapApplication.CLOUDINARY_CLOUD_NAME + "/image/upload/w_512,h_512,c_thumb,bo_50px_solid_rgb:33b5e5/" + imageProfile.getFilename() + "." + imageProfile.getExtension());

            // Add the GeoObjects to the world
            mWorld.addBeyondarObject(geoObject, 1);
        }
        // ... and send it to the fragment
        mBeyondarFragment.setWorld(mWorld);

        // set on object click listener
        mBeyondarFragment.setOnClickBeyondarObjectListener(this);

        //static image size for all distance
        //https://github.com/BeyondAR/beyondar/issues/53
        mBeyondarFragment.setPushAwayDistance(25);

        // We also can see the Frames per seconds
        //mBeyondarFragment.showFPS(true);

        CustomBeyondarViewAdapter customBeyondarViewAdapter = new CustomBeyondarViewAdapter(this);
        mBeyondarFragment.setBeyondarViewAdapter(customBeyondarViewAdapter);

        //////

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        orientationSensor = Sensor.TYPE_ORIENTATION;
        sensorManager.registerListener(sensorEventListener, sensorManager
                .getDefaultSensor(orientationSensor), SensorManager.SENSOR_DELAY_NORMAL);
    }    final SensorEventListener sensorEventListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                headingAngle = sensorEvent.values[0];
                pitchAngle = sensorEvent.values[1];
                rollAngle = sensorEvent.values[2];

                if (pitchAngle < 7 && pitchAngle > -7 && rollAngle < 7 &&
                        rollAngle > -7) {
                    sensorManager.unregisterListener(sensorEventListener);
                    launchGoogleMapActivity();
                }
            }
        }

        public void launchGoogleMapActivity() {
            ARActivity.this.finish();
            Intent i = new Intent(ARActivity.this, GoogleMapActivity.class);
            i.putParcelableArrayListExtra("imageProfiles", imageProfiles);
            startActivity(i);
        }

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
            // TODO Auto-generated method stub
        }
    };

    @Override
    public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects) {
        if (beyondarObjects.size() > 0) {
            //attach view
            BeyondarObject beyondarObject = beyondarObjects.get(0);
            if (showViewOn.contains(beyondarObject)) {
                showViewOn.remove(beyondarObject);
            } else {
                showViewOn.add(beyondarObject);
            }
        }
    }

    @Override
    public void onClick(View view) {
        //Toast.makeText(this, "TADAH",Toast.LENGTH_LONG).show();
        ArrayList<ImageProfile> imageProfiles = new ArrayList<ImageProfile>();
        imageProfiles.add(imageProfile);

        Intent intent = new Intent(ARActivity.this.getApplicationContext(), ViewStoryActivity.class);
        intent.putParcelableArrayListExtra("imageProfiles", imageProfiles);
        startActivity(intent);
    }

    private class CustomBeyondarViewAdapter extends BeyondarViewAdapter {
        LayoutInflater inflater;

        public CustomBeyondarViewAdapter(Context context) {
            super(context);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(BeyondarObject beyondarObject, View recycledView, ViewGroup parent) {
            if (!showViewOn.contains(beyondarObject)) {
                return null;
            }
            if (recycledView == null) {
                recycledView = inflater.inflate(R.layout.beyondar_object_view, null);
            }

            Gson gson = new Gson();
            imageProfile = gson.fromJson(beyondarObject.getName(), ImageProfile.class);

            TextView textView = (TextView) recycledView.findViewById(R.id.titleTextView);
            textView.setText(imageProfile.getTitle() + "(" + Math.round(beyondarObject.getDistanceFromUser()) + "m)");

            TextView descriptionTextView = (TextView) recycledView.findViewById(R.id.descriptionTextView);
            descriptionTextView.setText(imageProfile.getDescription());

            Button button = (Button) recycledView.findViewById(R.id.button);
            button.setOnClickListener(ARActivity.this);
            // Once the view is ready we specify the position
            setPosition(beyondarObject.getScreenPositionTopRight());
            return recycledView;
        }
    }


}