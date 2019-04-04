package com.example.asus.penabuk.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.asus.penabuk.Model.Book;
import com.example.asus.penabuk.Model.ReqFilter;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    //searchfilter
    public static List<Book> searchBooks = new ArrayList<>();
    public static String[] searchTitles;
    public static String[] searchAuthors;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread thread = new Thread(){
            @Override
            public void run(){
                try{

                    sleep(1000);
                    //progressDialog = ProgressDialog.show(SplashActivity.this, null, "Processing", true);
                    doGetSearchFilter();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };

        thread.start();
        progressDialog = ProgressDialog.show(SplashActivity.this, null, "Processing", true);
    }

    private void doGetSearchFilter(){

        Call<ReqFilter> call = userService.getSearchFilter();
        call.enqueue(new Callback<ReqFilter>() {
            @Override
            public void onResponse(Call<ReqFilter> call, Response<ReqFilter> response) {
                ReqFilter reqFilter = response.body();
                searchBooks = reqFilter.getFilters();
                searchTitles = new String[searchBooks.size()];
                List<String> tempAuthor = new ArrayList<>();
                for(int i=0; i<searchBooks.size(); i++){
                    searchTitles[i] = searchBooks.get(i).getOriginal_title();
                    //searchAuthors[i] = searchBooks.get(i).getAuthors();
                    tempAuthor.add(searchBooks.get(i).getAuthors());
                }
                Set<String> set = new HashSet<>(tempAuthor);
                tempAuthor.clear();
                tempAuthor.addAll(set);
                searchAuthors = tempAuthor.toArray(new String[tempAuthor.size()]);
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                progressDialog.dismiss();
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<ReqFilter> call, Throwable t) {
                Toast.makeText(SplashActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

}
