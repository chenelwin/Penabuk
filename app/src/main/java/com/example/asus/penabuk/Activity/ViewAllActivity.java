package com.example.asus.penabuk.Activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.penabuk.Adapter.ViewAllAdapter;
import com.example.asus.penabuk.Dialog.BestDealDialog;
import com.example.asus.penabuk.Model.Book;
import com.example.asus.penabuk.Model.Order;
import com.example.asus.penabuk.Model.ReqBook;
import com.example.asus.penabuk.Model.ResMessage;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAllActivity extends AppCompatActivity implements ViewAllAdapter.PassingBtnAdd, BestDealDialog.BestDealDialogListener {

    UserService userService = ApiUtils.getUserService();
    SharedPrefManager sharedPrefManager;
    Context context;
    ImageView btnMic;
    TextView searchKey;
    RecyclerView rvViewAllActivity;
    LinearLayoutManager rvManager;
    ViewAllAdapter viewAllAdapter;
    List<Book> books;
    ImageView imgBack;
    Integer userId;
    ProgressDialog progressDialog;

    //EndlessScroll
    private int previousTotal = 0;
    private boolean loading = true;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    ProgressBar loadingNext;
    int page=1;

    Toolbar toolbarViewAll;
    MaterialSearchView materialSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        initView();
        doGetBook(userId, page);

    }



    private void initView(){
        context = this;
        sharedPrefManager = new SharedPrefManager(context);
        initToolbar();
        ViewAllAdapter.passingBtnAdd = this;
        rvViewAllActivity = (RecyclerView)findViewById(R.id.RvViewAllActivity);
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        loadingNext = (ProgressBar)findViewById(R.id.loadingNext);
    }

    private void initToolbar(){
        toolbarViewAll = (Toolbar)findViewById(R.id.toolbarViewAll);
        setSupportActionBar(toolbarViewAll);
        getSupportActionBar().setTitle("Lihat Semua");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        materialSearchView = (MaterialSearchView)findViewById(R.id.materialSearchView);
        materialSearchView.setVoiceSearch(true);

        toolbarViewAll.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doGetBookByVoice(query);
                getSupportActionBar().setTitle(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_viewall, menu);
        MenuItem menuItem = menu.findItem(R.id.item_search);
        materialSearchView.setMenuItem(menuItem);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_mic:
                //saySomething();
                openBestDealDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void passData(Integer book_id, int position){
        progressDialog = ProgressDialog.show(context, null, "Please Wait..", true);
        doAddCart(book_id, userId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0){
            if(resultCode==RESULT_OK && data != null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                doGetBookByVoice(result.get(0));
                getSupportActionBar().setTitle(result.get(0));
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(materialSearchView.isSearchOpen()){
            materialSearchView.closeSearch();
        }
        else {
            super.onBackPressed();
        }
    }

    private void openBestDealDialog(){
        BestDealDialog bestDealDialog = new BestDealDialog();
        bestDealDialog.show(getSupportFragmentManager(), "Best Deal Dialog");
    }

    @Override
    public void sendInput(String bestDealInput) {
        getSupportActionBar().setTitle(bestDealInput);
        doGetBookByBestDeal(bestDealInput);
    }

    private void saySomething(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        try{
            startActivityForResult(intent, 0);
        }catch (ActivityNotFoundException a){
            Toast.makeText(context, a.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void doGetBook(Integer id, Integer page){
        Call<ReqBook> call = userService.getBookRequest(id, page);
        call.enqueue(new Callback<ReqBook>() {
            @Override
            public void onResponse(Call<ReqBook> call, Response<ReqBook> response) {
                ReqBook reqBook = response.body();
                books = reqBook.getBooks();
                viewAllAdapter = new ViewAllAdapter(books);

                rvManager = new LinearLayoutManager(context);
                rvViewAllActivity.setLayoutManager(rvManager);
                rvViewAllActivity.setItemAnimator(new DefaultItemAnimator());
                rvViewAllActivity.setAdapter(viewAllAdapter);
                endlessScroll();
            }

            @Override
            public void onFailure(Call<ReqBook> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doGetBookByVoice(String voiceKey){
        Call<ReqBook> call = userService.getBookByVoice(voiceKey);
        call.enqueue(new Callback<ReqBook>() {
            @Override
            public void onResponse(Call<ReqBook> call, Response<ReqBook> response) {
                ReqBook reqBook = response.body();
                books = reqBook.getBooks();
                viewAllAdapter = new ViewAllAdapter(books);
                page=5;

                rvManager = new LinearLayoutManager(context);
                rvViewAllActivity.setLayoutManager(rvManager);
                rvViewAllActivity.setItemAnimator(new DefaultItemAnimator());
                rvViewAllActivity.setAdapter(viewAllAdapter);
            }

            @Override
            public void onFailure(Call<ReqBook> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doGetBookByBestDeal(String balance){
        Call<ReqBook> call = userService.getBookByBestDeal(userId, balance);
        call.enqueue(new Callback<ReqBook>() {
            @Override
            public void onResponse(Call<ReqBook> call, Response<ReqBook> response) {
                ReqBook reqBook = response.body();
                books = reqBook.getBooks();
                viewAllAdapter = new ViewAllAdapter(books);
                page=5;

                rvManager = new LinearLayoutManager(context);
                rvViewAllActivity.setLayoutManager(rvManager);
                rvViewAllActivity.setItemAnimator(new DefaultItemAnimator());
                rvViewAllActivity.setAdapter(viewAllAdapter);
            }

            @Override
            public void onFailure(Call<ReqBook> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doAddCart(Integer book_id, Integer userId){
        final Order order = new Order();
        order.setBook_id(book_id);
        Call<ResMessage> call = userService.addCartRequest(order, userId);
        call.enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                if(response.isSuccessful()){
                    ResMessage resMessage = response.body();
                    Toast.makeText(context, resMessage.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else {
                    try {
                        Toast.makeText(context, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResMessage> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
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
                viewAllAdapter.notifyDataSetChanged();
                loadingNext.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ReqBook> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                loadingNext.setVisibility(View.GONE);
            }
        });
    }

    private void endlessScroll(){
        rvViewAllActivity.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = rvViewAllActivity.getChildCount();
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
