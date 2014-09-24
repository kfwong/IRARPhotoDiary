package nyp.fypj.irarphotodiary.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;

import java.util.ArrayList;
import java.util.List;

import nyp.fypj.irarphotodiary.R;

public class DashboardHomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<String> data = new ArrayList<String>();
        data.add("January");
        data.add("February");
        data.add("March");

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.dashboardHomeFragmentSwipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.ICS_BLUE, R.color.grey, R.color.ICS_BLUE, R.color.grey);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getView().getContext(), "Refresh!", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getView().getContext(), "Done!", Toast.LENGTH_SHORT).show();
                    }
                }, 5000);
            }
        });

        StaggeredGridView staggeredGridView = (StaggeredGridView) getView().findViewById(R.id.dashboardHomeFragmentStaggeredGridView);
        DashboardHomeFragmentAdapter dashboardHomeFragmentAdapter = new DashboardHomeFragmentAdapter(staggeredGridView.getContext(), data);
        staggeredGridView.setAdapter(dashboardHomeFragmentAdapter);
    }

    private class DashboardHomeFragmentAdapter extends BaseAdapter{
        private LayoutInflater layoutInflater;
        private List<String> data;

        public DashboardHomeFragmentAdapter(Context context, List<String> data){
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
                view = layoutInflater.inflate(R.layout.adapter_fragment_dashboard_home, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.dashboardHomeItemImage = (ImageView) view.findViewById(R.id.dashboardHomeItemImage);
                viewHolder.dashboardHomeItemTitle = (TextView) view.findViewById(R.id.dashboardHomeItemTitle);
                viewHolder.dashboardHomeItemDescription = (TextView) view.findViewById(R.id.dashboardHomeItemDescription);
                view.setTag(viewHolder);
            }else{
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            String datum = data.get(i);
            viewHolder.dashboardHomeItemTitle.setText(datum);
            viewHolder.dashboardHomeItemDescription.setText(datum + " (description)");
            return view;
        }
        private class ViewHolder {
            public ImageView dashboardHomeItemImage;
            public TextView dashboardHomeItemTitle;
            public TextView dashboardHomeItemDescription;
        }
    }


}