package bharatia.com.bharatia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import bharatia.com.bharatia.DataModel.Account;
import bharatia.com.bharatia.DataModel.Post;
import bharatia.com.bharatia.Fragments.AccountFragment;
import bharatia.com.bharatia.Fragments.FeedFragment;
import bharatia.com.bharatia.Fragments.MapSearchFragment;
import bharatia.com.bharatia.Fragments.MyPropertiesFragment;
import bharatia.com.bharatia.Fragments.PostFragment;
import bharatia.com.bharatia.Fragments.SavedFragment;
import bharatia.com.bharatia.Fragments.SearchFragment;
import bharatia.com.bharatia.Utils.BottomNavigationBehavior;
import bharatia.com.bharatia.Utils.BottomNavigationViewHelper;
import bharatia.com.bharatia.Utils.ChangeLanguage;

public class PostFeedActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView mTextMessage;
    private Toolbar toolbar;

    private static final int TIME_INTERVAL = 2000;
    private long backPressed;
    private boolean backPressedOnce;
    private Handler handler = new Handler();

    int currentTab = 0;

    private boolean callingFromPostDetails = false;

    // Add the request to the RequestQueue.
    RequestQueue queue;

    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_feed2);

        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        // load the store fragment by default
        toolbar.setTitle(R.string.fragment_title_feed);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryGreen));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#00695C\">Properties</font>"));


        queue = Volley.newRequestQueue(this);

        //make status bar icons black
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);

        String userInfo = sharedPref.getString(getString(R.string.userInfo), "[]");
        String userId = sharedPref.getString(getString(R.string.userId), "");
        if(isNetworkAvailable()) {

            getUserInfo(userId);
        }


//        mTextMessage = (TextView) findViewById(R.id.message);
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        BottomNavigationViewHelper.disableShiftMode(navigation);


        // attaching bottom sheet behaviour - hide / show on scroll
//        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
//        layoutParams.setBehavior(new BottomNavigationBehavior());


        //toolbar = getSupportActionBar();

        // load the store fragment by default
        toolbar.setTitle(R.string.fragment_title_feed);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryGreen));

        loadFragment(new FeedFragment());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                PostFeedActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Gson gson = new Gson();
        try {
            Account account = gson.fromJson(userInfo, Account.class);

            if (account != null) {
                Log.e("err", "acc not null");

                View v = navigationView.getHeaderView(0);
                ImageView userPic = v.findViewById(R.id.nav_imageView);
                TextView uname = v.findViewById(R.id.nav_username);
                TextView email = v.findViewById(R.id.nav_email);
                uname.setText(account.getUsername());
                email.setText(account.getEmail());

                if (account.getPhoto() == null)
                    Log.e("err", "photo null");
                if (account.getPhoto().equals(""))
                    Log.e("err", "photo empty");

                if (account.getPhoto() != null && !account.getPhoto().trim().equals("")) {

                    Log.e("img", account.getPhoto());
                    Picasso.get().load(account.getPhoto()).placeholder(R.drawable.ic_account_grey600_36dp).resize(80,80).into(userPic);

                }

            }

        }catch (Exception e) {
            Log.e("err",e.toString());
        }
        try {
            if (getIntent().getStringExtra("callingActivity") != null) {
                if (getIntent().getStringExtra("callingActivity").equals("PostDetails")) {
                    callingFromPostDetails = true;
                    //TODO: GO TO EDIT FRAGMENT
                }
            }

        Boolean editRequested = sharedPref.getBoolean(getString(R.string.editRequested), false);

        ////Log.e("editReq","entered onCreate");

        if(editRequested) {
            ////Log.e("editReq","requested entered");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            PostFragment postFragment = new PostFragment();
            transaction.replace(R.id.frame_container, postFragment);
            transaction.disallowAddToBackStack();
            //transaction.addToBackStack(null);
//        transaction.disallowAddToBackStack();
            transaction.commit();
            currentTab=2;

            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.editRequested),false);
            editor.apply();
            //navigation.setSelectedItemId(R.id.post);
        }

        }catch (Exception e) {
            //Log.e("err",e.toString());
        }
//        viewPager = (ViewPager) findViewById(R.id.containerViewpager);
//        setupViewPager(viewPager);
    }


//    private void setupViewPager(ViewPager viewPager) {
//        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        adapter.addFragment(new FeedFragment(), "Feed");
//        adapter.addFragment(new SearchFragment(), "Search");
//        adapter.addFragment(new PostFragment(), "Post");
//        adapter.addFragment(new SavedFragment(), "Saved");
//        adapter.addFragment(new AccountFragment(), "Account");
//        viewPager.setAdapter(adapter);
//    }

//    class ViewPagerAdapter extends FragmentPagerAdapter {
//        private final List<Fragment> mFragmentList = new ArrayList<>();
//        private final List<String> mFragmentTitleList = new ArrayList<>();
//
//        public ViewPagerAdapter(FragmentManager manager) {
//            super(manager);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return mFragmentList.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return mFragmentList.size();
//        }
//
//        public void addFragment(Fragment fragment, String title) {
//            mFragmentList.add(fragment);
//            mFragmentTitleList.add(title);
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mFragmentTitleList.get(position);
//        }
//    }

    private void loadFragment(Fragment fragment) {
        if(callingFromPostDetails) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragment);
            transaction.replace(R.id.frame_container, fragment);
            transaction.disallowAddToBackStack();
            transaction.commit();
        }else {
            // load fragment
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_container, fragment);
            transaction.disallowAddToBackStack();
            //transaction.addToBackStack(null);
//        transaction.disallowAddToBackStack();
            transaction.commit();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment;

        if (id == R.id.nav_properties) {
            currentTab = 0;

            toolbar.setTitle(R.string.fragment_title_feed);
            fragment = new FeedFragment();
            loadFragment(fragment);

        }else if (id == R.id.nav_search) {

            startActivity(new Intent(this,SearchActivity.class));

        } else if (id == R.id.nav_new_post) {

            currentTab = 1;
            toolbar.setTitle(R.string.fragment_title_post);

            fragment = new PostFragment();
            loadFragment(fragment);

        }else if (id == R.id.nav_fav) {

            currentTab = 2;
            toolbar.setTitle(R.string.fragment_title_saved);
            fragment = new SavedFragment();
            loadFragment(fragment);

        }else if (id == R.id.nav_my_properies) {

            currentTab = 3;
            toolbar.setTitle(R.string.fragment_title_my_properties);
            fragment = new MyPropertiesFragment();
            loadFragment(fragment);

        }else if (id == R.id.nav_profile) {

            currentTab = 4;
            toolbar.setTitle(R.string.fragment_title_account);
            fragment = new AccountFragment();
            loadFragment(fragment);

        }else if (id == R.id.nav_about) {

            startActivity(new Intent(this, AboutActivity.class));

        }else if (id == R.id.nav_privacy) {

            startActivity(new Intent(this, PrivacyPolicyActivity.class));

        }else if (id == R.id.nav_logout) {

            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.userId), "");
            editor.putString(getString(R.string.salePost), "[]");
            editor.putString(getString(R.string.rentPost), "[]");
            editor.putString(getString(R.string.userEmail), "");
            editor.apply();

            facebookLogout();
            googleLogout();

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Back to exit
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            backPressedOnce = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();


            if (currentTab != 0) {
                currentTab = 0;
                toolbar.setTitle(R.string.fragment_title_feed);
                Fragment fragment = new FeedFragment();
                loadFragment(fragment);
            } else {
                if (backPressedOnce) {
                    super.onBackPressed();
                    finish();
                    return;
                }

                this.backPressedOnce = true;
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

                handler.postDelayed(runnable, 2000);

            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        try {
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
            Boolean editRequested = sharedPref.getBoolean(getString(R.string.editRequested), false);

            //Log.e("editReq", "entered onCreate");

            if (editRequested) {
                //Log.e("editReq", "requested entered");
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, new PostFragment());
                transaction.disallowAddToBackStack();
                //transaction.addToBackStack(null);
//        transaction.disallowAddToBackStack();
                transaction.commit();
                currentTab = 2;

                //navigation.setSelectedItemId(R.id.post);
            }
        }catch (Exception e) {
            //Log.e("err",e.toString());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_search:
                startActivity(new Intent(this,SearchActivity.class));
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        queue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                // do I have to cancel this?
                return true; // -> always yes
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        String lang_code = ChangeLanguage.getLang(newBase); //load it from SharedPref
        //SharedPreferences sharedPref = newBase.getSharedPreferences(getString(R.string.pref_file_key),Context.MODE_PRIVATE);
        //lang_code = sharedPref.getString(getString(R.string.locale), "en");
        Context context = ChangeLanguage.changeLang(newBase, lang_code);
        super.attachBaseContext(context);
    }

    GoogleApiClient mGoogleApiClient;
    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void facebookLogout() {
        LoginManager.getInstance().logOut();
    }
    public void googleLogout() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                        //Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
    }

    public Boolean isOnline() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            return reachable;
        } catch (Exception e) {
            // TODO Auto-generated catch block
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
                            Gson gson = new Gson();
                            Account account = gson.fromJson(response, Account.class);

                            if (account != null) {
                                View v = navigationView.getHeaderView(0);
                                ImageView userPic = v.findViewById(R.id.nav_imageView);
                                TextView uname = v.findViewById(R.id.nav_username);
                                TextView email = v.findViewById(R.id.nav_email);
                                uname.setText(account.getUsername());
                                email.setText(account.getEmail());

                                Log.e("err", account.getPhoto());

                                if (account.getPhoto() != null && !account.getPhoto().trim().equals("")) {

                                    Picasso.get().load(account.getPhoto()).placeholder(R.drawable.ic_account_grey600_36dp).resize(80,80).into(userPic);

                                }


                            }

                            //saving to local db
                            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(getString(R.string.userInfo), response);
                            editor.apply();

                        }catch (Exception e) {
                            Log.e("err",e.toString());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(PostFeedActivity.this,"Oops! Something went wrong...",Toast.LENGTH_SHORT).show();

            }
        });

        // Add the request to the RequestQueue
        queue.add(accountInfoRequest);


    }

}
