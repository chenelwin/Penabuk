package com.example.asus.penabuk.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.penabuk.ErrorUtils.ErrorUtils;
import com.example.asus.penabuk.Model.ReqUser;
import com.example.asus.penabuk.Model.ResMessage;
import com.example.asus.penabuk.Model.ResUser;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    EditText loginEmail;
    EditText loginPassword;
    TextView textForgotPassword;
    Button btnRegister;
    Button btnLogin;
    TextView textVerifyEmail;
    Context context;
    ProgressDialog progressDialog;
    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();

        if(sharedPrefManager.getSPSudahLogin()){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        textForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();
                if(validateLogin(email, password)) {
                    progressDialog = ProgressDialog.show(context, null, "Please Wait..", true);
                    doLogin(email, password);
                }
            }
        });

        textVerifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, VerifyEmailActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView(){
        sharedPrefManager = new SharedPrefManager(this);
        context = this;
        textForgotPassword = (TextView)findViewById(R.id.textForgotPassword);
        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        textVerifyEmail = (TextView)findViewById(R.id.textVerifyEmail);
        loginEmail = (EditText)findViewById(R.id.loginEmail);
        loginPassword = (EditText)findViewById(R.id.loginPassword);
    }

    private boolean validateLogin(String email, String password){
        if(email == null || email.trim().length() == 0){
            loginEmail.setError("Email harus diisi");
            return false;
        }
        if(password == null || password.trim().length() == 0){
            loginPassword.setError("Password harus diisi");
            return false;
        }
        if(!isEmailValid(email)){
            loginEmail.setError("Email tidak valid");
            return false;
        }
        return true;
    }

    private boolean isEmailValid(CharSequence email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void doLogin(String email, String password){
        final ReqUser reqUser = new ReqUser();
        reqUser.setEmail(email);
        reqUser.setPassword(password);
        Call<ResUser> call = userService.loginRequest(reqUser);
        call.enqueue(new Callback<ResUser>() {
            @Override
            public void onResponse(Call<ResUser> call, Response<ResUser> response) {
                if(response.isSuccessful()){
                    ResUser resUser = response.body();
                    Toast.makeText(LoginActivity.this, "Welcome", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    String passingid = resUser.getUser().getId().toString();
                    String passingnama = resUser.getUser().getName();
                    String passingemail = resUser.getUser().getEmail();
                    String passingnohp = resUser.getUser().getPhone_number();
                    String passingimage = resUser.getUser().getImage_url();
                    String passingbalance = resUser.getUser().getBalance().toString();
                    sharedPrefManager.saveSPString(SharedPrefManager.SP_EMAIL, passingemail);
                    sharedPrefManager.saveSPString(SharedPrefManager.SP_ID, passingid);
                    sharedPrefManager.saveSPString(SharedPrefManager.SP_NAMA, passingnama);
                    sharedPrefManager.saveSPString(SharedPrefManager.SP_BALANCE, passingbalance);
                    sharedPrefManager.saveSPString(SharedPrefManager.SP_NOHP, passingnohp);
                    sharedPrefManager.saveSPString(SharedPrefManager.SP_IMAGE, passingimage);
                    sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN, true);

                    progressDialog.dismiss();
                    startActivity(intent);
                    finish();
                }

                else{
                    ResMessage resMessage = ErrorUtils.parseError(response);
                    Toast.makeText(LoginActivity.this, resMessage.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResUser> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}
