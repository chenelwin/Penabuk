package com.example.asus.penabuk.Remote;

public class ApiUtils {
    public static final String BASE_URL = "https://penabuk.herokuapp.com";

    public static UserService getUserService(){
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }
}
