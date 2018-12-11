package bharatia.com.bharatia.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;

import bharatia.com.bharatia.Adapter.PostAdapter;
import bharatia.com.bharatia.DataModel.Post;
import bharatia.com.bharatia.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyPropertiesFragment extends Fragment {


    RequestQueue queue;
    String userPostUrl ="http://sadmanamin.com/android_connect/userPost.php";
    String userInfoUrl ="http://sadmanamin.com/android_connect/userInfo.php";

    Gson gson;

    String userId = "";

    private RecyclerView postRecycleView;
    private RecyclerView.Adapter postAdapter;
    private RecyclerView.LayoutManager postLayoutManager;

    private ArrayList<Post> postArrayList = new ArrayList<>();

    RelativeLayout noAccountPost;

    public MyPropertiesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_properties, container, false);

        noAccountPost = (RelativeLayout) view.findViewById(R.id.noAccountPostLayout);


        postRecycleView = (RecyclerView)view.findViewById(R.id.accountRecyclerview);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        postRecycleView.setHasFixedSize(true);


        postArrayList = new ArrayList<>();

        // use a linear layout manager
        postLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        postRecycleView.setLayoutManager(postLayoutManager);

        postAdapter = new PostAdapter(postArrayList,getContext());
        postRecycleView.setAdapter(postAdapter);

        queue = Volley.newRequestQueue(getContext());

        gson = new Gson();
        //Post post = gson.fromJson()


        postRecycleView.getRecycledViewPool().clear();
        postArrayList.clear();
        postAdapter.notifyDataSetChanged();
        //postRecycleView.swapAdapter(postAdapter,false);

        postAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {
                super.onChanged();
                checkEmpty();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                checkEmpty();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkEmpty();
            }

            void checkEmpty() {
                noAccountPost.setVisibility(postAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        });


        if(getContext()!= null) {
            SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
            String userId = sharedPref.getString(getString(R.string.userId), "");
            String userInfo = sharedPref.getString(getString(R.string.userInfo), "[]");
            String userPost = sharedPref.getString(getString(R.string.userPost), "[]");

//            try {
//                Post[] posts = gson.fromJson(userPost, Post[].class);
//
//
////                if (posts.length >= 1) {
////                    noAccountPost.setVisibility(View.GONE);
////                }else {
////                    noAccountPost.setVisibility(View.VISIBLE);
////                }
//
//                for (int i = 0; i < posts.length; i++) {
//                    postArrayList.add(posts[i]);
//                    postRecycleView.getRecycledViewPool().clear();
//                    postAdapter.notifyDataSetChanged();
//                    //postRecycleView.swapAdapter(postAdapter, false);
//                }
//
//            } catch (Exception e) {
//                //e.printStackTrace();
//                //Toast.makeText(getContext(), "Oops! Something went wrong...", Toast.LENGTH_SHORT).show();
//            }

            //getUserPost(userId);

        }

        getUserPost(userId);
        postRecycleView.getRecycledViewPool().clear();
        postAdapter.notifyDataSetChanged();
        //postRecycleView.swapAdapter(postAdapter,false);

        return view;
    }


    public void getUserPost(String userId) {

        // Request a string response from the provided URL.
        final StringRequest accountRequest = new StringRequest(Request.Method.GET, userPostUrl+"?UserID="+userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("res",response);

                        //progressBar.setVisibility(View.GONE);

                        try {

                            Post[] posts = gson.fromJson(response, Post[].class);
                            postArrayList.clear();
//                            if (posts.length >= 1) {
//                                //Toast.makeText(getContext(), "Swipe left to scroll", Toast.LENGTH_SHORT).show();
//                                noAccountPost.setVisibility(View.GONE);
//                            }else {
//                                noAccountPost.setVisibility(View.VISIBLE);
//                            }

                            for(int i=0;i<posts.length;i++){
                                //postArrayList.remove(i);

                                postArrayList.add(posts[i]);
                                postRecycleView.getRecycledViewPool().clear();
                                postAdapter.notifyDataSetChanged();
                                //postRecycleView.swapAdapter(postAdapter,false);

                                if(getContext() != null) {
                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(getString(R.string.userPost), response);
                                    editor.apply();
                                }
                            }

                        }catch (Exception e) {
                            Log.e("err",e.toString());
                            //Toast.makeText(getContext(),"Oops!Something went wrong!",Toast.LENGTH_SHORT).show();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                //Toast.makeText(getContext(),"Oops! Something went wrong...",Toast.LENGTH_SHORT).show();

            }
        });

        // Add the request to the RequestQueue.
        queue.add(accountRequest);

    }

}
