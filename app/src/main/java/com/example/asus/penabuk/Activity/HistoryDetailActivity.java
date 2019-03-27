package com.example.asus.penabuk.Activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.penabuk.Adapter.HistoryDetailAdapter;
import com.example.asus.penabuk.Model.History;
import com.example.asus.penabuk.Model.ReqHistoryId;
import com.example.asus.penabuk.Model.ResMessage;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryDetailActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    SharedPrefManager sharedPrefManager;
    Context context;
    ImageView imgBack;
    TextView textOrderid;
    TextView textDate;
    TextView textAddress;
    TextView textTotalprice;
    TextView textStatus;
    TextView textTotalQty;
    Button btnCancel;
    Button btnConfirm;
    History historyDetail;
    Integer userId;
    String orderId;

    RecyclerView rvHistoryDetail;
    HistoryDetailAdapter historyDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        initView();
        doGetHistoryById(orderId, userId);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doCancelOrder(orderId, userId);
            }
        });
    }

    public void initView(){
        context = this;
        sharedPrefManager = new SharedPrefManager(context);
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        imgBack = (ImageView)findViewById(R.id.imgBack);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnConfirm = (Button)findViewById(R.id.btnConfirm);
        textOrderid = (TextView)findViewById(R.id.textOrderid);
        textDate = (TextView)findViewById(R.id.textDate);
        textAddress = (TextView)findViewById(R.id.textAddress);
        textTotalprice = (TextView)findViewById(R.id.textTotalharga);
        textStatus = (TextView)findViewById(R.id.textStatus);
        textTotalQty = (TextView)findViewById(R.id.textTotalQty);
        orderId = getIntent().getStringExtra("passingorderid");
        rvHistoryDetail = (RecyclerView)findViewById(R.id.RvHistoryDetail);
    }

    public void doGetHistoryById(String orderId, Integer userId){
        Call<ReqHistoryId> call = userService.getHistoryById(orderId, userId);
        call.enqueue(new Callback<ReqHistoryId>() {
            @Override
            public void onResponse(Call<ReqHistoryId> call, Response<ReqHistoryId> response) {
                ReqHistoryId reqHistoryId = response.body();
                historyDetail = reqHistoryId.getHistories();

                textOrderid.setText(historyDetail.getOrder_id());
                textDate.setText(historyDetail.getCreatedAt());
                textAddress.setText(historyDetail.getAddress().getAddress_line());
                textStatus.setText(historyDetail.getStatus());
                showButton(historyDetail.getStatus());
                textTotalprice.setText("Rp. "+historyDetail.getTotal_price());
                Integer totalQty = 0;
                for(int i=0; i<historyDetail.getDetails().size(); i++){
                    totalQty += historyDetail.getDetails().get(i).getCount();
                }
                textTotalQty.setText(totalQty.toString());

                historyDetailAdapter = new HistoryDetailAdapter(historyDetail.getDetails());
                rvHistoryDetail.setLayoutManager(new LinearLayoutManager(context));
                rvHistoryDetail.setItemAnimator(new DefaultItemAnimator());
                rvHistoryDetail.setAdapter(historyDetailAdapter);

            }

            @Override
            public void onFailure(Call<ReqHistoryId> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showButton(String statusorder){
        if(statusorder.equals("Pending")){
            btnCancel.setVisibility(View.VISIBLE);
            btnConfirm.setVisibility(View.GONE);
        }
        if(statusorder.equals("Sedang dikirim") || statusorder.equals("Telah sampai tujuan")){
            btnCancel.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.VISIBLE);
        }
        if(statusorder.equals("Batal")){
            btnCancel.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.GONE);
        }
    }

    private void doCancelOrder(String orderId, Integer userId){
        Call<ResMessage> call = userService.cancelOrderRequest(orderId, userId);
        call.enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                ResMessage resMessage = response.body();
                Toast.makeText(context, resMessage.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<ResMessage> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
