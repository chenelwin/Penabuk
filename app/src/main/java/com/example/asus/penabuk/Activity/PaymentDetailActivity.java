package com.example.asus.penabuk.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.penabuk.Adapter.PaymentDetailAdapter;
import com.example.asus.penabuk.Model.Address;
import com.example.asus.penabuk.Model.Book;
import com.example.asus.penabuk.Model.BookPayment;
import com.example.asus.penabuk.Model.Order;
import com.example.asus.penabuk.Model.Payment;
import com.example.asus.penabuk.Model.ReqAddress;
import com.example.asus.penabuk.Model.ResMessage;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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
    Button btnTopup;
    Button btnAddAddress;
    Integer userId;
    Integer addressId;
    RecyclerView rvPaymentDetailActivity;
    PaymentDetailAdapter paymentDetailAdapter;
    List<BookPayment> bookPayments;
    ProgressDialog progressDialog;
    TextView paymentPrice;
    TextView paymentCount;
    TextView ongkir;
    TextView balance;
    TextView paymentTotal;
    TextView leftbalance;

    Toolbar toolbarPaymentDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);
        initView();
        doGetAddress(userId);


        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentDetailActivity.this, AddAddressActivity.class);
                startActivity(intent);
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = ProgressDialog.show(context, null, "Please Wait..", true);
                if(addresses.size()==0){
                    Toast.makeText(context, "Pilih alamat terlebih dahulu.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else {
                    doPayment(bookPayments);
                }
            }
        });

        btnTopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentDetailActivity.this, TopUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView(){
        context = this;
        sharedPrefManager = new SharedPrefManager(context);
        initToolbar();
        spinnerAlamat = (Spinner)findViewById(R.id.spinnerAlamat);
        btnPay = (Button)findViewById(R.id.btnPay);
        btnTopup = (Button)findViewById(R.id.btnTopup);
        btnAddAddress = (Button)findViewById(R.id.btnAddAddress);
        paymentPrice = (TextView)findViewById(R.id.paymentPrice);
        paymentCount = (TextView)findViewById(R.id.paymentCount);
        ongkir = (TextView)findViewById(R.id.ongkir);
        balance = (TextView)findViewById(R.id.balance);
        paymentTotal = (TextView)findViewById(R.id.paymentTotal);
        leftbalance = (TextView)findViewById(R.id.leftbalance);
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        rvPaymentDetailActivity = (RecyclerView)findViewById(R.id.RvPaymentDetail);
        bookPayments = new ArrayList<>();
        List<Book> books = (List<Book>)getIntent().getSerializableExtra("passingbook");
        List<Integer> passingcartid = (List<Integer>)getIntent().getSerializableExtra("passingcartid");
        List<Integer> passingcount = (List<Integer>)getIntent().getSerializableExtra("passingcount");
        List<Integer> passingmerchantid = (List<Integer>)getIntent().getSerializableExtra("passingmerchantid");
        initBook(books, passingcartid, passingcount, passingmerchantid);
        paymentDetailAdapter = new PaymentDetailAdapter(bookPayments);
        paymentDetailAdapter.notifyDataSetChanged();
        rvPaymentDetailActivity.setLayoutManager(new LinearLayoutManager(context));
        rvPaymentDetailActivity.setItemAnimator(new DefaultItemAnimator());
        rvPaymentDetailActivity.setAdapter(paymentDetailAdapter);
        rvPaymentDetailActivity.setFocusable(false);
    }

    private void initToolbar(){
        toolbarPaymentDetail = (Toolbar)findViewById(R.id.toolbarPaymentDetail);
        setSupportActionBar(toolbarPaymentDetail);
        getSupportActionBar().setTitle("Payment Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarPaymentDetail.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initPaymentPrice(List<BookPayment> bookPayments, List<Integer> merchantids){
        Integer total = 0;
        Integer count = 0;
        for(int i=0; i<bookPayments.size(); i++){
            total += (bookPayments.get(i).getBook().getPrice() * bookPayments.get(i).getCount());
            count += bookPayments.get(i).getCount();
        }
        Integer tmpongkir = 50000*merchantids.size();
        Integer tmpbalance = Integer.parseInt(sharedPrefManager.getSPBalance());
        Integer totalpayment = total+tmpongkir;
        Integer tmpleftbalance = tmpbalance-totalpayment;

        DecimalFormat formatter = new DecimalFormat("#,###,###");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        String priceformat = formatter.format(total);
        String tmpongkirformat = formatter.format(tmpongkir);
        String tmpbalanceformat = formatter.format(tmpbalance);
        String totalpaymentformat = formatter.format(totalpayment);
        String tmpleftbalanceformat = formatter.format(tmpleftbalance);
        paymentPrice.setText("Rp " + priceformat);
        paymentCount.setText(count.toString());
        ongkir.setText("Rp " + tmpongkirformat);
        balance.setText("Rp " + tmpbalanceformat);
        paymentTotal.setText("-Rp " + totalpaymentformat);
        paymentTotal.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        if(tmpleftbalance>=0){
            btnPay.setVisibility(View.VISIBLE); btnTopup.setVisibility(View.GONE);
            leftbalance.setText("Rp " + tmpleftbalanceformat);
            leftbalance.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        }
        else {
            btnPay.setVisibility(View.GONE); btnTopup.setVisibility(View.VISIBLE);
            tmpleftbalance*=-1;
            tmpleftbalanceformat = formatter.format(tmpleftbalance);
            leftbalance.setText("-Rp " + tmpleftbalanceformat);
            leftbalance.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));

        }

    }

    private void initBook(List<Book> books, List<Integer> passingcartid, List<Integer> passingcount, List<Integer> merchantids){
        for(int i=0; i<books.size(); i++){
            BookPayment bookPayment = new BookPayment();
            bookPayment.setBook(books.get(i));
            bookPayment.setCount(passingcount.get(i));
            bookPayment.setCart_id(passingcartid.get(i));
            bookPayments.add(bookPayment);
        }
        initPaymentPrice(bookPayments, merchantids);
    }

    @Override
    public void onResume(){
        super.onResume();
        doGetAddress(userId);
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
                for(int i=0; i<addresses.size(); i++){
                    if(addresses.get(i).isIs_primary()){
                        spinnerAlamat.setSelection(i);
                        break;
                    }
                }

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

    private void doPayment(List<BookPayment> bookPayments){
        List<Order> orders = new ArrayList<>();
        for(int i=0; i<bookPayments.size(); i++){
            Order order = new Order();
            order.setBook_id(bookPayments.get(i).getBook().getId());
            order.setCount(bookPayments.get(i).getCount());
            if(bookPayments.get(i).getCart_id()!=null){
                order.setCart_id(bookPayments.get(i).getCart_id());
            }
            orders.add(order);
        }
        Intent intent = new Intent(PaymentDetailActivity.this, ConfirmPaymentActivity.class);
        intent.putExtra("passingorder", (Serializable)orders);
        intent.putExtra("passingaddress", addressId);
        startActivity(intent);
        progressDialog.dismiss();
    }
}
