package com.example.asus.penabuk.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.penabuk.Activity.AddressActivity;
import com.example.asus.penabuk.Activity.EditProfileActivity;
import com.example.asus.penabuk.Activity.HistoryBalanceActivity;
import com.example.asus.penabuk.Activity.HistoryTopupActivity;
import com.example.asus.penabuk.Activity.LoginActivity;
import com.example.asus.penabuk.Activity.NotificationActivity;
import com.example.asus.penabuk.Activity.TopUpActivity;
import com.example.asus.penabuk.Activity.ViewAllActivity;
import com.example.asus.penabuk.Adapter.HomeFragmentAdapter;
import com.example.asus.penabuk.Adapter.ViewAllAdapter;
import com.example.asus.penabuk.Model.Book;
import com.example.asus.penabuk.Model.HistoryBalance;
import com.example.asus.penabuk.Model.ReqBook;
import com.example.asus.penabuk.Model.ReqFilter;
import com.example.asus.penabuk.Model.ReqNotification;
import com.example.asus.penabuk.Model.ResUser;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    UserService userService = ApiUtils.getUserService();
    SharedPrefManager sharedPrefManager;
    public View view;
    RecyclerView rvHomeFragment;
    HomeFragmentAdapter homeFragmentAdapter;
    GridLayoutManager rvManager;
    List<Book> books;
    Integer userId;

    //EndlessScroll
    private int previousTotal = 0;
    private boolean loading = true;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    ProgressBar loadingNext;
    int page=1;

    //ToolBar
    DrawerLayout drawerHome;
    Toolbar toolbarHome;
    NavigationView navigation_drawer;
    TextView textBadge;
    Integer notifcount = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        initView();
        doGetNotification();
        doGetBook(userId, page);

        return view;
    }

    private void initView(){
        sharedPrefManager = new SharedPrefManager(view.getContext());
        initToolbar();
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        rvHomeFragment = (RecyclerView)view.findViewById(R.id.RvHomeFragment);
        loadingNext = (ProgressBar)view.findViewById(R.id.loadingNext);
    }

    private void initToolbar(){
        drawerHome = (DrawerLayout)view.findViewById(R.id.drawerHome);
        navigation_drawer = (NavigationView)view.findViewById(R.id.navigation_drawer);
        //header drawer
        initDrawerHeaderData(navigation_drawer);


        toolbarHome = (Toolbar)view.findViewById(R.id.toolbarHome);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbarHome);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Penabuk");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        toolbarHome.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerHome.openDrawer(GravityCompat.START);
            }
        });

        navigation_drawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId())
                {
                    case R.id.nav_editprofile:
                        item.setChecked(true);
                        intent = new Intent(view.getContext(), EditProfileActivity.class);
                        drawerHome.closeDrawers();
                        view.getContext().startActivity(intent);
                        return true;
                    case R.id.nav_address:
                        item.setChecked(true);
                        intent = new Intent(view.getContext(), AddressActivity.class);
                        drawerHome.closeDrawers();
                        view.getContext().startActivity(intent);
                        return true;
                    case R.id.nav_historybalance:
                        item.setChecked(true);
                        intent = new Intent(view.getContext(), HistoryBalanceActivity.class);
                        drawerHome.closeDrawers();
                        view.getContext().startActivity(intent);
                        return true;
                    case R.id.nav_historytopup:
                        item.setChecked(true);
                        intent = new Intent(view.getContext(), HistoryTopupActivity.class);
                        drawerHome.closeDrawers();
                        view.getContext().startActivity(intent);
                        return true;
                    case R.id.nav_logout:
                        item.setChecked(true);
                        intent = new Intent(view.getContext(), LoginActivity.class);
                        sharedPrefManager.saveSPString(SharedPrefManager.SP_EMAIL, "");
                        sharedPrefManager.saveSPString(SharedPrefManager.SP_ID, "");
                        sharedPrefManager.saveSPString(SharedPrefManager.SP_NAMA, "");
                        sharedPrefManager.saveSPString(SharedPrefManager.SP_BALANCE, "");
                        sharedPrefManager.saveSPString(SharedPrefManager.SP_NOHP, "");
                        sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN, false);
                        startActivity(intent);
                        getActivity().finish();
                        return true;
                }

                return false;
            }
        });
    }

    private void initDrawerHeaderData(NavigationView navdrawer){
        View headDrawer = navdrawer.getHeaderView(0);
        CircleImageView headDrawerImage = (CircleImageView)headDrawer.findViewById(R.id.headDrawerImage);
        TextView headDrawerName = (TextView)headDrawer.findViewById(R.id.headDrawerName);
        TextView headDrawerEmail = (TextView)headDrawer.findViewById(R.id.headDrawerEmail);
        Picasso.with(view.getContext())
                .load(ApiUtils.BASE_URL+"/image?id="+sharedPrefManager.getSPImage())
                .centerCrop()
                .resize(60, 60)
                .into(headDrawerImage);
        headDrawerName.setText(sharedPrefManager.getSPNama());
        headDrawerEmail.setText(sharedPrefManager.getSPEmail());

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        final MenuItem menuItem = menu.findItem(R.id.item_notification);
        View notifview = menuItem.getActionView();
        textBadge = (TextView)notifview.findViewById(R.id.textBadge);
        notifview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(menuItem);
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.item_notification:
                Intent intent = new Intent(view.getContext(), NotificationActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        doGetNotification();
        //doGetUser(userId);
    }

    private void doGetNotification(){
        Call<ReqNotification> call = userService.getNotification(userId);
        call.enqueue(new Callback<ReqNotification>() {
            @Override
            public void onResponse(Call<ReqNotification> call, Response<ReqNotification> response) {
                ReqNotification reqNotification = response.body();
                notifcount = reqNotification.getCount();
                setNotificationBadge(notifcount);
            }

            @Override
            public void onFailure(Call<ReqNotification> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setNotificationBadge(Integer notifcount){
        if(textBadge!=null){
            if(notifcount==0){
                if(textBadge.getVisibility()!=View.GONE){
                    textBadge.setVisibility(View.GONE);
                }
            }
            else {
                textBadge.setText(notifcount.toString());
                if(textBadge.getVisibility()!=View.VISIBLE){
                    textBadge.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    private void doGetBook(Integer id, Integer page){
        Call<ReqBook> call = userService.getBookRequest(id, page);
        call.enqueue(new Callback<ReqBook>() {
            @Override
            public void onResponse(final Call<ReqBook> call, Response<ReqBook> response) {
                final ReqBook reqBook = response.body();
                books = reqBook.getBooks();
                homeFragmentAdapter = new HomeFragmentAdapter(books);

                rvManager = new GridLayoutManager(view.getContext(), 2);
                rvManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        switch (homeFragmentAdapter.getItemViewType(position)){
                            case HomeFragmentAdapter.LAYOUT_HEAD:
                                return 2;
                            case HomeFragmentAdapter.LAYOUT_LIST:
                                return 1;
                            default:
                                return 2;
                        }
                    }
                });
                rvHomeFragment.setLayoutManager(rvManager);
                rvHomeFragment.setItemAnimator(new DefaultItemAnimator());
                rvHomeFragment.setAdapter(homeFragmentAdapter);
                endlessScroll();
            }

            @Override
            public void onFailure(Call<ReqBook> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doGetNextPage(Integer id, Integer page){
        loadingNext.setVisibility(View.VISIBLE);
        Call<ReqBook> call = userService.getBookRequest(id, page);
        call.enqueue(new Callback<ReqBook>() {
            @Override
            public void onResponse(Call<ReqBook> call, Response<ReqBook> response) {
                ReqBook reqBook = response.body();
                List<Book> nextBook = reqBook.getBooks();
                for(int i=0; i<nextBook.size(); i++){
                    books.add(nextBook.get(i));
                }
                homeFragmentAdapter.notifyDataSetChanged();
                loadingNext.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ReqBook> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                loadingNext.setVisibility(View.GONE);
            }
        });
    }
    /*
    private void doGetUser(Integer id){
        Call<ResUser> call = userService.getUser(id);
        call.enqueue(new Callback<ResUser>() {
            @Override
            public void onResponse(Call<ResUser> call, Response<ResUser> response) {
                ResUser resUser = response.body();
                String updbalance = resUser.getUser().getBalance().toString();
                sharedPrefManager.saveSPString(SharedPrefManager.SP_BALANCE, updbalance);

                Integer tmpbalance = Integer.parseInt(sharedPrefManager.getSPBalance());
                DecimalFormat formatter = new DecimalFormat("#,###,###");
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setGroupingSeparator('.');
                formatter.setDecimalFormatSymbols(symbols);
                String priceformat = formatter.format(tmpbalance);
                balance.setText("Rp. " + priceformat);
            }

            @Override
            public void onFailure(Call<ResUser> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    private void endlessScroll(){
        rvHomeFragment.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = rvHomeFragment.getChildCount();
                totalItemCount = rvManager.getItemCount();
                firstVisibleItem = rvManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if(!loading && (visibleItemCount+firstVisibleItem)==totalItemCount){
                    // End has been reached
                    page++;
                    doGetNextPage(userId, page);

                    loading = true;
                }
            }
        });
    }



}
