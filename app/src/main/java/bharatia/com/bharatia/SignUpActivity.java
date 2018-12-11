package bharatia.com.bharatia;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.media.ExifInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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
import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;
//import com.zhihu.matisse.Matisse;
//import com.zhihu.matisse.MimeType;
//import com.zhihu.matisse.engine.impl.GlideEngine;
//import com.zhihu.matisse.engine.impl.PicassoEngine;
//import com.zhihu.matisse.filter.Filter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bharatia.com.bharatia.DataModel.FacebookPicture;
import bharatia.com.bharatia.Utils.AppHelper;
import bharatia.com.bharatia.Utils.ChangeLanguage;
import bharatia.com.bharatia.Utils.VolleyMultipartRequest;
import bharatia.com.bharatia.Utils.VolleySingleton;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class SignUpActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 24;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 25;

    private static final int IMAGE_CODE = 2;
    private static final int GALLERY_PICTURE = 10;
    private static final int CAMERA_REQUEST = 11;
    private final String uploadUrl = "http://sadmanamin.com/android_connect/upload.php";

    EditText fName,lName,username, email,phoneNum, pw,confirmPw;
    Button signUp,next;

    ProgressDialog pDialog;

    Button gglSignup, fbSignup;

    ImageButton userImage;

    List<Uri> selectedUri;
    Uri uri;
    String userPicDownloadLink = "";

    // Instantiate the RequestQueue.
    RequestQueue queue;
    String url = "http://sadmanamin.com/android_connect/create_user.php";
    String loginUrl = "http://sadmanamin.com/android_connect/login.php";

    private GoogleSignInApi mGoogleSignInClient;
    GoogleSignInAccount googleSignInAccount;

    GoogleApiClient mGoogleApiClient;
    private int RC_GGL_SIGN_IN = 12;
    private int RC_FB_SIGN_IN = 14;
    private String LOGIN_METHOD = "EMAIL";

    private String fbMail = "", fbUsername = "",fbFName = "",fbLName = "",fbPhone = "",fbPhoto = "";

    CallbackManager callbackManager;

    RelativeLayout mainLayout,secondLayout;
    private String path;
    private Bitmap bitmap;

    public int picChoice = -1;
    private File cameraFile;
    private String cameraFileName="";
    private File imgFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username = (EditText) findViewById(R.id.signUpUsername);
        email = (EditText) findViewById(R.id.signUpEmail);
        pw = (EditText) findViewById(R.id.signUpPassword);
        confirmPw = (EditText) findViewById(R.id.signUpPasswordReEnter);
        fName = (EditText) findViewById(R.id.signUpFirstName);
        lName = (EditText) findViewById(R.id.signUpLastName);
        phoneNum = (EditText) findViewById(R.id.signUpPhoneNumber);

        signUp = (Button) findViewById(R.id.finishSignUpBtn);
        next = (Button) findViewById(R.id.signUpNext);

        gglSignup = (Button) findViewById(R.id.signUpGgl);
        fbSignup = (Button) findViewById(R.id.signUpFb);

        userImage = (ImageButton) findViewById(R.id.signUpUserPicImage);

        mainLayout = (RelativeLayout) findViewById(R.id.signUpMainLayout);
        secondLayout = (RelativeLayout) findViewById(R.id.signUpSecondLayout);

        queue = Volley.newRequestQueue(this);

        //HACK FOR API>24 SO THAT CAMERA DOES NOT BREAK AND GIVES Exception android.os.FileUriExposedException: file:///storage/emulated/0/IMAGE_1522677906952_bharatia.jpg exposed beyond app through ClipData.Item.getUri()
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mainLayout.setVisibility(View.GONE);
                secondLayout.setVisibility(View.VISIBLE);

            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent(SignUpActivity.this, ChoosePhotoAppDialogActivity.class), IMAGE_CODE);
                //startDialog();


            }
        });

        //Configure Facebook Sign In
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        // App

                        //Log.e("LoginActivity", "succcess entered");
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("LoginActivity", response.toString());

                                        Log.e("obect", "" + object.toString());
                                        Log.e("response", "" + response.toString());
                                        try {
                                            // Application code
                                            // Application code
                                            String email = object.getString("email");
                                            //Log.e("LoginActivity", "" + email);
                                            String name = object.getString("name");
                                            //String phone = object.getString("phone");
                                            String photo = object.getString("picture");
                                            String[] nameSplit = name.split(" ");
                                            String fName = nameSplit[0];
                                            fbFName = fName;
                                            fbLName = nameSplit[nameSplit.length-1];
                                            fbUsername = fName;
                                            fbMail = email;
                                            //fbPhone = phone;
                                            Gson gson = new Gson();
                                            FacebookPicture facebookPicture = gson.fromJson(photo,FacebookPicture.class);
                                            fbPhoto = facebookPicture.getData().getUrl();

                                            Toast.makeText(SignUpActivity.this,"Logging you in to our server...",Toast.LENGTH_SHORT).show();
                                            loginRequest(email,"");

                                            //Log.e("LoginActivity", "" + name);
                                            //String birthday = object.getString("birthday"); // 01/31/1980 format
                                            ////Log.e("LoginActivity", "" + birthday);
                                        } catch (Exception e) {
                                            //Log.e("LoginActivity", "" + e.toString());
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,picture");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        // App code
                        //Log.e("LoginActivity", "cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        //Log.e("LoginActivity", exception.getCause().toString());
                    }
                });



        fbSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LOGIN_METHOD = "FACEBOOK";
                boolean loggedIn = AccessToken.getCurrentAccessToken() != null;
                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut();
                }
                //Log.e("LoginActivity", "fb entered");
                LoginManager.getInstance().logInWithReadPermissions(SignUpActivity.this, Arrays.asList("public_profile,email"));

            }
        });


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        gglSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LOGIN_METHOD = "GOOGLE";

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_GGL_SIGN_IN);
            }

        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isNetworkAvailable()) {
                    if (pw.getText().toString().equals(confirmPw.getText().toString())) {

                        if(!email.getText().toString().isEmpty() && !pw.getText().toString().isEmpty() && !fName.getText().toString().isEmpty() &&
                        !lName.getText().toString().isEmpty() && !username.getText().toString().isEmpty()) {
                            pDialog = new ProgressDialog(SignUpActivity.this);
                            pDialog.setMessage("Creating Account...");
                            pDialog.setCancelable(false);
                            pDialog.show();

                            try {
                                if (picChoice == 1)
                                    bitmap = new Compressor(SignUpActivity.this).compressToBitmap(new File(path));
                                else
                                    bitmap = new Compressor(SignUpActivity.this).compressToBitmap(cameraFile);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("err", e.toString());
                            }

//                        if (uri == null) {
//                            Log.e("err", "entered if");
//                            signUpRequest(fName.getText().toString(), lName.getText().toString(), username.getText().toString(), email.getText().toString(), phoneNum.getText().toString(), pw.getText().toString(), userPicDownloadLink);
//                        } else {
//                            Log.e("err", "entered else");

                            try {
                                //InputStream inputStream = getContentResolver().openInputStream(uri);
                                //Drawable d = Drawable.createFromStream(inputStream, uri.toString() );
                                //Drawable d = new BitmapDrawable(getResources(), bitmap);
//                                if(picChoice==1)
//                                    uploadWIthImage(d, path);
//                                else
//                                    uploadWIthImage(d, cameraFileName);

                                Drawable drawable;
                                drawable = Drawable.createFromPath(imgFile.getPath());
                                uploadWIthImage(drawable, path);
                            } catch (Exception e) {
                                Log.e("err while upload", e.toString());
                                signUpRequest(fName.getText().toString(), lName.getText().toString(), username.getText().toString(), email.getText().toString(), phoneNum.getText().toString(), pw.getText().toString(), userPicDownloadLink);

                            }
                        }else {

                            Toast.makeText(SignUpActivity.this, "Please fill up all the information", Toast.LENGTH_SHORT).show();

                        }
//                        }

                    } else {
                        Toast.makeText(SignUpActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(SignUpActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void signUpRequest(final String fName,final String lName,String username, final String email,final String phone, final String pw,final String userPic) {
        String curatedUrl = url + "?First="+fName+"&Last=" + lName +"&Username=" + username + "&Email=" + email + "&Password=" + pw+ "&Photo=" + userPic+ "&Phone=" + phone;
        Log.e("response", curatedUrl);
        // Request a string response from the provided URL.
        final StringRequest signUpRequest = new StringRequest(Request.Method.GET, curatedUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(pDialog.isShowing())
                            pDialog.dismiss();

                        //Log.e("response", response);

                        if (response.contains("Success")) {

                            String[] s = response.split(" ");
                            String userId = s[0].substring(1, s[0].length() - 1);
                            //Toast.makeText(SignUpActivity.this, "" + userId, Toast.LENGTH_SHORT).show();

                            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(getString(R.string.userId), userId);
                            editor.apply();

                            startActivity(new Intent(SignUpActivity.this, PostFeedActivity.class));
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Wrong email/password used", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(pDialog.isShowing())
                    pDialog.dismiss();

                Toast.makeText(SignUpActivity.this, "Oops! Something went wrong...", Toast.LENGTH_SHORT).show();

            }
        });
//        {
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                //params.put("Username", username.getText().toString());
//                params.put("Email", email);
//                params.put("Password", pw);
//
//                return params;
//            }
//
//        };

        // Add the request to the RequestQueue.
        queue.add(signUpRequest);

    }

    public void loginRequest(final String email, final String pw) {
        String curaedUrl = loginUrl + "?Email=" + email + "&Password=" + pw;
        //Log.e("response", curaedUrl);
        // Request a string response from the provided URL.
        final StringRequest loginRequest = new StringRequest(Request.Method.GET, curaedUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(pDialog.isShowing())
                            pDialog.dismiss();

                        Log.e("login res", response);
                        // Display the first 500 characters of the response string.
                        if (response.contains("Success")) {
                            String[] s = response.split(" ");
                            String userId = s[0].substring(1, s[0].length() - 1);
                            //Toast.makeText(SignUpActivity.this, "" + userId, Toast.LENGTH_SHORT).show();

                            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(getString(R.string.userId), userId);
                            editor.apply();

                            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
                            String uid = sharedPref.getString(getString(R.string.userId), "");

                           // Toast.makeText(SignUpActivity.this, "saved id" + uid, Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(SignUpActivity.this, PostFeedActivity.class));
                            finish();
                        } else {
                            if (LOGIN_METHOD.equals("GOOGLE")) {
                                Log.e("sign","signup ggl");
                                signUpRequest(googleSignInAccount.getGivenName(),googleSignInAccount.getFamilyName(),googleSignInAccount.getGivenName(), googleSignInAccount.getEmail(), "",
                                        "",googleSignInAccount.getPhotoUrl().toString());
                            } else if (LOGIN_METHOD.equals("FACEBOOK")) {
                                Log.e("sign","signup ggl");
                                signUpRequest(fbFName,fbLName,fbUsername, fbMail,fbPhone, "",fbPhoto);
                            } else {
                                Toast.makeText(SignUpActivity.this, "Failed to create account", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(pDialog.isShowing())
                    pDialog.dismiss();
                Toast.makeText(SignUpActivity.this, "Oops! Something went wrong...", Toast.LENGTH_SHORT).show();
                //Log.e("err", error.getMessage());

            }
        });
//        {
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Email", email);
//                params.put("Password", pw);
//
//                return params;
//            }

//        };

        // Add the request to the RequestQueue.
        queue.add(loginRequest);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMAGE_CODE) {
            if(resultCode == RESULT_OK){
                imgFile = (File) data.getExtras().get("file");
                //coverImage = selectedImage;


                path = data.getStringExtra("name");
                Glide
                        .with(this)
                        .load(imgFile)
                        .into(userImage);
                //addBitmapToMemoryCache("img",bitmap);
                //bitmap = uriToBitmap(coverImage);
            }
        }
        if (LOGIN_METHOD.equals("FACEBOOK")) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GGL_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //handleSignInResult(result);
            if (result.isSuccess()) {
                LOGIN_METHOD = "GOOGLE";
                GoogleSignInAccount acct = result.getSignInAccount();
                googleSignInAccount = acct;
                loginRequest(acct.getEmail(), "");
            }
        }


//        bitmap = null;
//        path = null;

        else if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {

            picChoice = 0;

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

                    Toast.makeText(getBaseContext(),

                            "Error while capturing image", Toast.LENGTH_LONG)

                            .show();

                    return;

                }



                cameraFile = f;

                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());

                bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);

                int rotate = 0;
                try {
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
                    e.printStackTrace();
                }
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);



                userImage.setImageBitmap(bitmap);
                //storeImageTosdCard(bitmap);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            if (data != null) {

                picChoice = 1;

                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                path = c.getString(columnIndex);
                c.close();


                bitmap = BitmapFactory.decodeFile(path); // load
                // preview image
                bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);



                userImage.setImageBitmap(bitmap);

            } else {
                //Toast.makeText(getApplicationContext(), "Cancelled",
                  //      Toast.LENGTH_SHORT).show();
            }
        }

//        if(requestCode==IMAGE_CODE) {
//            if(resultCode == RESULT_OK){
//
////                Uri selectedImage = data.getData();
////                uri = selectedImage;
//
//
////                selectedUri = Matisse.obtainResult(data);
////                uri = selectedUri.get(0);
//
//                //coverPicName.setText(selectedImage.getLastPathSegment());
//
//            }
//        }
//
////        //Picking the image with library
////        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
////            @Override
////            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
////                //Some error handling
////            }
////
////            @Override
////            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
////                uri = Uri.parse(imageFile.getPath());
////                path = imageFile.getPath();
////                Log.e("path", imageFile.getPath());
////                Log.e("path", imageFile.getAbsolutePath());
////                try {
////                    Log.e("path", imageFile.getCanonicalPath());
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////                Log.e("path", uri.getPath());
////                Log.e("path", uri.getLastPathSegment());
////                Log.e("path", uri.getEncodedPath());
////                userImage.setImageURI(uri);
////            }
////        });
    }

        @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT).show();
    }

    private void uploadWIthImage(final Drawable drawable, final String filePath) {

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, uploadUrl, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                Log.e("test", "on response: "+response);
                Log.e("test", "on response result: "+resultResponse);
                if(resultResponse.contains("sadmanamin.com/android_connect/uploads")) {



                    if(picChoice==1) {
                        String[] s = filePath.split("/");
                        userPicDownloadLink = "http://sadmanamin.com/android_connect/uploads/" + s[s.length-1];
                    }else {

                        userPicDownloadLink = "http://sadmanamin.com/android_connect/uploads/" + filePath;
                        
                    }

                    signUpRequest(fName.getText().toString(),lName.getText().toString(),username.getText().toString(), email.getText().toString(),phoneNum.getText().toString(), pw.getText().toString(),userPicDownloadLink);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e("test", "on error: "+networkResponse);
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

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("err",e.toString());
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
                params.put("fileToUpload", new DataPart(filePath, AppHelper.getFileDataFromDrawable(SignUpActivity.this, drawable), "image/*"));
                //params.put("cover", new DataPart("file_cover.jpg", AppHelper.getFileDataFromDrawable(context, drawable), "image/*"));

                //Log.e("test", "upload param");
                return params;
            }
        };

        //Log.e("test", "queue" );
        //queue.add(multipartRequest);
        VolleySingleton.getInstance(this).addToRequestQueue(multipartRequest);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        String lang_code = ChangeLanguage.getLang(newBase); //load it from SharedPref
        //SharedPreferences sharedPref = newBase.getSharedPreferences(getString(R.string.pref_file_key),Context.MODE_PRIVATE);
        //lang_code = sharedPref.getString(getString(R.string.locale), "en");
        Context context = ChangeLanguage.changeLang(newBase, lang_code);
        super.attachBaseContext(context);
    }

    public String getPath(Uri uri) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private void getReadStoragePermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(SignUpActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(SignUpActivity.this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
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
        if (ContextCompat.checkSelfPermission(SignUpActivity.this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(SignUpActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);

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

    private void startDialog() {
        final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        int permissionCheck = ContextCompat.checkSelfPermission(SignUpActivity.this,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE);

                        if (permissionCheck== PackageManager.PERMISSION_GRANTED) {
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

                        int permissionCheck = ContextCompat.checkSelfPermission(SignUpActivity.this,
                                android.Manifest.permission.CAMERA);

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