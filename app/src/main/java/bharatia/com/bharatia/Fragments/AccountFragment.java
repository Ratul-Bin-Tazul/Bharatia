package bharatia.com.bharatia.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bharatia.com.bharatia.Adapter.PostAdapter;
import bharatia.com.bharatia.DataModel.Account;
import bharatia.com.bharatia.DataModel.Post;
import bharatia.com.bharatia.R;
import bharatia.com.bharatia.SearchActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    RequestQueue queue;
    String userPostUrl ="http://sadmanamin.com/android_connect/userPost.php";
    String userInfoUrl ="http://sadmanamin.com/android_connect/userInfo.php";

    String updateInfoUrl ="http://sadmanamin.com/android_connect/updateUserInfo.php";


    Gson gson;


    TextView username,email,fullName,mobile,postCount,viewCount,savedCount;
    ImageView userPic;


    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        username = (TextView) v.findViewById(R.id.accountUsername);
        email = (TextView) v.findViewById(R.id.accountEmail);
        postCount = (TextView) v.findViewById(R.id.accountpostCount);
        viewCount = (TextView) v.findViewById(R.id.accountVisitCount);
        savedCount = (TextView) v.findViewById(R.id.accountSavedCount);
        fullName = (TextView) v.findViewById(R.id.accountFullName);
        mobile = (TextView) v.findViewById(R.id.accountMobile);
        userPic = (ImageView) v.findViewById(R.id.accountUserPicImage);


        queue = Volley.newRequestQueue(getContext());

        gson = new Gson();

        //SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file_key),Context.MODE_PRIVATE);
        //String uid = sharedPref.getString(getString(R.string.userId), "");

        if(getContext()!= null) {
            SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
            String userId = sharedPref.getString(getString(R.string.userId), "");
            String userInfo = sharedPref.getString(getString(R.string.userInfo), "{}");


            try {
                if (userInfo != null && !userInfo.equals("{}")) {
                    Account account = gson.fromJson(userInfo, Account.class);
                    username.setText(account.getUsername());
                    email.setText(account.getEmail());
                    if (account.getPhoto() != null && !account.getPhoto().trim().equals("")) {
                        Picasso.get().load(account.getPhoto()).placeholder(R.drawable.ic_account_grey600_48dp).resize(90,90).into(userPic);
                        Log.e("img", account.getPhoto());
                    } else {
                        Picasso.get().load(R.drawable.ic_account_grey600_48dp);
                    }
                    fullName.setText(account.getFirst() + " " + account.getLast());
                    if (account.getPhone().equals("") || account.getPhone().equals("-1")) {
                        mobile.setText("Not set");
                    } else {
                        mobile.setText(account.getPhone());
                    }

                    Log.e("mbl", mobile.getText().toString());
                    Log.e("mbl", account.getViewCnt());
                    savedCount.setText(account.getSaveCnt());
                    viewCount.setText(account.getViewCnt());

                }

                getUserInfo(userId);
            }catch (Exception e) {
                Log.e("err",e.toString());
            }
        }


        return v;
    }


    public void getUserInfo(final String userId) {

        String curatedUrl = userInfoUrl+"?UserID="+userId;

        Log.e("res",curatedUrl);

        // Request a string response from the provided URL.
        final StringRequest accountInfoRequest = new StringRequest(Request.Method.GET, curatedUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("res",response);

                        //progressBar.setVisibility(View.GONE);

                        try {

                            Account account = gson.fromJson(response, Account.class);
                            username.setText(account.getUsername());
                            email.setText(account.getEmail());
                            if(account.getPhoto()!=null || !account.getPhoto().isEmpty())
                                Picasso.get().load(account.getPhoto()).placeholder(R.drawable.ic_account_grey600_48dp).resize(90,90).into(userPic);
                            fullName.setText(account.getFirst()+" "+account.getLast());
                            if(account.getPhone().isEmpty() || account.getPhone().equals("-1"))
                                mobile.setText("Not set");
                            else
                                mobile.setText(account.getPhone());

                            savedCount.setText(account.getSaveCnt());
                            viewCount.setText(account.getViewCnt());



                            if(getContext() != null) {
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(getString(R.string.userInfo), response);
                                editor.apply();
                            }
                        }catch (Exception e) {
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
        queue.add(accountInfoRequest);

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
