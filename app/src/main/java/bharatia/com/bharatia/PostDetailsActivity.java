package bharatia.com.bharatia;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import bharatia.com.bharatia.Adapter.SliderAdapter;
import bharatia.com.bharatia.DataModel.Post;
import bharatia.com.bharatia.DataModel.PostPictureData;
import bharatia.com.bharatia.Utils.ChangeLanguage;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

public class PostDetailsActivity extends AppCompatActivity {

    private final int MY_PERMISSIONS_REQUEST_CALL = 32;
    private final int MY_PERMISSIONS_REQUEST_MESSAGE = 30;
    private final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 24;
    private final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 22;

    String phone,emailAddress;

    Menu menu;

    ProgressDialog pDialog;

    RequestQueue queue;
    String bookmarkUrl ="http://sadmanamin.com/android_connect/createBookmark.php";
    String deleteUrl ="http://sadmanamin.com/android_connect/deletePost.php?PostID=";
    String viewUrl ="http://sadmanamin.com/android_connect/viewCount.php?UserID=";

    Gson gson;

    TextView area, size, location, room, price, desc;
    TextView areaShare, sizeShare, roomShare, priceShare,postType;
    ImageView postImg;
    ImageView postImgSearch;

    //LinearLayout mainLayout;
    LinearLayout mainLinearLayout;
    RelativeLayout getDirection,shareLayout;

    Toolbar toolbar;


    boolean bookmarked = false;
    private int MENU_EDIT = 0;
    private int MENU_DELETE = 1;

    RelativeLayout contact;
    Button contactBtn;

    LinearLayout layoutBottomSheet,facillitiesLayout;

    BottomSheetBehavior sheetBehavior;

    TextView phoneText,messageText, emailText;

    RelativeLayout call, message, email;

    ImageButton shareCard;

    ImageButton bookmark;

    //Slider postSlider;

    ArrayList<String> photoLinksList = new ArrayList<>();

    String postId;

    ViewPager viewPager;
    CircleIndicator circleIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.transparentStatusbar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        //ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), EXTRA_IMAGE);

        if(pDialog!=null) {
            pDialog.dismiss();
        }


        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryGreen), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#00695C\">Post details</font>"));


        //toolbar = (Toolbar) findViewById(R.id.toolbarPostDetails);
        //toolbar.setTitle("");
//        setSupportActionBar(toolbar);

        area = (TextView) findViewById(R.id.postDetailsArea);
        size = (TextView) findViewById(R.id.postDetailsSize);
        //location = (TextView)findViewById(R.id.postDetailsAddress);
        room = (TextView) findViewById(R.id.postDetailsRooms);
        price = (TextView) findViewById(R.id.postDetailsPrice);
        desc = (TextView) findViewById(R.id.postDetailsDescription);
        //postImg = (ImageView) findViewById(R.id.postDetailsPhoto);
        //postSlider = findViewById(R.id.postDetailsSlider);

        roomShare = (TextView) findViewById(R.id.postDetailsShareRoom);
        sizeShare = (TextView) findViewById(R.id.postDetailsShareSize);
        areaShare = (TextView) findViewById(R.id.postDetailsShareArea);
        priceShare = (TextView) findViewById(R.id.postDetailsSharePrice);
        postType = (TextView) findViewById(R.id.postDetailsSharePostType);
        postImgSearch = (ImageView) findViewById(R.id.postDetailsShareImg);



        contact = (RelativeLayout) findViewById(R.id.postDetailsContact);
        contactBtn = (Button) findViewById(R.id.postDetailsContactButton);
        layoutBottomSheet = (LinearLayout) findViewById(R.id.contactBottomSheet);
        //sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        //sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        //mainLayout = (LinearLayout) findViewById(R.id.mainPostDetailsLayout);

        mainLinearLayout = (LinearLayout) findViewById(R.id.postDetailsMainLinearLayout);
        shareLayout = (RelativeLayout) findViewById(R.id.postDetailsShareLayout);

        shareCard = (ImageButton) findViewById(R.id.postDetailsShare);

        bookmark = (ImageButton) findViewById(R.id.postDetailsBookmark);

        facillitiesLayout = (LinearLayout) findViewById(R.id.postDetailsFacillitiesLayout);

        viewPager =  findViewById(R.id.sliderViewpage);
        circleIndicator =  findViewById(R.id.sliderIndicator);

        queue = Volley.newRequestQueue(this);

        gson = new Gson();
        //Post post = gson.fromJson()



        String[] descText = getIntent().getStringExtra("description").split("999");



        int margin = convertDpToPixel(5,this);
        int drawableMargin = convertDpToPixel(5,this);

        if (descText.length>1) {
            Log.e("facilities",descText[1]);

            String[] facillities = descText[1].split("00");

            for (String s : facillities) {
                Log.e("facilities",s);

                TextView textView = new TextView(this);
                textView.setText(s);
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_grey600_18dp, 0, 0, 0);
                textView.setCompoundDrawablePadding(drawableMargin);
                //textView.setTextSize(16);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0,margin,margin,margin);
                facillitiesLayout.addView(textView,layoutParams);
            }
        }

        String postId = getIntent().getStringExtra("postId");
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file_key),Context.MODE_PRIVATE);
        String savedResponse = sharedPref.getString(getString(R.string.savedPost), "[]");
        String userId = sharedPref.getString(getString(R.string.userId), "");

        viewCount(userId);

        Log.e("saved res",savedResponse);

        try {
            Post[] posts = gson.fromJson(savedResponse, Post[].class);

            ArrayList<Post> postArrayList = new ArrayList<>(Arrays.asList(posts));
            for (int i = 0; i < posts.length; i++) {
                if (postArrayList.get(i).getPostID().equals(postId)) {
                    bookmarked = true;
                    bookmark.setImageDrawable(getResources().getDrawable(R.drawable.green_heart_filled));
                    break;
                }
            }

        }catch(Exception e) {
            Log.e("err",e.toString());
        }


        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
                String userId = sharedPref.getString(getString(R.string.userId), "");

                String postId = getIntent().getStringExtra("postId");

                if(bookmarked) {
                    //Toast.makeText(PostDetailsActivity.this, "same icon", Toast.LENGTH_SHORT).show();
                    Toast.makeText(PostDetailsActivity.this,"Removed from saved posts",Toast.LENGTH_SHORT).show();
                    bookmark.setImageDrawable(getResources().getDrawable(R.drawable.green_heart_outline));

                    bookmarked=false;
                }else {

                    Toast.makeText(PostDetailsActivity.this,"Saved the post",Toast.LENGTH_SHORT).show();

                    bookmarked=true;
                    bookmark.setImageDrawable(getResources().getDrawable(R.drawable.green_heart_filled));
                }


                bookmarkPost(userId,postId);


            }
        });


        shareCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(PostDetailsActivity.this,"share entered",Toast.LENGTH_SHORT).show();

                int permissionCheckRead = ContextCompat.checkSelfPermission(PostDetailsActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE);
                int permissionCheckWrite = ContextCompat.checkSelfPermission(PostDetailsActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permissionCheckRead==PackageManager.PERMISSION_GRANTED && permissionCheckWrite==PackageManager.PERMISSION_GRANTED) {

                    //Toast.makeText(PostDetailsActivity.this,"if entered",Toast.LENGTH_SHORT).show();

                    try{


                        pDialog = new ProgressDialog(PostDetailsActivity.this);
                        pDialog.setMessage("Creating post...");
                        pDialog.setCancelable(false);
                        pDialog.show();

                        shareImage(shareLayout);

                        //print();
//                mainLayout.scrollBy(mainLayout.getHeight(),0);
//                print();

                    }catch (Exception e) {
                        Toast.makeText(PostDetailsActivity.this,"Failed!please try again.",Toast.LENGTH_SHORT).show();
                        Log.e("err",e.toString());
                    }

                }else {
                    getReadStroagePermission();
                }

            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                showContactDialog();
            }
        });
        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                showContactDialog();
            }
        });

//        String postId = getI
// ntent().getStringExtra("postId");
//        SharedPreferences rentPref = getSharedPreferences(getString(R.string.pref_file_key),Context.MODE_PRIVATE);
//        String savedResponse = rentPref.getString(getString(R.string.savedPost), "[]");
//
//        Log.e("saved res",savedResponse);
//
//        Post[] posts = gson.fromJson(savedResponse, Post[].class);
//
//        ArrayList<Post> postArrayList = new ArrayList<>(Arrays.asList(posts));
//        for (int i = 0; i < posts.length; i++) {
//            if(postArrayList.get(i).getPostID().equals(postId)) {
//                bookmarked = true;
//                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_favorite_white_24dp);
//                toolbar.getMenu().findItem(R.id.bookmark).setIcon(drawable);
//            }
//        }


        shareLayout.setVisibility(View.VISIBLE);

        if (getIntent().getStringExtra("photoLink") != null) {

            //Slider.init(new PicassoImageLoadingService(PostDetailsActivity.this));
            photoLinksList.add(getIntent().getStringExtra("photoLink"));
//            AdSliderAdapter adSliderAdapter = new AdSliderAdapter(photoLinksList);
//            postSlider.setAdapter(adSliderAdapter);
//            postSlider.setLoopSlides(true);
//            postSlider.setInterval(3000);

            Glide
                    .with(this)
                    .load(getIntent().getStringExtra("photoLink"))
                    .into(postImgSearch);

        }

        postId = getIntent().getStringExtra("postId");
        getPostPhotoes(postId);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            postImg.setClipToOutline(true);
//        }
//
//        if (getIntent().getStringExtra("photoLink") != null) {
//
//            Glide
//                    .with(this)
//                    .load(getIntent().getStringExtra("photoLink"))
//                    .into(postImg);
//
//            Glide
//                    .with(this)
//                    .load(getIntent().getStringExtra("photoLink"))
//                    .into(postImgSearch);
//
//        }

        String type1 = getIntent().getStringExtra("type1");
        area.setText(getIntent().getStringExtra("address"));
        if(type1!=null && type1.equals("Rent"))
         price.setText(getIntent().getStringExtra("price") +getString(R.string.post_details_tk) );
        else
            price.setText(getString(R.string.post_details_tk) + getIntent().getStringExtra("price"));
        room.setText(getIntent().getStringExtra("room") + getString(R.string.post_details_room));
        size.setText(getIntent().getStringExtra("size") + getString(R.string.post_details_sq_ft));
        //location.setText(getIntent().getStringExtra("address"));

        desc.setText(descText[0]);

        phone = getIntent().getStringExtra("phone");
        emailAddress = getIntent().getStringExtra("email");


        areaShare.setText(getIntent().getStringExtra("area"));
        roomShare.setText(getIntent().getStringExtra("room")+" rooms");
        sizeShare.setText(getIntent().getStringExtra("size")+" sq. ft");
        priceShare.setText(getIntent().getStringExtra("price") + " Tk");
        postType.setText(getIntent().getStringExtra("type2"));

    }

    private void showContactDialog() {


        BottomSheetDialog dialog = new BottomSheetDialog(PostDetailsActivity.this);
        final View v = View.inflate(PostDetailsActivity.this,R.layout.fragment_contact_bottom_sheet,null);


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                phoneText = (TextView) v.findViewById(R.id.bottomSheetCallMainText);
                messageText = (TextView) v.findViewById(R.id.bottomSheetMessageMainText);
                emailText = (TextView) v.findViewById(R.id.bottomSheetEmailMainText);

                call = (RelativeLayout) v.findViewById(R.id.postDetailsCall);
                message = (RelativeLayout) v.findViewById(R.id.postDetailsMesage);
                email = (RelativeLayout) v.findViewById(R.id.postDetailsEmail);
                getDirection = (RelativeLayout) v.findViewById(R.id.postDetailsGetDirection);



                phoneText.setText(phone);
                messageText.setText(phone);
                emailText.setText(emailAddress);

                call.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onClick(View view) {

                        int permissionCheck = ContextCompat.checkSelfPermission(PostDetailsActivity.this,
                                Manifest.permission.CALL_PHONE);

                        if (permissionCheck== PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent(Intent.ACTION_CALL);

                            intent.setData(Uri.parse("tel:" + phone));
                            startActivity(intent);
                        }else{
                            getCallPermission();
                        }

                    }
                });

                message.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onClick(View view) {
//                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
//                smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
//                smsIntent.setType("vnd.android-dir/mms-sms");
//                smsIntent.putExtra("address",phone );
//                //smsIntent.putExtra("sms_body","Body of Message");
//                startActivity(smsIntent);
                        int permissionCheck = ContextCompat.checkSelfPermission(PostDetailsActivity.this,
                                Manifest.permission.SEND_SMS);

                        if (permissionCheck==PackageManager.PERMISSION_GRANTED) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null)));
                        }else {
                            getMessagePermission();
                        }
                    }
                });

                email.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onClick(View view) {
//                Intent i = new Intent(Intent.ACTION_SEND);
//                i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ emailAddress });
//                i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Client from Bharatia.");
//                startActivity(Intent.createChooser(i, "Send email"));
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse("mailto: "+emailAddress));
                        startActivity(Intent.createChooser(emailIntent, "Choose email sender"));
                    }
                });



                getDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String lat = getIntent().getStringExtra("lat");
                        String lon = getIntent().getStringExtra("lon");

                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("https://www.google.com/maps/dir/?api=1&destination="+lat+","+lon));
                        startActivity(intent);

                    }
                });

            }
        });

        dialog.setContentView(v);
        dialog.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==MENU_EDIT) {

            if (getIntent().getStringExtra("photoLink") != null) {
                Glide
                        .with(this)
                        .load(getIntent().getStringExtra("photoLink"))
                        .into(postImgSearch);
            }

            Post post = new Post();
            post.setPostID(getIntent().getStringExtra("postId"));
            post.setCoverPhoto(getIntent().getStringExtra("photoLink"));
            post.setArea(getIntent().getStringExtra("area"));
            post.setPrice(getIntent().getStringExtra("price"));
            post.setRoomNo(getIntent().getStringExtra("room"));
            post.setSize(getIntent().getStringExtra("size"));
            post.setAddress(getIntent().getStringExtra("address"));
            post.setDescription(getIntent().getStringExtra("description"));
            post.setPhoneNo(getIntent().getStringExtra("phone"));
            post.setEmail(getIntent().getStringExtra("email"));
            post.setLat(getIntent().getStringExtra("lat"));
            post.setLon(getIntent().getStringExtra("lon"));
            post.setType1(getIntent().getStringExtra("type1"));
            post.setType2(getIntent().getStringExtra("type2"));
            Gson gson = new Gson();

            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.editRequested),true);
            editor.putString(getString(R.string.editRequestPost),gson.toJson(post));
            editor.apply();
            finish();

//            Intent i = new Intent(PostDetailsActivity.this,PostFeedActivity.class);
//            i.putExtra("callingActivity","PostDetails");
//            i.putExtra("postId",getIntent().getStringExtra("postId"));
//            i.putExtra("photoLink",getIntent().getStringExtra("photoLink"));
//            i.putExtra("area",getIntent().getStringExtra("area"));
//            i.putExtra("price",getIntent().getStringExtra("price"));
//            i.putExtra("room",getIntent().getStringExtra("room"));
//            i.putExtra("size",getIntent().getStringExtra("size"));
//            i.putExtra("address",getIntent().getStringExtra("address"));
//            i.putExtra("description",getIntent().getStringExtra("description"));
//            i.putExtra("phone",getIntent().getStringExtra("phone"));
//            i.putExtra("email",getIntent().getStringExtra("email"));
//            i.putExtra("lat",getIntent().getStringExtra("lat"));
//            i.putExtra("lon",getIntent().getStringExtra("lon"));
//            i.putExtra("type1",getIntent().getStringExtra("type1"));
//            i.putExtra("type2",getIntent().getStringExtra("type2"));
//
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//            startActivity(i);
//            finish();

        }else if(item.getItemId()==MENU_DELETE) {

            deletePost(getIntent().getStringExtra("postId"));

        }
//        else if(item.getItemId()==R.id.bookmark) {
//
//        }
        else if(item.getItemId()==android.R.id.home) {
            this.finish();
        }else {

        }
        return false;
    }


    private void shareImage(View view) {

        Uri imgUri = getBitmapUri(takeScreenShot(view));
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM,imgUri);
        //shareIntent.putExtra(Intent.EXTRA_TEXT,"Check out this awsome propert I found at Bharatia app.");
        //shareLayout.setVisibility(View.GONE);
        if(pDialog!=null) {

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,-850,0,0);
            shareLayout.setLayoutParams(params);

            pDialog.dismiss();
            pDialog=null;

        }


        startActivity(Intent.createChooser(shareIntent,"Share Image using"));
    }

    private Uri getBitmapUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"Post from Bharatia app","Post from Bharatia app");
        if(path==null)
            return null;
        else
            return Uri.parse(path);
    }

    private Bitmap takeScreenShot(View view) {
//        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        view.draw(canvas);
//        return bitmap;

        View v= view; //view.getRootView();
        v.setDrawingCacheEnabled(true);
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

        v.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false); // clear drawing cache
        return b;

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.post_details_menu,menu);

        this.menu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        try {
            String postId = getIntent().getStringExtra("postId");
            SharedPreferences rentPref = getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
            String savedResponse = rentPref.getString(getString(R.string.savedPost), "[]");


            //Checking if own post then allow edit
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
            String userPost = sharedPref.getString(getString(R.string.userPost), "[]");
            Post[] userPosts = gson.fromJson(userPost, Post[].class);
            for (Post userPost1 : userPosts) {
                //Log.e("testing own post", "entered");
                if (userPost1.getPostID().equals(postId)) {
                    //Log.e("testing own post", "found");
                    menu.add(1, MENU_EDIT, Menu.NONE, "edit").setIcon(R.drawable.ic_lead_pencil_grey600_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                    menu.add(1, MENU_DELETE, Menu.NONE, "delete").setIcon(R.drawable.ic_delete_grey600_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                    break;
                }
            }


            //Checking if post saved then change icon to saved
            //Log.e("saved res",savedResponse);
            //Log.e("postID",postId);
            Post[] posts = gson.fromJson(savedResponse, Post[].class);

            ArrayList<Post> postArrayList = new ArrayList<>(Arrays.asList(posts));
            for (int i = 0; i < posts.length; i++) {
                if (postArrayList.get(i).getPostID().equals(postId)) {
                    //Log.e("found","found match");
                    bookmarked = true;
                    break;
                    //Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_favorite_white_24dp);
                    //menu.findItem(R.id.bookmark).setIcon(drawable);
                }
            }
        }catch (Exception e) {

        }

        return true;
    }

    public void bookmarkPost(final String userId, String postId) {

        String curatedUrl = bookmarkUrl+"?UserID="+userId+"&PostID="+postId;
        Log.e("response",curatedUrl);
        // Request a string response from the provided URL.
        final StringRequest bookmarkRequest = new StringRequest(Request.Method.GET, curatedUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        //Log.e("bookmark response",response);
                        // Display the first 500 characters of the response string.
                        if(response.contains("Success")) {
                            //startActivity(new Intent(getContext(),PostFeedActivity.class));
                            //getActivity().finish();
                            //Toast.makeText(PostDetailsActivity.this,"Saved the post",Toast.LENGTH_SHORT).show();
                            saveBookmark(userId);

                        }
                        else {
                            //Toast.makeText(PostDetailsActivity.this,"Could not save bookmark",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                //Log.e("response",error.toString());
                Toast.makeText(PostDetailsActivity.this,"Check your internet connection",Toast.LENGTH_SHORT).show();

            }
        });

        // Add the request to the RequestQueue.
        queue.add(bookmarkRequest);

    }

    private void saveBookmark(String userId) {
        final Gson gson = new Gson();
        //Post post = gson.fromJson()

        // Request a string response from the provided URL.
        final StringRequest savedPostRequest = new StringRequest(Request.Method.POST, "http://sadmanamin.com/android_connect/searchBookmark.php?UserID="+userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Log.e("postDetails saved",response);

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

    public void deletePost(String postId) {

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Processing your request");
        pDialog.show();

        String curatedUrl = deleteUrl+postId;

        Log.e("response",curatedUrl);
        // Request a string response from the provided URL.
        final StringRequest bookmarkRequest = new StringRequest(Request.Method.GET, curatedUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {




                        //Log.e("bookmark response",response);
                        // Display the first 500 characters of the response string.
                        if(response.contains("Deleted Successfully")) {
                            //startActivity(new Intent(getContext(),PostFeedActivity.class));
                            //getActivity().finish();
                            //Toast.makeText(PostDetailsActivity.this,"Saved the post",Toast.LENGTH_SHORT).show();

                            if (pDialog.isShowing())
                                pDialog.dismiss();

                            startActivity(new Intent(PostDetailsActivity.this,PostFeedActivity.class));
                            finish();

                        }
                        else {
                            //Toast.makeText(PostDetailsActivity.this,"Could not save bookmark",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                //Log.e("response",error.toString());
                Toast.makeText(PostDetailsActivity.this,"Check your internet connection",Toast.LENGTH_SHORT).show();

            }
        });

        // Add the request to the RequestQueue.
        queue.add(bookmarkRequest);

    }

    public void getPostPhotoes(String postId) {

        String curatedUrl = "http://sadmanamin.com/android_connect/getPostPhotos.php?PostID="+postId;

        Log.e("response",curatedUrl);
        // Request a string response from the provided URL.
        final StringRequest bookmarkRequest = new StringRequest(Request.Method.GET, curatedUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Gson gson = new Gson();
                        PostPictureData[] postPictureData = gson.fromJson(response,PostPictureData[].class);

                        String cover = photoLinksList.get(0);
                        photoLinksList.clear();

                        photoLinksList.add(cover);
                        for(int i=0;i<postPictureData.length;i++)
                            photoLinksList.add(postPictureData[i].getLink());

                        sliderInit(viewPager,circleIndicator,photoLinksList);
//                        Slider.init(new PicassoImageLoadingService(PostDetailsActivity.this),PostDetailsActivity.this);
//                        AdSliderAdapter adSliderAdapter = new AdSliderAdapter(photoLinksList);
//                        postSlider.setAdapter(adSliderAdapter);
//                        postSlider.setLoopSlides(true);
//                        postSlider.setInterval(3000);

                        //Log.e("bookmark response",response);
                        // Display the first 500 characters of the response string.

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                //Log.e("response",error.toString());
                //Toast.makeText(PostDetailsActivity.this,"Unexpected error happened while deleting post",Toast.LENGTH_SHORT).show();

            }
        });

        // Add the request to the RequestQueue.
        queue.add(bookmarkRequest);

    }

    public void viewCount(String userId) {

        String curatedUrl = viewUrl+userId;

        Log.e("response",curatedUrl);
        // Request a string response from the provided URL.
        final StringRequest bookmarkRequest = new StringRequest(Request.Method.GET, curatedUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {




                        //Log.e("bookmark response",response);
                        // Display the first 500 characters of the response string.
                        if(response.contains("Deleted Successfully")) {
                            //startActivity(new Intent(getContext(),PostFeedActivity.class));
                            //getActivity().finish();
                            //Toast.makeText(PostDetailsActivity.this,"Saved the post",Toast.LENGTH_SHORT).show();



                        }
                        else {
                            //Toast.makeText(PostDetailsActivity.this,"Could not save bookmark",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                //Log.e("response",error.toString());
                //Toast.makeText(PostDetailsActivity.this,"Unexpected error happened while deleting post",Toast.LENGTH_SHORT).show();

            }
        });

        // Add the request to the RequestQueue.
        queue.add(bookmarkRequest);

    }

    private void getCallPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(PostDetailsActivity.this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(PostDetailsActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }
    }
    private void getMessagePermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(PostDetailsActivity.this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(PostDetailsActivity.this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_MESSAGE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }
    }


    private void getReadStroagePermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }
    }

    private void getWriteStroagePermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent intent = new Intent(Intent.ACTION_CALL);

                    intent.setData(Uri.parse("tel:" + phone));
                    startActivity(intent);


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_MESSAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null)));

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null)));

                    int permissionCheck = ContextCompat.checkSelfPermission(PostDetailsActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if (permissionCheck!=PackageManager.PERMISSION_GRANTED) {
                        getWriteStroagePermission();
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null)));

                    shareLayout.setVisibility(View.VISIBLE);
                    shareImage(shareLayout);
                    pDialog = new ProgressDialog(this);
                    pDialog.setMessage("Creating post...");
                    pDialog.show();
                    //print();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(pDialog!=null) {
            pDialog.dismiss();
            pDialog.cancel();
            pDialog = null;

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,-850,0,0);
            shareLayout.setLayoutParams(params);
        }
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        if(pDialog!=null) {
//            shareLayout.setVisibility(View.GONE);
//            pDialog.dismiss();
//            pDialog.cancel();
//            pDialog = null;
//        }
//    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        queue.cancelAll(new RequestQueue.RequestFilter() {
//            @Override
//            public boolean apply(Request<?> request) {
//                // do I have to cancel this?
//                return true; // -> always yes
//            }
//        });
//    }

    @Override
    protected void attachBaseContext(Context newBase) {
        String lang_code = ChangeLanguage.getLang(newBase); //load it from SharedPref
        //SharedPreferences sharedPref = newBase.getSharedPreferences(getString(R.string.pref_file_key),Context.MODE_PRIVATE);
        //lang_code = sharedPref.getString(getString(R.string.locale), "en");
        Context context = ChangeLanguage.changeLang(newBase, lang_code);
        super.attachBaseContext(context);
    }

    private void sliderInit(final ViewPager viewPager, CircleIndicator circleIndicator, final ArrayList<String> arrayList) {

        //mPager = (ViewPager) findViewById(R.id.pager);
        final SliderAdapter sliderAdapter = new SliderAdapter(this,arrayList);
        viewPager.setAdapter(sliderAdapter);
        //CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        circleIndicator.setViewPager(viewPager);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (sliderAdapter.getCurrentPage() == arrayList.size()) {
                    sliderAdapter.setCurrentPage(0);
                }
                viewPager.setCurrentItem(sliderAdapter.nextCurrentPage(), true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2500, 2500);
    }

    public int convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int)px;
    }
}


