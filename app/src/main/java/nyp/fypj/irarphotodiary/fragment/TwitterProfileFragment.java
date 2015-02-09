package nyp.fypj.irarphotodiary.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;

import nyp.fypj.irarphotodiary.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */

public class TwitterProfileFragment extends Fragment {

    private ImageView profilePictureView;
    private TextView profileName;
    private TextView profileLocation;
    private TextView profileEmail;
    private TextView profileBio;
    private TwitterSession session= Twitter.getSessionManager().getActiveSession();

    public TwitterProfileFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        getActivity().getActionBar()
                .setTitle(R.string.my_profile);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_twitter, container, false);

        profilePictureView = (ImageView) view.findViewById(R.id.profilePicture);
        profileName = (TextView) view.findViewById(R.id.profileName);
        profileLocation = (TextView) view.findViewById(R.id.profileLocation);
        profileEmail = (TextView) view.findViewById(R.id.profileEmail);
        profileBio = (TextView) view.findViewById(R.id.profileBio);

        updateUI();

        // Inflate the layout for this fragment
        return view;

    }

    private void updateUI() {
        final boolean enableButtons = (session != null);

      // TwitterAuthClient authClient = new TwitterAuthClient();

                try {
                    // If the response is successful

                        if (session.getUserName() != null) {
                         String twitterURL = "http://avatars.io/twitter/:username";
                          /*  twitterURL = twitterURL.replace(":username", session.getUserName());
                            Toast.makeText(getActivity().getApplicationContext(), twitterURL, Toast.LENGTH_LONG).show();
                            Bitmap bmp= null;
                            InputStream imageStream =null;

                            DefaultHttpClient httpclient = new DefaultHttpClient();
                            HttpGet httpget = new HttpGet(twitterURL);

                            HttpResponse response = httpclient.execute(httpget);


                            HttpEntity responseEntity = response.getEntity();
                            BufferedHttpEntity httpEntity =new BufferedHttpEntity(responseEntity);
                            imageStream = httpEntity.getContent();

                            bmp = BitmapFactory.decodeStream(imageStream);

                            //Uri myUri = Uri.parse(twitterURL);
                           // *get the profile image URL
                            profilePictureView.setImageBitmap(bmp);*/
                            ImageLoader.getInstance().displayImage("http://avatars.io/twitter/" + session.getUserName()+"?size=large", profilePictureView);

                            profileName.setText(session.getUserName());
                           // profileLocation.setText(user.getLocation().getName());
                            //profileEmail.setText(user.getProperty("email") != null ? user.getProperty("email").toString() : "[Email not available]");
                            //profileBio.setText(user.getProperty("bio").toString());

                            //greeting.setText(getString(R.string.hello_user, user.getFirstName()));
                        } else {
                            profilePictureView.setImageURI(null);
                            profileName.setText("");
                            profileLocation.setText("");
                            profileEmail.setText("");
                            profileBio.setText("");
                            //greeting.setText(null);
                        }



                } catch (Exception ex) {
                    Log.e("PROFILE_FRAGMENT", "MSG:" + ex.getMessage());
                }
            }





    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
