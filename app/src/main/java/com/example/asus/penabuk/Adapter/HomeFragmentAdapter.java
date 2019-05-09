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

import com.example.asus.penabuk.Activity.TopUpActivity;
import com.example.asus.penabuk.Activity.ViewAllActivity;
import com.example.asus.penabuk.Activity.ViewDetailActivity;
import com.example.asus.penabuk.Fragment.HomeFragment;
import com.example.asus.penabuk.Model.Book;
import com.example.asus.penabuk.Model.ReqSlider;
import com.example.asus.penabuk.Model.ResUser;
import com.example.asus.penabuk.Model.Slider;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;
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
    SharedPrefManager sharedPrefManager;
    Integer userId;
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
        sharedPrefManager = new SharedPrefManager(context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mholder, int position) {
        if(mholder.getItemViewType()==LAYOUT_HEAD){
            ViewHolderHead holder = (ViewHolderHead)mholder;
            userId = Integer.parseInt(sharedPrefManager.getSPId());
            doGetUser(userId, holder.balance);
            holder.btnTopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), TopUpActivity.class);
                    view.getContext().startActivity(intent);
                }
            });

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
            holder.bookPrice.setText("Rp " + priceformat);

            if(book.getImage_url().length()>0) {
                Picasso.with(context)
                        .load(book.getImage_url())
                        .placeholder(R.drawable.emptyimage)
                        .error(R.drawable.emptyimage)
                        //.resize(150, 200)
                        //.centerCrop()
                        .into(holder.bookImg);
            }
            else{
                Picasso.with(context)
                        .load(ApiUtils.BASE_URL +"/image?id="+books.get(position).getImage_local())
                        .placeholder(R.drawable.emptyimage)
                        .error(R.drawable.emptyimage)
                        .into(holder.bookImg);
            }

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

    public class ViewHolderHead extends RecyclerView.ViewHolder{
        TextView balance;
        Button btnTopup;
        CarouselView carousel;
        TextView textLihatSemua;

        public ViewHolderHead(View itemView){
            super(itemView);
            balance = (TextView)itemView.findViewById(R.id.balance);
            btnTopup = (Button)itemView.findViewById(R.id.btnTopup);
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

    private void doGetSlider(final CarouselView carousel){
        Call<ReqSlider> call = userService.getSlider();
        call.enqueue(new Callback<ReqSlider>() {
            @Override
            public void onResponse(Call<ReqSlider> call, Response<ReqSlider> response) {
                ReqSlider reqSlider = response.body();
                sliders = reqSlider.getSliders();
                doGetSliderId(sliders, carousel);
            }

            @Override
            public void onFailure(Call<ReqSlider> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doGetSliderId(final List<Slider> sliderList, final CarouselView carousel){
        carousel.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                Picasso.with(context)
                        .load(ApiUtils.BASE_URL +"/image?id="+sliderList.get(position).getUrl())
                        .placeholder(R.drawable.emptyimage)
                        .error(R.drawable.emptyimage)
                        //.centerCrop()
                        //.resize(carousel.getWidth(), 300)
                        .into(imageView);
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

    private void doGetUser(Integer userId, final TextView balance){
        Call<ResUser> call = userService.getUser(userId);
        call.enqueue(new Callback<ResUser>() {
            @Override
            public void onResponse(Call<ResUser> call, Response<ResUser> response) {
                ResUser resUser = response.body();
                String updbalance = resUser.getUser().getBalance().toString();
                sharedPrefManager.saveSPString(SharedPrefManager.SP_BALANCE, updbalance);

                Integer tmpbalance = Integer.parseInt(sharedPrefManager.getSPBalance());
                DecimalFormat formatter = new DecimalFormat("#,###,###");
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setGroupingSeparator('.');
                formatter.setDecimalFormatSymbols(symbols);
                String priceformat = formatter.format(tmpbalance);
                balance.setText("Rp. " + priceformat);
            }

            @Override
            public void onFailure(Call<ResUser> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
