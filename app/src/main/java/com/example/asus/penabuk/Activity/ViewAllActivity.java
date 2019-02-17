package com.example.asus.penabuk.Activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAllActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    Context context;
    RecyclerView rvViewAllActivity;
    ViewAllAdapter viewAllAdapter;
    List<Book> books;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        initView();
        doGetBook();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView(){
        context = this;
        rvViewAllActivity = (RecyclerView)findViewById(R.id.RvViewAllActivity);
        imgBack = (ImageView)findViewById(R.id.imgBack);
    }

    private void doGetBook(){
        Call<ReqBook> call = userService.getBookRequest();
        call.enqueue(new Callback<ReqBook>() {
            @Override
            public void onResponse(Call<ReqBook> call, Response<ReqBook> response) {
                ReqBook reqBook = response.body();
                books = reqBook.getBooks();
                viewAllAdapter = new ViewAllAdapter(books);

                rvViewAllActivity.setLayoutManager(new LinearLayoutManager(context));
                rvViewAllActivity.setItemAnimator(new DefaultItemAnimator());
                rvViewAllActivity.setAdapter(viewAllAdapter);
            }

            @Override
            public void onFailure(Call<ReqBook> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
