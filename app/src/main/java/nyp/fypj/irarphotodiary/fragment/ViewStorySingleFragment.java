package nyp.fypj.irarphotodiary.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.activity.ViewImageActivity;
import nyp.fypj.irarphotodiary.application.BootstrapApplication;
import nyp.fypj.irarphotodiary.dto.ImageProfile;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewStorySingleFragment extends Fragment {
    private ImageProfile imageProfile;
    private TextView date;
    private TextView day;
    private TextView monthyear;
    private TextView time;

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





        //initial refresh

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String date1= imageProfile.getDateUploaded();
        View view = inflater.inflate(R.layout.fragment_view_story_single, container, false);
        KenBurnsView viewStorySingleImage = (KenBurnsView) view.findViewById(R.id.viewStorySingleImage);
        TextView viewStorySingleTitle = (TextView) view.findViewById(R.id.viewStorySingleTitle);
        viewStorySingleTitle.setTextColor(Color.parseColor("#FCFC97"));
        TextView viewStorySingleDescription = (TextView) view.findViewById(R.id.viewStorySingleDescription);
        //display time
        date = (TextView) view.findViewById(R.id.date);
        day = (TextView) view.findViewById(R.id.day);
        monthyear = (TextView) view.findViewById(R.id.monthyear);
        time = (TextView) view.findViewById(R.id.time);

        List<String> arrayLists=new ArrayList<String>(Arrays.asList(date1.split(", ")));
        day.setText(arrayLists.get(0));
        date.setText(arrayLists.get(1));
        date.setTextColor(Color.parseColor("#FFFFFF"));
        monthyear.setText(arrayLists.get(2));
        time.setText(arrayLists.get(3));
        time.setTextColor(Color.parseColor("#D3F1FF"));

        if (imageProfile != null) {
            ImageLoader.getInstance().displayImage("http://res.cloudinary.com/" + BootstrapApplication.CLOUDINARY_CLOUD_NAME + "/image/upload/w_300,h_300/" + imageProfile.getFilename() + "." + imageProfile.getExtension(), viewStorySingleImage);
            viewStorySingleTitle.setText(imageProfile.getTitle());
            viewStorySingleDescription.setText(imageProfile.getDescription());
        }





        // Inflate the layout for this fragment
        return view;
    }
  /*  private void stackAFragment() {
        Fragment f;
        f = new ViewImageFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.string.view_image, f);
        ft.
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }*/
  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      imageProfile = getArguments().getParcelable("imageProfile");
      KenBurnsView viewStorySingleImage = (KenBurnsView) getView().findViewById(R.id.viewStorySingleImage);
      viewStorySingleImage.setOnClickListener(new View.OnClickListener() {

          public void onClick(View view) {


              Intent intent = new Intent(ViewStorySingleFragment.this.getActivity().getApplicationContext(), ViewImageActivity.class);
              intent.putExtra("imageProfile", imageProfile);
              startActivity(intent);

          }
      });
  }
}
