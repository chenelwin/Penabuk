package com.example.asus.penabuk.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.penabuk.Adapter.ViewDetailAdapter;
import com.example.asus.penabuk.ErrorUtils.ErrorUtils;
import com.example.asus.penabuk.Model.Book;
import com.example.asus.penabuk.Model.Order;
import com.example.asus.penabuk.Model.ReqBookId;
import com.example.asus.penabuk.Model.ReqReview;
import com.example.asus.penabuk.Model.ResMessage;
import com.example.asus.penabuk.Model.Review;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewDetailActivity extends AppCompatActivity implements ViewDetailAdapter.PassingBtnRemoveReview, ViewDetailAdapter.PassingBtnEditReview {

    UserService userService = ApiUtils.getUserService();
    SharedPrefManager sharedPrefManager;
    Context context;
    RecyclerView rvViewDetail;
    ViewDetailAdapter viewDetailAdapter;
    ImageView bookImg;
    LinearLayout layoutReviewUser;
    LinearLayout layoutFixBottom;
    TextView bookTitle;
    TextView bookAuthor;
    TextView bookIsbn;
    TextView bookPublish;
    TextView bookYear;
    TextView bookWeight;
    TextView bookPage;
    TextView bookMerchant;
    TextView bookUploadDate;
    TextView bookRating;
    TextView bookSinopsis;
    ImageView imgMinus;
    TextView bookQty;
    ImageView imgPlus;
    RatingBar bookRatingBar;
    TextView bookPrice;
    Integer userId;
    List<Review> reviews;
    List<Book> passingbook;
    List<Integer> passingcartid;
    List<Integer> passingcount;
    List<Integer> passingmerchantid;
    Button btnAdd;
    Button btnBuy;
    ProgressDialog progressDialog;
    Integer count = 1;

    //Review User
    RatingBar userRate;
    EditText userComment;
    Button btnReview;

    Toolbar toolbarViewDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail);
        initView();
        doGetBookById();

        findViewById(android.R.id.content).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                findViewById(android.R.id.content).getWindowVisibleDisplayFrame(r);
                int heightDiff = findViewById(android.R.id.content).getRootView().getHeight()-(r.bottom-r.top);
                if(heightDiff>244){
                    layoutFixBottom.setVisibility(View.GONE);
                }
                else{
                    layoutFixBottom.setVisibility(View.VISIBLE);
                }
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

        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer bookid = getIntent().getIntExtra("bookid", 0);
                Integer rating = Math.round(userRate.getRating());
                String review = userComment.getText().toString();
                if(validateReview(rating, review)) {
                    progressDialog = ProgressDialog.show(context, null, "Please Wait..", true);
                    doGiveReview(bookid, rating, review);
                }
            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passingcount = new ArrayList<>();
                passingcount.add(count);
                passingbook.get(0).setReviews(null);
                Intent intent = new Intent(ViewDetailActivity.this, PaymentDetailActivity.class);
                intent.putExtra("passingbook", (Serializable)passingbook);
                intent.putExtra("passingcartid", (Serializable)passingcartid);
                intent.putExtra("passingcount", (Serializable)passingcount);
                intent.putExtra("passingmerchantid", (Serializable)passingmerchantid);
                startActivity(intent);
            }
        });
    }

    private void initView(){
        context = this;
        sharedPrefManager = new SharedPrefManager(context);
        initToolbar();
        userId = Integer.parseInt(sharedPrefManager.getSPId());
        rvViewDetail = (RecyclerView)findViewById(R.id.RvViewDetail);
        layoutReviewUser = (LinearLayout)findViewById(R.id.layoutReviewUser);
        layoutFixBottom = (LinearLayout)findViewById(R.id.layoutFixBottom);
        bookImg = (ImageView)findViewById(R.id.bookImg);
        bookTitle = (TextView)findViewById(R.id.bookTitle);
        bookAuthor = (TextView)findViewById(R.id.bookAuthor);
        bookIsbn = (TextView)findViewById(R.id.bookIsbn);
        bookPublish = (TextView)findViewById(R.id.bookPublish);
        bookYear = (TextView)findViewById(R.id.bookYear);
        bookWeight = (TextView)findViewById(R.id.bookWeight);
        bookPage = (TextView)findViewById(R.id.bookPage);
        bookMerchant = (TextView)findViewById(R.id.bookMerchant);
        bookUploadDate = (TextView)findViewById(R.id.bookUploadDate);
        bookRating = (TextView)findViewById(R.id.bookRating);
        bookRatingBar = (RatingBar)findViewById(R.id.bookRatingBar);
        bookSinopsis = (TextView)findViewById(R.id.bookSinopsis);
        bookPrice = (TextView)findViewById(R.id.bookPrice);
        imgMinus = (ImageView)findViewById(R.id.imgMinus);
        bookQty = (TextView)findViewById(R.id.bookQty);
        bookQty.setText(String.valueOf(count));
        imgPlus = (ImageView)findViewById(R.id.imgPlus);
        //passingbook = new ArrayList<>();
        //passingcartid = new ArrayList<>();
        //passingmerchantid = new ArrayList<>();
        btnAdd = (Button)findViewById(R.id.btnAdd);
        btnBuy = (Button)findViewById(R.id.btnBuy);

        //Review User
        ViewDetailAdapter.passingBtnEditReview = this;
        ViewDetailAdapter.passingBtnRemoveReview = this;
        userRate = (RatingBar)findViewById(R.id.userRate);
        userComment = (EditText) findViewById(R.id.userComment);
        btnReview = (Button)findViewById(R.id.btnReview);
    }

    private void initToolbar(){
        toolbarViewDetail = (Toolbar)findViewById(R.id.toolbarViewDetail);
        setSupportActionBar(toolbarViewDetail);
        getSupportActionBar().setTitle("Detail Buku");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarViewDetail.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void passEditData(Integer rating, String comment, int position) {
        layoutReviewUser.setVisibility(View.VISIBLE);
        userRate.setRating(rating);
        userComment.setText(comment);
    }

    @Override
    public void passData(int position) {
        progressDialog = ProgressDialog.show(context, null, "Please Wait..", true);
        doRemoveReview(position);
    }

    private boolean validateReview(Integer rating, String review){
        if(rating<1){
            Toast.makeText(this, "Rating is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(review == null || review.trim().length() == 0){
            userComment.setError("Komentar harus diisi.");
            return false;
        }
        return true;
    }

    private void doGetBookById(){
        final Integer bookId = getIntent().getIntExtra("bookid", 0);
        Call<ReqBookId> call = userService.getBookById(bookId, userId);
        call.enqueue(new Callback<ReqBookId>() {
            @Override
            public void onResponse(Call<ReqBookId> call, Response<ReqBookId> response) {
                ReqBookId reqBookId = response.body();
                Book book = reqBookId.getBook();
                passingbook = new ArrayList<>();
                passingcartid = new ArrayList<>();
                passingmerchantid = new ArrayList<>();
                passingbook.add(book);
                passingcartid.add(null);
                passingmerchantid.add(book.getMerchant_id());
                bookTitle.setText(book.getOriginal_title());
                bookAuthor.setText(book.getAuthors());
                bookIsbn.setText(book.getIsbn_number());
                bookPublish.setText(book.getPublisher());
                bookYear.setText(book.getOriginal_publication_year());
                bookWeight.setText(book.getBook_weight().toString()+"gr");
                bookPage.setText(book.getPage_total().toString());
                bookMerchant.setText(book.getMerchant());
                bookUploadDate.setText(book.getCreatedAt());
                bookRating.setText(book.getAverage_rating().toString());
                bookSinopsis.setText(book.getSynopsis());
                bookRatingBar.setRating(book.getAverage_rating());
                DecimalFormat formatter = new DecimalFormat("#,###,###");
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setGroupingSeparator('.');
                formatter.setDecimalFormatSymbols(symbols);
                String priceformat = formatter.format(book.getPrice());
                bookPrice.setText("Rp. " + priceformat);

                checkReview(book.getUser_rating());

                if(book.getImage_url().length()>0) {
                    Picasso.with(context)
                            .load(book.getImage_url())
                            .resize(110, 170)
                            .centerCrop()
                            .placeholder(R.drawable.emptyimage)
                            .error(R.drawable.emptyimage)
                            .into(bookImg);
                }
                else {
                    Picasso.with(context)
                            .load(ApiUtils.BASE_URL +"/image?id="+book.getImage_local())
                            .resize(110, 170)
                            .centerCrop()
                            .placeholder(R.drawable.emptyimage)
                            .error(R.drawable.emptyimage)
                            .into(bookImg);
                }

                reviews = book.getReviews();
                viewDetailAdapter = new ViewDetailAdapter(reviews);
                rvViewDetail.setLayoutManager(new LinearLayoutManager(context));
                rvViewDetail.setItemAnimator(new DefaultItemAnimator());
                rvViewDetail.setAdapter(viewDetailAdapter);
            }

            @Override
            public void onFailure(Call<ReqBookId> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doGiveReview(Integer book_id, Integer rating, String review){
        final ReqReview reqReview = new ReqReview();
        reqReview.setBook_id(book_id);
        reqReview.setRating(rating);
        reqReview.setReview(review);

        Call<ResMessage> call = userService.reviewRequest(reqReview, userId);
        call.enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                if(response.isSuccessful()){
                    ResMessage resMessage = response.body();
                    Toast.makeText(ViewDetailActivity.this, resMessage.getMessage(), Toast.LENGTH_LONG).show();
                    doGetBookById();
                    progressDialog.dismiss();
                }
                else {
                    ResMessage resMessage = ErrorUtils.parseError(response);
                    Toast.makeText(ViewDetailActivity.this, resMessage.getMessage(), Toast.LENGTH_LONG).show();
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

    private void doRemoveReview(final int position){
        Integer bookId = getIntent().getIntExtra("bookid", 0);
        Call<ResMessage> call = userService.removeReviewRequest(bookId, userId);
        call.enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                ResMessage resMessage = response.body();
                Toast.makeText(ViewDetailActivity.this, resMessage.getMessage(), Toast.LENGTH_LONG).show();
                viewDetailAdapter.notifyItemRemoved(position);
                viewDetailAdapter.notifyItemRangeChanged(position, reviews.size());
                userRate.setRating(0);
                userComment.setText("");
                doGetBookById();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResMessage> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
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
                    ResMessage resMessage = ErrorUtils.parseError(response);
                    Toast.makeText(ViewDetailActivity.this, resMessage.getMessage(), Toast.LENGTH_LONG).show();
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

    private void checkReview(Integer rate){
        if(rate==0){
            layoutReviewUser.setVisibility(View.VISIBLE);
        }
        else if(rate>0){
            layoutReviewUser.setVisibility(View.GONE);
        }
    }
}
