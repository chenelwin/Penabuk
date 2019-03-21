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
import android.widget.Toast;

import com.example.asus.penabuk.ErrorUtils.ErrorUtils;
import com.example.asus.penabuk.Model.Order;
import com.example.asus.penabuk.Model.Payment;
import com.example.asus.penabuk.Model.ResMessage;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmPaymentActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    SharedPrefManager sharedPrefManager;
    Context context;
    EditText confirmPassword;
    List<Order> orders;
    Button btnConfirm;
    Integer addressId;
    Integer userId;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_payment);
        initView();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = ProgressDialog.show(context, null, "Please Wait..", true);
                String password = confirmPassword.getText().toString();
                doConfirmation(password);
            }
        });
    }

    private void initView(){
        context = this;
        sharedPrefManager = new SharedPrefManager(context);
        confirmPassword = (EditText)findViewById(R.id.confirmPassword);
        btnConfirm = (Button)findViewById(R.id.btnConfirm);
        orders = (List<Order>)getIntent().getSerializableExtra("passingorder");
        addressId = getIntent().getIntExtra("passingaddress", 0);
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        Log.e("userid", userId.toString());
    }

    private void doConfirmation(String password){
        Payment payment = new Payment();
        payment.setPassword(password);
        payment.setAddress_id(addressId);
        payment.setOrders(orders);

        Call<ResMessage> call = userService.paymentRequest(payment, userId);
        call.enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                if(response.isSuccessful()) {
                    ResMessage resMessage = response.body();
                    Toast.makeText(context, resMessage.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ConfirmPaymentActivity.this, PaymentSuccessActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    progressDialog.dismiss();
                    finish();
                }
                else {
                    ResMessage resMessage = ErrorUtils.parseError(response);
                    Toast.makeText(ConfirmPaymentActivity.this, resMessage.getMessage(), Toast.LENGTH_LONG).show();
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
