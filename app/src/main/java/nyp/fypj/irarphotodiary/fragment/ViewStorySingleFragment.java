package nyp.fypj.irarphotodiary.fragment;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nyp.fypj.irarphotodiary.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ViewStorySingleFragment extends Fragment {


    public ViewStorySingleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_story_single, container, false);
    }


}
