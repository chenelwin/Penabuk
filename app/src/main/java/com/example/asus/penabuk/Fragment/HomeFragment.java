package com.example.asus.penabuk.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.penabuk.Activity.TopUpActivity;
import com.example.asus.penabuk.Activity.ViewAllActivity;
import com.example.asus.penabuk.Adapter.HomeFragmentAdapter;
import com.example.asus.penabuk.Adapter.ViewAllAdapter;
import com.example.asus.penabuk.Model.Book;
import com.example.asus.penabuk.Model.ReqBook;
import com.example.asus.penabuk.Model.ResUser;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    UserService userService = ApiUtils.getUserService();
    SharedPrefManager sharedPrefManager;
    public View view;
    CarouselView carousel;
    TextView balance;
    TextView textLihatsemua;
    RecyclerView rvHomeFragment;
    HomeFragmentAdapter homeFragmentAdapter;
    GridLayoutManager rvManager;
    List<Book> books;
    Button btnTopup;
    Integer userId;

    //Carousel
    int[] images;
    String[] imagetitle;

    //EndlessScroll
    private int previousTotal = 0;
    private boolean loading = true;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    ProgressBar loadingNext;
    int page=1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initView();
        initCarouselItem();
        doGetBook(userId, page);

        textLihatsemua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ViewAllActivity.class);
                startActivity(intent);
            }
        });

        btnTopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TopUpActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void initView(){
        sharedPrefManager = new SharedPrefManager(view.getContext());
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        carousel = (CarouselView)view.findViewById(R.id.carousel);
        balance = (TextView)view.findViewById(R.id.balance);
        textLihatsemua = (TextView)view.findViewById(R.id.textLihatsemua);
        rvHomeFragment = (RecyclerView)view.findViewById(R.id.RvHomeFragment);
        loadingNext = (ProgressBar)view.findViewById(R.id.loadingNext);
        btnTopup = (Button)view.findViewById(R.id.btnTopup);
    }

    private void initCarouselItem(){
        images = new int[]{ R.drawable.ikonmic, R.drawable.ikonsearch, R.drawable.ic_home_black_24dp, R.drawable.ic_favorite_black_24dp};
        imagetitle = new String[] { "Mic", "Search", "Home", "Fav" };
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
                Toast.makeText(view.getContext(), imagetitle[position].toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        doGetUser(userId);
    }

    private void doGetBook(Integer id, Integer page){
        Call<ReqBook> call = userService.getBookRequest(id, page);
        call.enqueue(new Callback<ReqBook>() {
            @Override
            public void onResponse(Call<ReqBook> call, Response<ReqBook> response) {
                ReqBook reqBook = response.body();
                books = reqBook.getBooks();
                homeFragmentAdapter = new HomeFragmentAdapter(books);

                rvManager = new GridLayoutManager(view.getContext(), 2);
                rvHomeFragment.setLayoutManager(rvManager);
                rvHomeFragment.setItemAnimator(new DefaultItemAnimator());
                rvHomeFragment.setAdapter(homeFragmentAdapter);
                endlessScroll();
            }

            @Override
            public void onFailure(Call<ReqBook> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doGetNextPage(Integer id, Integer page){
        loadingNext.setVisibility(View.VISIBLE);
        Call<ReqBook> call = userService.getBookRequest(id, page);
        call.enqueue(new Callback<ReqBook>() {
            @Override
            public void onResponse(Call<ReqBook> call, Response<ReqBook> response) {
                ReqBook reqBook = response.body();
                List<Book> nextBook = reqBook.getBooks();
                for(int i=0; i<nextBook.size(); i++){
                    books.add(nextBook.get(i));
                }
                homeFragmentAdapter.notifyDataSetChanged();
                loadingNext.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ReqBook> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                loadingNext.setVisibility(View.GONE);
            }
        });
    }

    private void doGetUser(Integer id){
        Call<ResUser> call = userService.getUser(id);
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
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void endlessScroll(){
        rvHomeFragment.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = rvHomeFragment.getChildCount();
                totalItemCount = rvManager.getItemCount();
                firstVisibleItem = rvManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if(!loading && (visibleItemCount+firstVisibleItem)==totalItemCount){
                    // End has been reached
                    page++;
                    doGetNextPage(userId, page);

                    loading = true;
                }
            }
        });
    }

}
