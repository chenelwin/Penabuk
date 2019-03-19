package com.example.asus.penabuk.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.asus.penabuk.R;

public class TopUpActivity extends AppCompatActivity {

    Context context;
    ImageView imgBack;
    ImageView imgUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);
        initView();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] option = {"Kamera", "Pilih Galeri"};
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(option[i].equals("Kamera")){
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 0);
                        }
                        else if(option[i].equals("Pilih Galeri")){
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, 1);
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private void initView(){
        context = this;
        imgBack = (ImageView)findViewById(R.id.imgBack);
        imgUpload = (ImageView)findViewById(R.id.imgUpload);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0){
            if(resultCode==RESULT_OK && data!=null){
                Uri imgdata = data.getData();
                imgUpload.setImageURI(imgdata);
            }
        }
        else if(requestCode==1){
            if(resultCode==RESULT_OK && data!=null){
                Uri imgdata = data.getData();
                imgUpload.setImageURI(imgdata);
            }
        }
    }
}
