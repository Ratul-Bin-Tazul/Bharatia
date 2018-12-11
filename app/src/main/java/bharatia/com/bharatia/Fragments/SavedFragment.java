package bharatia.com.bharatia.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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
public class SavedFragment extends Fragment {

    // Instantiate the RequestQueue.
    RequestQueue queue;
    String url ="http://sadmanamin.com/android_connect/searchBookmark.php";

    private RecyclerView postRecycleView;
    private RecyclerView.Adapter postAdapter;
    private RecyclerView.LayoutManager postLayoutManager;

    private ArrayList<Post> postArrayList = new ArrayList<>();

    RelativeLayout noSvedPostLayout;

    Context context;

    public boolean paused = false;

    public SavedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_saved, container, false);

        while (context==null) {
            context = getContext();
        }

        postRecycleView = (RecyclerView)v.findViewById(R.id.savedRecyclerview);

        noSvedPostLayout = (RelativeLayout) v.findViewById(R.id.noSavedPostLayout);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        postRecycleView.setHasFixedSize(true);


        postArrayList = new ArrayList<>();

        // use a linear layout manager
        postLayoutManager = new LinearLayoutManager(context);
        postRecycleView.setLayoutManager(postLayoutManager);

        postAdapter = new PostAdapter(postArrayList,context);
        postRecycleView.setAdapter(postAdapter);

        if (context!=null) {
            queue = Volley.newRequestQueue(context);
        }

        postRecycleView.getRecycledViewPool().clear();
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
                noSvedPostLayout.setVisibility(postAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        });


        if(isAdded()) {
            SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
            String userId = sharedPref.getString(getString(R.string.userId), "");

            SharedPreferences salePref = getActivity().getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
            String savedResponse = salePref.getString(getString(R.string.savedPost), "[]");

            Gson gson = new Gson();

            postArrayList.clear();

//            if (!savedResponse.equals("") && savedResponse.substring(0,1).equals("[")) {
//                Post[] posts = gson.fromJson(savedResponse, Post[].class);
//                //postArrayList = new ArrayList<>(Arrays.asList(posts));
//
//                //Showing the no post sad layout
////                if(posts.length<=0) {
////                    noSvedPostLayout.setVisibility(View.VISIBLE);
////                    postRecycleView.setVisibility(View.GONE);
////                }else {
////                    noSvedPostLayout.setVisibility(View.GONE);
////                    postRecycleView.setVisibility(View.VISIBLE);
////                }
//
//                for (int i = 0; i < posts.length; i++) {
//                    postArrayList.add(posts[i]);
//                    postRecycleView.getRecycledViewPool().clear();
//                    postAdapter.notifyDataSetChanged();
//                    //postRecycleView.swapAdapter(postAdapter, false);
//                }
//            }


            getBookmark(userId);
        }

        return v;
    }

    private void getBookmark(String userId) {
        final Gson gson = new Gson();
        //Post post = gson.fromJson()

        // Request a string response from the provided URL.
        final StringRequest savedPostRequest = new StringRequest(Request.Method.POST, url+"?UserID="+userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Log.e("res",response);

                        Post[] posts = gson.fromJson(response,Post[].class);
                        //postArrayList = new ArrayList<>(Arrays.asList(posts));

                        postArrayList.clear();

//                        if(posts.length<=0) {
//                            noSvedPostLayout.setVisibility(View.VISIBLE);
//                        }else {
//                            noSvedPostLayout.setVisibility(View.GONE);
//                        }


                        if(isAdded()) {
                            SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(getString(R.string.savedPost), response);
                            editor.apply();

                            for (int i = 0; i < posts.length; i++) {
                                postArrayList.add(posts[i]);
                                postRecycleView.getRecycledViewPool().clear();
                                postAdapter.notifyDataSetChanged();
                                //postRecycleView.swapAdapter(postAdapter, false);
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                //Toast.makeText(loginActivity.this,"Oops! Something went wrong...",Toast.LENGTH_SHORT).show();

            }
        });


        postRecycleView.getRecycledViewPool().clear();
        postAdapter.notifyDataSetChanged();
        //postRecycleView.swapAdapter(postAdapter,false);

        // Add the request to the RequestQueue.
        queue.add(savedPostRequest);

    }


    @Override
    public void onPause() {
        super.onPause();

        paused = true;
        postArrayList.clear();
    }

    @Override
    public void onResume() {
        super.onResume();

            if(isAdded() && paused) {
                try {
                    paused = false;
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    SavedFragment fragment = new SavedFragment();
                    transaction.replace(R.id.frame_container, fragment);
                    transaction.disallowAddToBackStack();
                    //transaction.addToBackStack(null);
//        transaction.disallowAddToBackStack();
                    transaction.commit();

                    postArrayList.clear();

                }catch (Exception e) {

                }
            }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
