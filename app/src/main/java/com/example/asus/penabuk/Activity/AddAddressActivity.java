package com.example.asus.penabuk.Activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.asus.penabuk.Model.Province;
import com.example.asus.penabuk.Model.ReqProvince;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    Context context;
    ImageView imgBack;
    Spinner spinnerProvinsi;
    ArrayAdapter<Province> spinnerProvinsiAdapter;
    List<Province> provinces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        initView();

        doGetProvinces();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView(){
        context = this;
        imgBack = (ImageView)findViewById(R.id.imgBack);
        spinnerProvinsi = (Spinner)findViewById(R.id.spinnerProvinsi);
    }

    private void doGetProvinces(){
        Call<ReqProvince> call = userService.getProvince();
        call.enqueue(new Callback<ReqProvince>() {
            @Override
            public void onResponse(Call<ReqProvince> call, Response<ReqProvince> response) {
                ReqProvince reqProvince = response.body();
                provinces = reqProvince.getProvinces();

                spinnerProvinsiAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, provinces);
                spinnerProvinsiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerProvinsi.setAdapter(spinnerProvinsiAdapter);
            }

            @Override
            public void onFailure(Call<ReqProvince> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
