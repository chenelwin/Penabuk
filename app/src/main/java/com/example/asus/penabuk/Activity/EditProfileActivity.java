package com.example.asus.penabuk.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.asus.penabuk.R;

public class EditProfileActivity extends AppCompatActivity {

    ImageView imgBack;
    Button btnAddAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initView();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, AddAddressActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView(){
        imgBack = (ImageView) findViewById(R.id.imgBack);
        btnAddAddress = (Button)findViewById(R.id.btnAddAddress);
    }
}
