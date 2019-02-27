package com.example.asus.penabuk.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.asus.penabuk.Adapter.CartFragmentAdapter;
import com.example.asus.penabuk.Model.Cart;
import com.example.asus.penabuk.Model.ReqCart;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    UserService userService = ApiUtils.getUserService();
    SharedPrefManager sharedPrefManager;
    public View view;
    RecyclerView rvCartFragment;
    CartFragmentAdapter cartFragmentAdapter;
    List<Cart> carts;
    Integer userId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cart, container, false);
        initView();
        doGetCart(userId);

        return view;
    }

    public void initView(){
        sharedPrefManager = new SharedPrefManager(view.getContext());
        rvCartFragment = (RecyclerView)view.findViewById(R.id.RvCartFragment);
        userId = Integer.parseInt(sharedPrefManager.getSPId());
    }

    @Override
    public void onResume(){
        super.onResume();
        doGetCart(userId);
    }

    public void doGetCart(Integer id){
        Call<ReqCart> call = userService.getCart(id);
        call.enqueue(new Callback<ReqCart>() {
            @Override
            public void onResponse(Call<ReqCart> call, Response<ReqCart> response) {
                ReqCart reqCart = response.body();
                carts = reqCart.getCarts();
                cartFragmentAdapter = new CartFragmentAdapter(carts);

                rvCartFragment.setLayoutManager(new LinearLayoutManager(view.getContext()));
                rvCartFragment.setItemAnimator(new DefaultItemAnimator());
                rvCartFragment.setAdapter(cartFragmentAdapter);
            }

            @Override
            public void onFailure(Call<ReqCart> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
