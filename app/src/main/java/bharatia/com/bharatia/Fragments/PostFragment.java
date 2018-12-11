package bharatia.com.bharatia.Fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.media.ExifInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.LruCache;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;
import com.xw.repo.BubbleSeekBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bharatia.com.bharatia.ChoosePhotoAppDialogActivity;
import bharatia.com.bharatia.DataModel.Post;
import bharatia.com.bharatia.PostDoneActivity;
import bharatia.com.bharatia.PostFeedActivity;
import bharatia.com.bharatia.R;
import bharatia.com.bharatia.SignUpActivity;
import bharatia.com.bharatia.Utils.AppHelper;
import bharatia.com.bharatia.Utils.VolleyMultipartRequest;
import bharatia.com.bharatia.Utils.VolleySingleton;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {

    private static final int IMAGE1_CODE = 100;
    private static final int IMAGE2_CODE = 101;
    private static final int IMAGE3_CODE = 102;
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 23;
    private final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 24;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 25;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 32;

    private static final int GALLERY_PICTURE = 10;
    private static final int CAMERA_REQUEST = 11;


    Button upload;

    RequestQueue queue;

    ProgressDialog pDialog;

    String postType = "flat";
    String saleOrRent = "Rent";
    String rooms = "0";
    double lat, lon;

    TextView selectedRoomsText;

    Uri coverImage;

    File imgFile;
    File imgFile1,imgFile2,imgFile3;

    private final int IMAGE_CODE = 0;
    private final int REQUEST_SUCCESS = 30;
    private final String uploadUrl = "http://sadmanamin.com/android_connect/upload.php";
    private final String postUrl = "http://sadmanamin.com/android_connect/give_post.php";
    private final String editUrl = "http://sadmanamin.com/android_connect/updatePost.php";
    private EditText room, size, price, area, address, desc, phn, email;

    FrameLayout hotel, sublet, flat;
    FrameLayout rent, sale;

    ImageButton hotelBtn, subletBtn, flatBtn;
    ImageButton rentBtn,saleBtn;

    Button roomAny, room1, room2, room3, room4, room5;
    TextView locate;
    Button postFinish;
    TextView coverPicName;
    TextView postPageHeading;

    LinearLayout roomsLayout;

    //LinearLayout layoutDone;
    ScrollView layoutMain;

    private FusedLocationProviderClient fusedLocationProviderClient;
    final int PLACE_PICKER_REQUEST = 1;
    Switch available;
    private boolean mLocationPermissionGranted=false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;

    private int currentLayoutSection=1;
    Button postPrev,postNext;
    LinearLayout section1,section2,section3,section4;

    Boolean editRequested = false;
    private String editPostId = "-1";
    private String photoLink;

    BubbleSeekBar seekBar;

    Context context;


    private String path,path1,path2,path3;
    private Bitmap bitmap;

    private String cameraFileName="";
    String postPicDownloadLink = "";


    CardView img1Card,img2Card,img3Card;
    ImageView img1,img2,img3;
    private String coverPhotoLink;

    ArrayList<AppCompatCheckBox> facillitiesList = new ArrayList<>();

    String facillitiesText = "";

    LinearLayout facillitiesLayout;

    EditText addFacillities;

    Button addFacillitiesButton;

    public PostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        queue = Volley.newRequestQueue(context);

        upload = (Button) view.findViewById(R.id.postContinueBtn);

        img1Card = view.findViewById(R.id.postImg1Card);
        img2Card = view.findViewById(R.id.postImg2Card);
        img3Card = view.findViewById(R.id.postImg3Card);

        img1 = view.findViewById(R.id.postImg1);
        img2 = view.findViewById(R.id.postImg2);
        img3 = view.findViewById(R.id.postImg3);
        //postPageHeading = (TextView) view.findViewById(R.id.postHeading);

        //room = (EditText) view.findViewById(R.id.postRoom);
        size = (EditText) view.findViewById(R.id.postSize);
        price = (EditText) view.findViewById(R.id.postPrice);
        area = (EditText) view.findViewById(R.id.postArea);
        address = (EditText) view.findViewById(R.id.postAddress);
        desc = (EditText) view.findViewById(R.id.postDesc);
        phn = (EditText) view.findViewById(R.id.postPhn);
        email = (EditText) view.findViewById(R.id.postEmail);

        hotel = (FrameLayout) view.findViewById(R.id.postHotel);
        sublet = (FrameLayout) view.findViewById(R.id.postSublet);
        flat = (FrameLayout) view.findViewById(R.id.postFlat);

        hotelBtn = (ImageButton) view.findViewById(R.id.postHotelButton);
        subletBtn = (ImageButton) view.findViewById(R.id.postSubletButton);
        flatBtn = (ImageButton) view.findViewById(R.id.postFlatButton);

        rent = (FrameLayout) view.findViewById(R.id.postRent);
        sale = (FrameLayout) view.findViewById(R.id.postSale);

        rentBtn = (ImageButton) view.findViewById(R.id.postRentButton);
        saleBtn = (ImageButton) view.findViewById(R.id.postSaleButton);



        seekBar = (BubbleSeekBar) view.findViewById(R.id.postRoomSeekbar);

        selectedRoomsText = (TextView) view.findViewById(R.id.postRoomsSelectedText);

//        roomAny = (Button) view.findViewById(R.id.postRoomAny);
//        room1 = (Button) view.findViewById(R.id.postRoom1);
//        room2 = (Button) view.findViewById(R.id.postRoom2);
//        room3 = (Button) view.findViewById(R.id.postRoom3);
//        room4 = (Button) view.findViewById(R.id.postRoom4);
//        room5 = (Button) view.findViewById(R.id.postRoom5);

        locate = (TextView) view.findViewById(R.id.postGoogleMapLocation);
        //post = (Button) view.findViewById(R.id.postFinish);

        roomsLayout = (LinearLayout) view.findViewById(R.id.postRoomButtonLayout);

        layoutMain = (ScrollView) view.findViewById(R.id.layoutMain);
        //layoutDone = (LinearLayout) view.findViewById(R.id.layoutDone);

        ///room = (EditText) view.findViewById(R.id.postRooms);
        coverPicName = (TextView) view.findViewById(R.id.postCoverName);

        //postPrev = (Button) view.findViewById(R.id.postPrev);
        postNext = (Button) view.findViewById(R.id.postNext);
        section1 = (LinearLayout) view.findViewById(R.id.postSection1);
        section2 = (LinearLayout) view.findViewById(R.id.postSection2);
        section3 = (LinearLayout) view.findViewById(R.id.postSection3);
        section4 = (LinearLayout) view.findViewById(R.id.postSection4);


        facillitiesLayout = view.findViewById(R.id.postFacillitiesLayout);

        addFacillities = view.findViewById(R.id.postAddFacillitiesEditText);
        addFacillitiesButton = view.findViewById(R.id.postAddFacillitiesButton);

        postFinish = (Button) view.findViewById(R.id.postFinish);


        //Getting the user last known location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        //mMap = new GoogleMap();
        getCurrentLocation();


        //HACK FOR API>24 SO THAT CAMERA DOES NOT BREAK AND GIVES Exception android.os.FileUriExposedException: file:///storage/emulated/0/IMAGE_1522677906952_bharatia.jpg exposed beyond app through ClipData.Item.getUri()
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        addFacillitiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addFacillities.getText().toString().equals(""))
                    Toast.makeText(context,"Please give the facility a name",Toast.LENGTH_SHORT).show();
                else{
                    addFacillity(addFacillities.getText().toString());
                }

            }
        });


        img1Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(context, ChoosePhotoAppDialogActivity.class), IMAGE1_CODE);
            }
        });
        img2Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(context, ChoosePhotoAppDialogActivity.class), IMAGE2_CODE);
            }
        });
        img3Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(context, ChoosePhotoAppDialogActivity.class), IMAGE3_CODE);
            }
        });


        //Default selected buttons
        //flat.setBackground(getResources().getDrawable(R.drawable.button_dark_style));
        sale.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.colorTransparentWhite)));
        sublet.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.colorTransparentWhite)));
        hotel.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.colorTransparentWhite)));


        //BuyOrSell
        saleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,"sale",Toast.LENGTH_SHORT).show();
                saleOrRent = "Sale";
                sale.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.fui_transparent)));
                rent.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.colorTransparentWhite)));

            }
        });
        rentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,"rent",Toast.LENGTH_SHORT).show();
                saleOrRent = "Rent";
                sale.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.colorTransparentWhite)));
                rent.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.fui_transparent)));

            }
        });

        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(context,"txt view",Toast.LENGTH_SHORT).show();
                //try {

                // Assume thisActivity is the current activity
                int permissionCheck = ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION);

                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {


                    //Toast.makeText(context,"txt view",Toast.LENGTH_SHORT).show();

                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                    try {
                        startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                        //Toast.makeText(context,"try view",Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("err", e.toString());
                        //Toast.makeText(context,"catch view",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Toast.makeText(context,"else view",Toast.LENGTH_SHORT).show();
                    getLocationPermission();
                }
            }
        });

        //room1.setBackground(getResources().getDrawable(R.drawable.square_button_dark_style));
        //room.setText("1");

//        seekBar.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
//            @NonNull
//            @Override
//            public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
//                array.clear();
//                array.put(0, "0");
//                array.put(1, "1");
//                array.put(2, "2");
//                array.put(3, "3");
//                array.put(4, "4");
//                array.put(5, "5");
//                array.put(6, "6");
//                array.put(7, "7");
//
//                return array;
//            }
//        });

        seekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                rooms = progress+"";
                selectedRoomsText.setText("Selected: "+rooms);
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }
        });

        try {

            //Check and do things for edit
            if (context != null) {
                SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
                editRequested = sharedPref.getBoolean(getString(R.string.editRequested), false);
                if (editRequested) {
                    //check if came from postDetails meaning edit

                    SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(getString(R.string.editRequested), false);
                    editor.apply();


                    Gson gson = new Gson();
                    String editPost = sharedPref.getString(getString(R.string.editRequestPost), "[]");
                    //Log.e("edit post", editPost);

                    Post post = gson.fromJson(editPost, Post.class);

                    editPostId = post.getPostID();
                    photoLink = post.getCoverPhoto();
                    //Bundle bundle = this.getArguments();

                    String type1 = post.getType1();//bundle.getString("type1");
                    if (type1.equals("Sale")) {

                        saleOrRent = "Sale";
                        sale.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.fui_transparent)));
                        rent.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.colorTransparentWhite)));

                    }

                    String type2 = post.getType2();//bundle.getString("type2");
                    if (type2.equals("flat")) {
                        postType = "flat";
                        hotel.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.colorTransparentWhite)));
                        sublet.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.colorTransparentWhite)));
                        flat.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.fui_transparent)));

                    } else if (type2.equals("sublet")) {
                        postType = "sublet";
                        hotel.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.colorTransparentWhite)));
                        sublet.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.fui_transparent)));
                        flat.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.colorTransparentWhite)));


                    } else {
                        postType = "hotel";
                        hotel.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.fui_transparent)));
                        sublet.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.colorTransparentWhite)));
                        flat.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.colorTransparentWhite)));
                    }


                    String room = post.getRoomNo();//bundle.getString("room");
                    rooms = room;
                    seekBar.setProgress(Float.parseFloat(room));

                    size.setText(post.getSize());
                    price.setText(post.getPrice());
                    area.setText(post.getArea());
                    address.setText(post.getAddress());
                    //TODO exclude facillities

                    String[] descText = post.getDescription().split("999");
                    desc.setText(descText[0]);
                    phn.setText(post.getPhoneNo());
                    email.setText(post.getEmail());

                    if (descText.length>1) {
                        facillitiesLayout.removeAllViews();

                        String[] facillities = descText[1].split("00");

                        for (int i = 0; i < facillities.length; i++) {
                            addFacillity(facillities[i]);
                        }
                    }

//            i.putExtra("postId",getIntent().getStringExtra("postId"));
//            i.putExtra("photoLink",getIntent().getStringExtra("photoLink"));
//            i.putExtra("area",getIntent().getStringExtra(""));
//            i.putExtra("price",getIntent().getStringExtra("price"));
//            i.putExtra("room",getIntent().getStringExtra(""));
//            i.putExtra("size",getIntent().getStringExtra("size"));
//            i.putExtra("address",getIntent().getStringExtra("address"));
//            i.putExtra("description",getIntent().getStringExtra("description"));
//            i.putExtra("phone",getIntent().getStringExtra("phone"));
//            i.putExtra("email",getIntent().getStringExtra("email"));
//            i.putExtra("lat",getIntent().getStringExtra("lat"));
//            i.putExtra("lon",getIntent().getStringExtra("lon"));
                }
            }


            postNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    layoutMain.post(new Runnable() {
                        @Override
                        public void run() {
                            layoutMain.fullScroll(View.FOCUS_UP);
                        }
                    });
                    if (currentLayoutSection < 1) {
                        currentLayoutSection++;
                        postNext.setVisibility(View.VISIBLE);
                        postPrev.setVisibility(View.GONE);
                        section1.setVisibility(View.VISIBLE);
                        section2.setVisibility(View.GONE);
                        section3.setVisibility(View.GONE);
                        section4.setVisibility(View.GONE);
                        postFinish.setVisibility(View.INVISIBLE);
                    } else if (currentLayoutSection == 1) {
                        currentLayoutSection++;
                        postNext.setVisibility(View.VISIBLE);
                        //postPrev.setVisibility(View.VISIBLE);
                        section1.setVisibility(View.GONE);
                        section2.setVisibility(View.VISIBLE);
                        section3.setVisibility(View.GONE);
                        section4.setVisibility(View.GONE);
                        postFinish.setVisibility(View.INVISIBLE);
                    } else if (currentLayoutSection == 2) {
                        if (size.getText().toString().isEmpty() || price.getText().toString().isEmpty()) {
                            Toast.makeText(context, "Please fill out all the information", Toast.LENGTH_SHORT).show();
                        } else {
                            currentLayoutSection++;
                            postNext.setVisibility(View.VISIBLE);
                            //postPrev.setVisibility(View.VISIBLE);
                            section1.setVisibility(View.GONE);
                            section2.setVisibility(View.GONE);
                            section3.setVisibility(View.VISIBLE);
                            section4.setVisibility(View.GONE);
                            postFinish.setVisibility(View.INVISIBLE);
                        }
                    } else if (currentLayoutSection == 3) {
                        if (area.getText().toString().isEmpty() || address.getText().toString().isEmpty()) {
                            Toast.makeText(context, "Please fill out all the information", Toast.LENGTH_SHORT).show();
                        } else {
                            currentLayoutSection++;
                            //postPrev.setVisibility(View.VISIBLE);
                            postNext.setVisibility(View.GONE);
                            section1.setVisibility(View.GONE);
                            section2.setVisibility(View.GONE);
                            section3.setVisibility(View.GONE);
                            section4.setVisibility(View.VISIBLE);
                            postFinish.setVisibility(View.VISIBLE);
                        }
                    }

                }
            });

//        postPrev.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                layoutMain.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        layoutMain.fullScroll(View.FOCUS_UP);
//                    }
//                });
//
//                if(currentLayoutSection<1) {
//                    currentLayoutSection--;
//                    postNext.setVisibility(View.VISIBLE);
//                    postPrev.setVisibility(View.GONE);
//                    section1.setVisibility(View.VISIBLE);
//                    section2.setVisibility(View.GONE);
//                    section3.setVisibility(View.GONE);
//                    section4.setVisibility(View.GONE);
//                    postFinish.setVisibility(View.INVISIBLE);
//                }
//                else if(currentLayoutSection==2) {
//                    currentLayoutSection--;
//                    postNext.setVisibility(View.VISIBLE);
//                    postPrev.setVisibility(View.GONE);
//                    section1.setVisibility(View.VISIBLE);
//                    section2.setVisibility(View.GONE);
//                    section3.setVisibility(View.GONE);
//                    section4.setVisibility(View.GONE);
//                    postFinish.setVisibility(View.INVISIBLE);
//                }
//                else if(currentLayoutSection==3) {
//                    currentLayoutSection--;
//                    postNext.setVisibility(View.VISIBLE);
//                    postPrev.setVisibility(View.VISIBLE);
//                    section1.setVisibility(View.GONE);
//                    section2.setVisibility(View.VISIBLE);
//                    section3.setVisibility(View.GONE);
//                    section4.setVisibility(View.GONE);
//                    postFinish.setVisibility(View.INVISIBLE);
//                }
//                else if(currentLayoutSection==4) {
//                    currentLayoutSection--;
//                    postPrev.setVisibility(View.VISIBLE);
//                    postNext.setVisibility(View.VISIBLE);
//                    section1.setVisibility(View.GONE);
//                    section2.setVisibility(View.GONE);
//                    section3.setVisibility(View.VISIBLE);
//                    section4.setVisibility(View.GONE);
//                    postFinish.setVisibility(View.INVISIBLE);
//                }
//            }
//        });

//        // Construct a GeoDataClient.
//        mGeoDataClient = Places.getGeoDataClient(this, null);
//
//        // Construct a PlaceDetectionClient.
//        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

            // Construct a FusedLocationProviderClient.
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);



            //postType
            hotelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    postType = "hotel";
                    hotel.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.fui_transparent)));
                    sublet.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.colorTransparentWhite)));
                    flat.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.colorTransparentWhite)));

                }
            });
            subletBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    postType = "sublet";
                    hotel.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.colorTransparentWhite)));
                    sublet.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.fui_transparent)));
                    flat.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.colorTransparentWhite)));

                }
            });
            flatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    postType = "flat";
                    hotel.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.colorTransparentWhite)));
                    sublet.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.colorTransparentWhite)));
                    flat.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.fui_transparent)));

                }
            });

            //available = (Switch) view.findViewById(R.id.postAvailable);

            //Upload
            upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivityForResult(new Intent(context, ChoosePhotoAppDialogActivity.class), IMAGE_CODE);

                    //startDialog();
                }
            });

            postFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    Log.e("size", size.getText().toString());
//                    Log.e("price", price.getText().toString());
//                    Log.e("area", area.getText().toString());
//                    Log.e("addess", address.getText().toString());
//                    Log.e("desc", desc.getText().toString());
//                    Log.e("phn", phn.getText().toString());
//                    Log.e("email", phn.getText().toString());
//
//                    Log.e("room", rooms);
//                    Log.e("s/r", saleOrRent);
//                    Log.e("pType", postType);
//                Log.e("pType",coverImage.getLastPathSegment());

                    final int childCount = facillitiesLayout.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        AppCompatCheckBox AppCompatCheckBox = (AppCompatCheckBox) facillitiesLayout.getChildAt(i);
                        if (AppCompatCheckBox.isChecked()) {

                            if (facillitiesList.size()!=0) {
                                facillitiesText += "00" + AppCompatCheckBox.getText().toString();
                            }else {
                                facillitiesText += AppCompatCheckBox.getText().toString();
                            }
                            facillitiesList.add(AppCompatCheckBox);
                        }
                    }

                    pDialog = new ProgressDialog(context);

                    //editRequested...do edit
                    if (editRequested) {
                        if (imgFile==null) {
//
//                            try {
//                                if(//picChoice==1)
//                                    bitmap = new Compressor(context).compressToBitmap(new File(path));
//                                else
//                                    bitmap = new Compressor(context).compressToBitmap(cameraFile);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                if(pDialog.isShowing())
//                                    pDialog.dismiss();
//                                Log.e("err",e.toString());
//                            }


                            Log.e("facilities",desc.getText().toString()+"999"+facillitiesText);

                            editPost(editPostId, rooms, size.getText().toString(), price.getText().toString(), "" + lat, "" + lon, area.getText().toString()
                                    , address.getText().toString(), desc.getText().toString()+"999"+facillitiesText, "",
                                    phn.getText().toString(), email.getText().toString(), saleOrRent, postType);

                        } else { //EDIT IMAGE NOT NULL,UPLOAD IMG

                            //selected new pic update with pic
                            pDialog.setMessage("Uploading Picture...");
                            pDialog.setCancelable(false);
                            pDialog.show();

                            //imageview.setImageURI(selectedImage);

                            Drawable drawable;

                            try {
//                                InputStream inputStream = context.getContentResolver().openInputStream(coverImage);
//                                drawable = Drawable.createFromStream(inputStream, coverImage.toString() );
                                drawable = Drawable.createFromPath(imgFile.getPath());
                                uploadWIthImage(drawable, path);
                            } catch (Exception e) {
                                //drawable = getResources().getDrawable(R.drawable.default_image);
                                if(pDialog.isShowing())
                                    pDialog.dismiss();
                                Toast.makeText(context, "Error: "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }

//                            try {
//                                if(//picChoice==1)
//                                    bitmap = new Compressor(context).compressToBitmap(new File(path));
//                                else
//                                    bitmap = new Compressor(context).compressToBitmap(cameraFile);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                if(pDialog.isShowing())
//                                    pDialog.dismiss();
//                                Log.e("err",e.toString());
//                            }
//
//                            try {
//                                //InputStream inputStream = getContentResolver().openInputStream(uri);
//                                //Drawable d = Drawable.createFromStream(inputStream, uri.toString() );
//                                Drawable d = new BitmapDrawable(context.getResources(), bitmap);
//                                if(//picChoice==1)
//                                    uploadWIthImage(d, path);
//                                else
//                                    uploadWIthImage(d, cameraFileName);
//                            } catch (Exception e) {
//                                if(pDialog.isShowing())
//                                    pDialog.dismiss();
//                                //Log.e("err while upload", e.toString());
//                                //signUpRequest(fName.getText().toString(), lName.getText().toString(), username.getText().toString(), email.getText().toString(), phoneNum.getText().toString(), pw.getText().toString(), userPicDownloadLink);
//
//                            }


                            //uploadWIthImage(getDrawable(coverImage), coverImage.getLastPathSegment());
                        }

                    } else {
                        //do normal post
                        if (rooms != null && saleOrRent != null && postType != null && !size.getText().toString().equals("") && !price.getText().toString().equals("")
                                && !area.getText().toString().equals("") && !address.getText().toString().equals("") && !desc.getText().toString().equals("")
                                && !phn.getText().toString().equals("") && !email.getText().toString().equals("")) {

                            if (imgFile == null) {
                                Toast.makeText(context, "Please select a suitable cover photo", Toast.LENGTH_SHORT).show();
                            } else {
                                pDialog = new ProgressDialog(context);
                                pDialog.setMessage("Uploading Picture...");
                                pDialog.setCancelable(false);
                                pDialog.show();

                                Drawable drawable;

                                try {
//                                    InputStream inputStream = context.getContentResolver().openInputStream(coverImage);
//                                    drawable = Drawable.createFromStream(inputStream, coverImage.toString() );
                                    drawable = Drawable.createFromPath(imgFile.getPath());
                                    uploadWIthImage(drawable, path);
                                } catch (Exception e) {
                                    //drawable = getResources().getDrawable(R.drawable.default_image);
                                    if(pDialog.isShowing())
                                        pDialog.dismiss();
                                    Toast.makeText(context, "Error: "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }

//                                try {
//                                    if(//picChoice==1)
//                                        bitmap = new Compressor(context).compressToBitmap(new File(path));
//                                    else
//                                        bitmap = new Compressor(context).compressToBitmap(cameraFile);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                    if(pDialog.isShowing())
//                                        pDialog.dismiss();
//                                    Log.e("err",e.toString());
//                                }
//
//                                try {
//                                    //InputStream inputStream = getContentResolver().openInputStream(uri);
//                                    //Drawable d = Drawable.createFromStream(inputStream, uri.toString() );
//                                    Drawable d = new BitmapDrawable(getResources(), bitmap);
//                                    if(//picChoice==1)
//                                        uploadWIthImage(d, path);
//                                    else
//                                        uploadWIthImage(d, cameraFileName);
//
//                                } catch (Exception e) {
//                                    //signUpRequest(fName.getText().toString(), lName.getText().toString(), username.getText().toString(), email.getText().toString(), phoneNum.getText().toString(), pw.getText().toString(), userPicDownloadLink);
//
//                                    if(pDialog.isShowing())
//                                        pDialog.dismiss();
//                                }

                            }
                        } else {
                            Toast.makeText(context, "Please fill out all the information", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }catch (Exception e) {
            //Log.e("err",e.toString());
            if(pDialog.isShowing())
                pDialog.dismiss();
        }
        return view;
    }

    private void addFacillity(String facillity) {
        AppCompatCheckBox checkbox = new AppCompatCheckBox(context);
        checkbox.setText(facillity);
        checkbox.setTextColor(Color.parseColor("#00695c"));

        ColorStateList  colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{android.R.attr.state_checked} , // checked
                },
                new int[]{
                        Color.parseColor("#00695c"),
                        Color.parseColor("#00695c"),
                }
        );

        CompoundButtonCompat.setButtonTintList(checkbox,colorStateList);

        checkbox.setChecked(true);

        facillitiesLayout.addView(checkbox);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("err"," reqest result");
        if(requestCode==IMAGE_CODE) {
            if(resultCode == RESULT_OK){
                imgFile = (File) data.getExtras().get("file");
                //coverImage = selectedImage;


                path = data.getStringExtra("name");
                coverPicName.setText(imgFile.getPath());
                //addBitmapToMemoryCache("img",bitmap);
                //bitmap = uriToBitmap(coverImage);
            }
        }
        else if(requestCode==IMAGE1_CODE) {
            if(resultCode == RESULT_OK){
                imgFile1 = (File) data.getExtras().get("file");
                //coverImage = selectedImage;


                path1 = data.getStringExtra("name");
                Picasso.get().load(imgFile1).placeholder(R.drawable.ic_camera_black_24dp).fit().centerCrop().into(img1);
                //coverPicName.setText(imgFile.getPath());
                //addBitmapToMemoryCache("img",bitmap);
                //bitmap = uriToBitmap(coverImage);
            }
        }
        else if(requestCode==IMAGE2_CODE) {
            if(resultCode == RESULT_OK){
                imgFile2 = (File) data.getExtras().get("file");
                //coverImage = selectedImage;


                path2 = data.getStringExtra("name");
                Picasso.get().load(imgFile2).placeholder(R.drawable.ic_camera_black_24dp).fit().centerCrop().into(img2);
                //addBitmapToMemoryCache("img",bitmap);
                //bitmap = uriToBitmap(coverImage);
            }
        }
        else if(requestCode==IMAGE3_CODE) {
            if(resultCode == RESULT_OK){
                imgFile3 = (File) data.getExtras().get("file");
                //coverImage = selectedImage;


                path3 = data.getStringExtra("name");
                Picasso.get().load(imgFile3).placeholder(R.drawable.ic_camera_black_24dp).fit().centerCrop().into(img3);
                //addBitmapToMemoryCache("img",bitmap);
                //bitmap = uriToBitmap(coverImage);
            }
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == PLACE_PICKER_REQUEST) {
                try {
                    Place place = PlacePicker.getPlace(context,data);
                    String toastMsg = String.format("Place: %s", place.getName());
                    LatLng latLng = place.getLatLng();

                    lat = latLng.latitude;
                    lon = latLng.longitude;
                }catch (Exception e) {
                    Toast.makeText(context, "Failed to get location. Please try again", Toast.LENGTH_LONG).show();
                }

                //Toast.makeText(context, toastMsg+"lat "+lat+" lon "+lon, Toast.LENGTH_LONG).show();
            }else if (requestCode == CAMERA_REQUEST) {

                Log.e("err","camera reqest result");
                ////picChoice = 0;

                try {


                    File f = new File(Environment.getExternalStorageDirectory()
                            .toString());
                    for (File temp : f.listFiles()) {
                        if (temp.getName().equals(cameraFileName)) {
                            f = temp;
                            break;
                        }
                    }

                    if (!f.exists()) {

                        Toast.makeText(context,

                                "Error while capturing image", Toast.LENGTH_LONG)

                                .show();

                        return;

                    }



                    //cameraFile = f;

                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());

                    bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);

                    int rotate = 0;
                    try {
                        coverPicName.setText(f.getAbsolutePath());

                        ExifInterface exif = new ExifInterface(f.getAbsolutePath());
                        int orientation = exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL);

                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotate = 270;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotate = 180;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotate = 90;
                                break;
                        }
                    } catch (Exception e) {
                        Log.e("err",e.toString());

                        if(pDialog!=null && pDialog.isShowing())
                            pDialog.dismiss();

                        e.printStackTrace();
                        Toast.makeText(context,"Failed to get the image",Toast.LENGTH_SHORT).show();
                    }
                    Matrix matrix = new Matrix();
                    matrix.postRotate(rotate);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                            bitmap.getHeight(), matrix, true);



                    //userImage.setImageBitmap(bitmap);
                    //storeImageTosdCard(bitmap);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.e("err",e.toString());
                    e.printStackTrace();
                    if(pDialog!=null && pDialog.isShowing())
                        pDialog.dismiss();

                    Toast.makeText(context,"Failed to get the image",Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == GALLERY_PICTURE) {
                if (data != null) {

                    Log.e("err","gallery reqest result");

                    ////picChoice = 1;

                    Uri selectedImage = data.getData();
                    String[] filePath = { MediaStore.Images.Media.DATA };
                    Cursor c = getActivity().getContentResolver().query(selectedImage, filePath,
                            null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    path = c.getString(columnIndex);
                    c.close();


                    bitmap = BitmapFactory.decodeFile(path); // load
                    // preview image
                    bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);



                    coverPicName.setText(path);
                    //userImage.setImageBitmap(bitmap);

                }
            }

        }

    }

    private Bitmap uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);


            parcelFileDescriptor.close();

            return image;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error: " +e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
        return null;
    }

    private Drawable getDrawable(Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            //Log.e("test", "get drawable result");
            Drawable d = new BitmapDrawable(getResources(), bitmap);
            return d;
        } catch (FileNotFoundException e) {

            return null;
        }
    }

    private void uploadImage(final Drawable drawable, final String fileName, final int imgCOunt, final int currentCount, final String postId) {

        //Log.e("post path", fileName);

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, uploadUrl, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                Log.e("response uploadImage",resultResponse);
                //Log.e("test", "on response: "+response);
                //Log.e("test", "on response result: "+resultResponse);
                if(response.statusCode==200) {
                    Gson gson = new Gson();
                    //Post post = gson.fromJson(resultResponse, Post.class);
                    //String postId = post.getPostID();

                    postPicDownloadLink = "http://sadmanamin.com/android_connect/uploads/" + fileName;
                    addPhotoToPost(postId,postPicDownloadLink,imgCOunt,currentCount);
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(pDialog.isShowing())
                    pDialog.dismiss();

                Log.e("response uploadImage",error.toString());

                NetworkResponse networkResponse = error.networkResponse;
                //Log.e("test", "on error: "+networkResponse);
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        //Log.e("Error Status", status);
                        //Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }

                        Toast.makeText(context,errorMessage,Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        if(pDialog.isShowing())
                            pDialog.dismiss();
                        e.printStackTrace();
                    }
                }
                //Log.e("Error", errorMessage);

            }
        }) {
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("fileToUpload", new DataPart(fileName, AppHelper.getFileDataFromDrawable(context, drawable), "image/*"));
                //params.put("cover", new DataPart("file_cover.jpg", AppHelper.getFileDataFromDrawable(context, drawable), "image/*"));

                //Log.e("test", "upload param");
                return params;
            }
        };

        //Log.e("test", "queue" );
        //queue.add(multipartRequest);
        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest);
    }

    private void addPhotoToPost(String postId, String postPicDownloadLink, final int imgCount, final int currentCount) {

        String curatedUrl = "http://sadmanamin.com/android_connect/uploadPostPhotos.php?"+"PostID="+postId+"&PhotoLink="+postPicDownloadLink;

        Log.e("response addPhotoToPost",curatedUrl);

        pDialog.setMessage("Uploading Property Images...");

        //Log.e("response",curatedUrl);
        // Request a string response from the provided URL.
        final StringRequest postRequest = new StringRequest(Request.Method.POST, curatedUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("response addPhotoToPost",response);
                        if (imgCount==currentCount) {
                            if (pDialog.isShowing())
                                pDialog.dismiss();

                            Intent i = new Intent(context, PostDoneActivity.class);
                            i.putExtra("photoLink", coverPhotoLink);
                            i.putExtra("rooms", rooms);
                            i.putExtra("size", size.getText().toString());
                            i.putExtra("area", area.getText().toString());
                            i.putExtra("price", price.getText().toString());
                            i.putExtra("type2", postType);
                            startActivity(i);
                        }

                        // Display the first 500 characters of the response string.
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(pDialog.isShowing())
                    pDialog.dismiss();
                //Log.e("response",error.toString());
                Toast.makeText(context,"Unexpected error happened while posting",Toast.LENGTH_SHORT).show();

            }
        });
        queue.add(postRequest);


    }

    private void uploadWIthImage(final Drawable drawable, final String fileName) {

        //Log.e("post path", fileName);

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, uploadUrl, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                //Log.e("test", "on response: "+response);
                //Log.e("test", "on response result: "+resultResponse);
                if(resultResponse.contains("sadmanamin.com/android_connect/uploads")) {

//                    if(//picChoice==1) {
//                        String[] s = fileName.split("/");
//                        postPicDownloadLink = "http://sadmanamin.com/android_connect/uploads/" + s[s.length-1];
//                    }else {

                        postPicDownloadLink = "http://sadmanamin.com/android_connect/uploads/" + fileName;

//                        try {
//                            File file = new File(Environment.getExternalStorageDirectory().toString() + path);
//                            file.delete();
//                            if (file.exists()) {
//                                file.getCanonicalFile().delete();
//                                if (file.exists()) {
//                                    getApplicationContext().deleteFile(file.getName());
//                                }
//                            }
//                        }catch (Exception e) {
//
//                        }
                    }

                    String userId = "-1";
                    try {
                        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
                        userId = sharedPref.getString(getString(R.string.userId), "");
                    }catch (Exception e) {

                        if(pDialog.isShowing())
                            pDialog.dismiss();
                        Log.e("err",e.toString());
                    }

                Log.e("facilities",desc.getText().toString()+"999"+facillitiesText);

                    if(editRequested) {
                        editPost(editPostId, rooms, size.getText().toString(), price.getText().toString(), "" + lat, "" + lon, area.getText().toString()
                                , address.getText().toString(), desc.getText().toString()+"999"+facillitiesText, postPicDownloadLink,
                                phn.getText().toString(), email.getText().toString(), saleOrRent, postType);
                    }else {
                        givePost(userId, rooms, size.getText().toString(), price.getText().toString(), "" + lat, "" + lon, area.getText().toString()
                                , address.getText().toString(), desc.getText().toString()+"999"+facillitiesText, postPicDownloadLink,
                                phn.getText().toString(), email.getText().toString(), saleOrRent, postType);
                    }
                }
//                try {
//                    JSONObject result = new JSONObject(resultResponse);
//                    String status = result.getString("status");
//                    String message = result.getString("message");
//
//
//                    if (status.equals(REQUEST_SUCCESS)) {
//                        // tell everybody you have succed upload image and post strings
//                        Log.e("Messsage", message);
//                    } else {
//                        Log.e("Unexpected", message);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            //}
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(pDialog.isShowing())
                    pDialog.dismiss();

                NetworkResponse networkResponse = error.networkResponse;
                //Log.e("test", "on error: "+networkResponse);
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        //Log.e("Error Status", status);
                        //Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }

                        Toast.makeText(context,errorMessage,Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        if(pDialog.isShowing())
                            pDialog.dismiss();
                        e.printStackTrace();
                    }
                }
                //Log.e("Error", errorMessage);

            }
        }) {
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("fileToUpload", new DataPart(fileName, AppHelper.getFileDataFromDrawable(context, drawable), "image/*"));
                //params.put("cover", new DataPart("file_cover.jpg", AppHelper.getFileDataFromDrawable(context, drawable), "image/*"));

                //Log.e("test", "upload param");
                return params;
            }
        };

        //Log.e("test", "queue" );
        //queue.add(multipartRequest);
        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest);
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            //etText.setError("Must not be empty");
            return false;
        }

        return true;
    }

    private boolean givePost(String uid, final String room, final String size, final String price, String lat, String lon, final String area,
                             String address, String desc, final String photoUrl, String phn, String email, String type1, String type2) {

        pDialog.setMessage("Creating Post...");

        String curatedUrl = postUrl+"?UserID="+uid+"&Room_No="+room+"&Size="+size+"&Price="+price+"&Lat="+lat+"&Lon="+lon+"&Area="+area.replace(" ","_").replace("\n","_")
                +"&Address="+address.replace(" ","_").replace("\n","_")+"&Description="+desc.replace(" ","_").replace("\n","_")+"&Photo="+photoUrl+"&Phone_No="+phn+"&Email="+email+"&Type1="+type1+"&Type2="+type2;

        //Log.e("response",curatedUrl);
        // Request a string response from the provided URL.
        final StringRequest postRequest = new StringRequest(Request.Method.POST, curatedUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Log.e("response",response);
                        // Display the first 500 characters of the response string.

                        Gson gson = new Gson();
//                        Post post = gson.fromJson(response,Post.class);

                        String[] s = response.split("]");
                        String postId = s[0].substring(12,s[0].length()-2);
                        Log.e("postId",postId);
                        int imgCount = 0;
                        if(imgFile1!=null)
                            ++imgCount;
                        if(imgFile2!=null)
                            ++imgCount;
                        if(imgFile3!=null)
                            ++imgCount;

                        Log.e("imgCOunt",""+imgCount);

                        int currentCOunt = 0;
                        if(imgFile1!=null) {

                            try {
//                                    InputStream inputStream = context.getContentResolver().openInputStream(coverImage);
//                                    drawable = Drawable.createFromStream(inputStream, coverImage.toString() );
                                Drawable drawable = Drawable.createFromPath(imgFile1.getPath());
                                uploadImage(drawable, path1,imgCount,++currentCOunt,postId);
                            } catch (Exception e) {
                                //drawable = getResources().getDrawable(R.drawable.default_image);
                                if(pDialog.isShowing())
                                    pDialog.dismiss();
                                Toast.makeText(context, "Error: "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        if(imgFile2!=null) {

                            try {
//                                    InputStream inputStream = context.getContentResolver().openInputStream(coverImage);
//                                    drawable = Drawable.createFromStream(inputStream, coverImage.toString() );
                                Drawable drawable = Drawable.createFromPath(imgFile2.getPath());
                                uploadImage(drawable, path2,imgCount,++currentCOunt,postId);
                            } catch (Exception e) {
                                //drawable = getResources().getDrawable(R.drawable.default_image);
                                if(pDialog.isShowing())
                                    pDialog.dismiss();
                                Toast.makeText(context, "Error: "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        if(imgFile3!=null) {

                            try {
//                                    InputStream inputStream = context.getContentResolver().openInputStream(coverImage);
//                                    drawable = Drawable.createFromStream(inputStream, coverImage.toString() );
                                Drawable drawable = Drawable.createFromPath(imgFile3.getPath());
                                uploadImage(drawable, path3,imgCount,++currentCOunt,postId);
                            } catch (Exception e) {
                                //drawable = getResources().getDrawable(R.drawable.default_image);
                                if(pDialog.isShowing())
                                    pDialog.dismiss();
                                Toast.makeText(context, "Error: "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                            //startActivity(new Intent(context,PostFeedActivity.class));
                            //context.finish();
                            //layoutMain.setVisibility(View.GONE);
                            //layoutDone.setVisibility(View.VISIBLE);

//                            Intent i = new Intent(context, PostDoneActivity.class);
//                        coverPhotoLink = photoUrl.isEmpty()? photoLink : photoUrl;
//                            i.putExtra("photoLink",photoUrl.isEmpty()? photoLink : photoUrl);
//                            i.putExtra("rooms",room);
//                            i.putExtra("size",size);
//                            i.putExtra("area",area);
//                            i.putExtra("price",price);
//                            i.putExtra("type2",postType);
//
//                            //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(i);
//
//
//                            clearFields();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(pDialog.isShowing())
                    pDialog.dismiss();
                //Log.e("response",error.toString());
                Toast.makeText(context,"Unexpected error happened while posting",Toast.LENGTH_SHORT).show();

            }
        });
//        {
//            @Override
//            protected Map<String, String> getParams(){
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("UserID", "3"); //TODO: User ID
//                params.put("Room_No", rooms);
//                params.put("Size", size.getText().toString());
//                params.put("Price", price.getText().toString());
//                params.put("Lat", ""+lat);
//                params.put("Lon", ""+lon);
//                params.put("Area", area.getText().toString());
//                params.put("Address", address.getText().toString());
//                params.put("Description", desc.getText().toString());
//                params.put("Photo", photoUrl);
//                params.put("Phone_No", phn.getText().toString());
//                //params.put("Availability", available.isChecked()+"");
//                //params.put("Availability", "true");
//                params.put("Email", email.getText().toString());
//                params.put("Type1", saleOrRent);
//                params.put("Type2", postType);
//                return params;
//            }
//        };

        // Add the request to the RequestQueue.
        queue.add(postRequest);

        return true;
    }

    private boolean editPost(String postId, String room, final String size, final String price, String lat, String lon, final String area,
                             String address, String desc, final String photoUrl, String phn, String email, String type1, String type2) {

        pDialog.setMessage("Saving your edit...");

        String curatedUrl = editUrl;

        if(photoUrl.isEmpty()) {

            curatedUrl = editUrl + "?PostID=" + postId + "&Room=" + room + "&Size=" + size + "&Price=" + price + "&Lat=" + lat + "&Lon=" + lon + "&Area=" + area.replace(" ","_").replace("\n","_")
                    + "&Address=" + address.replace(" ","_").replace("\n","_") + "&Description=" + desc.replace(" ","_").replace("\n","_") + "&Phone_No=" + phn + "&Email=" + email + "&Type1=" + type1 + "&Type2=" + type2;

        }else {
            curatedUrl = editUrl + "?PostID=" + postId + "&Room=" + room + "&Size=" + size + "&Price=" + price + "&Lat=" + lat + "&Lon=" + lon + "&Area=" + area.replace(" ","_").replace("\n","_")
                    + "&Address=" + address.replace(" ","_").replace("\n","_") + "&Description=" + desc.replace(" ","_").replace("\n","_") + "&Photo=" + photoUrl + "&Phone_No=" + phn + "&Email=" + email + "&Type1=" + type1 + "&Type2=" + type2;

        }

        //Log.e("edit link",curatedUrl);
        // Request a string response from the provided URL.
        final StringRequest postRequest = new StringRequest(Request.Method.GET, curatedUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(pDialog.isShowing())
                            pDialog.dismiss();

                        //Log.e("edit response",response);
                        // Display the first 500 characters of the response string.
                        if(response.contains("Success")) {
                            //startActivity(new Intent(context,PostFeedActivity.class));
                            //context.finish();
                            //layoutMain.setVisibility(View.GONE);
                            //layoutDone.setVisibility(View.VISIBLE);

                            clearFields();

                            //GOING TO POST DONE
                            Intent i = new Intent(context, PostDoneActivity.class);
                            i.putExtra("edit","edit");
                            i.putExtra("photoLink",photoUrl.isEmpty()? photoLink : photoUrl);
                            i.putExtra("room",rooms);
                            i.putExtra("size",size);
                            i.putExtra("area",area);
                            i.putExtra("price",price);
                            i.putExtra("postType",postType);

                            startActivity(i);

                            clearFields();
                        }
                        else {
                            Toast.makeText(context,"Could not edit post",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(pDialog.isShowing())
                    pDialog.dismiss();
                //Log.e("response",error.toString());
                Toast.makeText(context,"Unexpected error happened while posting",Toast.LENGTH_SHORT).show();

            }
        });
//        {
//            @Override
//            protected Map<String, String> getParams(){
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("UserID", "3"); //TODO: User ID
//                params.put("Room_No", rooms);
//                params.put("Size", size.getText().toString());
//                params.put("Price", price.getText().toString());
//                params.put("Lat", ""+lat);
//                params.put("Lon", ""+lon);
//                params.put("Area", area.getText().toString());
//                params.put("Address", address.getText().toString());
//                params.put("Description", desc.getText().toString());
//                params.put("Photo", photoUrl);
//                params.put("Phone_No", phn.getText().toString());
//                //params.put("Availability", available.isChecked()+"");
//                //params.put("Availability", "true");
//                params.put("Email", email.getText().toString());
//                params.put("Type1", saleOrRent);
//                params.put("Type2", postType);
//                return params;
//            }
//        };

        // Add the request to the RequestQueue.
        queue.add(postRequest);

        return true;
    }

    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }
    }

    private void getReadStoragePermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }
    }

    private void getCameraPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }
    }

//    private void getDeviceLocation() {
//    /*
//     * Get the best and most recent location of the device, which may be null in rare
//     * cases when a location is not available.
//     */
//        try {
//            if (mLocationPermissionGranted) {
//                Task locationResult = mFusedLocationProviderClient.getLastLocation();
//
//                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//                        if (task.isSuccessful()) {
//                            // Set the map's camera position to the current location of the device.
//                            mLastKnownLocation = task.getResult();
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                                    new LatLng(mLastKnownLocation.getLatitude(),
//                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
//                        } else {
//                            Log.d("null", "Current location is null. Using defaults.");
//                            Log.e("err", "Exception: %s", task.getException());
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
//                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
//                        }
//                    }
//                });
//            }
//        } catch(SecurityException e)  {
//            Log.e("Exception: %s", e.getMessage());
//        }
//    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {

        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck==PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location!=null) {
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                    }

//                    Toast.makeText(context,"lat "+lat+" lon "+lon,Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            // The user has not granted permission.
            //Log.i("err", "The user did not grant location permission.");


            // Add a default marker, because the user hasn't selected a place.
//            mMap.addMarker(new MarkerOptions()
//                    .title("Select your place")
//                    .position(new LatLng(23.81,90.41))
//                    .snippet("default"));

            // Prompt the user for permission.
            //getLocationPermission();

            getLocationPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    getCurrentLocation();
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Intent pictureActionIntent = null;

                    pictureActionIntent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(
                            pictureActionIntent,
                            GALLERY_PICTURE);


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    getReadStoragePermission();
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Intent intent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    long time = System.currentTimeMillis();
                    cameraFileName = "IMAGE_" + time + "_bharatia.jpg";

                    File f = new File(android.os.Environment
                            .getExternalStorageDirectory(), cameraFileName);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(f));

                    startActivityForResult(intent,
                            CAMERA_REQUEST);


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    getCameraPermission();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void clearFields() {
//        room.getText().clear();
        size.getText().clear();
        price.getText().clear();
        area.getText().clear();
        address.getText().clear();
        desc.getText().clear();
        phn.getText().clear();
        email.getText().clear();

        //coverPicName.setText("");

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, new FeedFragment()).commit();

    }

    public String getPath(Uri uri) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }


    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                getActivity());
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        int permissionCheck = ContextCompat.checkSelfPermission(context,
                                Manifest.permission.READ_EXTERNAL_STORAGE);

                        if (permissionCheck==PackageManager.PERMISSION_GRANTED) {
                            Intent pictureActionIntent = null;

                            pictureActionIntent = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(
                                    pictureActionIntent,
                                    GALLERY_PICTURE);
                        }else{
                            getReadStoragePermission();
                        }

                    }
                });

        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        int permissionCheck = ContextCompat.checkSelfPermission(context,
                                Manifest.permission.CAMERA);

                        if (permissionCheck==PackageManager.PERMISSION_GRANTED) {


                            Intent intent = new Intent(
                                    MediaStore.ACTION_IMAGE_CAPTURE);
                            long time = System.currentTimeMillis();
                            cameraFileName = "IMAGE_" + time + "_bharatia.jpg";

                            File f = new File(android.os.Environment
                                    .getExternalStorageDirectory(), cameraFileName);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(f));

                            startActivityForResult(intent,
                                    CAMERA_REQUEST);
                        }else{
                            getCameraPermission();
                        }

                    }
                });
        myAlertDialog.show();
    }

}

//{
//
//@Override
//protected Map<String, String> getParams(String photoUrl) {
//
//        }
//
//        }
