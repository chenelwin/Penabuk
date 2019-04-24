package com.example.asus.penabuk.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.penabuk.Activity.AddressActivity;
import com.example.asus.penabuk.Activity.ChangePasswordActivity;
import com.example.asus.penabuk.Activity.EditProfileActivity;
import com.example.asus.penabuk.Activity.HistoryBalanceActivity;
import com.example.asus.penabuk.Activity.HistoryTopupActivity;
import com.example.asus.penabuk.Activity.LoginActivity;
import com.example.asus.penabuk.Model.ResUser;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    public View view;
    LinearLayout btnEditProfile;
    LinearLayout btnAddress;
    LinearLayout btnHistoryBalance;
    LinearLayout btnHistoryTopup;
    LinearLayout btnChangePassword;
    LinearLayout btnLogout;
    SharedPrefManager sharedPrefManager;
    UserService userService = ApiUtils.getUserService();
    Integer userId;
    TextView profileNama;
    CircleImageView profileImage;

    Toolbar toolbarProfile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initView();

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        btnAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddressActivity.class);
                startActivity(intent);
            }
        });

        btnHistoryBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), HistoryBalanceActivity.class);
                startActivity(intent);
            }
        });

        btnHistoryTopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), HistoryTopupActivity.class);
                startActivity(intent);
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                sharedPrefManager.saveSPString(SharedPrefManager.SP_EMAIL, "");
                sharedPrefManager.saveSPString(SharedPrefManager.SP_ID, "");
                sharedPrefManager.saveSPString(SharedPrefManager.SP_NAMA, "");
                sharedPrefManager.saveSPString(SharedPrefManager.SP_BALANCE, "");
                sharedPrefManager.saveSPString(SharedPrefManager.SP_NOHP, "");
                sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN, false);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    private void initView(){
        sharedPrefManager = new SharedPrefManager(view.getContext());
        initToolbar();
        profileNama = (TextView)view.findViewById(R.id.profileNama);
        profileNama.setText(sharedPrefManager.getSPNama());
        profileImage = (CircleImageView)view.findViewById(R.id.profileImage);
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        btnEditProfile = (LinearLayout)view.findViewById(R.id.btnEditProfile);
        btnAddress = (LinearLayout)view.findViewById(R.id.btnAddress);
        btnHistoryBalance = (LinearLayout)view.findViewById(R.id.btnHistoryBalance);
        btnHistoryTopup = (LinearLayout)view.findViewById(R.id.btnHistoryTopup);
        btnChangePassword = (LinearLayout)view.findViewById(R.id.btnChangePassword);
        btnLogout = (LinearLayout)view.findViewById(R.id.btnLogout);
    }

    private void initToolbar(){
        toolbarProfile = (Toolbar)view.findViewById(R.id.toolbarProfile);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbarProfile);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Profil");
    }

    @Override
    public void onResume(){
        super.onResume();
        profileNama.setText(sharedPrefManager.getSPNama());
        doGetUser(userId);
    }

    private void doGetUser(Integer userId){
        Call<ResUser> call = userService.getUser(userId);
        call.enqueue(new Callback<ResUser>() {
            @Override
            public void onResponse(Call<ResUser> call, Response<ResUser> response) {
                ResUser resUser = response.body();
                sharedPrefManager.saveSPString(SharedPrefManager.SP_IMAGE, resUser.getUser().getImage_url());
                Picasso.with(view.getContext())
                        .load(ApiUtils.BASE_URL+"/image?id="+sharedPrefManager.getSPImage())
                        .centerCrop()
                        .resize(60, 60)
                        .into(profileImage);
            }

            @Override
            public void onFailure(Call<ResUser> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
