package com.example.asus.penabuk.Remote;

import com.example.asus.penabuk.Model.Address;
import com.example.asus.penabuk.Model.Payment;
import com.example.asus.penabuk.Model.ReqAddress;
import com.example.asus.penabuk.Model.ReqBook;
import com.example.asus.penabuk.Model.ReqBookId;
import com.example.asus.penabuk.Model.ReqCart;
import com.example.asus.penabuk.Model.ReqChangePassword;
import com.example.asus.penabuk.Model.ReqCity;
import com.example.asus.penabuk.Model.ReqDistrict;
import com.example.asus.penabuk.Model.ReqHistory;
import com.example.asus.penabuk.Model.ReqHistoryId;
import com.example.asus.penabuk.Model.ReqProvince;
import com.example.asus.penabuk.Model.ReqUser;
import com.example.asus.penabuk.Model.ResMessage;
import com.example.asus.penabuk.Model.ResUser;
import com.example.asus.penabuk.Model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {
    @POST("/login")
    Call<ResUser> loginRequest(@Body ReqUser reqUser);

    @POST("/register")
    Call<ResUser> registerRequest(@Body ReqUser reqUser);

    @POST("/addresses")
    Call<ResMessage> addAddressRequest(@Body Address address, @Query("token") Integer id);

    @POST("/payments")
    Call<ResMessage> paymentRequest(@Body List<Payment> payments, @Query("token") Integer id);

    @POST("/users/profile")
    Call<ResMessage> changeProfileRequest(@Body User user, @Query("token") Integer id);

    @POST("/users/password")
    Call<ResMessage> changePasswordRequest(@Body ReqChangePassword reqChangePassword, @Query("token") Integer id);

    @GET("/books")
    Call<ReqBook> getBookRequest(@Query("token") Integer id, @Query("page") Integer page);

    @GET("/books/{id}")
    Call<ReqBookId> getBookById(@Path("id") Integer id);

    @GET("/provinces")
    Call<ReqProvince> getProvince();

    @GET("/cities")
    Call<ReqCity> getCity(@Query("province_id") Integer id);

    @GET("/districts")
    Call<ReqDistrict> getDistrict(@Query("city_id") Integer id);

    @GET("/addresses")
    Call<ReqAddress> getAddress(@Query("token") Integer id);

    @GET("/users")
    Call<ResUser> getUser(@Query("token") Integer id);

    @GET("/histories")
    Call<ReqHistory> getHistory(@Query("token") Integer id);

    @GET("/histories/{order_id}")
    Call<ReqHistoryId> getHistoryById(@Path("order_id") String orderId, @Query("token") Integer userId);

    @GET("/carts")
    Call<ReqCart> getCart(@Query("token") Integer id);

}
