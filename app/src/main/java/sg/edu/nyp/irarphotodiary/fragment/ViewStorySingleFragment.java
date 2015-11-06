package sg.edu.nyp.irarphotodiary.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.nostra13.universalimageloader.core.ImageLoader;

import sg.edu.nyp.irarphotodiary.R;
import sg.edu.nyp.irarphotodiary.application.BootstrapApplication;
import sg.edu.nyp.irarphotodiary.dto.ImageProfile;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewStorySingleFragment extends Fragment {
    private ImageProfile imageProfile;

    public ViewStorySingleFragment() {
        // Required empty public constructor
    }

    public static ViewStorySingleFragment newInstance(ImageProfile imageProfile) {
        ViewStorySingleFragment viewStorySingleFragment = new ViewStorySingleFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("imageProfile", imageProfile);
        viewStorySingleFragment.setArguments(bundle);
        return viewStorySingleFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            imageProfile = getArguments().getParcelable("imageProfile");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_story_single, container, false);
        KenBurnsView viewStorySingleImage = (KenBurnsView) view.findViewById(R.id.viewStorySingleImage);
        TextView viewStorySingleTitle = (TextView) view.findViewById(R.id.viewStorySingleTitle);
        TextView viewStorySingleDescription = (TextView) view.findViewById(R.id.viewStorySingleDescription);

        if (imageProfile != null) {
            ImageLoader.getInstance().displayImage("http://res.cloudinary.com/" + BootstrapApplication.CLOUDINARY_CLOUD_NAME + "/image/upload/w_300,h_300/" + imageProfile.getFilename() + "." + imageProfile.getExtension(), viewStorySingleImage);
            viewStorySingleTitle.setText(imageProfile.getTitle());
            viewStorySingleDescription.setText(imageProfile.getDescription());
        }

        // Inflate the layout for this fragment
        return view;
    }

}
