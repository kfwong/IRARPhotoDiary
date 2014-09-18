package nyp.fypj.irarphotodiary.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.application.BootstrapApplication;
import nyp.fypj.irarphotodiary.util.ColorProfiler;

public class ThirdFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.third_frag, container, false);

        TextView tv = (TextView) v.findViewById(R.id.tvFragThird);
        tv.setText(getArguments().getString("msg"));

        Button uploadButton = (Button) v.findViewById(R.id.btnUpload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, 1);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            final String picturePath = cursor.getString(columnIndex);
            cursor.close();

            AsyncTask<String,Void,String> task = new AsyncTask<String,Void,String>() {
                @Override
                protected String doInBackground(String... strings) {

                    try {
                        // Generate image profile (dominant color, etc)
                        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                        String jsonProfile = ColorProfiler.generateJsonProfile(bitmap);

                        // Get instance from application constant DO NOT INITIALIZE ANOTHER.
                        // Upload image to cloudinary
                        File inputStream = new File(picturePath);
                        Cloudinary cloudinary = ((BootstrapApplication) ThirdFragment.this.getActivity().getApplication()).getCloudinary();
                        cloudinary.uploader().upload(inputStream, Cloudinary.emptyMap());

                        // Upload image profile in json format to database
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost("http://fypj-124465r.rhcloud.com/images");
                        httpPost.setHeader("Content-Type", "application/json");
                        httpPost.setEntity(new StringEntity(jsonProfile));
                        HttpResponse httpResponse = httpClient.execute(httpPost);

                        // Return HTTP status code
                        return Integer.toString(httpResponse.getStatusLine().getStatusCode());
                    }catch (FileNotFoundException ex){
                        return "filenotfound";
                    }catch (IOException ex){
                        return "ioexception";
                    }
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);

                    Toast.makeText(getActivity().getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                }

                @Override
                protected void onProgressUpdate(Void... values) {
                    super.onProgressUpdate(values);
                }
            };

            task.execute();
        }


    }

    public static ThirdFragment newInstance(String text) {

        ThirdFragment f = new ThirdFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}