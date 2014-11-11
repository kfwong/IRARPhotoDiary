package nyp.fypj.irarphotodiary.fragment;



import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.activity.CreateStoryListActivity;
import nyp.fypj.irarphotodiary.activity.ViewStoryActivity;
import nyp.fypj.irarphotodiary.application.BootstrapApplication;
import nyp.fypj.irarphotodiary.dto.Album;
import nyp.fypj.irarphotodiary.dto.ImageProfile;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MyDiaryFragment extends Fragment {


    public MyDiaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_diary, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.myDiaryFragmentProgressBar);

        final StaggeredGridView staggeredGridView = (StaggeredGridView) getView().findViewById(R.id.myDiaryFragmentStaggeredGridView);
        staggeredGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Album selectedAlbum = (Album)adapterView.getItemAtPosition(i);

                Intent intent = new Intent(MyDiaryFragment.this.getActivity().getApplicationContext(), ViewStoryActivity.class);
                intent.putParcelableArrayListExtra("imageProfiles", selectedAlbum.getImageProfiles());
                startActivity(intent);
            }
        });

        progressBar.setVisibility(ProgressBar.VISIBLE);

        Ion.with(this)
                .load("https://fypj-124465r.rhcloud.com/albums/")
                .as(new TypeToken<ArrayList<Album>>(){})
                .setCallback(new FutureCallback<ArrayList<Album>>() {
                    @Override
                    public void onCompleted(Exception e, ArrayList<Album> albums) {
                        MyDiaryFragmentAdapter myDiaryFragmentAdapter = new MyDiaryFragmentAdapter(staggeredGridView.getContext(), albums);
                        staggeredGridView.setAdapter(myDiaryFragmentAdapter);

                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                    }
                });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.my_diary_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.myDiaryCreateStory:
                Intent intent = new Intent(getView().getContext(), CreateStoryListActivity.class);
                startActivityForResult(intent, 1);// TODO

                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class MyDiaryFragmentAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private ArrayList<Album> albums;

        public MyDiaryFragmentAdapter(Context context, ArrayList<Album> albums){
            this.layoutInflater = LayoutInflater.from(context);
            this.albums = albums;
        }

        @Override
        public int getCount() {
            return albums.size();
        }

        @Override
        public Object getItem(int i) {
            return albums.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;

            if(convertView == null){
                view = layoutInflater.inflate(R.layout.adapter_fragment_my_diary_list_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.myDiaryItemImage = (ImageView) view.findViewById(R.id.myDiaryItemImage);
                viewHolder.myDiaryItemTitle = (TextView) view.findViewById(R.id.myDiaryItemTitle);
                viewHolder.myDiaryItemDescription = (TextView) view.findViewById(R.id.myDiaryItemDescription);
                view.setTag(viewHolder);
            }else{
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            Album album = albums.get(i);
            ImageProfile coverImage = albums.get(i).getImageProfiles().get(0);

            viewHolder.myDiaryItemTitle.setText(album.getTitle());
            viewHolder.myDiaryItemDescription.setText(album.getDescription());
            ImageLoader.getInstance().displayImage("http://res.cloudinary.com/"+ BootstrapApplication.CLOUDINARY_CLOUD_NAME+"/image/upload/w_0.1/"+coverImage.getFilename()+"."+coverImage.getExtension(), viewHolder.myDiaryItemImage);
            return view;
        }
        private class ViewHolder {
            public ImageView myDiaryItemImage;
            public TextView myDiaryItemTitle;
            public TextView myDiaryItemDescription;
        }
    }
}
