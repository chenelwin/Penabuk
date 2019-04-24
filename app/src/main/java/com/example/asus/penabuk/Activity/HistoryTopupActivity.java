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

public class HistoryTopupActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    SharedPrefManager sharedPrefManager;
    Context context;

    Toolbar toolbarHistoryTopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_topup);
        initView();
    }

    private void initView(){
        context = this;
        sharedPrefManager = new SharedPrefManager(context);
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
}
