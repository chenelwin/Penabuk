package com.example.asus.penabuk.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.penabuk.Adapter.HistoryDetailAdapter;
import com.example.asus.penabuk.Dialog.CancelOrderDialog;
import com.example.asus.penabuk.Model.History;
import com.example.asus.penabuk.Model.ReqCancelOrder;
import com.example.asus.penabuk.Model.ReqHistoryId;
import com.example.asus.penabuk.Model.ResMessage;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryDetailActivity extends AppCompatActivity implements CancelOrderDialog.CancelOrderDialogListener{

    UserService userService = ApiUtils.getUserService();
    SharedPrefManager sharedPrefManager;
    Context context;
    TextView textOrderid;
    TextView textDate;
    TextView textAddress;
    TextView textTotalprice;
    TextView textStatus;
    LinearLayout layoutAlasanBatal;
    TextView textAlasan;
    TextView textTotalQty;
    Button btnCancel;
    Button btnConfirm;
    History historyDetail;
    Integer userId;
    String orderId;
    ProgressDialog progressDialog;

    RecyclerView rvHistoryDetail;
    HistoryDetailAdapter historyDetailAdapter;

    Toolbar toolbarHistoryDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        initView();
        doGetHistoryById(orderId, userId);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCancelOrderDialog();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = ProgressDialog.show(context, null, "Please Wait..", true);
                doConfirmOrder(orderId, userId);
            }
        });
    }

    public void initView(){
        context = this;
        sharedPrefManager = new SharedPrefManager(context);
        initToolbar();
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnConfirm = (Button)findViewById(R.id.btnConfirm);
        textOrderid = (TextView)findViewById(R.id.textOrderid);
        textDate = (TextView)findViewById(R.id.textDate);
        textAddress = (TextView)findViewById(R.id.textAddress);
        textTotalprice = (TextView)findViewById(R.id.textTotalharga);
        textStatus = (TextView)findViewById(R.id.textStatus);
        layoutAlasanBatal = (LinearLayout)findViewById(R.id.layoutAlasanBatal);
        textAlasan = (TextView)findViewById(R.id.textAlasan);
        textTotalQty = (TextView)findViewById(R.id.textTotalQty);
        orderId = getIntent().getStringExtra("passingorderid");
        rvHistoryDetail = (RecyclerView)findViewById(R.id.RvHistoryDetail);
    }

    private void initToolbar(){
        toolbarHistoryDetail = (Toolbar)findViewById(R.id.toolbarHistoryDetail);
        setSupportActionBar(toolbarHistoryDetail);
        getSupportActionBar().setTitle("History Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarHistoryDetail.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void openCancelOrderDialog(){
        CancelOrderDialog cancelOrderDialog = new CancelOrderDialog();
        cancelOrderDialog.show(getSupportFragmentManager(), "Cancel Order");
    }

    @Override
    public void sendInput(String cancelOrderInput) {
        progressDialog = ProgressDialog.show(context, null, "Please Wait..", true);
        Log.e("ALASAN", ""+cancelOrderInput);
        doCancelOrder(orderId, userId, cancelOrderInput);
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
                String tmpaddress = historyDetail.getAddress().getAddress_line()+", "+historyDetail.getAddress().getCity()+", "+historyDetail.getAddress().getDistrict()+", "+historyDetail.getAddress().getProvince()+" "+historyDetail.getAddress().getZip_code();
                textAddress.setText(tmpaddress);
                textStatus.setText(historyDetail.getStatus());
                if(historyDetail.getDescription().length()>0){
                    textAlasan.setText(historyDetail.getDescription());
                    layoutAlasanBatal.setVisibility(View.VISIBLE);
                }
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
        if(statusorder.equals("Pending") || statusorder.equals("Barang Sedang Dikemas")){
            btnCancel.setVisibility(View.VISIBLE);
            btnConfirm.setVisibility(View.GONE);
        }
        else if(statusorder.equals("Barang Telah Sampai") || statusorder.equals("Barang Sedang Dikirim")){
            btnCancel.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.VISIBLE);
        }
        else if(statusorder.equals("Batal") || statusorder.equals("Barang Telah Diterima") || statusorder.equals("Ditolak")){
            btnCancel.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.GONE);
        }
    }

    private void doCancelOrder(String orderId, Integer userId, String description){
        ReqCancelOrder reqCancelOrder = new ReqCancelOrder();
        reqCancelOrder.setDescription(description);
        Call<ResMessage> call = userService.cancelOrderRequest(orderId, userId, reqCancelOrder);
        call.enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                ResMessage resMessage = response.body();
                Toast.makeText(context, resMessage.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                finish();
            }

            @Override
            public void onFailure(Call<ResMessage> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void doConfirmOrder(String orderId, Integer userId){
        Call<ResMessage> call = userService.confirmOrderRequest(orderId, userId);
        call.enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                ResMessage resMessage = response.body();
                Toast.makeText(context, resMessage.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                finish();
            }

            @Override
            public void onFailure(Call<ResMessage> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}
