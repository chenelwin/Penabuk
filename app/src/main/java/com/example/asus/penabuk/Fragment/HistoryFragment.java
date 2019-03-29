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
import android.widget.Toast;

import com.example.asus.penabuk.Adapter.HistoryFragmentAdapter;
import com.example.asus.penabuk.Model.History;
import com.example.asus.penabuk.Model.ReqHistory;
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

    Toolbar toolbarHistory;

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
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        rvHistoryFragment = (RecyclerView)view.findViewById(R.id.RvHistoryFragment);
    }

    private void initToolbar(){
        toolbarHistory = (Toolbar)view.findViewById(R.id.toolbarHistory);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbarHistory);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Riwayat");
    }

    @Override
    public void onResume(){
        super.onResume();
        doGetHistory(userId);
    }

    public void doGetHistory(Integer id){
        Call<ReqHistory> call = userService.getHistory(id);
        call.enqueue(new Callback<ReqHistory>() {
            @Override
            public void onResponse(Call<ReqHistory> call, Response<ReqHistory> response) {
                ReqHistory reqHistory = response.body();
                histories = reqHistory.getHistories();
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
}
