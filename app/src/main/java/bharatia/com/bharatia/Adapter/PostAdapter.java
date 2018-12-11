package bharatia.com.bharatia.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import bharatia.com.bharatia.DataModel.Post;
import bharatia.com.bharatia.PostDetailsActivity;
import bharatia.com.bharatia.R;


/**
 * Created by SAMSUNG on 9/16/2017.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder>{
    private ArrayList<Post> arrayList;
    private Context context;

    public PostAdapter(ArrayList<Post> arrayList, Context ctx) {
        this.arrayList = arrayList;
        this.context = ctx;
    }

    @Override
    public PostAdapter.PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_layout,parent,false);

        PostAdapter.PostHolder PostHolder = new PostAdapter.PostHolder(view);
        return PostHolder;
    }

    @Override
    public void onBindViewHolder(final PostHolder holder, final int position) {

        final Post post = arrayList.get(position);

        //holder.message.setText(Post.getMessage());
        //holder.messageSent.setText(Post.getMessage());

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        //int year = c.get(Calendar.YEAR);

        //System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());

        try {

            String[] date = post.getDate().split("-");

            int diff;

            if(year != Integer.parseInt(date[0])) {
                diff = year - Integer.parseInt(date[0]);
                holder.postTime.setText(diff+ context.getString(R.string.post_item_years_ago));
            }else {
                if(month+1 != Integer.parseInt(date[1])) {
                    diff = month+1 - Integer.parseInt(date[1]);
                    holder.postTime.setText(diff+context.getString(R.string.post_item_months_ago));
                }else {
                    if(day != Integer.parseInt(date[2])) {
                        diff = day - Integer.parseInt(date[2]);
                        if(diff>7) {
                            diff = diff%7;
                            holder.postTime.setText(diff + context.getString(R.string.post_item_weeks_ago));
                        }else{
                            holder.postTime.setText(diff + context.getString(R.string.post_item_days_ago));
                        }

                    }else {
                        holder.postTime.setText(R.string.post_item_posted_today);
                    }
                }
            }

        }catch (Exception e) {

            holder.postTime.setText(R.string.post_item_uknown_time);

        }


        holder.postCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+post.getPhoneNo()));
                context.startActivity(intent);

            }
        });

        holder.postMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto: "+post.getEmail()));
                context.startActivity(Intent.createChooser(emailIntent, "Choose email sender"));

            }
        });

        holder.postMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/dir/?api=1&destination="+post.getLat()+","+post.getLon()));
                context.startActivity(intent);

            }
        });


        holder.area.setText(post.getArea());
        holder.price.setText(context.getString(R.string.post_item_tk)+post.getPrice());
        holder.room.setText(post.getRoomNo()+context.getString(R.string.post_item_room));
        holder.size.setText(post.getSize()+context.getString(R.string.post_item_sqft));
        holder.postType.setText(post.getType2().toUpperCase());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.postImg.setClipToOutline(true);
        }

        if(post.getCoverPhoto()!=null) {


            Glide
                    .with(context)
                    .load(post.getCoverPhoto())
                    .into(holder.postImg);

        }

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,PostDetailsActivity.class);
                i.putExtra("postId",post.getPostID());
                i.putExtra("photoLink",post.getCoverPhoto());
                i.putExtra("area",post.getArea());
                i.putExtra("price",post.getPrice());
                i.putExtra("room",post.getRoomNo());
                i.putExtra("size",post.getSize());
                i.putExtra("address",post.getAddress());
                i.putExtra("description",post.getDescription());
                i.putExtra("phone",post.getPhoneNo());
                i.putExtra("email",post.getEmail());
                i.putExtra("lat",post.getLat());
                i.putExtra("lon",post.getLon());
                i.putExtra("type1",post.getType1());
                i.putExtra("type2",post.getType2());
                context.startActivity(i);
            }
        });

    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class PostHolder extends RecyclerView.ViewHolder {
        public TextView room,size,area,price,postType,postTime;
        public ImageView postImg,postCall,postMail,postMap;
        public RelativeLayout card;

        public PostHolder(final View itemView) {
            super(itemView);
            area = (TextView)itemView.findViewById(R.id.area);
            price = (TextView)itemView.findViewById(R.id.price);
            room = (TextView)itemView.findViewById(R.id.room);
            size = (TextView)itemView.findViewById(R.id.size);
            postType = (TextView)itemView.findViewById(R.id.postType);
            postTime = (TextView)itemView.findViewById(R.id.postTime);
            postImg = (ImageView) itemView.findViewById(R.id.postImg);
            card = (RelativeLayout) itemView.findViewById(R.id.postCard);
            postCall = itemView.findViewById(R.id.postCall);
            postMail =  itemView.findViewById(R.id.postMail);
            postMap =  itemView.findViewById(R.id.postMap);
        }


    }
}
