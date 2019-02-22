package com.example.asus.penabuk.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.asus.penabuk.Adapter.PaymentDetailAdapter;
import com.example.asus.penabuk.Model.Address;
import com.example.asus.penabuk.Model.Book;
import com.example.asus.penabuk.Model.BookPayment;
import com.example.asus.penabuk.Model.Payment;
import com.example.asus.penabuk.Model.ReqAddress;
import com.example.asus.penabuk.Model.ReqPayment;
import com.example.asus.penabuk.Model.ResMessage;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class PaymentDetailActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    Context context;
    SharedPrefManager sharedPrefManager;
    Spinner spinnerAlamat;
    ArrayAdapter<Address> spinnerAlamatAdapter;
    List<Address> addresses;
    Button btnPay;
    Integer userId;
    Integer addressId;
    RecyclerView rvPaymentDetailActivity;
    PaymentDetailAdapter paymentDetailAdapter;
    List<BookPayment> bookPayments;
    ProgressDialog progressDialog;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);
        initView();
        doGetAddress(userId);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = ProgressDialog.show(context, null, "Please Wait..", true);
                doPayment(userId, bookPayments);
            }
        });
    }

    private void initView(){
        context = this;
        sharedPrefManager = new SharedPrefManager(context);
        spinnerAlamat = (Spinner)findViewById(R.id.spinnerAlamat);
        btnPay = (Button)findViewById(R.id.btnPay);
        imgBack = (ImageView)findViewById(R.id.imgBack);
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        rvPaymentDetailActivity = (RecyclerView)findViewById(R.id.RvPaymentDetail);
        bookPayments = new ArrayList<BookPayment>();
        List<Book> books = (List<Book>)getIntent().getSerializableExtra("passingbook");
        initBook(books);
        paymentDetailAdapter = new PaymentDetailAdapter(bookPayments);
        rvPaymentDetailActivity.setLayoutManager(new LinearLayoutManager(context));
        rvPaymentDetailActivity.setItemAnimator(new DefaultItemAnimator());
        rvPaymentDetailActivity.setAdapter(paymentDetailAdapter);
    }

    private void initBook(List<Book> books){
        for(int i=0; i<books.size(); i++){
            BookPayment bookPayment = new BookPayment();
            bookPayment.setBook(books.get(i));
            bookPayment.setCount(1);
            bookPayments.add(bookPayment);
        }
    }

    private void doGetAddress(Integer userId){
        Call<ReqAddress> call = userService.getAddress(userId);
        call.enqueue(new Callback<ReqAddress>() {
            @Override
            public void onResponse(Call<ReqAddress> call, Response<ReqAddress> response) {
                ReqAddress reqAddress = response.body();
                addresses = reqAddress.getAddresses();

                spinnerAlamatAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, addresses);
                spinnerAlamatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerAlamat.setAdapter(spinnerAlamatAdapter);

                spinnerAlamat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        Address addressselected = (Address)spinnerAlamat.getSelectedItem();
                        addressId = addressselected.getId();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onFailure(Call<ReqAddress> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doPayment(Integer id, List<BookPayment> bookPayments){
        //ReqPayment reqPayment = new ReqPayment();
        List<Payment> payments = new ArrayList<>();
        for(int i=0; i<bookPayments.size(); i++){
            Payment payment = new Payment();
            payment.setBook_id(bookPayments.get(i).getBook().getId());
            payment.setAddress_id(addressId);
            payment.setCount(bookPayments.get(i).getCount());
            //Log.e("testing", ""+payment.getBook_id()+payment.getAddress_id()+payment.getCount());
            payments.add(payment);
        }
        //reqPayment.setPayments(payments);

        Call<ResMessage> call = userService.paymentRequest(payments, id);
        call.enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                if(response.isSuccessful()) {
                    ResMessage resMessage = response.body();
                    Toast.makeText(context, resMessage.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PaymentDetailActivity.this, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    progressDialog.dismiss();
                    finish();
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
