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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    UserService userService = ApiUtils.getUserService();
    SharedPrefManager sharedPrefManager;
    public View view;
    TextView balance;
    TextView textLihatsemua;
    RecyclerView rvHomeFragment;
    HomeFragmentAdapter homeFragmentAdapter;
    List<Book> books;
    Button btnTopup;
    Integer userId;

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
        balance = (TextView)view.findViewById(R.id.balance);
        balance.setText("Rp. "+sharedPrefManager.getSPBalance());
        textLihatsemua = (TextView)view.findViewById(R.id.textLihatsemua);
        rvHomeFragment = (RecyclerView)view.findViewById(R.id.RvHomeFragment);
        btnTopup = (Button)view.findViewById(R.id.btnTopup);
    }

    @Override
    public void onResume(){
        super.onResume();
        doGetUser(userId);
    }

    private void doGetBook(){
        Call<ReqBook> call = userService.getBookRequest(userId, 1);
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

    private void doGetUser(Integer id){
        Call<ResUser> call = userService.getUser(id);
        call.enqueue(new Callback<ResUser>() {
            @Override
            public void onResponse(Call<ResUser> call, Response<ResUser> response) {
                ResUser resUser = response.body();
                String updbalance = resUser.getUser().getBalance().toString();
                sharedPrefManager.saveSPString(SharedPrefManager.SP_BALANCE, updbalance);
                balance.setText("Rp. " + sharedPrefManager.getSPBalance());
            }

            @Override
            public void onFailure(Call<ResUser> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
