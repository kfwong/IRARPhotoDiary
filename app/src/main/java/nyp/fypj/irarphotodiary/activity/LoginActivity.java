package nyp.fypj.irarphotodiary.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.dto.Album;
import nyp.fypj.irarphotodiary.dto.ImageProfile;


public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Remove divider under actionbar
        getActionBar().setBackgroundDrawable(null);

        Button loginNormal = (Button) findViewById(R.id.loginNormal);
        loginNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(LoginActivity.this, NavigationActivity.class);
            startActivity(i);

            finish();
            }
        });

        ///////TODO:DEBUG///////////////////
        AsyncTask<Void,Void,Void> task = new AsyncTask<Void,Void,Void>() {

            private ArrayList<Album> albums;

            @Override
            protected Void doInBackground(Void... voids) {
                HttpClient httpClient = new DefaultHttpClient();
                String responseString = null;
                try {
                    HttpPost httpPost = new HttpPost("http://fypj-124465r.rhcloud.com/albums/images");
                    httpPost.setHeader("Content-Type", "application/json");
                    httpPost.setEntity(new StringEntity("{\"tags\":[{\"confidence\":43.10115825222821,\"tag\":\"flower\"},{\"confidence\":35.64136447136573,\"tag\":\"pink\"},{\"confidence\":28.634130526364913,\"tag\":\"blossom\"},{\"confidence\":26.31923191840067,\"tag\":\"spring\"},{\"confidence\":25.342480116322598,\"tag\":\"garden\"},{\"confidence\":25.186642759326695,\"tag\":\"flowers\"},{\"confidence\":25.01607544605281,\"tag\":\"petal\"},{\"confidence\":24.922798569394423,\"tag\":\"plant\"},{\"confidence\":23.854798038972252,\"tag\":\"floral\"},{\"confidence\":23.6310246044367,\"tag\":\"bloom\"},{\"confidence\":21.284477322919674,\"tag\":\"flora\"},{\"confidence\":16.41916774293343,\"tag\":\"summer\"},{\"confidence\":15.924073499807283,\"tag\":\"botanical\"},{\"confidence\":14.153358313439634,\"tag\":\"blooming\"},{\"confidence\":13.941621753334235,\"tag\":\"petals\"},{\"confidence\":13.355016885025824,\"tag\":\"leaf\"},{\"confidence\":13.263226955288609,\"tag\":\"color\"},{\"confidence\":12.762897096197667,\"tag\":\"natural\"},{\"confidence\":12.304395954588891,\"tag\":\"fresh\"},{\"confidence\":11.942053845058787,\"tag\":\"bouquet\"},{\"confidence\":11.590074551540075,\"tag\":\"purple\"},{\"confidence\":11.459935893853261,\"tag\":\"love\"},{\"confidence\":11.17032493611733,\"tag\":\"stem\"},{\"confidence\":10.937021188956088,\"tag\":\"gardening\"},{\"confidence\":10.73786747877004,\"tag\":\"vibrant\"},{\"confidence\":10.641141536527808,\"tag\":\"season\"},{\"confidence\":10.609717312271904,\"tag\":\"rose\"},{\"confidence\":10.486696938995667,\"tag\":\"leaves\"},{\"confidence\":10.367484912773484,\"tag\":\"detail\"},{\"confidence\":10.16931649178633,\"tag\":\"valentine\"},{\"confidence\":10.063638268605324,\"tag\":\"yellow\"},{\"confidence\":10.055453699549224,\"tag\":\"close\"},{\"confidence\":10.010387068459641,\"tag\":\"bright\"},{\"confidence\":9.898910661552959,\"tag\":\"bud\"},{\"confidence\":9.806836361136309,\"tag\":\"botany\"},{\"confidence\":9.482202942019109,\"tag\":\"single\"},{\"confidence\":9.38476707587706,\"tag\":\"seasonal\"},{\"confidence\":9.32538663456405,\"tag\":\"growth\"},{\"confidence\":9.24789530334149,\"tag\":\"romance\"},{\"confidence\":9.240484929767163,\"tag\":\"closeup\"},{\"confidence\":8.88218743907557,\"tag\":\"gift\"},{\"confidence\":8.7363573671482,\"tag\":\"colorful\"},{\"confidence\":8.617269673821534,\"tag\":\"pollen\"},{\"confidence\":8.230822955761846,\"tag\":\"head\"},{\"confidence\":7.948509169334445,\"tag\":\"stamen\"},{\"confidence\":7.650950892396568,\"tag\":\"decoration\"},{\"confidence\":7.644616101339689,\"tag\":\"vase\"},{\"confidence\":7.615840885034735,\"tag\":\"delicate\"},{\"confidence\":7.531991104303519,\"tag\":\"bunch\"},{\"confidence\":7.339018451436309,\"tag\":\"plants\"},{\"confidence\":7.2020861189028915,\"tag\":\"shrub\"},{\"confidence\":7.16238302631312,\"tag\":\"daisy\"}]}"));
                    HttpResponse response = httpClient.execute(httpPost); //TODO: not used?

                    StatusLine statusLine = response.getStatusLine();
                    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        out.close();
                        responseString = out.toString();
                    } else{
                        //Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Gson gson = new Gson();
                albums = gson.fromJson(responseString, new TypeToken<ArrayList<Album>>(){}.getType());

                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                Log.e("TADAH", "result:" +albums.get(0).getImageProfiles().get(0).getTags().get(0).getTag());
            }
        }; // AsyncTask

        task.execute();


    }
}
