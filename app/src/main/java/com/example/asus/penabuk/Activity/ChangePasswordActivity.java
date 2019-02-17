package com.example.asus.penabuk.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.asus.penabuk.R;

public class ChangePasswordActivity extends AppCompatActivity {

    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initView();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView(){
        imgBack = (ImageView)findViewById(R.id.imgBack);
    }
}
