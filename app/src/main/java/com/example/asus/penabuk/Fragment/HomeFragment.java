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
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.penabuk.Activity.TopUpActivity;
import com.example.asus.penabuk.Activity.ViewAllActivity;
import com.example.asus.penabuk.Adapter.HomeFragmentAdapter;
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

public class HomeFragment extends Fragment {

    UserService userService = ApiUtils.getUserService();
    public View view;
    TextView textLihatsemua;
    RecyclerView rvHomeFragment;
    HomeFragmentAdapter homeFragmentAdapter;
    List<Book> books;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initView();
        doGetBook();

        textLihatsemua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ViewAllActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void initView(){
        textLihatsemua = (TextView)view.findViewById(R.id.textLihatsemua);
        rvHomeFragment = (RecyclerView)view.findViewById(R.id.RvHomeFragment);
    }

    private void doGetBook(){
        Call<ReqBook> call = userService.getBookRequest();
        call.enqueue(new Callback<ReqBook>() {
            @Override
            public void onResponse(Call<ReqBook> call, Response<ReqBook> response) {
                ReqBook reqBook = response.body();
                books = reqBook.getBooks();
                homeFragmentAdapter = new HomeFragmentAdapter(books);

                rvHomeFragment.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
                rvHomeFragment.setItemAnimator(new DefaultItemAnimator());
                rvHomeFragment.setAdapter(homeFragmentAdapter);
            }

            @Override
            public void onFailure(Call<ReqBook> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
