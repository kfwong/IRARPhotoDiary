package nyp.fypj.irarphotodiary.fragment;



import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;

import java.util.ArrayList;
import java.util.List;

import nyp.fypj.irarphotodiary.R;
import nyp.fypj.irarphotodiary.activity.CreateStoryListActivity;

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

        List<String> data = new ArrayList<String>();
        data.add("TADAH");
        data.add("OPPS");
        data.add("OMG");

        StaggeredGridView staggeredGridView = (StaggeredGridView) getView().findViewById(R.id.myDiaryFragmentStaggeredGridView);
        MyDiaryFragmentAdapter myDiaryFragmentAdapter = new MyDiaryFragmentAdapter(staggeredGridView.getContext(), data);
        staggeredGridView.setAdapter(myDiaryFragmentAdapter);
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
        private List<String> data;

        public MyDiaryFragmentAdapter(Context context, List<String> data){
            this.layoutInflater = LayoutInflater.from(context);
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return data.get(i);
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

            String datum = data.get(i);
            viewHolder.myDiaryItemTitle.setText(datum);
            viewHolder.myDiaryItemDescription.setText(datum + " (description)");
            return view;
        }
        private class ViewHolder {
            public ImageView myDiaryItemImage;
            public TextView myDiaryItemTitle;
            public TextView myDiaryItemDescription;
        }
    }

}
