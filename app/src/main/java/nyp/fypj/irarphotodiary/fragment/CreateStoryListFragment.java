package nyp.fypj.irarphotodiary.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
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

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.activity.CreateStoryActivity;
import nyp.fypj.irarphotodiary.util.BitmapUtils;

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
        HashMap<String, String> datum1 = new HashMap<String, String>();
        datum1.put("title", "New stuff coming up soon!");
        datum1.put("description", "How about adding some interesting description to this image?");
        datum1.put("imageUri", "");
        HashMap<String, String> datum2 = new HashMap<String, String>();
        datum2.put("title", "New stuff coming up soon!");
        datum2.put("description", "How about adding some interesting description to this image?");
        datum2.put("imageUri", "");

        List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        data.add(datum1);
        data.add(datum2);

        createStoryListAdapter = new CreateStoryListAdapter(this.getListView().getContext(), data);

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
        HashMap<String, String> datum = (HashMap<String, String>) createStoryListAdapter.getItem(position);

        Intent intent = new Intent(getListView().getContext(), CreateStoryActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("datum", datum);
        startActivityForResult(intent, 1); //TODO: make the request code final
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == 1){
            if(resultCode == getActivity().RESULT_OK){
                HashMap<String, String> datum = (HashMap<String, String>)intent.getSerializableExtra("datum");
                int position = intent.getIntExtra("position", -1);
                createStoryListAdapter.set(position, datum);
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
                HashMap<String, String> datum = new HashMap<String, String>();
                datum.put("title", "New stuff coming up soon!");
                datum.put("description", "How about adding some interesting description to this image?");
                datum.put("imageUri","");
                createStoryListAdapter.add(datum);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class CreateStoryListAdapter extends BaseAdapter {

        private List<HashMap<String, String>> data;
        private LayoutInflater layoutInflater;

        private CreateStoryListAdapter(Context context, List<HashMap<String, String>> data) {
            this.data = data;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
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

            HashMap<String, String> datum = data.get(position);
            viewHolder.createStoryItemTitle.setText(datum.get("title"));
            viewHolder.createStoryItemDescription.setText(datum.get("description"));
            viewHolder.createStoryItemPosition.setText("#"+position);
            if(datum.get("imageUri") != "" || datum.get("imageUri") != null){
                viewHolder.createStoryItemThumbnail.setImageBitmap(null);
                ImageLoader.getInstance().loadImage(datum.get("imageUri"), thumbnailSize, new SimpleImageLoadingListener() {

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

        public void add(HashMap<String, String> datum){
            data.add(datum);
            notifyDataSetChanged();
        }

        public void add(int position, HashMap<String, String> datum){
            data.add(position, datum);
            notifyDataSetChanged();
        }

        public void set(int position, HashMap<String, String> datum){
            data.set(position, datum);
            notifyDataSetChanged();
        }

        public void remove(int position){
            data.remove(position);
            notifyDataSetChanged();
        }

        public void reorder(int from, int to){
            HashMap<String, String> datum = data.get(from);
            data.remove(from);
            data.add(to,datum);
            notifyDataSetChanged();
        }

        public void swap(int from, int to){
            Collections.swap(data, from, to);
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
