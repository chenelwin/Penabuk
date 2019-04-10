package com.example.asus.penabuk.Activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.asus.penabuk.R;

public class NotificationActivity extends AppCompatActivity {

    Context context;

    Toolbar toolbarNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initView();
    }

    private void initView(){
        context = this;
        initToolbar();
    }

    private void initToolbar(){
        toolbarNotification = (Toolbar) findViewById(R.id.toolbarNotification);
        setSupportActionBar(toolbarNotification);
        getSupportActionBar().setTitle("Notifikasi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarNotification.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
