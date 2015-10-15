package nyp.fypj.irarphotodiary.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import com.etsy.android.grid.StaggeredGridView;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;

import java.util.ArrayList;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.activity.CreateStoryListActivity;
import nyp.fypj.irarphotodiary.activity.ManageStoryActivity;
import nyp.fypj.irarphotodiary.application.BootstrapApplication;
import nyp.fypj.irarphotodiary.dto.Album;
import nyp.fypj.irarphotodiary.dto.ImageProfile;
/**
 * A simple {@link Fragment} subclass.
 */
public class MyDiaryFragment extends Fragment {
    Album selectedAlbum;
    MyDiaryFragmentAdapter myDiaryFragmentAdapter;
    public MyDiaryFragment() {
        // Required empty public constructor
    }
    @Override
    public void onResume() {
        super.onResume();
        // Set title
        getActivity().getActionBar()
                .setTitle(R.string.manage_my_diary);
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
        //to remove cache

        // MemoryCacheUtils.removeFromCache("nodejs-irarphotodiary.rhcloud.com/albums/", ImageLoader.getInstance().getMemoryCache());

        // ImageLoaderConfiguration config= new ImageLoaderConfiguration().Builder(getActivity().getApplicationContext()).memoryCache(new UsingFreqLimitedMemoryCache((2*1024*1024))).discCache(new UnlimitedDiscCache(cacheDir));
        final ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.myDiaryFragmentProgressBar);
        final StaggeredGridView staggeredGridView = (StaggeredGridView) getView().findViewById(R.id.myDiaryFragmentStaggeredGridView);
        staggeredGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Album selectedAlbum = (Album) myDiaryFragmentAdapter.getItem(i);
                Intent intent = new Intent(MyDiaryFragment.this.getActivity().getApplicationContext(), ManageStoryActivity.class);
                intent.putParcelableArrayListExtra("imageProfiles", selectedAlbum.getImageProfiles());
                intent.putExtra("albumId", selectedAlbum.get_id());
                intent.putExtra("albumTitle", selectedAlbum.getTitle());
                intent.putExtra("albumDescription", selectedAlbum.getDescription());
                intent.putExtra("albumDateUploaded", selectedAlbum.getDateUploaded());
                startActivity(intent);
            }
        });
        progressBar.setVisibility(ProgressBar.VISIBLE);
        Ion.with(this)
                .load("nodejs-irarphotodiary.rhcloud.com/albums/")
                .as(new TypeToken<ArrayList<Album>>() {
                })
                .setCallback(new FutureCallback<ArrayList<Album>>() {
                    @Override
                    public void onCompleted(Exception e, ArrayList<Album> albums) {
                        myDiaryFragmentAdapter = new MyDiaryFragmentAdapter(staggeredGridView.getContext(), albums);
                        staggeredGridView.setAdapter(myDiaryFragmentAdapter);

                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                    }
                });

        //onbackpress


    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.my_diary_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        public MyDiaryFragmentAdapter(Context context, ArrayList<Album> albums) {
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
                view = layoutInflater.inflate(R.layout.adapter_fragment_my_diary_list_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.myDiaryItemImage = (ImageView) view.findViewById(R.id.myDiaryItemImage);
                viewHolder.myDiaryItemTitle = (TextView) view.findViewById(R.id.myDiaryItemTitle);
                viewHolder.myDiaryItemTitle.setTextColor(Color.parseColor("#ff33B5E5"));
                viewHolder.myDiaryItemDescription = (TextView) view.findViewById(R.id.myDiaryItemDescription);
                viewHolder.myDiaryItemSize=(TextView) view.findViewById(R.id.myDiaryItemSize);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            Album album = albums.get(i);
            ImageProfile coverImage = albums.get(i).getImageProfiles().get(0);
            viewHolder.myDiaryItemTitle.setText(album.getTitle());
            viewHolder.myDiaryItemSize.setText("("+Integer.toString(album.getImageProfiles().size())+")");
            viewHolder.myDiaryItemTitle.setTextColor(Color.parseColor("#ff33B5E5"));
            viewHolder.myDiaryItemDescription.setText(album.getDescription());
            viewHolder.myDiaryItemSize.setTextColor(Color.parseColor("#FFFFFF"));
            DiskCacheUtils.removeFromCache("http://res.cloudinary.com/" + BootstrapApplication.CLOUDINARY_CLOUD_NAME + "/image/upload/w_0.1/" + coverImage.getFilename() + "." + coverImage.getExtension(), ImageLoader.getInstance().getDiscCache());
            String imageUrl="http://res.cloudinary.com/" + BootstrapApplication.CLOUDINARY_CLOUD_NAME + "/image/upload/w_0.1/" + coverImage.getFilename() + "." + coverImage.getExtension();
            ImageLoader.getInstance().displayImage("http://res.cloudinary.com/" + BootstrapApplication.CLOUDINARY_CLOUD_NAME + "/image/upload/w_0.1/" + coverImage.getFilename() + "." + coverImage.getExtension(), viewHolder.myDiaryItemImage);
            return view;
        }
        private class ViewHolder {
            public ImageView myDiaryItemImage;
            public TextView myDiaryItemTitle;
            public TextView myDiaryItemDescription;
            public TextView myDiaryItemSize;
        }
    }
/**    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
 // Called when the action mode is created; startActionMode() was called
 @Override
 public boolean onCreateActionMode(ActionMode mode, Menu menu) {
 // Inflate a menu resource providing context menu items
 MenuInflater inflater = mode.getMenuInflater();
 inflater.inflate(R.menu.context_menu, menu);
 return true;
 }
 // Called each time the action mode is shown. Always called after onCreateActionMode, but
 // may be called multiple times if the mode is invalidated.
 @Override
 public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
 return false; // Return false if nothing is done
 }
 // Called when the user selects a contextual menu item
 @Override
 public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
 switch (item.getItemId()) {
 case R.id.menu_share:
 shareCurrentItem();
 mode.finish(); // Action picked, so close the CAB
 return true;
 default:
 return false;
 }
 }
 // Called when the user exits the action mode
 @Override
 public void onDestroyActionMode(ActionMode mode) {
 mActionMode = null;
 }
 };**/

}