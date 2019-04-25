package com.example.asus.penabuk.Activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.asus.penabuk.Adapter.HistoryTopupAdapter;
import com.example.asus.penabuk.Model.ReqHistoryTopup;
import com.example.asus.penabuk.Model.Topup;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryTopupActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    SharedPrefManager sharedPrefManager;
    Context context;
    Integer userId;
    RecyclerView rvHistoryTopup;
    HistoryTopupAdapter historyTopupAdapter;
    List<Topup> topups;

    Toolbar toolbarHistoryTopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_topup);
        initView();
        doGetHistoryTopup();
    }

    private void initView(){
        context = this;
        sharedPrefManager = new SharedPrefManager(context);
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        rvHistoryTopup = (RecyclerView)findViewById(R.id.RvHistoryTopup);
        initToolbar();
    }

    private void initToolbar(){
        toolbarHistoryTopup = (Toolbar)findViewById(R.id.toolbarHistoryTopup);
        setSupportActionBar(toolbarHistoryTopup);
        getSupportActionBar().setTitle("Riwayat Top Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarHistoryTopup.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void doGetHistoryTopup(){
        Call<ReqHistoryTopup> call = userService.getHistoryTopup(userId);
        call.enqueue(new Callback<ReqHistoryTopup>() {
            @Override
            public void onResponse(Call<ReqHistoryTopup> call, Response<ReqHistoryTopup> response) {
                ReqHistoryTopup reqHistoryTopup = response.body();
                topups = reqHistoryTopup.getTopUp();

                historyTopupAdapter = new HistoryTopupAdapter(topups);
                rvHistoryTopup.setLayoutManager(new LinearLayoutManager(context));
                rvHistoryTopup.setItemAnimator(new DefaultItemAnimator());
                rvHistoryTopup.setAdapter(historyTopupAdapter);
            }

            @Override
            public void onFailure(Call<ReqHistoryTopup> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
