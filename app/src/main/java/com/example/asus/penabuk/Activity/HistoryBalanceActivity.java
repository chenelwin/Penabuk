package com.example.asus.penabuk.Activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

public class HistoryBalanceActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    SharedPrefManager sharedPrefManager;
    Context context;

    Toolbar toolbarHistoryBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_balance);
        initView();
    }

    private void initView(){
        context = this;
        sharedPrefManager = new SharedPrefManager(context);
        initToolbar();
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

}
