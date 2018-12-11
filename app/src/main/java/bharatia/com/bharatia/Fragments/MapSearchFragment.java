package bharatia.com.bharatia.Fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bharatia.com.bharatia.DataModel.Post;
import bharatia.com.bharatia.PostDetailsActivity;
import bharatia.com.bharatia.R;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class MapSearchFragment extends Fragment implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, PlaceSelectionListener, GoogleMap.OnMarkerClickListener{


    private final int MY_PERMISSIONS_REQUEST_LOCATION = 0;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double lat, lon;

    SupportMapFragment mapFragment;
    PlaceAutocompleteFragment autocompleteFragment;

    Button filter;

    Post[] salePosts,rentPosts;
    GoogleMap map;

    Context context;

    CardView cardView;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;

    TextView searchePlaceText;

    public MapSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map_search, container, false);

        filter = (Button)v.findViewById(R.id.filterSearch);

        cardView = (CardView) v.findViewById(R.id.placePickerCardView);

        searchePlaceText = (TextView) v.findViewById(R.id.searchPlaceText);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (isAdded()) {

                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                        .build(getActivity());
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    }
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });
        while(context==null) {
            context = getContext();
        }

        //Going to the filter activity
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAdded()) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.map_container, new SearchFragment()).commit();
                }
            }
        });

        //Getting the user last known location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

//        getCurrentLocation();

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        FragmentManager fm = getChildFragmentManager();
        //mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.mapFragment);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.mapFragment, mapFragment).commit();
            fm.executePendingTransactions();
        }

        mapFragment.getMapAsync(this);

//        try {
//
//            if(autocompleteFragment==null && isAdded()) {
//                autocompleteFragment = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.place_autoCompleteFragment);
//            }
//
//            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//                @Override
//                public void onPlaceSelected(Place place) {
//                    // TODO: Get info about the selected place.
//                    //Log.e("place", "Place: " + place.getName());
//                    LatLng currentPos = place.getLatLng();
//
//                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPos,15),1000,null);
//                }
//
//                @Override
//                public void onError(Status status) {
//                    // TODO: Handle the error.
//                    //Log.e("err", "An error occurred: " + status);
//                }
//            });
//
//        }catch (Exception e) {
//            //Toast.makeText(context,"Oops!Something went wrong",Toast.LENGTH_SHORT).show();
//        }



        return v;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        getCurrentLocation();

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //Toast.makeText(context,marker.getTitle(),Toast.LENGTH_SHORT).show();

                String title = marker.getTitle();
                if(title!=null) {
                    if (title.contains("Sale")) {
                        String s = marker.getTag().toString();
                        String positon = s;
                        //int index = (Integer) marker.getTag();
                        //Log.e("sale postion",positon);

                        Post post = salePosts[Integer.parseInt(positon)];
                        Intent i = new Intent(context, PostDetailsActivity.class);
                        i.putExtra("postId", post.getPostID());
                        i.putExtra("photoLink", post.getCoverPhoto());
                        i.putExtra("area", post.getArea());
                        i.putExtra("price", post.getPrice());
                        i.putExtra("room", post.getRoomNo());
                        i.putExtra("size", post.getSize());
                        i.putExtra("address", post.getAddress());
                        i.putExtra("description", post.getDescription());
                        i.putExtra("phone", post.getPhoneNo());
                        i.putExtra("email", post.getEmail());
                        i.putExtra("lat", post.getLat());
                        i.putExtra("lon", post.getLon());
                        context.startActivity(i);
                    } else if (title.contains("Rent")) {

                        String s = marker.getTag().toString();
                        String positon = s;
                        //int index = (Integer) marker.getTag();
                        //Log.e("sale postion",positon);

                        //int index = (Integer) marker.getTag();
                        Post post = rentPosts[Integer.parseInt(positon)];
                        Intent i = new Intent(context, PostDetailsActivity.class);

                        i.putExtra("postId", post.getPostID());
                        i.putExtra("photoLink", post.getCoverPhoto());
                        i.putExtra("area", post.getArea());
                        i.putExtra("price", post.getPrice());
                        i.putExtra("room", post.getRoomNo());
                        i.putExtra("size", post.getSize());
                        i.putExtra("address", post.getAddress());
                        i.putExtra("description", post.getDescription());
                        i.putExtra("phone", post.getPhoneNo());
                        i.putExtra("email", post.getEmail());
                        i.putExtra("lat", post.getLat());
                        i.putExtra("lon", post.getLon());
                        i.putExtra("type1", post.getType1());
                        i.putExtra("type2", post.getType2());

                        context.startActivity(i);
                    }
                }
            }
        });
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //googleMap.setMyLocationEnabled(true);
            getLocationPermission();
        }else {
            //getLocationPermission();
            googleMap.setMyLocationEnabled(true);
        }

        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.

        //Toast.makeText(context,"lat "+lat+" lon "+lon,Toast.LENGTH_SHORT).show();

        map = googleMap;

        //get all sale post
        SharedPreferences salePref = context.getSharedPreferences(getString(R.string.pref_file_key),Context.MODE_PRIVATE);
        String saleResponse = salePref.getString(getString(R.string.salePost), "[]");

        //get all sale post
        SharedPreferences rentPref = context.getSharedPreferences(getString(R.string.pref_file_key),Context.MODE_PRIVATE);
        String rentResponse = rentPref.getString(getString(R.string.rentPost), "[]");

        final Gson gson = new Gson();
        salePosts = gson.fromJson(saleResponse,Post[].class);
        rentPosts = gson.fromJson(rentResponse,Post[].class);

        for(int i=0;i<salePosts.length;i++) {
            Post post = salePosts[i];
            LatLng salePos = new LatLng(Double.parseDouble(post.getLat()),Double.parseDouble(post.getLon()));
            if(post.getType2().equals("hotel")) {
                googleMap.addMarker(new MarkerOptions()
                        .title("Sale: " + post.getPrice() + " Tk").icon(BitmapDescriptorFactory.fromResource(R.drawable.hotel_red_map))
                        .snippet(post.getRoomNo() + " rooms, " + post.getSize() + " sq.ft")
                        .position(salePos)).setTag(i);
            }else if(post.getType2().equals("flat")) {
                googleMap.addMarker(new MarkerOptions()
                            .title("Sale: " + post.getPrice() + " Tk").icon(BitmapDescriptorFactory.fromResource(R.drawable.flat_red_map))
                            .snippet(post.getRoomNo() + " rooms, " + post.getSize() + " sq.ft")
                            .position(salePos)).setTag(i);
            }else{
                googleMap.addMarker(new MarkerOptions()
                        .title("Sale: " + post.getPrice() + " Tk").icon(BitmapDescriptorFactory.fromResource(R.drawable.sublet_red_map))
                        .snippet(post.getRoomNo() + " rooms, " + post.getSize() + " sq.ft")
                        .position(salePos)).setTag(i);
            }


        }

        for(int i=0;i<rentPosts.length;i++) {
            Post post = rentPosts[i];
            LatLng rentPos = new LatLng(Double.parseDouble(post.getLat()),Double.parseDouble(post.getLon()));
            if(post.getType2().equals("hotel")) {
                googleMap.addMarker(new MarkerOptions()
                        .title("Rent: " + post.getPrice() + " Tk").icon(BitmapDescriptorFactory.fromResource(R.drawable.hotel_green_map))
                        .snippet(post.getRoomNo() + " rooms, " + post.getSize() + " sq.ft")
                        .position(rentPos)).setTag(i);
            }else if(post.getType2().equals("flat")) {
                googleMap.addMarker(new MarkerOptions()
                        .title("Rent: " + post.getPrice() + " Tk").icon(BitmapDescriptorFactory.fromResource(R.drawable.flat_green_map))
                        .snippet(post.getRoomNo() + " rooms, " + post.getSize() + " sq.ft")
                        .position(rentPos)).setTag(i);
            }else{
                googleMap.addMarker(new MarkerOptions()
                        .title("Rent: " + post.getPrice() + " Tk").icon(BitmapDescriptorFactory.fromResource(R.drawable.sublet_green_map))
                        .snippet(post.getRoomNo() + " rooms, " + post.getSize() + " sq.ft")
                        .position(rentPos)).setTag(i);
            }

//            map.addMarker(new MarkerOptions()
//                    .title("Rent: "+post.getPrice()+" Tk")
//                    .snippet(post.getRoomNo()+" rooms\n"+post.getSize()+" sq.ft")
//                    .position(rentPos)).setTag(i);

        }


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

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {

        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck==PackageManager.PERMISSION_GRANTED) {

            //Getting the user last known location
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if(location!=null) {
                        lat = location.getLatitude();
                        lon = location.getLongitude();

                        LatLng currentPos = new LatLng(lat, lon);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 15));

                        map.addMarker(new MarkerOptions()
                                .title("My current location")
                                //.snippet("test.........")
                                .position(currentPos));

                        //Toast.makeText(context,"lat "+lat+" lon "+lon,Toast.LENGTH_SHORT).show();

                    }
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

                    getCurrentLocation();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    getCurrentLocation();
                }

            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onPlaceSelected(Place place) {
        //TODO select place
    }

    @Override
    public void onError(Status status) {

    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }



    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(context,marker.getTitle(),Toast.LENGTH_SHORT).show();

        if(marker.getTitle().contains("My location")) {

            if (marker.getTitle().contains("My location")) {
                return false;
            } else {

                if (marker.getTag().toString().contains("sale")) {
                    String[] s = marker.getTag().toString().split(" ");
                    String positon = s[1];
                    //int index = (Integer) marker.getTag();
                    //Log.e("sale postion",positon);

                    Post post = salePosts[Integer.parseInt(positon)];
                    Intent i = new Intent(context, PostDetailsActivity.class);
                    i.putExtra("postId", post.getPostID());
                    i.putExtra("photoLink", post.getCoverPhoto());
                    i.putExtra("area", post.getArea());
                    i.putExtra("price", post.getPrice());
                    i.putExtra("room", post.getRoomNo());
                    i.putExtra("size", post.getSize());
                    i.putExtra("address", post.getAddress());
                    i.putExtra("description", post.getDescription());
                    i.putExtra("phone", post.getPhoneNo());
                    i.putExtra("email", post.getEmail());
                    i.putExtra("lat", post.getLat());
                    i.putExtra("lon", post.getLon());
                    context.startActivity(i);
                } else {

                    String[] s = marker.getTag().toString().split(" ");
                    String positon = s[1];
                    //int index = (Integer) marker.getTag();
                    //Log.e("sale postion",positon);

                    //int index = (Integer) marker.getTag();
                    Post post = rentPosts[Integer.parseInt(positon)];
                    Intent i = new Intent(context, PostDetailsActivity.class);

                    i.putExtra("postId", post.getPostID());
                    i.putExtra("photoLink", post.getCoverPhoto());
                    i.putExtra("area", post.getArea());
                    i.putExtra("price", post.getPrice());
                    i.putExtra("room", post.getRoomNo());
                    i.putExtra("size", post.getSize());
                    i.putExtra("address", post.getAddress());
                    i.putExtra("description", post.getDescription());
                    i.putExtra("phone", post.getPhoneNo());
                    i.putExtra("email", post.getEmail());
                    i.putExtra("lat", post.getLat());
                    i.putExtra("lon", post.getLon());
                    i.putExtra("type1", post.getType1());
                    i.putExtra("type2", post.getType2());

                    context.startActivity(i);
                }
                return false;
            }
        }

        else {
            return true;
        }
    }

//    private void gotoLocation(double lat, double lng, int i) {
//        // TODO Auto-generated method stub
//
//        Marker myMarker = map.addMarker(new MarkerOptions()
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.home))
//                .position(p1)
//                .snippet("Lat:" + lat + "Lng:" + lng)
//                .title("HOME"));
//
//        map = ((MapFragment) getFragmentManager()
//                .findFragmentById(R.id.map2)).getMap();
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), i));
//    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        autocompleteFragment = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

//        autocompleteFragment = null;
//        getActivity().getFragmentManager().beginTransaction()
//                .remove(autocompleteFragment)
//                .commitAllowingStateLoss();

        try {

            if (isAdded()) {

                //Log.e("res","place dlted");
                //getChildFragmentManager().beginTransaction().remove(autocompleteFragment).commitAllowingStateLoss();

                android.app.FragmentManager fragmentManager = getActivity().getFragmentManager();
                android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(autocompleteFragment);
                fragmentTransaction.commitAllowingStateLoss();

                autocompleteFragment = null;

                getChildFragmentManager().beginTransaction()
                        .remove(mapFragment)
                        .commitAllowingStateLoss();
                mapFragment = null;

                getChildFragmentManager().beginTransaction().remove(getChildFragmentManager().findFragmentById(R.id.mapFragment)).commitAllowingStateLoss();

            }


//            Fragment fragment = getChildFragmentManager()
//                    .findFragmentById(R.id.mapFragment);
//            if (null != fragment) {
//                getChildFragmentManager().beginTransaction()
//                        .remove(fragment)
//                        .commitAllowingStateLoss();
//            }

        }catch (Exception e) {
            //Log.e("mapSearch err",e.toString());
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
            //fusedLocationProviderClient.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

        Toast.makeText(context,"Connection suspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(context,"Connection failed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(context, data);
                //Log.i(TAG, "Place: " + place.getName());
                searchePlaceText.setText(place.getName());

                LatLng currentPos = place.getLatLng();
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPos,15),1000,null);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(context, data);
                Toast.makeText(context,"Error: "+status.getStatusMessage(),Toast.LENGTH_SHORT).show();
                // TODO: Handle the error.
                //Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

    }
}