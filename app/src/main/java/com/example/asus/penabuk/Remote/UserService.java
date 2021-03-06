package com.example.asus.penabuk.Remote;

import com.example.asus.penabuk.Model.Address;
import com.example.asus.penabuk.Model.BestDealPrice;
import com.example.asus.penabuk.Model.Order;
import com.example.asus.penabuk.Model.Payment;
import com.example.asus.penabuk.Model.ReqAddress;
import com.example.asus.penabuk.Model.ReqBook;
import com.example.asus.penabuk.Model.ReqBookId;
import com.example.asus.penabuk.Model.ReqCancelOrder;
import com.example.asus.penabuk.Model.ReqCart;
import com.example.asus.penabuk.Model.ReqChangePassword;
import com.example.asus.penabuk.Model.ReqCity;
import com.example.asus.penabuk.Model.ReqDistrict;
import com.example.asus.penabuk.Model.ReqFilter;
import com.example.asus.penabuk.Model.ReqHistory;
import com.example.asus.penabuk.Model.ReqHistoryBalance;
import com.example.asus.penabuk.Model.ReqHistoryId;
import com.example.asus.penabuk.Model.ReqHistoryTopup;
import com.example.asus.penabuk.Model.ReqNotification;
import com.example.asus.penabuk.Model.ReqOrderStatus;
import com.example.asus.penabuk.Model.ReqProvince;
import com.example.asus.penabuk.Model.ReqReview;
import com.example.asus.penabuk.Model.ReqSlider;
import com.example.asus.penabuk.Model.ReqHistoryTopupId;
import com.example.asus.penabuk.Model.ReqUser;
import com.example.asus.penabuk.Model.ResMessage;
import com.example.asus.penabuk.Model.ResUser;
import com.example.asus.penabuk.Model.User;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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
    Call<ResMessage> paymentRequest(@Body Payment payment, @Query("token") Integer id);

    @POST("/users/profile")
    Call<ResMessage> changeProfileRequest(@Body User user, @Query("token") Integer id);

    @POST("/users/password")
    Call<ResMessage> changePasswordRequest(@Body ReqChangePassword reqChangePassword, @Query("token") Integer id);

    @POST("/users/password/reset")
    Call<ResMessage> resetPasswordRequest(@Body User user);

    @POST("/carts")
    Call<ResMessage> addCartRequest(@Body Order order, @Query("token") Integer id);

    @POST("/histories/{id}/cancel")
    Call<ResMessage> cancelOrderRequest(@Path("id") String orderId, @Query("token") Integer userId, @Body ReqCancelOrder description);

    @POST("/histories/{id}/confirm")
    Call<ResMessage> confirmOrderRequest(@Path("id") String orderId, @Query("token") Integer userId);

    @POST("/remove/cart/{id}")
    Call<ResMessage> removeCartRequest(@Path("id") Integer cartId, @Query("token") Integer userId);

    @POST("/remove/address/{id}")
    Call<ResMessage> removeAddressRequest(@Path("id") Integer addressId, @Query("token") Integer userId);

    @POST("/books/rate")
    Call<ResMessage> reviewRequest(@Body ReqReview reqReview, @Query("token") Integer userId);

    @POST("/primary/address/{id}")
    Call<ResMessage> primaryAddressRequest(@Path("id") Integer addressId, @Query("token") Integer userId);

    @POST("/notifications/read")
    Call<ResMessage> readNotificationRequest(@Query("token") Integer userId);

    @POST("/remove/rate/{id}")
    Call<ResMessage> removeReviewRequest(@Path("id") Integer bookId, @Query("token") Integer userId);

    @POST("/users/verify")
    Call<ResMessage> verifyAccountRequest(@Body User user);

    @Multipart
    @POST("/topup/balance")
    Call<ResMessage> uploadRequest(@Part("balance") RequestBody balance, @Part MultipartBody.Part image, @Query("token") Integer userId);

    @Multipart
    @POST("/users/image")
    Call<ResMessage> changeImageRequest(@Part MultipartBody.Part image, @Query("token") Integer userId);

    @GET("/books")
    Call<ReqBook> getBookRequest(@Query("token") Integer id, @Query("page") Integer page);

    @GET("/books/{id}")
    Call<ReqBookId> getBookById(@Path("id") Integer bookId, @Query("token") Integer userId);

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
    Call<ReqHistory> getHistory(@Query("token") Integer id, @Query("orderType") Integer orderType);

    @GET("/histories/{order_id}")
    Call<ReqHistoryId> getHistoryById(@Path("order_id") String orderId, @Query("token") Integer userId);

    @GET("/carts")
    Call<ReqCart> getCart(@Query("token") Integer id);

    @GET("/search")
    Call<ReqBook> getBookByVoice(@Query("voice") String voiceKey);

    @GET("/bestdeals")
    Call<ReqBook> getBookByBestDeal(@Query("token") Integer userId, @Query("balance") String balance);

    @GET("/bestdeals/price")
    Call<BestDealPrice> getMinimalPrice();

    @GET("/filters")
    Call<ReqFilter> getSearchFilter();

    @GET("/search")
    Call<ReqBook> getBookByFilter(@Query("type") Integer filterType, @Query("value") String text);

    @GET("/admin/sliders")
    Call<ReqSlider> getSlider();

    @GET("/orderStatus")
    Call<ReqOrderStatus> getOrderStatus();

    @GET("/users/balance")
    Call<ReqHistoryBalance> getHistoryBalance(@Query("token") Integer userId);

    @GET("/users/topup")
    Call<ReqHistoryTopup> getHistoryTopup(@Query("token") Integer userId);

    @GET("/users/topup/{id}")
    Call<ReqHistoryTopupId> getHistoryTopupById(@Path("id") Integer topupId, @Query("token") Integer userId);

    @GET("/notifications")
    Call<ReqNotification> getNotification(@Query("token") Integer userId);

}
