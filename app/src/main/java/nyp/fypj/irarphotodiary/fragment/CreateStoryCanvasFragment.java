package nyp.fypj.irarphotodiary.fragment;



import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nyp.fypj.irarphotodiary.R;

public class CreateStoryCanvasFragment extends Fragment {

    private final int ACTION_IMAGE_CAPTURE_REQUEST_CODE = 1;
    private View view;
    private Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //TODO;
        if(savedInstanceState!= null){
            bitmap = savedInstanceState.getParcelable("bitmap");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_story_canvas, container, false);

        if(bitmap !=null){
            ImageView createStoryCanvasFragmentImageView = (ImageView) view.findViewById(R.id.createStoryCanvasFragmentImageView);
            createStoryCanvasFragmentImageView.setImageBitmap(bitmap);
        }

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.createStoryCanvasTakePhoto:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, ACTION_IMAGE_CAPTURE_REQUEST_CODE);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.create_story_canvas_fragment, menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ACTION_IMAGE_CAPTURE_REQUEST_CODE){
            if(resultCode == getActivity().RESULT_OK){
                bitmap = (Bitmap) data.getExtras().get("data");
                ImageView createStoryCanvasFragmentImageView = (ImageView) view.findViewById(R.id.createStoryCanvasFragmentImageView);
                createStoryCanvasFragmentImageView.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("bitmap", bitmap);
    }
}
