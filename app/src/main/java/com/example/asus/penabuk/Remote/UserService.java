package com.example.asus.penabuk.Remote;

import com.example.asus.penabuk.Model.ReqUser;
import com.example.asus.penabuk.Model.ResUser;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("/login")
    Call<ResUser> loginRequest(@Body ReqUser reqUser);

    @POST("/register")
    Call<ResUser> registerRequest(@Body ReqUser reqUser);
}
