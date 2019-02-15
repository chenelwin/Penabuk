package com.example.asus.penabuk.Remote;

import com.example.asus.penabuk.Model.ReqBook;
import com.example.asus.penabuk.Model.ReqBookId;
import com.example.asus.penabuk.Model.ReqUser;
import com.example.asus.penabuk.Model.ResUser;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {
    @POST("/login")
    Call<ResUser> loginRequest(@Body ReqUser reqUser);

    @POST("/register")
    Call<ResUser> registerRequest(@Body ReqUser reqUser);

    @GET("/books")
    Call<ReqBook> getBookRequest();

    @GET("/books/{id}")
    Call<ReqBookId> getBookById(@Path("id") Integer id);
}
