package bharatia.com.bharatia;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.novoda.merlin.MerlinsBeard;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import bharatia.com.bharatia.DataModel.FacebookPicture;
import bharatia.com.bharatia.Utils.ChangeLanguage;

public class loginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    EditText email,pw;
    Button logIn,signUp;
    Button gglLogin,fbLogin;

    ProgressDialog pDialog;

    String errorMsg = "Oops! Something went wrong...";

    // Instantiate the RequestQueue.
    RequestQueue queue;
    String url ="http://sadmanamin.com/android_connect/login.php";
    String signupUrl ="http://sadmanamin.com/android_connect/create_user.php";
    private GoogleSignInApi mGoogleSignInClient;
    GoogleSignInAccount googleSignInAccount;

    GoogleApiClient mGoogleApiClient;
    private int RC_GGL_SIGN_IN = 12;
    private int RC_FB_SIGN_IN = 14;
    private String LOGIN_METHOD = "EMAIL";

    private String fbMail = "", fbUsername = "",fbFName = "",fbLName = "",fbPhone = "-1",fbPhoto = "";

    private Handler handler = new Handler();

    //MerlinsBeard merlinsBeard;

    CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText)findViewById(R.id.loginEmail);
        pw = (EditText)findViewById(R.id.loginPassword);

        logIn = (Button) findViewById(R.id.loginBtn);
        signUp = (Button)findViewById(R.id.signUpBtn);

        gglLogin = (Button) findViewById(R.id.loginGgl);
        fbLogin = (Button)findViewById(R.id.loginFb);

        //handler.post(runnable);

        queue = Volley.newRequestQueue(this);

        //merlinsBeard = MerlinsBeard.from(getApplicationContext());

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
                                        //Log.v("LoginActivity", response.toString());

                                        //Log.e("obect", ""+object.toString());
                                        //Log.e("response", ""+response.toString());
                                        try {
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

                                            Toast.makeText(loginActivity.this,"Logging you in to our server...",Toast.LENGTH_SHORT).show();
                                            loginRequest(email,"");

                                            //Log.e("LoginActivity", ""+name);
                                            //String birthday = object.getString("birthday"); // 01/31/1980 format
                                            //Log.e("LoginActivity", ""+birthday);
                                        }catch (Exception e) {
                                            Log.e("LoginActivity", ""+e.toString());
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
                        Toast.makeText(loginActivity.this,"Login Cancelled!",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.e("LoginActivity", exception.getCause().toString());
                        Toast.makeText(loginActivity.this,exception.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                });


        fbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    //if (isOnline()) {
                        // Connected, do something!
                        LOGIN_METHOD = "FACEBOOK";
                        boolean loggedIn = AccessToken.getCurrentAccessToken() != null;
                        if (AccessToken.getCurrentAccessToken() != null) {
                            LoginManager.getInstance().logOut();
                        }
                        //Log.e("LoginActivity", "fb entered");
                        LoginManager.getInstance().logInWithReadPermissions(loginActivity.this, Arrays.asList("public_profile,email"));
//                    }else {
//                        Toast.makeText(loginActivity.this,"Can't connect to the internt",Toast.LENGTH_SHORT).show();
//                    }
                }else {
                    Toast.makeText(loginActivity.this,"No internet access",Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();


        gglLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNetworkAvailable()) {
                    //if (isOnline()) {

                    LOGIN_METHOD = "GOOGLE";

                        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                        startActivityForResult(signInIntent, RC_GGL_SIGN_IN);
//                    }else {
//                        Toast.makeText(loginActivity.this,"Can't connect to the internet",Toast.LENGTH_SHORT).show();
//                    }
                }else {
                    Toast.makeText(loginActivity.this,"No internet access",Toast.LENGTH_SHORT).show();
                }
            }

        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNetworkAvailable()) {
                    //if (isOnline()) {
                        pDialog = new ProgressDialog(loginActivity.this);
                        pDialog.setMessage("Loading...");
                        pDialog.show();

                        loginRequest(email.getText().toString(), pw.getText().toString());
//                    } else {
//                        Toast.makeText(loginActivity.this,"Can't connect to the internet",Toast.LENGTH_SHORT).show();
//                    }
                }else {
                    Toast.makeText(loginActivity.this,"No internet access",Toast.LENGTH_SHORT).show();
                }

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(loginActivity.this,SignUpActivity.class));
            }
        });
    }

    public void loginRequest(final String email, final String pw) {
        String curaedUrl = url+"?Email="+email+"&Password="+pw;
        Log.e("response",curaedUrl);
        // Request a string response from the provided URL.
        final StringRequest loginRequest = new StringRequest(Request.Method.POST, curaedUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(pDialog!=null)
                            pDialog.dismiss();

                        Log.e("response",response);
                        // Display the first 500 characters of the response string.
                        if(response.contains("Success")) {
                            String[] s = response.split(" ");
                            String userId = s[0].substring(1,s[0].length()-1);
                            //Toast.makeText(loginActivity.this,""+userId,Toast.LENGTH_SHORT).show();

                            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_file_key),Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(getString(R.string.userId),userId);
                            editor.apply();

                            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_file_key),Context.MODE_PRIVATE);
                            String uid = sharedPref.getString(getString(R.string.userId), "");

                            //Toast.makeText(loginActivity.this,"saved id"+uid,Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(loginActivity.this,PostFeedActivity.class));
                            finish();
                        } else {
                            if(LOGIN_METHOD.equals("GOOGLE")) {
                                if(googleSignInAccount!= null && googleSignInAccount.getPhotoUrl()!=null) {
                                    //Toast.makeText(loginActivity.this,"Logging you in to our server...",Toast.LENGTH_SHORT).show();
                                    signUpRequest(googleSignInAccount.getGivenName(),googleSignInAccount.getFamilyName(),googleSignInAccount.getGivenName(), googleSignInAccount.getEmail(), "-1",
                                            "",googleSignInAccount.getPhotoUrl().toString());
                                }else {
                                    signUpRequest(googleSignInAccount.getGivenName(), googleSignInAccount.getFamilyName(), googleSignInAccount.getDisplayName(), googleSignInAccount.getEmail(), "-1",
                                            "", "");
                                }
                            }else if(LOGIN_METHOD.equals("FACEBOOK")) {
                                //Toast.makeText(loginActivity.this,"Logging you in to our server...",Toast.LENGTH_SHORT).show();
                                signUpRequest(fbFName,fbLName,fbUsername, fbMail,fbPhone, "",fbPhoto);
                            } else {
                                Toast.makeText(loginActivity.this, "Wrong email/password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(pDialog!=null)
                    pDialog.dismiss();
                Toast.makeText(loginActivity.this,errorMsg,Toast.LENGTH_SHORT).show();
                //Log.e("err",error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Email", email);
                params.put("Password", pw);

                return params;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(loginRequest);
    }

    public void signUpRequest(final String fName,final String lName,String username, final String email,final String phone, final String pw,final String userPic) {
        String curatedUrl = signupUrl + "?First="+fName+"&Last=" + lName +"&Username=" + username + "&Email=" + email + "&Password=" + pw+ "&Photo=" + userPic+ "&Phone=" + phone;
        Log.e("signup url", curatedUrl);
        // Request a string response from the provided URL.
        final StringRequest signUpRequest = new StringRequest(Request.Method.GET, curatedUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        pDialog.dismiss();

                        Log.e("response",response);

                        Log.e("response","signup entered");


                        if(response.contains("Success")) {

                            String[] s = response.split(" ");
                            String userId = s[0].substring(1,s[0].length()-1);
                            //Toast.makeText(loginActivity.this,""+userId,Toast.LENGTH_SHORT).show();

                            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(getString(R.string.userId),userId);
                            editor.apply();

                            Intent i = new Intent(loginActivity.this,PostFeedActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();
                        }else {
                            Log.e("err","wrong credetial in signup");
                            Toast.makeText(loginActivity.this,errorMsg,Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

  //              pDialog.dismiss();
                Log.e("err",error.toString()+"");
                Toast.makeText(loginActivity.this,errorMsg,Toast.LENGTH_SHORT).show();

            }
        }) ;
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
//    private void signIn() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(LOGIN_METHOD.equals("FACEBOOK")) {
            if(data!=null)
                callbackManager.onActivityResult(requestCode, resultCode, data);
            else {
                Toast.makeText(loginActivity.this,"Login failed. Please try other login methods.",Toast.LENGTH_SHORT).show();
            }
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GGL_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //handleSignInResult(result);
            if(result.isSuccess()) {
                LOGIN_METHOD = "GOOGLE";
                GoogleSignInAccount acct = result.getSignInAccount();
                googleSignInAccount = acct;
                if(acct.getEmail()!=null) {
                    Toast.makeText(loginActivity.this,"Logging you in to our server...",Toast.LENGTH_SHORT).show();
                    loginRequest(acct.getEmail(), "");
                }else {
                    Toast.makeText(loginActivity.this,"success but email null",Toast.LENGTH_SHORT).show();
                    Toast.makeText(loginActivity.this,result.getStatus().getStatusMessage(),Toast.LENGTH_SHORT).show();
                    Toast.makeText(loginActivity.this,"Login failed. Please try other login methods.",Toast.LENGTH_SHORT).show();
                }
            }else{
                //Toast.makeText(loginActivity.this,"success but email null",Toast.LENGTH_SHORT).show();
                Toast.makeText(loginActivity.this,result.getStatus().getStatusMessage(),Toast.LENGTH_SHORT).show();
                Toast.makeText(loginActivity.this,"Login failed. Please try other login methods.",Toast.LENGTH_SHORT).show();
            }
        }

//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignInResult.getSignedInAccountFromIntent(data);
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                firebaseAuthWithGoogle(account);
//            } catch (ApiException e) {
//                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
//                // ...
//            }
//        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //TODO on connection failed
        Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT).show();
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



    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(!isOnline()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorMsg = "Can't connect to the internet";
                        Toast.makeText(loginActivity.this,errorMsg,Toast.LENGTH_SHORT).show();
                    }
                });
            }
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

}
