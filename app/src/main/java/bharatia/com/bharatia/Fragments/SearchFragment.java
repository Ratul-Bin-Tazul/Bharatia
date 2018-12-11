package bharatia.com.bharatia.Fragments;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.xw.repo.BubbleSeekBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bharatia.com.bharatia.Adapter.PostAdapter;
import bharatia.com.bharatia.DataModel.Post;
import bharatia.com.bharatia.R;
import bharatia.com.bharatia.SearchActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    // Instantiate the RequestQueue.
    RequestQueue queue;
    String url ="http://sadmanamin.com/android_connect/search1.php";

    Gson gson;

    private RecyclerView postRecycleView;
    private RecyclerView.Adapter postAdapter;
    private RecyclerView.LayoutManager postLayoutManager;

    private ArrayList<Post> postArrayList = new ArrayList<>();

    ProgressBar progressBar;

    EditText area,priceMin,priceMax,sizeMin,sizeMax;
    Button search,searchAgain,back;

    FrameLayout hotel, sublet, flat;
    FrameLayout rent, sale;

    ImageButton hotelBtn, subletBtn, flatBtn;
    ImageButton rentBtn,saleBtn;

    BubbleSeekBar seekBar;

    Button roomAny,room1,room2,room3,room4,room5;
    EditText room;

    LinearLayout searchFormLayout,resultLayout;

    LinearLayout roomsLayout;

    String postType = "";
    String saleOrRent = "";
    String rooms = "";

    TextView resultCount;
    TextView selectedRoomsText;

    Context context;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_search, container, false);

        setHasOptionsMenu(true);

        hotel = (FrameLayout) v.findViewById(R.id.searchHotel);
        sublet = (FrameLayout) v.findViewById(R.id.searchSublet);
        flat = (FrameLayout) v.findViewById(R.id.searchFlat);

        hotelBtn = (ImageButton) v.findViewById(R.id.searchHotelButton);
        subletBtn = (ImageButton) v.findViewById(R.id.searchSubletButton);
        flatBtn = (ImageButton) v.findViewById(R.id.searchFlatButton);

        rent = (FrameLayout) v.findViewById(R.id.searchRent);
        sale = (FrameLayout) v.findViewById(R.id.searchSale);

        rentBtn = (ImageButton) v.findViewById(R.id.searchRentButton);
        saleBtn = (ImageButton) v.findViewById(R.id.searchSaleButton);

        seekBar = (BubbleSeekBar) v.findViewById(R.id.searchRoomSeekbar);

        selectedRoomsText = (TextView) v.findViewById(R.id.searchRoomsSelectedText);


        area = (EditText)v.findViewById(R.id.searchArea);
        priceMin = (EditText)v.findViewById(R.id.searchPriceLower);
        priceMax = (EditText)v.findViewById(R.id.searchPriceUpper);
        sizeMin = (EditText)v.findViewById(R.id.searchSizeLower);
        sizeMax = (EditText)v.findViewById(R.id.searchSizeUpper);

//        roomAny = (Button) v.findViewById(R.id.searchRoomAny);
//        room1 = (Button) v.findViewById(R.id.searchRoom1);
//        room2 = (Button) v.findViewById(R.id.searchRoom2);
//        room3 = (Button) v.findViewById(R.id.searchRoom3);
//        room4 = (Button) v.findViewById(R.id.searchRoom4);
//        room5 = (Button) v.findViewById(R.id.searchRoom5);
//
//        room = (EditText)v.findViewById(R.id.searchRooms);

        search = (Button) v.findViewById(R.id.searchBtn);
        searchAgain = (Button) v.findViewById(R.id.searchAgainBtn);
        back = (Button) v.findViewById(R.id.searchBackBtn);

        progressBar = new ProgressBar(getContext());

        searchFormLayout = (LinearLayout) v.findViewById(R.id.searchFormLayout);
        resultLayout = (LinearLayout) v.findViewById(R.id.searchResultLayout);

        roomsLayout = (LinearLayout) v.findViewById(R.id.searchRoomButtonLayout);

        resultCount = (TextView) v.findViewById(R.id.searchCountText);


        seekBar.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
            @NonNull
            @Override
            public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                array.clear();
                array.put(0, "Any");
                array.put(1, "1");
                array.put(2, "2");
                array.put(3, "3");
                array.put(4, "4");
                array.put(5, "5");
                array.put(6, "6");
                array.put(7, "7");

                return array;
            }
        });

        seekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                if(progress==0) {
                    rooms = "";
                    selectedRoomsText.setText("Selected: Any");
                }
                else {
                    rooms = progress + "";
                    selectedRoomsText.setText("Selected: "+rooms);
                }
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchFormLayout.setVisibility(View.GONE);
                resultLayout.setVisibility(View.VISIBLE);
                progressBar.setIndeterminate(true);
                progressBar.setVisibility(View.VISIBLE);

                searchPosts(saleOrRent,postType,area.getText().toString(),rooms,
                        priceMin.getText().toString(),priceMax.getText().toString(),sizeMin.getText().toString(),sizeMax.getText().toString());
            }
        });

        searchAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postArrayList.clear();
                resultLayout.setVisibility(View.GONE);
                searchFormLayout.setVisibility(View.VISIBLE);
                postArrayList.clear();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postArrayList.clear();
                resultLayout.setVisibility(View.GONE);
                searchFormLayout.setVisibility(View.VISIBLE);
                postArrayList.clear();

            }
        });

//        //Default selected buttons
//        //flat.setBackground(getResources().getDrawable(R.drawable.button_dark_style));
//        rent.setBackground(getResources().getDrawable(R.drawable.button_left_round_dark));
//        //room1.setBackground(getResources().getDrawable(R.drawable.square_button_dark_style));


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


        postRecycleView = (RecyclerView)v.findViewById(R.id.searchRecyclerview);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        postRecycleView.setHasFixedSize(true);


        postArrayList = new ArrayList<>();

        // use a linear layout manager
        postLayoutManager = new LinearLayoutManager(getContext());
        postRecycleView.setLayoutManager(postLayoutManager);

        postAdapter = new PostAdapter(postArrayList,getContext());
        postRecycleView.setAdapter(postAdapter);

        postRecycleView.setNestedScrollingEnabled(false);

        queue = Volley.newRequestQueue(getContext());

        gson = new Gson();
        //Post post = gson.fromJson()


        postAdapter.notifyDataSetChanged();
        postRecycleView.swapAdapter(postAdapter,false);


        return v;
    }

    private void searchPosts(String type1,String type2,String area,String rooms,String minPrice,String maxPrice,String minSize,String maxSize) {

        if(!maxPrice.isEmpty() && minPrice.isEmpty())
            minPrice = "1";
        if(!maxSize.isEmpty() && minSize.isEmpty())
            minSize = "1";
        String curatedUrl = url+"?Type1="+type1+"&Type2="+type2+"&Room_No="+rooms+"&Area="+area
                +"&Price1="+minPrice+"&Price2="+maxPrice+"&Size1="+minSize
                +"&Size2="+maxSize;
        Log.e("res",curatedUrl);

        // Request a string response from the provided URL.
        final StringRequest searchRequest = new StringRequest(Request.Method.GET, curatedUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("res",response);

                        progressBar.setVisibility(View.GONE);
                        try {
                            postArrayList.clear();

                            Post[] posts = gson.fromJson(response, Post[].class);
                            resultCount.setText(posts.length+" results found matching your requirment");
                            Toast.makeText(getContext(),posts.length+" posts found",Toast.LENGTH_SHORT).show();
                            for(int i=0;i<posts.length;i++){
                                postArrayList.add(posts[i]);
                                postAdapter.notifyItemInserted(i);
                                postAdapter.notifyDataSetChanged();
                                postRecycleView.swapAdapter(postAdapter,false);
                            }

                        }catch (Exception e) {
                            Log.e("res",response);
                            e.printStackTrace();
                            Toast.makeText(getContext(),"Oops! Something went wrong...",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                //Toast.makeText(getContext(),"Oops! Something went wrong...",Toast.LENGTH_SHORT).show();

            }
        });
//        {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Rooms", rooms.getText().toString());
//                params.put("Area", area.getText().toString());
//                params.put("Price1", lowerPrice.getText().toString());
//                params.put("Price2", upperPrice.getText().toString());
//                return params;
//            }
//        };

        // Add the request to the RequestQueue.
        queue.add(searchRequest);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.search_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_map :
                getFragmentManager().beginTransaction().replace(R.id.map_container,new MapSearchFragment()).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
