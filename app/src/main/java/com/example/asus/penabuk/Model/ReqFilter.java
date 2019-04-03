package com.example.asus.penabuk.Model;

import android.widget.Toast;

import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReqFilter {

    private List<Book> filters;

    public List<Book> getFilters() {
        return filters;
    }

    public void setFilters(List<Book> filters) {
        this.filters = filters;
    }

}
