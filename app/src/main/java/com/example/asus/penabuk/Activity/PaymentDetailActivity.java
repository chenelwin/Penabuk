package com.example.asus.penabuk.Activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.asus.penabuk.Adapter.PaymentDetailAdapter;
import com.example.asus.penabuk.Model.Address;
import com.example.asus.penabuk.Model.Book;
import com.example.asus.penabuk.Model.ReqAddress;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentDetailActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    Context context;
    SharedPrefManager sharedPrefManager;
    Spinner spinnerAlamat;
    ArrayAdapter<Address> spinnerAlamatAdapter;
    List<Address> addresses;
    Integer userId;
    RecyclerView rvPaymentDetailActivity;
    PaymentDetailAdapter paymentDetailAdapter;
    List<Book> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);
        initView();
        doGetAddress(userId);
    }

    private void initView(){
        context = this;
        sharedPrefManager = new SharedPrefManager(context);
        spinnerAlamat = (Spinner)findViewById(R.id.spinnerAlamat);
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        rvPaymentDetailActivity = (RecyclerView)findViewById(R.id.RvPaymentDetail);

        books = new ArrayList<Book>();
        books = (List<Book>)getIntent().getSerializableExtra("passingbook");
        paymentDetailAdapter = new PaymentDetailAdapter(books);
        rvPaymentDetailActivity.setLayoutManager(new LinearLayoutManager(context));
        rvPaymentDetailActivity.setItemAnimator(new DefaultItemAnimator());
        rvPaymentDetailActivity.setAdapter(paymentDetailAdapter);
    }

    private void doGetAddress(Integer userId){
        Call<ReqAddress> call = userService.getAddress(userId);
        call.enqueue(new Callback<ReqAddress>() {
            @Override
            public void onResponse(Call<ReqAddress> call, Response<ReqAddress> response) {
                ReqAddress reqAddress = response.body();
                addresses = reqAddress.getAddresses();

                spinnerAlamatAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, addresses);
                spinnerAlamatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerAlamat.setAdapter(spinnerAlamatAdapter);
            }

            @Override
            public void onFailure(Call<ReqAddress> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
