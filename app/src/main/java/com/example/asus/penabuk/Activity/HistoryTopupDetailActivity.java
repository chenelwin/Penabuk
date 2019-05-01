package com.example.asus.penabuk.Activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.penabuk.Model.ReqHistoryTopupId;
import com.example.asus.penabuk.Model.Topup;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryTopupDetailActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    SharedPrefManager sharedPrefManager;
    Context context;
    Integer userId;
    Integer topupId;
    TextView textId;
    TextView textDate;
    TextView textBalance;
    TextView textStatus;
    ImageView imageTopup;
    Toolbar toolbarHistoryTopupDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_topup_detail);
        initView();
        doGetHistoryTopupById(topupId);
    }

    private void initView(){
        context = this;
        sharedPrefManager = new SharedPrefManager(context);
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        topupId = getIntent().getIntExtra("passingtopupid", 0);
        textId = (TextView)findViewById(R.id.textId);
        textDate = (TextView)findViewById(R.id.textDate);
        textBalance = (TextView)findViewById(R.id.textBalance);
        textStatus = (TextView)findViewById(R.id.textStatus);
        imageTopup = (ImageView)findViewById(R.id.imageTopup);
        initToolbar();
    }

    private void initToolbar(){
        toolbarHistoryTopupDetail = (Toolbar)findViewById(R.id.toolbarHistoryTopupDetail);
        setSupportActionBar(toolbarHistoryTopupDetail);
        getSupportActionBar().setTitle("Top Up Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarHistoryTopupDetail.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void doGetHistoryTopupById(final Integer topupId){
        Call<ReqHistoryTopupId> call = userService.getHistoryTopupById(topupId, userId);
        call.enqueue(new Callback<ReqHistoryTopupId>() {
            @Override
            public void onResponse(Call<ReqHistoryTopupId> call, Response<ReqHistoryTopupId> response) {
                ReqHistoryTopupId reqHistoryTopupId = response.body();
                Topup topup = reqHistoryTopupId.getTopUp();
                textId.setText(topup.getId().toString());
                textDate.setText(topup.getCreatedAt());

                textStatus.setText(topup.getStatus());
                DecimalFormat formatter = new DecimalFormat("#,###,###");
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setGroupingSeparator('.');
                formatter.setDecimalFormatSymbols(symbols);
                String tmpbalance = formatter.format(topup.getBalance());
                textBalance.setText("Rp " + tmpbalance);

                Picasso.with(context)
                        .load(ApiUtils.BASE_URL +"/image?id="+topup.getImage_url())
                        .into(imageTopup);
            }

            @Override
            public void onFailure(Call<ReqHistoryTopupId> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
