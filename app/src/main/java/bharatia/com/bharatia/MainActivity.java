package bharatia.com.bharatia;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.novoda.merlin.Merlin;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import bharatia.com.bharatia.DataModel.Account;
import bharatia.com.bharatia.Utils.ChangeLanguage;

public class MainActivity extends AppCompatActivity {

    //Merlin merlin;

    Handler handler;

    Button eng,ban;

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      //  merlin = new Merlin.Builder().withConnectableCallbacks().build(getApplicationContext());

        queue = Volley.newRequestQueue(this);

        handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file_key),Context.MODE_PRIVATE);
                        String userId = sharedPref.getString(getString(R.string.userId), "");

                        if(!userId.equals("")) {
                            startActivity(new Intent(MainActivity.this,PostFeedActivity.class));
                            finish();
                        }else {
                            startActivity(new Intent(MainActivity.this,loginActivity.class));
                            finish();
                        }

                    }
                });
            }
        },1500);


        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        String userId = sharedPref.getString(getString(R.string.userId), "");


        try {

            if(isNetworkAvailable()) {

                getUserInfo(userId);

                saveBookmark(userId);

                getUserPost(userId);
            }else {
                //Toast.makeText(this,"You are offline",Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e) {

        }


//        eng = (Button) findViewById(R.id.eng);
//        ban = (Button)findViewById(R.id.ban);
//
//        eng.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                //saving to local db
//                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString(getString(R.string.locale), "en");
//                editor.apply();
//
//                ChangeLanguage.setLang("en");
//
//                startActivity(new Intent(MainActivity.this,loginActivity.class));
//                finishAffinity();
//            }
//        });
//
//        ban.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Locale locale = new Locale("bn_BD");
////                Locale.setDefault(locale);
////                Configuration config = new Configuration();
////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
////                    Log.e("set locale","locale set");
////                    config.setLocale(locale);
////                }else{
////
////                    Toast.makeText(MainActivity.this,"This feature is not available for your device",Toast.LENGTH_SHORT).show();
////                }
////
////                getApplicationContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
////                recreate();
////
////                Locale current = getResources().getConfiguration().locale;
////                Log.e("locale",current.getCountry());
//
//                //saving to local db
//                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString(getString(R.string.locale), "bn");
//                editor.apply();
//
//                ChangeLanguage.setLang("bn");
//
//                startActivity(new Intent(MainActivity.this,loginActivity.class));
//                finishAffinity();
//
//            }
//        });


    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        merlin.bind();
//    }
//
//    @Override
//    protected void onPause() {
//        merlin.unbind();
//        super.onPause();
//    }



    public void getUserPost(String userId) {

        // Request a string response from the provided URL.
        final StringRequest accountRequest = new StringRequest(Request.Method.GET, "http://sadmanamin.com/android_connect/userPost.php?UserID="+userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Log.e("res",response);

                        //progressBar.setVisibility(View.GONE);

                        Gson gson = new Gson();
                        //Account account = gson.fromJson(response, Account.class);

                        //saving to local db
                        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(getString(R.string.userPost),response);
                        editor.apply();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(getApplicationContext(),"Oops! Something went wrong...",Toast.LENGTH_SHORT).show();

            }
        });

        // Add the request to the RequestQueue.
        queue.add(accountRequest);

    }

    private void getUserInfo(String userId) {


        String curatedUrl = "http://sadmanamin.com/android_connect/userInfo.php?UserID="+userId;

        //Log.e("res",curatedUrl);

        // Request a string response from the provided URL.
        final StringRequest accountInfoRequest = new StringRequest(Request.Method.GET, curatedUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Log.e("res",response);

                        try {
                            //progressBar.setVisibility(View.GONE);


                        }catch (Exception e) {
                            Log.e("err",e.toString());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                //Toast.makeText(.this,"Oops! Something went wrong...",Toast.LENGTH_SHORT).show();

            }
        });

        // Add the request to the RequestQueue
        queue.add(accountInfoRequest);


    }
    private void saveBookmark(String userId) {
        final Gson gson = new Gson();
        //Post post = gson.fromJson()

        // Request a string response from the provided URL.
        final StringRequest savedPostRequest = new StringRequest(Request.Method.POST, "http://sadmanamin.com/android_connect/searchBookmark.php?UserID="+userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Log.e("res",response);

                        //saving to local db
                        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(getString(R.string.savedPost),response);
                        editor.apply();

                        //Post[] posts = gson.fromJson(response,Post[].class);
                        //postArrayList = new ArrayList<>(Arrays.asList(posts));


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                //Toast.makeText(loginActivity.this,"Oops! Something went wrong...",Toast.LENGTH_SHORT).show();

            }
        });


        queue.add(savedPostRequest);

    }


    public Boolean isOnline() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            return reachable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
