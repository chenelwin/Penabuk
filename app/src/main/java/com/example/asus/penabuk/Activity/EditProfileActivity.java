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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.asus.penabuk.ErrorUtils.ErrorUtils;
import com.example.asus.penabuk.Model.Address;
import com.example.asus.penabuk.Model.ReqAddress;
import com.example.asus.penabuk.Model.ResMessage;
import com.example.asus.penabuk.Model.User;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    Context context;
    SharedPrefManager sharedPrefManager;
    Integer userId;
    EditText editEmail;
    EditText editName;
    EditText editPhonenumber;
    Button btnSimpan;
    ProgressDialog progressDialog;
    CircleImageView userProfile;
    Boolean imgAttached;
    File finalFile;

    Toolbar toolbarEditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initView();

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyPermission();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editName.getText().toString();
                String phone_number = editPhonenumber.getText().toString();
                if(validateChangeProfile(name,phone_number)) {
                    progressDialog = ProgressDialog.show(context, null, "Please Wait..", true);
                    doChangeProfile(userId, name, phone_number);
                }
                if(imgAttached){
                    doChangeImage(finalFile);
                }
            }
        });
    }

    private void initView(){
        context = this;
        sharedPrefManager = new SharedPrefManager(context);
        initToolbar();
        editEmail = (EditText)findViewById(R.id.editEmail);
        editEmail.setText(sharedPrefManager.getSPEmail());
        editName = (EditText)findViewById(R.id.editName);
        editName.setText(sharedPrefManager.getSPNama());
        editPhonenumber = (EditText)findViewById(R.id.editPhonenumber);
        editPhonenumber.setText(sharedPrefManager.getSPNohp());
        userProfile = (CircleImageView)findViewById(R.id.userProfile);
        doGetProfileImage();
        imgAttached = false;

        btnSimpan = (Button)findViewById(R.id.btnSimpan);

        userId = Integer.parseInt(sharedPrefManager.getSPId());
    }

    private void initToolbar(){
        toolbarEditProfile = (Toolbar)findViewById(R.id.toolbarEditProfile);
        setSupportActionBar(toolbarEditProfile);
        getSupportActionBar().setTitle("Edit Profil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarEditProfile.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void verifyPermission(){
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(EditProfileActivity.this, permissions[0]) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(EditProfileActivity.this, permissions[1]) == PackageManager.PERMISSION_GRANTED){
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
            ActivityCompat.requestPermissions(EditProfileActivity.this, permissions, 100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0){
            if(resultCode==RESULT_OK && data!=null){
                Bitmap imgdata = (Bitmap) data.getExtras().get("data");
                userProfile.setImageBitmap(imgdata);
                Uri tempUri = getImageUri(context, imgdata);
                finalFile = new File(getRealPathFromUri(tempUri));
                imgAttached = true;
                Log.e("Nama Path: ", ""+ getRealPathFromUri(tempUri));
            }
        }
        else if(requestCode==1){
            if(resultCode==RESULT_OK && data!=null){
                Uri imgdata = data.getData();
                userProfile.setImageURI(imgdata);
                finalFile = new File(getRealPathFromUri(imgdata));
                imgAttached = true;
                Log.e("Nama Path2: ", ""+getRealPathFromUri(imgdata));
            }
        }
    }

    private boolean validateChangeProfile(String name, String phone_number){
        if(name == null || name.trim().length() == 0){
            editName.setError("Nama harus diisi.");
            return false;
        }
        if(phone_number == null || phone_number.trim().length() == 0){
            editPhonenumber.setError("Nomor HP harus diisi.");
            return false;
        }
        return true;
    }

    private void doGetProfileImage(){
        Picasso.with(context)
                .load(ApiUtils.BASE_URL+"/image?id="+sharedPrefManager.getSPImage())
                .centerCrop()
                .resize(200, 200)
                .into(userProfile);
    }

    private void doChangeProfile(Integer id, final String name, final String phone_number){
        User user = new User();
        user.setName(name);
        user.setPhone_number(phone_number);
        Call<ResMessage> call = userService.changeProfileRequest(user, id);
        call.enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                if(response.isSuccessful()) {
                    ResMessage resMessage = response.body();
                    Toast.makeText(context, resMessage.getMessage(), Toast.LENGTH_SHORT).show();
                    sharedPrefManager.saveSPString(SharedPrefManager.SP_NAMA, name);
                    sharedPrefManager.saveSPString(SharedPrefManager.SP_NOHP, phone_number);
                    finish();
                    progressDialog.dismiss();
                }
                else {
                    ResMessage resMessage = ErrorUtils.parseError(response);
                    Toast.makeText(EditProfileActivity.this, resMessage.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResMessage> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void doChangeImage(final File file){
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part image = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        Call<ResMessage> call = userService.changeImageRequest(image, userId);
        call.enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                if(response.isSuccessful()) {
                    ResMessage resMessage = response.body();
                    Toast.makeText(context, resMessage.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                    progressDialog.dismiss();
                }
                else {
                    ResMessage resMessage = ErrorUtils.parseError(response);
                    Toast.makeText(EditProfileActivity.this, resMessage.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResMessage> call, Throwable t) {
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
