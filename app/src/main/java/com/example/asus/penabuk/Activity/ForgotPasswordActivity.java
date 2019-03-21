package com.example.asus.penabuk.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.asus.penabuk.ErrorUtils.ErrorUtils;
import com.example.asus.penabuk.Model.ResMessage;
import com.example.asus.penabuk.Model.User;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    Context context;
    ImageView imgBack;
    EditText inputEmail;
    Button btnReset;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initView();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = inputEmail.getText().toString();
                if(validateReset(email)){
                    progressDialog = ProgressDialog.show(context, null, "Please Wait..", true);
                    doResetPassword(email);
                }
            }
        });
    }

    private void initView(){
        context = this;
        imgBack = (ImageView)findViewById(R.id.imgBack);
        inputEmail = (EditText)findViewById(R.id.inputEmail);
        btnReset = (Button)findViewById(R.id.btnReset);
    }

    private boolean validateReset(String email){
        if(email == null || email.trim().length() == 0){
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void doResetPassword(String email){
        final User user = new User();
        user.setEmail(email);
        Call<ResMessage> call = userService.resetPasswordRequest(user);
        call.enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                if(response.isSuccessful()){
                    ResMessage resMessage = response.body();
                    Toast.makeText(context, resMessage.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    finish();
                }
                else {
                    ResMessage resMessage = ErrorUtils.parseError(response);
                    Toast.makeText(ForgotPasswordActivity.this, resMessage.getMessage(), Toast.LENGTH_LONG).show();
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
