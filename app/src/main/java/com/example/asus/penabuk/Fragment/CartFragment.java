package com.example.asus.penabuk.Fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.penabuk.Activity.PaymentDetailActivity;
import com.example.asus.penabuk.Activity.ViewAllActivity;
import com.example.asus.penabuk.Adapter.CartFragmentAdapter;
import com.example.asus.penabuk.Model.Book;
import com.example.asus.penabuk.Model.Cart;
import com.example.asus.penabuk.Model.Order;
import com.example.asus.penabuk.Model.ReqCart;
import com.example.asus.penabuk.Model.ResMessage;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment implements CartFragmentAdapter.PassingBtnRemoveCart, CartFragmentAdapter.TotalHarga, CartFragmentAdapter.PassingCountCart
{

    UserService userService = ApiUtils.getUserService();
    SharedPrefManager sharedPrefManager;
    public View view;
    //SwipeRefreshLayout refreshCart;
    CheckBox checkAll;
    TextView cartTotalPrice;
    Button btnBuy;
    RecyclerView rvCartFragment;
    CartFragmentAdapter cartFragmentAdapter;
    List<Cart> carts;
    List<Book> passingbuku;
    List<Integer> passingcartid;
    List<Integer> passingcount;
    List<Integer> passingmerchantid;
    LinearLayout layoutCart;
    LinearLayout layoutNoCart;
    Button btnNoCart;
    Integer userId;
    ProgressDialog progressDialog;
    boolean triggerUncheckAll=true;

    Toolbar toolbarCart;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cart, container, false);
        initView();
        doGetCart(userId);


        checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    cartFragmentAdapter.selectAll();
                    triggerUncheckAll = true;
                    //passTotalHarga(carts);
                }
                else if(!b){
                    if(triggerUncheckAll) {
                        cartFragmentAdapter.deselectAll();
                    }
                    //passTotalHarga(carts);
                }
            }
        });

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
                    intent.putExtra("passingmerchantid", (Serializable) passingmerchantid);
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
    public void passData(final Integer cart_id, final int position){
        progressDialog = ProgressDialog.show(view.getContext(), null, "Please Wait..", true);
        doRemoveCart(cart_id, userId, position);
    }

    @Override
    public void passCount(Integer book_id, Integer count, int position){
        doSaveQty(book_id, count);
    }

    @Override
    public void passTotalHarga(List<Cart> cartChecked){
        Integer count=0;
        Integer currentPrice = 0;
        for(int i=0; i<carts.size(); i++){
            if(cartChecked.get(i).isSelected()) {
                currentPrice += (cartChecked.get(i).getCount() * cartChecked.get(i).getBook().getPrice());
                count++;
            }
        }

        if(count==carts.size()){
            checkAll.setChecked(true);
        }
        else{
            triggerUncheckAll = false;
            checkAll.setChecked(false);
        }

        DecimalFormat formatter = new DecimalFormat("#,###,###");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        String priceformat = formatter.format(currentPrice);
        cartTotalPrice.setText("Rp. " + priceformat);

    }


    private void initCount(List<Cart> carts){
        passingcount = new ArrayList<>();
        passingbuku = new ArrayList<>();
        passingcartid = new ArrayList<>();
        passingmerchantid = new ArrayList<>();
        for(int i=0; i<carts.size(); i++){
            if(carts.get(i).isSelected()) {
                passingcount.add(carts.get(i).getCount());
                passingbuku.add(carts.get(i).getBook());
                passingcartid.add(carts.get(i).getCart_id());
                passingmerchantid.add(carts.get(i).getBook().getMerchant_id());
            }
        }
        Set<Integer> set = new HashSet<>(passingmerchantid);
        passingmerchantid.clear();
        passingmerchantid.addAll(set);
    }

    public void initView(){
        sharedPrefManager = new SharedPrefManager(view.getContext());
        initToolbar();
        //initRefreshCart();
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        CartFragmentAdapter.passingBtnRemoveCart = this;
        CartFragmentAdapter.totalHarga = this;
        CartFragmentAdapter.passingCountCart = this;
        rvCartFragment = (RecyclerView)view.findViewById(R.id.RvCartFragment);
        checkAll = (CheckBox)view.findViewById(R.id.checkAll);
        btnBuy = (Button)view.findViewById(R.id.btnBuy);
        cartTotalPrice = (TextView)view.findViewById(R.id.cartTotalPrice);
        layoutCart = (LinearLayout)view.findViewById(R.id.layoutCart);
        layoutNoCart = (LinearLayout)view.findViewById(R.id.layoutNoCart);
        btnNoCart = (Button)view.findViewById(R.id.btnNoCart);
    }

    private void initToolbar(){
        toolbarCart = (Toolbar)view.findViewById(R.id.toolbarCart);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbarCart);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Keranjang");
    }
    /*
    private void initRefreshCart(){
        refreshCart = (SwipeRefreshLayout)view.findViewById(R.id.refreshCart);

        refreshCart.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshCart.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }*/

    private void doSaveQty(Integer book_id, Integer count){
        Order order = new Order();
        order.setBook_id(book_id);
        order.setCount(count);
        Call<ResMessage> call = userService.addCartRequest(order, userId);
        call.enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {

            }

            @Override
            public void onFailure(Call<ResMessage> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    private void doRemoveCart(Integer cartId, Integer userId, final int position){
        Call<ResMessage> call = userService.removeCartRequest(cartId, userId);
        call.enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                ResMessage resMessage = response.body();
                Toast.makeText(view.getContext(), resMessage.getMessage(), Toast.LENGTH_SHORT).show();
                cartFragmentAdapter.notifyItemRemoved(position);
                cartFragmentAdapter.notifyItemRangeChanged(position, carts.size());
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
