package com.example.asus.penabuk.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.asus.penabuk.Model.Address;
import com.example.asus.penabuk.Model.ReqAddress;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    Context context;
    SharedPrefManager sharedPrefManager;
    Integer userId;
    ImageView imgBack;
    Button btnAddAddress;
    Spinner spinnerAlamat;
    ArrayAdapter<Address> spinnerAlamatAdapter;
    List<Address> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
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
                Intent intent = new Intent(EditProfileActivity.this, AddAddressActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView(){
        context = this;
        imgBack = (ImageView) findViewById(R.id.imgBack);
        btnAddAddress = (Button)findViewById(R.id.btnAddAddress);
        spinnerAlamat = (Spinner)findViewById(R.id.spinnerAlamat);
        sharedPrefManager = new SharedPrefManager(context);
        userId = Integer.parseInt(sharedPrefManager.getSPId());
    }

    private void doGetAddress(Integer id){
        Call<ReqAddress> call = userService.getAddress(id);
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
