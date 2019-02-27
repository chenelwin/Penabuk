package com.example.asus.penabuk.Activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.penabuk.Adapter.HistoryDetailAdapter;
import com.example.asus.penabuk.Model.History;
import com.example.asus.penabuk.Model.ReqHistoryId;
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
    }

    public void initView(){
        context = this;
        sharedPrefManager = new SharedPrefManager(context);
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        imgBack = (ImageView)findViewById(R.id.imgBack);
        textOrderid = (TextView)findViewById(R.id.textOrderid);
        textDate = (TextView)findViewById(R.id.textDate);
        textAddress = (TextView)findViewById(R.id.textAddress);
        textTotalprice = (TextView)findViewById(R.id.textTotalharga);
        textStatus = (TextView)findViewById(R.id.textStatus);
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
                textTotalprice.setText("Rp. "+historyDetail.getTotal_price());
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
}