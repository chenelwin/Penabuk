package com.example.asus.penabuk.Activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.asus.penabuk.Adapter.HistoryBalanceAdapter;
import com.example.asus.penabuk.Model.HistoryBalance;
import com.example.asus.penabuk.Model.ReqHistoryBalance;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryBalanceActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    SharedPrefManager sharedPrefManager;
    Context context;
    Integer userId;
    RecyclerView rvHistoryBalance;
    HistoryBalanceAdapter historyBalanceAdapter;
    List<HistoryBalance> historyBalances;
    LinearLayout layoutNoHistoryBalance;

    Toolbar toolbarHistoryBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_balance);
        initView();
        doGetHistoryBalance();
    }

    private void initView(){
        context = this;
        sharedPrefManager = new SharedPrefManager(context);
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        initToolbar();
        rvHistoryBalance = (RecyclerView)findViewById(R.id.rvHistoryBalance);
        layoutNoHistoryBalance = (LinearLayout)findViewById(R.id.layoutNoHistoryBalance);
    }

    private void initToolbar(){
        toolbarHistoryBalance = (Toolbar)findViewById(R.id.toolbarHistoryBalance);
        setSupportActionBar(toolbarHistoryBalance);
        getSupportActionBar().setTitle("Riwayat Saldo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarHistoryBalance.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void doGetHistoryBalance(){
        Call<ReqHistoryBalance> call = userService.getHistoryBalance(userId);
        call.enqueue(new Callback<ReqHistoryBalance>() {
            @Override
            public void onResponse(Call<ReqHistoryBalance> call, Response<ReqHistoryBalance> response) {
                ReqHistoryBalance reqHistoryBalance = response.body();
                historyBalances = reqHistoryBalance.getHistoryBalance();
                checkHistoryBalanceSize(historyBalances);
                historyBalanceAdapter = new HistoryBalanceAdapter(historyBalances);
                rvHistoryBalance.setLayoutManager(new LinearLayoutManager(context));
                rvHistoryBalance.setItemAnimator(new DefaultItemAnimator());
                rvHistoryBalance.setAdapter(historyBalanceAdapter);
            }

            @Override
            public void onFailure(Call<ReqHistoryBalance> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkHistoryBalanceSize(List<HistoryBalance> historyBalances){
        if(historyBalances.size()==0){
            layoutNoHistoryBalance.setVisibility(View.VISIBLE);
        }
        else {
            layoutNoHistoryBalance.setVisibility(View.GONE);
        }
    }

}
