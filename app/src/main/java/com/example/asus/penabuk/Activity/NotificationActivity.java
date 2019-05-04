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

import com.example.asus.penabuk.Adapter.NotificationAdapter;
import com.example.asus.penabuk.Model.Notification;
import com.example.asus.penabuk.Model.ReqNotification;
import com.example.asus.penabuk.Model.ResMessage;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    SharedPrefManager sharedPrefManager;
    Context context;
    Integer userId;
    RecyclerView rvNotification;
    NotificationAdapter notificationAdapter;
    List<Notification> notifications;
    LinearLayout layoutNoNotification;
    Toolbar toolbarNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initView();
        doGetNotification();
        doReadNotification();
    }

    private void initView(){
        context = this;
        sharedPrefManager = new SharedPrefManager(context);
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        initToolbar();
        rvNotification = (RecyclerView)findViewById(R.id.rvNotification);
        layoutNoNotification = (LinearLayout)findViewById(R.id.layoutNoNotification);

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

    private void doGetNotification(){
        Call<ReqNotification> call = userService.getNotification(userId);
        call.enqueue(new Callback<ReqNotification>() {
            @Override
            public void onResponse(Call<ReqNotification> call, Response<ReqNotification> response) {
                ReqNotification reqNotification = response.body();
                notifications = reqNotification.getNotifications();
                checkNotificationSize(notifications);
                notificationAdapter = new NotificationAdapter(notifications);
                rvNotification.setLayoutManager(new LinearLayoutManager(context));
                rvNotification.setItemAnimator(new DefaultItemAnimator());
                rvNotification.setAdapter(notificationAdapter);
            }

            @Override
            public void onFailure(Call<ReqNotification> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkNotificationSize(List<Notification> notifications){
        if(notifications.size()==0){
            layoutNoNotification.setVisibility(View.VISIBLE);
        }
        else{
            layoutNoNotification.setVisibility(View.GONE);
        }
    }

    private void doReadNotification(){
        Call<ResMessage> call = userService.readNotificationRequest(userId);
        call.enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                ResMessage resMessage = response.body();
            }

            @Override
            public void onFailure(Call<ResMessage> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
