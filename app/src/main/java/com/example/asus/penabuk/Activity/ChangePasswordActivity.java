package com.example.asus.penabuk.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.asus.penabuk.ErrorUtils.ErrorUtils;
import com.example.asus.penabuk.Model.ReqChangePassword;
import com.example.asus.penabuk.Model.ResMessage;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    Context context;
    SharedPrefManager sharedPrefManager;
    EditText oldpass;
    EditText newpass;
    EditText repass;
    Button btnGanti;
    Integer userId;
    ProgressDialog progressDialog;

    Toolbar toolbarChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initView();


        btnGanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldpassword = oldpass.getText().toString();
                String newpassword = newpass.getText().toString();
                String repassword = repass.getText().toString();
                if(validateChangePassword(oldpassword, newpassword, repassword)){
                    progressDialog = ProgressDialog.show(context, null, "Please Wait..", true);
                    doChangePassword(userId, oldpassword, newpassword);
                }
            }
        });
    }

    private void initView(){
        context = this;
        sharedPrefManager = new SharedPrefManager(context);
        initToolbar();
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        oldpass = (EditText)findViewById(R.id.oldpass);
        newpass = (EditText)findViewById(R.id.newpass);
        repass = (EditText)findViewById(R.id.repass);
        btnGanti = (Button)findViewById(R.id.btnGanti);
    }

    private void initToolbar(){
        toolbarChangePassword = (Toolbar)findViewById(R.id.toolbarChangePassword);
        setSupportActionBar(toolbarChangePassword);
        getSupportActionBar().setTitle("Ubah Kata Sandi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarChangePassword.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private boolean validateChangePassword(String oldpassword, String newpassword, String repassword){
        if(oldpassword == null || oldpassword.trim().length() == 0){
            Toast.makeText(this, "Old Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(newpassword == null || newpassword.trim().length() == 0){
            Toast.makeText(this, "New Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(repassword == null || repassword.trim().length() == 0){
            Toast.makeText(this, "Repeat Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!newpassword.equals(repassword)){
            Toast.makeText(this, "Repeat Password must be the same as Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void doChangePassword(Integer id, String oldpass, String newpass){
        ReqChangePassword reqChangePassword = new ReqChangePassword();
        reqChangePassword.setOld_password(oldpass);
        reqChangePassword.setNew_password(newpass);
        Call<ResMessage> call = userService.changePasswordRequest(reqChangePassword, id);
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
                    Toast.makeText(ChangePasswordActivity.this, resMessage.getMessage(), Toast.LENGTH_LONG).show();
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
