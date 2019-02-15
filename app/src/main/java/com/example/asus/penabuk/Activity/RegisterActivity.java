package com.example.asus.penabuk.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.penabuk.Model.ReqUser;
import com.example.asus.penabuk.Model.ResUser;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    TextView textLogin;
    EditText regisEmail;
    EditText regisNama;
    EditText regisNohp;
    EditText regisPassword;
    EditText regisRepassword;
    ProgressDialog progressDialog;
    Context context;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();

        textLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = regisEmail.getText().toString();
                String name = regisNama.getText().toString();
                String nomor_handphone = regisNohp.getText().toString();
                String password = regisPassword.getText().toString();
                String repassword = regisRepassword.getText().toString();

                if(validateRegister(email, name, nomor_handphone, password, repassword)){
                    progressDialog = ProgressDialog.show(context, null, "Please Wait..", true);
                    doRegister(email, name, nomor_handphone, password);
                }

            }
        });
    }

    private void initView(){
        context = this;
        textLogin = (TextView)findViewById(R.id.textLogin);
        regisEmail = (EditText)findViewById(R.id.regisEmail);
        regisNama = (EditText)findViewById(R.id.regisNama);
        regisNohp = (EditText)findViewById(R.id.regisNohp);
        regisPassword = (EditText)findViewById(R.id.regisPassword);
        regisRepassword = (EditText)findViewById(R.id.regisRepassword);
        btnRegister = (Button)findViewById(R.id.btnRegister);
    }

    private boolean validateRegister(String email, String name, String nohp, String password, String repassword){
        if(email == null || email.trim().length() == 0){
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(name == null || name.trim().length() == 0){
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(nohp == null || nohp.trim().length() == 0){
            Toast.makeText(this, "Nomor HP is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password == null || password.trim().length() == 0){
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(repassword == null || repassword.trim().length() == 0){
            Toast.makeText(this, "Repeat Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!password.equals(repassword)){
            Toast.makeText(this, "Repeat Password must be the same as Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void doRegister(String email, String name, String nomor_handphone, String password){
        ReqUser reqUser = new ReqUser();
        reqUser.setEmail(email);
        reqUser.setName(name);
        reqUser.setPhone_number(nomor_handphone);
        reqUser.setPassword(password);
        Call<ResUser> call = userService.registerRequest(reqUser);
        call.enqueue(new Callback<ResUser>() {
            @Override
            public void onResponse(Call<ResUser> call, Response<ResUser> response) {
                if(response.code()==200){
                    ResUser resUser = response.body();
                    Toast.makeText(RegisterActivity.this, "Berhasil Register", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    progressDialog.dismiss();
                    startActivity(intent);
                    finish();
                }
                else{
                    try {
                        Toast.makeText(RegisterActivity.this, ""+response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResUser> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}
