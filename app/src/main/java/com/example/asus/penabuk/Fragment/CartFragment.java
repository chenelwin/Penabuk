package com.example.asus.penabuk.Fragment;

import android.app.ProgressDialog;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.asus.penabuk.Activity.PaymentDetailActivity;
import com.example.asus.penabuk.Activity.ViewAllActivity;
import com.example.asus.penabuk.Adapter.CartFragmentAdapter;
import com.example.asus.penabuk.Model.Book;
import com.example.asus.penabuk.Model.Cart;
import com.example.asus.penabuk.Model.ReqCart;
import com.example.asus.penabuk.Model.ResMessage;
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

public class CartFragment extends Fragment implements CartFragmentAdapter.PassingBtnRemove {

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
    List<Integer> passingcount;
    LinearLayout layoutCart;
    LinearLayout layoutNoCart;
    Button btnNoCart;
    Integer userId;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cart, container, false);
        initView();
        doGetCart(userId);

        /*
        checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    cartFragmentAdapter.selectAll();
                }
                else if(!b){
                    cartFragmentAdapter.deselectAll();
                }
            }
        });*/

        btnNoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ViewAllActivity.class);
                startActivity(intent);
            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initCount(carts);
                if(passingcount.size()>0) {

                    Intent intent = new Intent(view.getContext(), PaymentDetailActivity.class);
                    intent.putExtra("passingbook", (Serializable) passingbuku);
                    intent.putExtra("passingcartid", (Serializable) passingcartid);
                    intent.putExtra("passingcount", (Serializable) passingcount);
                    startActivity(intent);
                }
                else if(passingcount.size()==0){
                    Toast.makeText(view.getContext(), "Pilih buku yang ingin dibeli", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void passData(Integer cart_id, int position){
        progressDialog = ProgressDialog.show(view.getContext(), null, "Please Wait..", true);
        doRemoveCart(cart_id, userId);
    }


    private void initCount(List<Cart> carts){
        passingcount = new ArrayList<>();
        passingbuku = new ArrayList<>();
        passingcartid = new ArrayList<>();
        for(int i=0; i<carts.size(); i++){
            if(carts.get(i).isSelected()) {
                passingcount.add(carts.get(i).getCount());
                passingbuku.add(carts.get(i).getBook());
                passingcartid.add(carts.get(i).getCart_id());
            }
        }
    }

    public void initView(){
        sharedPrefManager = new SharedPrefManager(view.getContext());
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        CartFragmentAdapter.passingBtnRemove = this;
        rvCartFragment = (RecyclerView)view.findViewById(R.id.RvCartFragment);
        checkAll = (CheckBox)view.findViewById(R.id.checkAll);
        btnBuy = (Button)view.findViewById(R.id.btnBuy);
        //passingbuku = new ArrayList<>();
        //passingcartid = new ArrayList<>();
        layoutCart = (LinearLayout)view.findViewById(R.id.layoutCart);
        layoutNoCart = (LinearLayout)view.findViewById(R.id.layoutNoCart);
        btnNoCart = (Button)view.findViewById(R.id.btnNoCart);
    }

    public void doGetCart(Integer id){
        Call<ReqCart> call = userService.getCart(id);
        call.enqueue(new Callback<ReqCart>() {
            @Override
            public void onResponse(Call<ReqCart> call, Response<ReqCart> response) {
                ReqCart reqCart = response.body();
                carts = reqCart.getCarts();
                /*
                for(int i=0; i<carts.size(); i++){
                    Book book = carts.get(i).getBook();
                    passingbuku.add(book);
                    Integer cartid = carts.get(i).getCart_id();
                    passingcartid.add(cartid);
                }*/
                checkCartSize(carts);

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

    private void doRemoveCart(Integer cartId, Integer userId){
        Call<ResMessage> call = userService.removeCartRequest(cartId, userId);
        call.enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                ResMessage resMessage = response.body();
                Toast.makeText(view.getContext(), resMessage.getMessage(), Toast.LENGTH_SHORT).show();
                checkCartSize(carts);

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResMessage> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void checkCartSize(List<Cart> carts){
        if(carts.size()==0){
            layoutCart.setVisibility(View.GONE);
            layoutNoCart.setVisibility(View.VISIBLE);
        }
        else if(carts.size()>0){
            layoutCart.setVisibility(View.VISIBLE);
            layoutNoCart.setVisibility(View.GONE);
        }
    }



}
