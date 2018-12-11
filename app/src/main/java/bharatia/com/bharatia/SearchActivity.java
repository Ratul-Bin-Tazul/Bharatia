package bharatia.com.bharatia;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import bharatia.com.bharatia.Adapter.PostAdapter;
import bharatia.com.bharatia.DataModel.Post;
import bharatia.com.bharatia.Fragments.MapSearchFragment;

public class SearchActivity extends AppCompatActivity {

    // Instantiate the RequestQueue.
    RequestQueue queue;
    String url ="http://sadmanamin.com/android_connect/search.php";
    Gson gson;

    EditText rooms,area,lowerPrice,upperPrice;
    LinearLayout searchForm,result;
    Button searchBtn,searchAgainBtn;
    private RecyclerView postRecycleView;
    private RecyclerView.Adapter postAdapter;
    private RecyclerView.LayoutManager postLayoutManager;

    private ArrayList<Post> postArrayList = new ArrayList<>();

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //make status bar icons black
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#00695C\">Search</font>"));

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryGreen), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);


        Fragment mapFragment = new MapSearchFragment();
        loadFragment(mapFragment);

//
//        rooms = (EditText) findViewById(R.id.searchRoom);
//        area = (EditText) findViewById(R.id.searchArea);
//        lowerPrice = (EditText) findViewById(R.id.searchPriceLower);
//        upperPrice = (EditText) findViewById(R.id.searchPriceUpper);
//
//        searchBtn = (Button) findViewById(R.id.searchBtnActivity);
//        searchAgainBtn = (Button) findViewById(R.id.searchAgainBtn);
//
//        searchForm = (LinearLayout) findViewById(R.id.searchForm);
//        result = (LinearLayout) findViewById(R.id.searchResult);
//
//        progressBar = new ProgressBar(this);

//        searchBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                searchForm.setVisibility(View.GONE);
//                result.setVisibility(View.VISIBLE);
//                progressBar.setIndeterminate(true);
//                progressBar.setVisibility(View.VISIBLE);
//                searchPosts();
//            }
//        });
//
//        searchAgainBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                result.setVisibility(View.GONE);
//                searchForm.setVisibility(View.VISIBLE);
//                postArrayList.clear();
//            }
//        });
//
//        postRecycleView = (RecyclerView)findViewById(R.id.searchRecyclerview);
//        // use this setting to improve performance if you know that changes
//        // in content do not change the layout size of the RecyclerView
//        postRecycleView.setHasFixedSize(true);
//
//
//        postArrayList = new ArrayList<>();
//
//        // use a linear layout manager
//        postLayoutManager = new LinearLayoutManager(this);
//        postRecycleView.setLayoutManager(postLayoutManager);
//
//        postAdapter = new PostAdapter(postArrayList,this);
//        postRecycleView.setAdapter(postAdapter);
//
//        queue = Volley.newRequestQueue(this);
//
//        gson = new Gson();
//        //Post post = gson.fromJson()
//
//
//
//        postAdapter.notifyDataSetChanged();
//        postRecycleView.swapAdapter(postAdapter,false);
//
//
//        postAdapter.notifyDataSetChanged();
//        postRecycleView.swapAdapter(postAdapter,false);

    }

    private void searchPosts() {

        String curatedUrl;
        // Request a string response from the provided URL.
        final StringRequest searchRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("res",response);

                        progressBar.setVisibility(View.GONE);
                        try {
                            Post[] posts = gson.fromJson(response, Post[].class);
                            for(int i=0;i<posts.length;i++){
                                postArrayList.add(posts[i]);
                                postAdapter.notifyItemInserted(i);
                                postAdapter.notifyDataSetChanged();
                                postRecycleView.swapAdapter(postAdapter,false);
                            }

                        }catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(SearchActivity.this,"Oops! Something went wrong...",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(SearchActivity.this,"Oops! Something went wrong...",Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Rooms", rooms.getText().toString());
                params.put("Area", area.getText().toString());
                params.put("Price1", lowerPrice.getText().toString());
                params.put("Price2", upperPrice.getText().toString());
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(searchRequest);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home) {
            this.finish();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void loadFragment(Fragment fragment) {

            // load fragment
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.map_container, fragment);
            transaction.disallowAddToBackStack();
            //transaction.addToBackStack(null);
//        transaction.disallowAddToBackStack();
            transaction.commit();

    }
}
