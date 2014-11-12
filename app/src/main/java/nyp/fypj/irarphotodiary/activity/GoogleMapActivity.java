package nyp.fypj.irarphotodiary.activity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.beyondar.android.plugin.googlemap.GoogleMapWorldPlugin;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

import java.util.ArrayList;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.application.BootstrapApplication;
import nyp.fypj.irarphotodiary.dto.ImageProfile;

/**
 * Created by L33534 on 11/4/2014.
 */
public class GoogleMapActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener {

    float headingAngle;
    float pitchAngle;
    float rollAngle;
    private ArrayList<ImageProfile> imageProfiles;
    private SensorManager sensorManager;
    private int orientationSensor;
    private GoogleMap map;
    private GoogleMapWorldPlugin googleMapWorldPlugin;
    private World world;

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }    final SensorEventListener sensorEventListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                headingAngle = sensorEvent.values[0];
                pitchAngle = sensorEvent.values[1];
                rollAngle = sensorEvent.values[2];

                if (pitchAngle > 7 || pitchAngle < -7 || rollAngle > 7
                        || rollAngle < -7) {
                    sensorManager.unregisterListener(sensorEventListener);
                    launchARActivity();
                }
            }
        }

        public void launchARActivity() {
            GoogleMapActivity.this.finish();
            Intent i = new Intent(GoogleMapActivity.this, ARActivity.class);
            i.putParcelableArrayListExtra("imageProfiles", imageProfiles);
            startActivity(i);
        }

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
            // TODO Auto-generated method stub
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        ////
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        if (map == null) {
            return;
        }

        world = new World(this);
        world.setGeoPosition(1.37912487, 103.84926296);

        world.setDefaultImage(R.drawable.placeholder);

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
            world.addBeyondarObject(geoObject, 1);
        }

        googleMapWorldPlugin = new GoogleMapWorldPlugin(this);
        googleMapWorldPlugin.setGoogleMap(map);
        world.addPlugin(googleMapWorldPlugin);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(googleMapWorldPlugin.getLatLng(), 15));
        map.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null);

        GeoObject user = new GeoObject(10001);
        user.setGeoPosition(1.37912487, 103.84926296);
        user.setImageResource(R.drawable.placeholder);
        user.setName("User Position");
        world.addBeyondarObject(user);

        ////

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        orientationSensor = Sensor.TYPE_ORIENTATION;
        sensorManager.registerListener(sensorEventListener, sensorManager
                .getDefaultSensor(orientationSensor), SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        GeoObject geoObject = googleMapWorldPlugin.getGeoObjectOwner(marker);
        if (geoObject != null) {
            Toast.makeText(this,
                    "Click on a marker owned by a GeoOject with the name: " + geoObject.getName(),
                    Toast.LENGTH_SHORT).show();
        }
        return false;
    }


}