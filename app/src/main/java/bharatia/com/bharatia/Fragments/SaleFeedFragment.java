package bharatia.com.bharatia.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import bharatia.com.bharatia.Adapter.PostAdapter;
import bharatia.com.bharatia.DataModel.Post;
import bharatia.com.bharatia.R;


public class SaleFeedFragment extends Fragment {

    // Instantiate the RequestQueue.
    RequestQueue queue;
    String url ="http://sadmanamin.com/android_connect/allSalePost.php";


    private RecyclerView postRecycleView;
    private RecyclerView.Adapter postAdapter;
    private RecyclerView.LayoutManager postLayoutManager;

    private ArrayList<Post> postArrayList = new ArrayList<>();

    public SaleFeedFragment() {
        // Required empty public constructor
    }

    Context context;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sale_feed, container, false);

        while (context==null) {
            context = getContext();
        }

        postRecycleView = (RecyclerView)v.findViewById(R.id.saleRecyclerview);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        postRecycleView.setHasFixedSize(true);


        postArrayList = new ArrayList<>();

        // use a linear layout manager
        postLayoutManager = new LinearLayoutManager(context);
        postRecycleView.setLayoutManager(postLayoutManager);

        postAdapter = new PostAdapter(postArrayList,context);
        postRecycleView.setAdapter(postAdapter);

        queue = Volley.newRequestQueue(context);

        final Gson gson = new Gson();
        //Post post = gson.fromJson()

        try {
            if (context != null) {
                SharedPreferences salePref = context.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
                String saelResponse = salePref.getString(getString(R.string.salePost), "[]");

                //Log.e("sale json", saelResponse);


                Post[] posts = gson.fromJson(saelResponse, Post[].class);
                //postArrayList = new ArrayList<>(Arrays.asList(posts));
                for (int i = 0; i < posts.length; i++) {
                    postArrayList.add(posts[i]);
                    postRecycleView.getRecycledViewPool().clear();
                    postAdapter.notifyDataSetChanged();
                    postRecycleView.swapAdapter(postAdapter, false);
                }

            }

            postRecycleView.getRecycledViewPool().clear();
            postAdapter.notifyDataSetChanged();
            //postRecycleView.swapAdapter(postAdapter, false);


            saleRequest();

        }catch (Exception e) {
            Log.e("err",e.toString());
        }
        postRecycleView.getRecycledViewPool().clear();
        postAdapter.notifyDataSetChanged();
        //postRecycleView.swapAdapter(postAdapter,false);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//
//                shareImage(postRecycleView,postRecycleView.getWidth(),postRecycleView.getHeight());
//
//            }
//        }).start();

        return v;
    }


    void saleRequest() {
        // Request a string response from the provided URL.
        final StringRequest allSalePostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Log.e("res",response);

                        try {

                            Gson gson = new Gson();

                            Post[] posts = gson.fromJson(response, Post[].class);
                            postArrayList.clear();
                            for (int i = 0; i < posts.length; i++) {
                                //postArrayList.remove(i);

                                postArrayList.add(posts[i]);
                                postRecycleView.getRecycledViewPool().clear();
                                postAdapter.notifyDataSetChanged();
                                //postRecycleView.swapAdapter(postAdapter, false);

                            }

                            Context context = getContext();
                            if (context != null) {
                                //saving to local db
                                SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(getString(R.string.salePost), response);
                                editor.apply();

                            }
                        }catch (Exception e) {
                            Log.e("err",e.toString());
                        }
                        //postArrayList = new ArrayList<>(Arrays.asList(posts));


//                        ArrayList<Post> tempPost = new ArrayList<>(Arrays.asList(posts));
//                        for(int i=0;i<posts.length;i++){
//                            for(int j=0;j<tempPost.size();j++) {
//                                if(postArrayList.get(i).getPostID().equals(tempPost.get(j).getPostID())) {
//                                    postArrayList.add(tempPost.get(j));
//                                    tempPost.remove(j);
//
//                                    postAdapter.notifyItemInserted(i);
//                                    postAdapter.notifyDataSetChanged();
//                                    postRecycleView.swapAdapter(postAdapter,false);
//                                    break;
//                                }
//                            }

//                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                //Toast.makeText(loginActivity.this,"Oops! Something went wrong...",Toast.LENGTH_SHORT).show();

            }
        });


        // Add the request to the RequestQueue.
        queue.add(allSalePostRequest);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        queue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                // do I have to cancel this?
                return true; // -> always yes
            }
        });
    }
}
