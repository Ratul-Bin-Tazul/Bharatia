package bharatia.com.bharatia.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

import bharatia.com.bharatia.Adapter.PostAdapter;
import bharatia.com.bharatia.DataModel.Post;
import bharatia.com.bharatia.PostFeedActivity;
import bharatia.com.bharatia.R;
import bharatia.com.bharatia.SearchActivity;
import bharatia.com.bharatia.loginActivity;

public class FeedFragment extends Fragment {


    private TabLayout tabLayout;

    private FrameLayout frameLayout;



    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_feed, container, false);

        //setHasOptionsMenu(true);

        //Tablayout
        //viewPager = (ViewPager) view.findViewById(R.id.newsContainer);
        //setupViewPager(viewPager);

        frameLayout = (FrameLayout)v.findViewById(R.id.feedFrame);
        tabLayout = (TabLayout) v.findViewById(R.id.feedTablayout);
        //tabLayout.setupWithViewPager(viewPager);




        final SaleFeedFragment saleFeed = new SaleFeedFragment();
        final RentFeedFragment rentFeed = new RentFeedFragment();


        getChildFragmentManager().beginTransaction()
                .replace(R.id.feedFrame, rentFeed).disallowAddToBackStack().commitAllowingStateLoss();

        tabLayout.addTab(tabLayout.newTab().setText(R.string.fragment_rent_header),0);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.fragment_sale_header),1);

        TabLayout.Tab rentTab = tabLayout.getTabAt(0);
        TabLayout.Tab saleTab = tabLayout.getTabAt(1);

        //tabLayout.setSelectedTabIndicatorColor(Color.parseColor(""));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==0) {
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.feedFrame, rentFeed).disallowAddToBackStack().commitAllowingStateLoss();
                }else {
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.feedFrame, saleFeed).disallowAddToBackStack().commitAllowingStateLoss();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return v;
    }

}
