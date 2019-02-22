package com.example.asus.penabuk.Activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.asus.penabuk.Adapter.ViewAllAdapter;
import com.example.asus.penabuk.Model.Book;
import com.example.asus.penabuk.Model.ReqBook;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAllActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    SharedPrefManager sharedPrefManager;
    Context context;
    RecyclerView rvViewAllActivity;
    LinearLayoutManager rvManager;
    ViewAllAdapter viewAllAdapter;
    List<Book> books;
    ImageView imgBack;
    Integer userId;

    //EndlessScroll
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    int page=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        initView();
        doGetBook(userId, page);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView(){
        context = this;
        sharedPrefManager = new SharedPrefManager(context);
        rvViewAllActivity = (RecyclerView)findViewById(R.id.RvViewAllActivity);
        imgBack = (ImageView)findViewById(R.id.imgBack);
        userId = Integer.parseInt(sharedPrefManager.getSPId());
    }

    private void doGetBook(Integer id, Integer page){
        Call<ReqBook> call = userService.getBookRequest(id, page);
        call.enqueue(new Callback<ReqBook>() {
            @Override
            public void onResponse(Call<ReqBook> call, Response<ReqBook> response) {
                ReqBook reqBook = response.body();
                books = reqBook.getBooks();
                viewAllAdapter = new ViewAllAdapter(books);

                rvManager = new LinearLayoutManager(context);
                rvViewAllActivity.setLayoutManager(rvManager);
                rvViewAllActivity.setItemAnimator(new DefaultItemAnimator());
                rvViewAllActivity.setAdapter(viewAllAdapter);
                endlessScroll();
            }

            @Override
            public void onFailure(Call<ReqBook> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void endlessScroll(){
        rvViewAllActivity.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = rvViewAllActivity.getChildCount();
                totalItemCount = rvManager.getItemCount();
                firstVisibleItem = rvManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                /*
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {*/
                if(!loading && (visibleItemCount+firstVisibleItem)==totalItemCount){
                    // End has been reached

                    Log.e("Yaeye!", "end called");

                    page++;
                    doGetBook(userId, page);

                    loading = true;
                }
            }
        });
    }
}
