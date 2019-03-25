package com.example.asus.penabuk.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.asus.penabuk.R;

import java.io.IOException;

public class TopUpActivity extends AppCompatActivity {

    Context context;
    ImageView imgBack;
    ImageView imgUpload;
    EditText balanceUpload;
    Button btnUpload;

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

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initView(){
        context = this;
        imgBack = (ImageView)findViewById(R.id.imgBack);
        imgUpload = (ImageView)findViewById(R.id.imgUpload);
        balanceUpload = (EditText)findViewById(R.id.balanceUpload);
        btnUpload = (Button)findViewById(R.id.btnUpload);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0){
            if(resultCode==RESULT_OK && data!=null){
                Bitmap imgdata = (Bitmap) data.getExtras().get("data");
                imgUpload.setImageBitmap(imgdata);
            }
        }
        else if(requestCode==1){
            if(resultCode==RESULT_OK && data!=null){
                Uri imgdata = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgdata);
                    imgUpload.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doUpload(){

    }
}
