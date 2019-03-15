package com.example.asus.penabuk.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.penabuk.Model.Book;
import com.example.asus.penabuk.Model.Order;
import com.example.asus.penabuk.Model.ReqBookId;
import com.example.asus.penabuk.Model.ResMessage;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewDetailActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    SharedPrefManager sharedPrefManager;
    Context context;
    ImageView bookImg;
    TextView bookTitle;
    TextView bookAuthor;
    TextView bookPublish;
    TextView bookRating;
    ImageView imgMinus;
    TextView bookQty;
    ImageView imgPlus;
    RatingBar bookRatingBar;
    TextView bookPrice;
    Integer userId;
    ImageView imgBack;
    List<Book> passingbook;
    List<Integer> passingcartid;
    List<Integer> passingcount;
    Button btnAdd;
    Button btnBuy;
    ProgressDialog progressDialog;
    Integer count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail);
        initView();
        doGetBookById();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qtyMinus();
            }
        });

        imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qtyPlus();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = ProgressDialog.show(context, null, "Please Wait..", true);
                Integer bookid = getIntent().getIntExtra("bookid", 0);
                doAddCart(bookid, userId);
            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passingcount = new ArrayList<>();
                passingcount.add(count);
                Log.e("qty", count.toString() + " " + passingcount.get(0));
                Intent intent = new Intent(ViewDetailActivity.this, PaymentDetailActivity.class);
                intent.putExtra("passingbook", (Serializable)passingbook);
                intent.putExtra("passingcartid", (Serializable)passingcartid);
                intent.putExtra("passingcount", (Serializable)passingcount);
                startActivity(intent);
            }
        });
    }

    private void initView(){
        context = this;
        sharedPrefManager = new SharedPrefManager(context);
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        bookImg = (ImageView)findViewById(R.id.bookImg);
        bookTitle = (TextView)findViewById(R.id.bookTitle);
        bookAuthor = (TextView)findViewById(R.id.bookAuthor);
        bookPublish = (TextView)findViewById(R.id.bookPublish);
        bookRating = (TextView)findViewById(R.id.bookRating);
        bookRatingBar = (RatingBar)findViewById(R.id.bookRatingBar);
        bookPrice = (TextView)findViewById(R.id.bookPrice);
        imgMinus = (ImageView)findViewById(R.id.imgMinus);
        bookQty = (TextView)findViewById(R.id.bookQty);
        bookQty.setText(String.valueOf(count));
        imgPlus = (ImageView)findViewById(R.id.imgPlus);
        passingbook = new ArrayList<>();
        passingcartid = new ArrayList<>();
        imgBack = (ImageView)findViewById(R.id.imgBack);
        btnAdd = (Button)findViewById(R.id.btnAdd);
        btnBuy = (Button)findViewById(R.id.btnBuy);
    }

    private void doGetBookById(){
        Integer id = getIntent().getIntExtra("bookid", 0);
        Call<ReqBookId> call = userService.getBookById(id);
        call.enqueue(new Callback<ReqBookId>() {
            @Override
            public void onResponse(Call<ReqBookId> call, Response<ReqBookId> response) {
                ReqBookId reqBookId = response.body();
                Book book = reqBookId.getBook();
                passingbook.add(book);
                passingcartid.add(null);
                bookTitle.setText(book.getOriginal_title());
                bookAuthor.setText(book.getAuthors());
                bookPublish.setText(book.getOriginal_publication_year());
                bookRating.setText(book.getAverage_rating().toString());
                bookRatingBar.setRating(book.getAverage_rating());
                bookPrice.setText("Rp. " + book.getPrice());

                Picasso.with(context)
                        .load(book.getImage_url())
                        .resize(100, 140)
                        .centerCrop()
                        .into(bookImg);
            }

            @Override
            public void onFailure(Call<ReqBookId> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void qtyMinus(){
        if(count>1) {
            count--;
            bookQty.setText(String.valueOf(count));
        }
    }

    private void qtyPlus(){
        count++;
        bookQty.setText(String.valueOf(count));
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
}
