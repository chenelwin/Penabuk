package com.example.asus.penabuk.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.asus.penabuk.Model.Address;
import com.example.asus.penabuk.Model.ReqAddress;
import com.example.asus.penabuk.Model.ResMessage;
import com.example.asus.penabuk.Model.User;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

import java.io.IOException;
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
    EditText editName;
    EditText editPhonenumber;
    Button btnAddAddress;
    Button btnSimpan;
    Spinner spinnerAlamat;
    ArrayAdapter<Address> spinnerAlamatAdapter;
    List<Address> addresses;
    ProgressDialog progressDialog;

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

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editName.getText().toString();
                String phone_number = editPhonenumber.getText().toString();
                if(validateChangeProfile(name,phone_number)) {
                    progressDialog = ProgressDialog.show(context, null, "Please Wait..", true);
                    doChangeProfile(userId, name, phone_number);
                }
            }
        });
    }

    private void initView(){
        context = this;
        sharedPrefManager = new SharedPrefManager(context);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        editName = (EditText)findViewById(R.id.editName);
        editName.setText(sharedPrefManager.getSPNama());
        editPhonenumber = (EditText)findViewById(R.id.editPhonenumber);
        editPhonenumber.setText(sharedPrefManager.getSPNohp());
        btnAddAddress = (Button)findViewById(R.id.btnAddAddress);
        btnSimpan = (Button)findViewById(R.id.btnSimpan);
        spinnerAlamat = (Spinner)findViewById(R.id.spinnerAlamat);

        userId = Integer.parseInt(sharedPrefManager.getSPId());
    }

    @Override
    public void onResume(){
        super.onResume();
        doGetAddress(userId);
    }

    private boolean validateChangeProfile(String name, String phone_number){
        if(name == null || name.trim().length() == 0){
            Toast.makeText(this, "Name cannot empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(phone_number == null || phone_number.trim().length() == 0){
            Toast.makeText(this, "Phone Number cannot empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

    private void doChangeProfile(Integer id, final String name, final String phone_number){
        User user = new User();
        user.setName(name);
        user.setPhone_number(phone_number);
        Call<ResMessage> call = userService.changeProfileRequest(user, id);
        call.enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                if(response.isSuccessful()) {
                    ResMessage resMessage = response.body();
                    Toast.makeText(context, resMessage.getMessage(), Toast.LENGTH_SHORT).show();
                    sharedPrefManager.saveSPString(SharedPrefManager.SP_NAMA, name);
                    sharedPrefManager.saveSPString(SharedPrefManager.SP_NOHP, phone_number);
                    finish();
                    progressDialog.dismiss();
                }
                else {
                    try {
                        Toast.makeText(context, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResMessage> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}
