package com.example.asus.penabuk.ErrorUtils;

import com.example.asus.penabuk.Activity.LoginActivity;
import com.example.asus.penabuk.Model.ResMessage;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.RetrofitClient;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class ErrorUtils {

    public static ResMessage parseError(Response<?> response){
        Converter<ResponseBody, ResMessage> converter = RetrofitClient.getClient(ApiUtils.BASE_URL).responseBodyConverter(ResMessage.class, new Annotation[0]);
        ResMessage resMessage;
        try {
            resMessage = converter.convert(response.errorBody());
        }
        catch (IOException e){
            return new ResMessage();
        }

        return resMessage;
    }
}
