package com.example.asus.penabuk.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.asus.penabuk.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    ImageView imgBack;
    EditText inputEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
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
        inputEmail = (EditText)findViewById(R.id.inputEmail);
    }

}
