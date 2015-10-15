package nyp.fypj.irarphotodiary.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.activity.LoginActivity;

/*import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;*/

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */

public class TwitterProfileFragment extends Fragment {

    private ImageView profilePictureView;
    private TextView profileName;
    private TextView profileLocation;
    private TextView profileEmail;
    private TextView profileBio;
    private static SharedPreferences mSharedPreferences;
    //  private TwitterSession session= Twitter.getSessionManager().getActiveSession();

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
        return view;
        // Inflate the layout for this fragment
    }

        public void updateUI(){
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences(LoginActivity.PREF_KEY_USER_NAME, Context.MODE_PRIVATE);
        Editor e = mSharedPreferences.edit();

        String username =
            mSharedPreferences.getString(LoginActivity.PREF_KEY_USER_NAME,null);


        ImageLoader.getInstance().displayImage("http://avatars.io/twitter/" + username + "?size=large", profilePictureView);

        profileName.setText(username);
        // profileLocation.setText(user.getLocation().getName());
        //profileEmail.setText(user.getProperty("email") != null ? user.getProperty("email").toString() : "[Email not available]");
        //profileBio.setText(user.getProperty("bio").toString());

        //greeting.setText(getString(R.string.hello_user, user.getFirstName()));

    }







    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
