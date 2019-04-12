package com.example.asus.penabuk.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.penabuk.Activity.ViewAllActivity;
import com.example.asus.penabuk.Activity.ViewDetailActivity;
import com.example.asus.penabuk.Fragment.HomeFragment;
import com.example.asus.penabuk.Model.Book;
import com.example.asus.penabuk.Model.ReqSlider;
import com.example.asus.penabuk.Model.Slider;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    UserService userService = ApiUtils.getUserService();
    List<Book> books;
    Context context;
    public static final int LAYOUT_HEAD = 1;
    public static final int LAYOUT_LIST = 0;

    //carousel
    List<Slider> sliders;

    public HomeFragmentAdapter(List<Book> bookList){ this.books = bookList;}

    @Override
    public int getItemViewType(int position) {
        if(position==0){return LAYOUT_HEAD;}
        else {return LAYOUT_LIST;}
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        if(viewType==LAYOUT_HEAD){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_home_header, parent, false);
            viewHolder = new ViewHolderHead(view);
        }
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_home_fragment, parent, false);
            viewHolder = new ViewHolderList(view);
        }
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mholder, int position) {
        if(mholder.getItemViewType()==LAYOUT_HEAD){
            ViewHolderHead holder = (ViewHolderHead)mholder;

            holder.textLihatSemua.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ViewAllActivity.class);
                    view.getContext().startActivity(intent);
                }
            });

            //carousel
            doGetSlider(holder.carousel);
        }

        else {
            ViewHolderList holder = (ViewHolderList)mholder;
            final Book book = books.get(holder.getAdapterPosition());
            holder.bookTitle.setText(book.getOriginal_title());
            DecimalFormat formatter = new DecimalFormat("#,###,###");
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator('.');
            formatter.setDecimalFormatSymbols(symbols);
            String priceformat = formatter.format(book.getPrice());
            holder.bookPrice.setText("Rp. " + priceformat);

            Picasso.with(context)
                    .load(book.getImage_url())
                    .resize(90, 130)
                    .centerCrop()
                    .into(holder.bookImg);

            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ViewDetailActivity.class);
                    intent.putExtra("bookid", book.getId());
                    view.getContext().startActivity(intent);
                }
            });
        }
    }



    @Override
    public int getItemCount() {
        return books.size();
    }
    /*
    @Override
    public HomeFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        HomeFragmentAdapter.ViewHolder viewHolder = null;
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_home_fragment, parent, false);
        //HomeFragmentAdapter.ViewHolder viewHolder = new ViewHolder(view);
        //context = parent.getContext();
        if(viewType==LAYOUT_HEAD){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_home_header, parent, false);
            viewHolder = new ViewHolderHead(view);
        }
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_home_fragment, parent, false);
            viewHolder = new ViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HomeFragmentAdapter.ViewHolder holder, int position) {
        if(holder.getItemViewType()==LAYOUT_HEAD){

        }
        else {
            ViewHolderList holderList = (ViewHolderList)holder;
            final Book book = books.get(holder.getAdapterPosition());
            holder.bookTitle.setText(book.getOriginal_title());
            DecimalFormat formatter = new DecimalFormat("#,###,###");
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator('.');
            formatter.setDecimalFormatSymbols(symbols);
            String priceformat = formatter.format(book.getPrice());
            holder.bookPrice.setText("Rp. " + priceformat);

            Picasso.with(context)
                    .load(book.getImage_url())
                    .resize(80, 120)
                    .centerCrop()
                    .into(holder.bookImg);

            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ViewDetailActivity.class);
                    intent.putExtra("bookid", book.getId());
                    view.getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
    */

    public class ViewHolderHead extends RecyclerView.ViewHolder{
        CarouselView carousel;
        TextView textLihatSemua;

        public ViewHolderHead(View itemView){
            super(itemView);
            carousel = (CarouselView)itemView.findViewById(R.id.carousel);
            textLihatSemua = (TextView)itemView.findViewById(R.id.textLihatsemua);
        }
    }

    public class ViewHolderList extends RecyclerView.ViewHolder{
        ImageView bookImg;
        TextView bookTitle;
        TextView bookPrice;
        CardView cv;

        public ViewHolderList(View itemView){
            super(itemView);
            bookImg = (ImageView)itemView.findViewById(R.id.bookImg);
            bookTitle = (TextView)itemView.findViewById(R.id.bookTitle);
            bookPrice = (TextView)itemView.findViewById(R.id.bookPrice);
            cv = (CardView)itemView.findViewById(R.id.cvHomeFragment);
        }
    }
/*
    private void initCarousel(CarouselView carousel){
        doGetSlider(carousel);
        //final int[] images = new int[]{ R.drawable.slider1, R.drawable.slider2, R.drawable.slider3, R.drawable.slider4};
        //final String[] imagetitle = new String[] { "Pic1", "Pic2", "Pic3", "Pic4" };
        carousel.setPageCount(images.length);
        carousel.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(images[position]);
            }
        });

        carousel.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(context, imagetitle[position], Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    private void doGetSlider(final CarouselView carousel){
        Call<ReqSlider> call = userService.getSlider();
        call.enqueue(new Callback<ReqSlider>() {
            @Override
            public void onResponse(Call<ReqSlider> call, Response<ReqSlider> response) {
                ReqSlider reqSlider = response.body();
                sliders = reqSlider.getSliders();
                //ImageView[] imageViews = new ImageView[sliders.size()];
                doGetSliderId(sliders, carousel);
            }

            @Override
            public void onFailure(Call<ReqSlider> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doGetSliderId(final List<Slider> sliderList, CarouselView carousel){
        final Bitmap[] bitmaps = new Bitmap[sliderList.size()];
        for(int i=0; i<sliderList.size(); i++){
            ImageView iv = new ImageView(context);

            Picasso.with(context)
                    .load(ApiUtils.BASE_URL +"/image?id="+sliderList.get(i).getUrl())
                    .centerCrop()
                    .resize(100, 100)
                    .into(iv);
            //iv.setImageResource(R.drawable.ic_launcher_background);
            iv.buildDrawingCache();
            bitmaps[i] = iv.getDrawingCache();
            iv.destroyDrawingCache();
        }

        carousel.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageBitmap(bitmaps[position]);
            }
        });
        carousel.setPageCount(sliderList.size());
        carousel.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(context, sliderList.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
