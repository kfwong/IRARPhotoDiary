package nyp.fypj.irarphotodiary.fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.activity.CreateStoryActivity;
import nyp.fypj.irarphotodiary.application.BootstrapApplication;
import nyp.fypj.irarphotodiary.dto.ImageProfile;
import nyp.fypj.irarphotodiary.util.BitmapUtils;
import nyp.fypj.irarphotodiary.util.ColorProfiler;

public class CreateStoryListFragment extends ListFragment {
    private DragSortListView createStoryList;
    private CreateStoryListAdapter createStoryListAdapter;
    private ImageSize thumbnailSize = new ImageSize(128,128);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        createStoryList = (DragSortListView) inflater.inflate(R.layout.fragment_create_story, container, false);
        return createStoryList;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ImageProfile imageProfile1 = new ImageProfile();
        imageProfile1.setTitle("New stuff coming up soon!");
        imageProfile1.setDescription("How about adding some interesting description to this image?");

        ImageProfile imageProfile2 = new ImageProfile();
        imageProfile2.setTitle("New stuff coming up soon!");
        imageProfile2.setDescription("How about adding some interesting description to this image?");

        List<ImageProfile> imageProfiles = new ArrayList<ImageProfile>();
        imageProfiles.add(imageProfile1);
        imageProfiles.add(imageProfile2);

        createStoryListAdapter = new CreateStoryListAdapter(this.getListView().getContext(), imageProfiles);

        //http://stackoverflow.com/questions/14813882/bauerca-drag-sort-listview-simple-example
        createStoryList = (DragSortListView) getListView();
        createStoryList.setAdapter(createStoryListAdapter);
        createStoryList.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                if(from != to){
                    createStoryListAdapter.reorder(from, to);
                }
            }
        });

        DragSortController dragSortController = new DragSortController(createStoryList);
        dragSortController.setRemoveEnabled(false);
        dragSortController.setSortEnabled(true);
        dragSortController.setDragInitMode(DragSortController.ON_DOWN);
        dragSortController.setDragHandleId(R.id.createStoryItemPosition);

        createStoryList.setFloatViewManager(dragSortController);
        createStoryList.setOnTouchListener(dragSortController);
        createStoryList.setDragEnabled(true);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ImageProfile imageProfile = (ImageProfile) createStoryListAdapter.getItem(position);

        Intent intent = new Intent(getListView().getContext(), CreateStoryActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("imageProfile", imageProfile);
        startActivityForResult(intent, 1); //TODO: make the request code final
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == 1){
            if(resultCode == getActivity().RESULT_OK){
                int position = intent.getIntExtra("position", -1);
                ImageProfile imageProfile = intent.getExtras().getParcelable("imageProfile");
                createStoryListAdapter.set(position, imageProfile);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.create_story_fragment_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.createStoryAddNew:
                ImageProfile imageProfile = new ImageProfile();
                imageProfile.setTitle("New stuff coming up soon!");
                imageProfile.setDescription("How about adding some interesting description to this image?");
                createStoryListAdapter.add(imageProfile);
                break;
            case R.id.createStoryUpload:
                final List<ImageProfile> imageProfiles = createStoryListAdapter.imageProfiles;

                final NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                final NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(this.getView().getContext());
                notificationCompat.setSmallIcon(R.drawable.ic_launcher);
                notificationCompat.setContentTitle("Upload Album");

                ///// test & debugging
                new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            notificationCompat.setProgress(0, 0, true);
                            notificationManager.notify(1, notificationCompat.build());

                            try {
                                for(int i = 0; i<4;i++){
                                    notificationCompat.setContentText("Uploading in progress. ("+(i+1)+"/4)");
                                    notificationManager.notify(1, notificationCompat.build());
                                    Thread.sleep(3*1000);
                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            // When the loop is finished, updates the notification
                            notificationCompat.setContentText("Upload completed.");
                            notificationCompat.setProgress(0, 0, false);
                            notificationManager.notify(1, notificationCompat.build());
                        }
                    }
                ).start();
                /////
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class CreateStoryListAdapter extends BaseAdapter {

        private List<ImageProfile> imageProfiles;
        private LayoutInflater layoutInflater;

        private CreateStoryListAdapter(Context context, List<ImageProfile> imageProfiles) {
            this.imageProfiles = imageProfiles;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return imageProfiles.size();
        }

        @Override
        public Object getItem(int position) {
            return imageProfiles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            final ViewHolder viewHolder;

            if(convertView == null){
                view = layoutInflater.inflate(R.layout.adapter_fragment_create_story_list, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.createStoryItemThumbnail = (ImageView) view.findViewById(R.id.createStoryItemThumbnail);
                viewHolder.createStoryItemTitle = (TextView) view.findViewById(R.id.createStoryItemTitle);
                viewHolder.createStoryItemDescription = (TextView) view.findViewById(R.id.createStoryItemDescription);
                viewHolder.createStoryItemPosition = (TextView) view.findViewById(R.id.createStoryItemPosition);

                view.setTag(viewHolder);

            }else{
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            ImageProfile imageProfile = imageProfiles.get(position);
            viewHolder.createStoryItemTitle.setText(imageProfile.getTitle());
            viewHolder.createStoryItemDescription.setText(imageProfile.getDescription());
            viewHolder.createStoryItemPosition.setText("#"+position);
            if(imageProfile.getUri() != "" || imageProfile.getUri() != null){
                viewHolder.createStoryItemThumbnail.setImageBitmap(null);
                ImageLoader.getInstance().loadImage(imageProfile.getUri(), thumbnailSize, new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(loadedImage, 128, 128);
                        viewHolder.createStoryItemThumbnail.setImageBitmap(thumbnail);

                        Animation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
                        fadeIn.setDuration(300);
                        fadeIn.setFillAfter(true);
                        viewHolder.createStoryItemThumbnail.startAnimation(fadeIn);
                    }

                });
            }

            return view;
        }

        public void add(ImageProfile imageProfile){
            imageProfiles.add(imageProfile);
            notifyDataSetChanged();
        }

        public void add(int position, ImageProfile imageProfile){
            imageProfiles.add(position, imageProfile);
            notifyDataSetChanged();
        }

        public void set(int position, ImageProfile imageProfile){
            imageProfiles.set(position, imageProfile);
            notifyDataSetChanged();
        }

        public void remove(int position){
            imageProfiles.remove(position);
            notifyDataSetChanged();
        }

        public void reorder(int from, int to){
            ImageProfile imageProfile = imageProfiles.get(from);
            imageProfiles.remove(from);
            imageProfiles.add(to,imageProfile);
            notifyDataSetChanged();
        }

        public void swap(int from, int to){
            Collections.swap(imageProfiles, from, to);
            notifyDataSetChanged();
        }

        private class ViewHolder{
            public ImageView createStoryItemThumbnail;
            public TextView createStoryItemTitle;
            public TextView createStoryItemDescription;
            public TextView createStoryItemPosition;
        }
    }

    // Dumb bug fix for calling nested fragments onActivityResult
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // notifying nested fragments (support library bug fix)
//        final FragmentManager childFragmentManager = getChildFragmentManager();
//
//        if (childFragmentManager != null) {
//            final List<Fragment> nestedFragments = childFragmentManager.getFragments();
//
//            if (nestedFragments == null || nestedFragments.size() == 0) return;
//
//            for (Fragment childFragment : nestedFragments) {
//                //TODO: need to prevent double executing while attaching same fragment
//                if (childFragment != null && !childFragment.isDetached() && !childFragment.isRemoving()) {
//                    childFragment.onActivityResult(requestCode, resultCode, data);
//                }
//            }
//        }
//    }
}
