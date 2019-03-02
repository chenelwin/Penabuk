package com.example.asus.penabuk.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.asus.penabuk.Activity.PaymentDetailActivity;
import com.example.asus.penabuk.Adapter.CartFragmentAdapter;
import com.example.asus.penabuk.Model.Book;
import com.example.asus.penabuk.Model.Cart;
import com.example.asus.penabuk.Model.ReqCart;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    UserService userService = ApiUtils.getUserService();
    SharedPrefManager sharedPrefManager;
    public View view;
    CheckBox checkAll;
    Button btnBuy;
    RecyclerView rvCartFragment;
    CartFragmentAdapter cartFragmentAdapter;
    List<Cart> carts;
    List<Book> passingbuku;
    List<Integer> passingcartid;
    Integer userId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cart, container, false);
        initView();
        doGetCart(userId);

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PaymentDetailActivity.class);
                intent.putExtra("passingbook", (Serializable)passingbuku);
                intent.putExtra("passingcartid", (Serializable)passingcartid);
                startActivity(intent);
            }
        });

        /*
        checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(checkAll.isChecked()){
                    doCheckAll(carts);
                }
                else{
                    doUncheckAll(carts);
                }
            }
        });*/

        return view;
    }

    public void initView(){
        sharedPrefManager = new SharedPrefManager(view.getContext());
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        rvCartFragment = (RecyclerView)view.findViewById(R.id.RvCartFragment);
        checkAll = (CheckBox)view.findViewById(R.id.checkAll);
        btnBuy = (Button)view.findViewById(R.id.btnBuy);
        passingbuku = new ArrayList<>();
        passingcartid = new ArrayList<>();
    }

    public void doGetCart(Integer id){
        Call<ReqCart> call = userService.getCart(id);
        call.enqueue(new Callback<ReqCart>() {
            @Override
            public void onResponse(Call<ReqCart> call, Response<ReqCart> response) {
                ReqCart reqCart = response.body();
                carts = reqCart.getCarts();
                for(int i=0; i<carts.size(); i++){
                    Book book = carts.get(i).getBook();
                    passingbuku.add(book);
                    Integer cartid = carts.get(i).getCart_id();
                    passingcartid.add(cartid);
                    Log.e("cart", "ke-"+cartid);
                }

                cartFragmentAdapter = new CartFragmentAdapter(carts);

                rvCartFragment.setLayoutManager(new LinearLayoutManager(view.getContext()));
                rvCartFragment.setItemAnimator(new DefaultItemAnimator());
                rvCartFragment.setAdapter(cartFragmentAdapter);
                Log.e("selesai", "size" + passingbuku.size());
            }

            @Override
            public void onFailure(Call<ReqCart> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void doCheckAll(List<Cart> carts){
        for(int i=0; i<carts.size(); i++){
            carts.get(i).setSelected(true);
        }
    }

    public void doUncheckAll(List<Cart> carts){
        for(int i=0; i<carts.size(); i++){
            carts.get(i).setSelected(false);
        }
    }

}
