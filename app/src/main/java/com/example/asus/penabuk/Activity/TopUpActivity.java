package com.example.asus.penabuk.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.asus.penabuk.ErrorUtils.ErrorUtils;
import com.example.asus.penabuk.Model.ResMessage;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Callback;
import retrofit2.Response;

public class TopUpActivity extends AppCompatActivity {

    Context context;
    UserService userService = ApiUtils.getUserService();
    SharedPrefManager sharedPrefManager;
    ImageView imgUpload;
    EditText balanceUpload;
    Button btnUpload;
    File finalFile;
    Integer userId;
    Boolean imgAttached;
    ProgressDialog progressDialog;

    Toolbar toolbarTopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);
        initView();

        imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyPermission();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String balance = balanceUpload.getText().toString();
                if(validateImage(imgAttached, balance)){
                    progressDialog = ProgressDialog.show(context, null, "Please Wait..", true);
                    doUpload(finalFile, balance);
                }
            }
        });
    }

    private void initView(){
        context = this;
        sharedPrefManager = new SharedPrefManager(context);
        initToolbar();
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        imgAttached = false;
        imgUpload = (ImageView)findViewById(R.id.imgUpload);
        balanceUpload = (EditText)findViewById(R.id.balanceUpload);
        btnUpload = (Button)findViewById(R.id.btnUpload);
    }

    private void initToolbar(){
        toolbarTopup = (Toolbar)findViewById(R.id.toolbarTopup);
        setSupportActionBar(toolbarTopup);
        getSupportActionBar().setTitle("Top Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarTopup.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0){
            if(resultCode==RESULT_OK && data!=null){
                Bitmap imgdata = (Bitmap) data.getExtras().get("data");
                imgUpload.setImageBitmap(imgdata);
                Uri tempUri = getImageUri(getApplicationContext(), imgdata);
                finalFile = new File(getRealPathFromUri(tempUri));
                imgAttached = true;
                Log.e("Nama Path: ", ""+ getRealPathFromUri(tempUri));
            }
        }
        else if(requestCode==1){
            if(resultCode==RESULT_OK && data!=null){
                Uri imgdata = data.getData();
                imgUpload.setImageURI(imgdata);
                finalFile = new File(getRealPathFromUri(imgdata));
                imgAttached = true;
                Log.e("Nama Path2: ", ""+getRealPathFromUri(imgdata));
            }
        }
    }

    private boolean validateImage(Boolean imgAttached, String mbalance){
        if(mbalance == null || mbalance.trim().length() == 0){
            balanceUpload.setError("Saldo harus diisi");
            return false;
        }
        if(!imgAttached){
            Toast.makeText(this, "Gambar harus diisi", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void verifyPermission(){
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(TopUpActivity.this, permissions[0]) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(TopUpActivity.this, permissions[1]) == PackageManager.PERMISSION_GRANTED){
            final String[] option = {"Kamera", "Pilih Galeri"};
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setItems(option, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (option[i].equals("Kamera")) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 0);
                    } else if (option[i].equals("Pilih Galeri")) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 1);
                    }
                }
            });
            builder.show();
        }
        else {
            ActivityCompat.requestPermissions(TopUpActivity.this, permissions, 100);
        }
    }

    private void doUpload(File file, String mbalance){
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part image = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        RequestBody balance = RequestBody.create(MediaType.parse("multipart/form-data"), mbalance);

        retrofit2.Call<ResMessage> call = userService.uploadRequest(balance, image, userId);
        call.enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(retrofit2.Call<ResMessage> call, Response<ResMessage> response) {
                if(response.isSuccessful()) {
                    ResMessage resMessage = response.body();
                    Toast.makeText(context, resMessage.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    ResMessage resMessage = ErrorUtils.parseError(response);
                    Toast.makeText(TopUpActivity.this, resMessage.getMessage(), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(retrofit2.Call<ResMessage> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private Uri getImageUri(Context c, Bitmap bitmap){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(c.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private String getRealPathFromUri(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(index);
    }
}
