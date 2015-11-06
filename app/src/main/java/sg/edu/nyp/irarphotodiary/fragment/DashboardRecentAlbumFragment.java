package sg.edu.nyp.irarphotodiary.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import sg.edu.nyp.irarphotodiary.R;
import sg.edu.nyp.irarphotodiary.activity.ViewStoryActivity;
import sg.edu.nyp.irarphotodiary.application.BootstrapApplication;
import sg.edu.nyp.irarphotodiary.dto.Album;
import sg.edu.nyp.irarphotodiary.dto.ImageProfile;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardRecentAlbumFragment extends Fragment {


    public DashboardRecentAlbumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard_recent_album, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.dashboardRecentAlbumFragmentProgressBar);

        final StaggeredGridView staggeredGridView = (StaggeredGridView) getView().findViewById(R.id.dashboardRecentAlbumFragmentStaggeredGridView);
        staggeredGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Album selectedAlbum = (Album) adapterView.getItemAtPosition(i);

                Intent intent = new Intent(DashboardRecentAlbumFragment.this.getActivity().getApplicationContext(), ViewStoryActivity.class);
                intent.putParcelableArrayListExtra("imageProfiles", selectedAlbum.getImageProfiles());
                startActivity(intent);
            }
        });

        progressBar.setVisibility(ProgressBar.VISIBLE);

        Ion.with(this)
                .load("https://app-irarphotodiary.rhcloud.com/albums/")
                .as(new TypeToken<ArrayList<Album>>() {
                })
                .setCallback(new FutureCallback<ArrayList<Album>>() {
                    @Override
                    public void onCompleted(Exception e, ArrayList<Album> albums) {
                        dashboardRecentAlbumFragmentAdapter dashboardRecentAlbumFragmentAdapter = new dashboardRecentAlbumFragmentAdapter(staggeredGridView.getContext(), albums);
                        staggeredGridView.setAdapter(dashboardRecentAlbumFragmentAdapter);

                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                    }
                });
    }

    private class dashboardRecentAlbumFragmentAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private ArrayList<Album> albums;

        public dashboardRecentAlbumFragmentAdapter(Context context, ArrayList<Album> albums) {
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

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.adapter_fragment_dashboard_recent_album_list_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.dashboardRecentAlbumItemImage = (ImageView) view.findViewById(R.id.dashboardRecentAlbumItemImage);
                viewHolder.dashboardRecentAlbumItemTitle = (TextView) view.findViewById(R.id.dashboardRecentAlbumItemTitle);
                viewHolder.dashboardRecentAlbumItemDescription = (TextView) view.findViewById(R.id.dashboardRecentAlbumItemDescription);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            Album album = albums.get(i);
            ImageProfile coverImage = albums.get(i).getImageProfiles().get(0);

            viewHolder.dashboardRecentAlbumItemTitle.setText(album.getTitle());
            viewHolder.dashboardRecentAlbumItemDescription.setText(album.getDescription());
            ImageLoader.getInstance().displayImage("http://res.cloudinary.com/" + BootstrapApplication.CLOUDINARY_CLOUD_NAME + "/image/upload/w_0.1/" + coverImage.getFilename() + "." + coverImage.getExtension(), viewHolder.dashboardRecentAlbumItemImage);
            return view;
        }

        private class ViewHolder {
            public ImageView dashboardRecentAlbumItemImage;
            public TextView dashboardRecentAlbumItemTitle;
            public TextView dashboardRecentAlbumItemDescription;
        }
    }
}
