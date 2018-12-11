package bharatia.com.bharatia;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.ByteArrayOutputStream;

import bharatia.com.bharatia.Utils.ChangeLanguage;

public class PostDoneActivity extends AppCompatActivity {

    ImageButton fbShare,instaShare,whatsappShare;

    RelativeLayout shareLayout;

    TextView areaShare, sizeShare, roomShare, priceShare,postType;
    ImageView postImg;
    TextView postDoneText;
    private final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 32;
    private final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 30;

    ProgressDialog pDialog;

    Button share,home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_done);


        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#00695C\">Posted Successfully</font>"));

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryGreen), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);


//        fbShare = (ImageButton) findViewById(R.id.postFacebook);
//        instaShare = (ImageButton) findViewById(R.id.postInstagram);
//        whatsappShare = (ImageButton) findViewById(R.id.postWhatsapp);

        shareLayout = (RelativeLayout) findViewById(R.id.postShareLayout);
        //shareLayout.setVisibility(View.GONE);

        roomShare = (TextView) findViewById(R.id.postShareRoom);
        sizeShare = (TextView) findViewById(R.id.postShareSize);
        areaShare = (TextView) findViewById(R.id.postShareArea);
        priceShare = (TextView) findViewById(R.id.postSharePrice);
        postType = (TextView) findViewById(R.id.postSharePostType);
        postImg = (ImageView) findViewById(R.id.postShareImg);

        share = (Button) findViewById(R.id.postDoneShare);
        home = (Button) findViewById(R.id.postDoneHome);

        //postDoneText = (TextView) findViewById(R.id.postDonePostedText);


        areaShare.setText(getIntent().getStringExtra("area"));
        priceShare.setText(getString(R.string.post_done_tk) + getIntent().getStringExtra("price"));
        roomShare.setText(getIntent().getStringExtra("rooms") + getString(R.string.post_done_room));
        sizeShare.setText(getIntent().getStringExtra("size") + getString(R.string.post_done_sq_ft));
        postType.setText(getIntent().getStringExtra("type2"));

        Glide
                .with(this)
                .load(getIntent().getStringExtra("photoLink"))
                .into(postImg);

        Log.e("photo link",getIntent().getStringExtra("photoLink")+"");

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                //Toast.makeText(PostDetailsActivity.this,"share entered",Toast.LENGTH_SHORT).show();

                int permissionCheckRead = ContextCompat.checkSelfPermission(PostDoneActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE);
                int permissionCheckWrite = ContextCompat.checkSelfPermission(PostDoneActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permissionCheckRead==PackageManager.PERMISSION_GRANTED && permissionCheckWrite==PackageManager.PERMISSION_GRANTED) {

                    //Toast.makeText(PostDetailsActivity.this,"if entered",Toast.LENGTH_SHORT).show();

                    try{


                        pDialog = new ProgressDialog(PostDoneActivity.this);
                        pDialog.setMessage("Creating post...");
                        pDialog.setCancelable(false);
                        pDialog.show();

                        shareImage(shareLayout);

                        //print();
//                mainLayout.scrollBy(mainLayout.getHeight(),0);
//                print();

                    }catch (Exception e) {
                        Toast.makeText(PostDoneActivity.this,"Failed!please try again.",Toast.LENGTH_SHORT).show();
                        Log.e("err",e.toString());
                    }

                }else {
                    getReadStroagePermission();
                }

            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostDoneActivity.this,PostFeedActivity.class));
                finish();
            }
        });
//        fbShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                shareLayout.setVisibility(View.VISIBLE);
//
//                pDialog = new ProgressDialog(PostDoneActivity.this);
//                pDialog.setMessage("Creating post...");
//                pDialog.setCancelable(false);
//                pDialog.show();
//
//                //TODO SHARE THE POST TAKING SCREENSHOT
//                int permissionCheckRead = ContextCompat.checkSelfPermission(PostDoneActivity.this,
//                        android.Manifest.permission.READ_EXTERNAL_STORAGE);
//                int permissionCheckWrite = ContextCompat.checkSelfPermission(PostDoneActivity.this,
//                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//                if (permissionCheckRead== PackageManager.PERMISSION_GRANTED && permissionCheckWrite==PackageManager.PERMISSION_GRANTED) {
//
//                    shareLayout.setVisibility(View.VISIBLE);
//                    Bitmap bitmap;
//
//                    try{
//                        bitmap = takeScreenShot(shareLayout);
//
//                        SharePhoto photo = new SharePhoto.Builder()
//                                .setBitmap(bitmap)
//                                .build();
//                        SharePhotoContent content = new SharePhotoContent.Builder()
//                                .addPhoto(photo)
//                                .build();
//
//                        ShareDialog shareDialog = new ShareDialog(PostDoneActivity.this);
//                        shareDialog.show(content);
//
//                    }catch (Exception e) {
//                        Toast.makeText(PostDoneActivity.this,"Failed!please try again.",Toast.LENGTH_SHORT).show();
//                    }
//                }else {
//                    getReadStroagePermission();
//                }
//
//
//            }
//        });
//        instaShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                int permissionCheckRead = ContextCompat.checkSelfPermission(PostDoneActivity.this,
//                        android.Manifest.permission.READ_EXTERNAL_STORAGE);
//                int permissionCheckWrite = ContextCompat.checkSelfPermission(PostDoneActivity.this,
//                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//                if (permissionCheckRead== PackageManager.PERMISSION_GRANTED && permissionCheckWrite==PackageManager.PERMISSION_GRANTED) {
//
//                    try {
//
//                        shareLayout.setVisibility(View.VISIBLE);
//                        Bitmap bitmap;
//                        bitmap = takeScreenShot(shareLayout);
//                        shareLayout.setVisibility(View.GONE);
//
//                        Glide
//                                .with(PostDoneActivity.this)
//                                .load(getIntent().getStringExtra("photoLink"))
//                                .into(postImg);
//
//                        Uri imgUri = getBitmapUri(bitmap);
//
//                        if(imgUri!=null) {
//                            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
//                            whatsappIntent.setType("image/*");
//                            whatsappIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
//                            whatsappIntent.setPackage("com.instagram.android");
//                            //whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
//                            try {
//                                startActivity(whatsappIntent);
//                            } catch (android.content.ActivityNotFoundException ex) {
//                                Toast.makeText(PostDoneActivity.this, "Instagram have not been installed.", Toast.LENGTH_SHORT).show();
//                            }
//
//                        }else {
//                            Toast.makeText(PostDoneActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
//                        }
//                    }catch (Exception e) {
//                        Toast.makeText(PostDoneActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//            }
//        });
//
//        whatsappShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                int permissionCheckRead = ContextCompat.checkSelfPermission(PostDoneActivity.this,
//                        android.Manifest.permission.READ_EXTERNAL_STORAGE);
//                int permissionCheckWrite = ContextCompat.checkSelfPermission(PostDoneActivity.this,
//                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//                if (permissionCheckRead== PackageManager.PERMISSION_GRANTED && permissionCheckWrite==PackageManager.PERMISSION_GRANTED) {
//
//                    try {
//                        //shareLayout.setVisibility(View.VISIBLE);
//                        shareLayout.setVisibility(View.VISIBLE);
//                        Bitmap bitmap;
//                        bitmap = takeScreenShot(shareLayout);
//
//                        Glide
//                                .with(PostDoneActivity.this)
//                                .load(getIntent().getStringExtra("photoLink"))
//                                .into(postImg);
//
//
//                        Uri imgUri = getBitmapUri(bitmap);
//
//                        if(imgUri!=null) {
//                            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
//                            whatsappIntent.setType("image/*");
//                            whatsappIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
//                            whatsappIntent.setPackage("com.whatsapp");
//                            //whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
//                            try {
//                                startActivity(whatsappIntent);
//                            } catch (android.content.ActivityNotFoundException ex) {
//                                Toast.makeText(PostDoneActivity.this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
//                            }
//
//                        }else {
//                            Toast.makeText(PostDoneActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (Exception e) {
//                        Toast.makeText(PostDoneActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });

    }

//    private Uri getBitmapUri(Bitmap bitmap) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG,100,bytes);
//        String path = MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"Post from Bharatia app",null);
//        if(path==null)
//            return null;
//        else
//            return Uri.parse(path);
//    }
//
//    private Bitmap takeScreenShot(View view) {
////        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(), Bitmap.Config.ARGB_8888);
////        Canvas canvas = new Canvas(bitmap);
////        view.draw(canvas);
////        return bitmap;
//
//        View v= view; //view.getRootView();
//        v.setDrawingCacheEnabled(true);
//        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
//
//        v.buildDrawingCache(true);
//        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
//        v.setDrawingCacheEnabled(false); // clear drawing cache
//
//        if(pDialog!=null) {
//            pDialog.dismiss();
//            pDialog.cancel();
//            pDialog=null;
//        }
//
//        return b;
//
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            this.finish();
        return true;
    }

    private void getReadStroagePermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
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
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
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


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null)));

                    int permissionCheck = ContextCompat.checkSelfPermission(PostDoneActivity.this,
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
                    takeScreenShot(shareLayout);
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

        Intent i = new Intent(this,PostFeedActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finishAffinity();
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
    protected void attachBaseContext(Context newBase) {
        String lang_code = ChangeLanguage.getLang(newBase); //load it from SharedPref
        //SharedPreferences sharedPref = newBase.getSharedPreferences(getString(R.string.pref_file_key),Context.MODE_PRIVATE);
        //lang_code = sharedPref.getString(getString(R.string.locale), "en");
        Context context = ChangeLanguage.changeLang(newBase, lang_code);
        super.attachBaseContext(context);
    }
}
