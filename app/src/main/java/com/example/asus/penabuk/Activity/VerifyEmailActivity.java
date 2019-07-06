package com.example.asus.penabuk.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus.penabuk.ErrorUtils.ErrorUtils;
import com.example.asus.penabuk.Model.ResMessage;
import com.example.asus.penabuk.Model.User;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyEmailActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    Context context;
    EditText inputEmail;
    Button btnVerifyEmail;
    ProgressDialog progressDialog;

    Toolbar toolbarVerifyEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);
        initView();
        btnVerifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = inputEmail.getText().toString();
                if(validateVerifyEmail(email)){
                    progressDialog = ProgressDialog.show(context, null, "Please Wait..", true);
                    doVerifyEmail(email);
                }
            }
        });
    }

    private void initView(){
        context = this;
        initToolbar();
        inputEmail = (EditText)findViewById(R.id.inputEmail);
        btnVerifyEmail = (Button)findViewById(R.id.btnVerifyEmail);
    }

    private void initToolbar(){
        toolbarVerifyEmail = (Toolbar)findViewById(R.id.toolbarVerifyEmail);
        setSupportActionBar(toolbarVerifyEmail);
        getSupportActionBar().setTitle("Verifikasi Email");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarVerifyEmail.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private boolean validateVerifyEmail(String email){
        if(email == null || email.trim().length() == 0){
            inputEmail.setError("Email harus diisi");
            return false;
        }
        return true;
    }

    private void doVerifyEmail(String email){
        final User user = new User();
        user.setEmail(email);
        Call<ResMessage> call = userService.verifyAccountRequest(user);
        call.enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                if(response.isSuccessful()) {
                    ResMessage resMessage = response.body();
                    Toast.makeText(context, resMessage.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    finish();
                }
                else {
                    ResMessage resMessage = ErrorUtils.parseError(response);
                    Toast.makeText(VerifyEmailActivity.this, resMessage.getMessage(), Toast.LENGTH_LONG).show();
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
