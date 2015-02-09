package nyp.fypj.irarphotodiary.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

import nyp.fypj.irarphotodiary.R;

/**
 * A simple {@link Fragment} subclass.
 */

public class ProfileFragment extends Fragment {

    private ProfilePictureView profilePictureView;
    private TextView profileName;
    private TextView profileLocation;
    private TextView profileEmail;
    private TextView profileBio;
    private Session session = Session.getActiveSession();

    public ProfileFragment() {

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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profilePictureView = (ProfilePictureView) view.findViewById(R.id.profilePicture);
        profileName = (TextView) view.findViewById(R.id.profileName);
        profileLocation = (TextView) view.findViewById(R.id.profileLocation);
        profileEmail = (TextView) view.findViewById(R.id.profileEmail);
        profileBio = (TextView) view.findViewById(R.id.profileBio);

        updateUI();

        // Inflate the layout for this fragment
        return view;

    }

    private void updateUI() {
        final boolean enableButtons = (session != null && session.isOpened());

        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {

                try {
                    // If the response is successful
                    if (session == Session.getActiveSession()) {
                        if (user != null) {
                            if (enableButtons && user != null) {
                                profilePictureView.setProfileId(user.getId());
                                profileName.setText(user.getName());
                                profileLocation.setText(user.getLocation().getName());
                                profileEmail.setText(user.getProperty("email") != null ? user.getProperty("email").toString() : "[Email not available]");
                                profileBio.setText(user.getProperty("bio").toString());

                                //greeting.setText(getString(R.string.hello_user, user.getFirstName()));
                            } else {
                                profilePictureView.setProfileId(null);
                                profileName.setText("");
                                profileLocation.setText("");
                                profileEmail.setText("");
                                profileBio.setText("");
                                //greeting.setText(null);
                            }

                        }
                    }
                } catch (Exception ex) {
                    Log.e("PROFILE_FRAGMENT", "MSG:" + ex.getMessage());
                }
            }
        });

        Request.executeBatchAsync(request);

    }
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
