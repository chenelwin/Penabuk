package com.example.asus.penabuk.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.asus.penabuk.Adapter.HistoryFragmentAdapter;
import com.example.asus.penabuk.Model.Cart;
import com.example.asus.penabuk.Model.History;
import com.example.asus.penabuk.Model.OrderStatus;
import com.example.asus.penabuk.Model.ReqHistory;
import com.example.asus.penabuk.Model.ReqOrderStatus;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    UserService userService = ApiUtils.getUserService();
    SharedPrefManager sharedPrefManager;
    public View view;
    Integer userId;
    RecyclerView rvHistoryFragment;
    HistoryFragmentAdapter historyFragmentAdapter;
    List<History> histories;

    LinearLayout layoutNoHistory;

    Toolbar toolbarHistory;

    //dropdown
    Spinner spinnerHistory;
    ArrayAdapter<OrderStatus> spinnerHistoryAdapter;
    List<OrderStatus> orderStatuses;
    Integer orderType = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history, container, false);
        initView();

        return view;
    }

    public void initView(){
        sharedPrefManager = new SharedPrefManager(view.getContext());
        initToolbar();
        layoutNoHistory = (LinearLayout)view.findViewById(R.id.layoutNoHistory);
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        rvHistoryFragment = (RecyclerView)view.findViewById(R.id.RvHistoryFragment);
    }

    private void initToolbar(){
        toolbarHistory = (Toolbar)view.findViewById(R.id.toolbarHistory);
        initDropdownFilter();
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbarHistory);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Riwayat");
    }

    private void initDropdownFilter(){
        spinnerHistory = (Spinner)view.findViewById(R.id.spinnerHistory);
        doGetOrderStatus();
    }

    @Override
    public void onResume(){
        super.onResume();
        doGetOrderStatus();
        doGetHistory(userId, orderType);
    }

    public void doGetHistory(Integer id, Integer orderType){
        Call<ReqHistory> call = userService.getHistory(id, orderType);
        call.enqueue(new Callback<ReqHistory>() {
            @Override
            public void onResponse(Call<ReqHistory> call, Response<ReqHistory> response) {
                ReqHistory reqHistory = response.body();
                histories = reqHistory.getHistories();
                checkHistorySize(histories);
                historyFragmentAdapter = new HistoryFragmentAdapter(histories);

                rvHistoryFragment.setLayoutManager(new LinearLayoutManager(view.getContext()));
                rvHistoryFragment.setItemAnimator(new DefaultItemAnimator());
                rvHistoryFragment.setAdapter(historyFragmentAdapter);
            }

            @Override
            public void onFailure(Call<ReqHistory> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkHistorySize(List<History> histories){
        if(histories.size()==0){
            layoutNoHistory.setVisibility(View.VISIBLE);
        }
        else if(histories.size()>0){
            layoutNoHistory.setVisibility(View.GONE);
        }
    }

    private void doGetOrderStatus(){
        Call<ReqOrderStatus> call = userService.getOrderStatus();
        call.enqueue(new Callback<ReqOrderStatus>() {
            @Override
            public void onResponse(Call<ReqOrderStatus> call, Response<ReqOrderStatus> response) {
                ReqOrderStatus reqOrderStatus = response.body();
                orderStatuses = reqOrderStatus.getOrderStatuses();
                OrderStatus semua = new OrderStatus();
                semua.setId(0);semua.setName("Semua");
                orderStatuses.add(0, semua);
                spinnerHistoryAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, orderStatuses);
                spinnerHistoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerHistory.setAdapter(spinnerHistoryAdapter);

                spinnerHistory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        OrderStatus orderStatus = (OrderStatus) spinnerHistory.getSelectedItem();
                        doGetHistory(userId, orderStatus.getId());

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

            }

            @Override
            public void onFailure(Call<ReqOrderStatus> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
