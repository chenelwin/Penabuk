package com.example.asus.penabuk.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.asus.penabuk.Adapter.AddressAdapter;
import com.example.asus.penabuk.Model.Address;
import com.example.asus.penabuk.Model.ReqAddress;
import com.example.asus.penabuk.Model.ResMessage;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressActivity extends AppCompatActivity implements AddressAdapter.PassingBtnRemove {

    Context context;
    SharedPrefManager sharedPrefManager;
    UserService userService = ApiUtils.getUserService();
    ProgressDialog progressDialog;
    ImageView imgBack;
    Button btnAddAddress;
    RecyclerView rvAddress;
    AddressAdapter addressAdapter;
    List<Address> addresses;
    Integer userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        initView();
        doGetAddress(userId);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddressActivity.this, AddAddressActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        doGetAddress(userId);
    }

    @Override
    public void passData(Integer address_id, int position){
        progressDialog = ProgressDialog.show(context, null, "Please Wait..", true);
        doRemoveAddress(address_id, userId, position);
    }

    private void initView(){
        context = this;
        sharedPrefManager = new SharedPrefManager(context);
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        AddressAdapter.passingBtnRemove = this;
        imgBack = (ImageView)findViewById(R.id.imgBack);
        btnAddAddress = (Button)findViewById(R.id.btnAddAddress);
        rvAddress = (RecyclerView)findViewById(R.id.RvAddress);
    }

    private void doGetAddress(Integer userId){
        Call<ReqAddress> call = userService.getAddress(userId);
        call.enqueue(new Callback<ReqAddress>() {
            @Override
            public void onResponse(Call<ReqAddress> call, Response<ReqAddress> response) {
                ReqAddress reqAddress = response.body();
                addresses = reqAddress.getAddresses();

                addressAdapter = new AddressAdapter(addresses);
                rvAddress.setLayoutManager(new LinearLayoutManager(context));
                rvAddress.setItemAnimator(new DefaultItemAnimator());
                rvAddress.setAdapter(addressAdapter);
            }

            @Override
            public void onFailure(Call<ReqAddress> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doRemoveAddress(Integer addressId, Integer userId, final int position){
        Call<ResMessage> call = userService.removeAddressRequest(addressId, userId);
        call.enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                ResMessage resMessage = response.body();
                Toast.makeText(context, resMessage.getMessage(), Toast.LENGTH_SHORT).show();
                addressAdapter.notifyItemRemoved(position);
                addressAdapter.notifyItemRangeChanged(position, addresses.size());
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResMessage> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}
