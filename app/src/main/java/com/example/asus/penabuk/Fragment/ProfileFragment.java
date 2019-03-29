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

import com.example.asus.penabuk.Activity.AddressActivity;
import com.example.asus.penabuk.Activity.ChangePasswordActivity;
import com.example.asus.penabuk.Activity.EditProfileActivity;
import com.example.asus.penabuk.Activity.LoginActivity;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

public class ProfileFragment extends Fragment {

    public View view;
    LinearLayout btnEditProfile;
    LinearLayout btnAddress;
    LinearLayout btnChangePassword;
    LinearLayout btnLogout;
    SharedPrefManager sharedPrefManager;
    TextView profileNama;

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
        btnEditProfile = (LinearLayout)view.findViewById(R.id.btnEditProfile);
        btnAddress = (LinearLayout)view.findViewById(R.id.btnAddress);
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
    }
}
