package com.example.asus.penabuk.Activity;

import android.content.Context;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.penabuk.Model.Book;
import com.example.asus.penabuk.Model.ReqBook;
import com.example.asus.penabuk.Model.ReqBookId;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewDetailActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    Context context;
    ImageView bookImg;
    TextView bookTitle;
    TextView bookAuthor;
    TextView bookPublish;
    TextView bookPrice;
    ImageView imgBack;


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
    }

    private void initView(){
        context = this;
        bookImg = (ImageView)findViewById(R.id.bookImg);
        bookTitle = (TextView)findViewById(R.id.bookTitle);
        bookAuthor = (TextView)findViewById(R.id.bookAuthor);
        bookPublish = (TextView)findViewById(R.id.bookPublish);
        bookPrice = (TextView)findViewById(R.id.bookPrice);
        imgBack = (ImageView)findViewById(R.id.imgBack);
    }

    private void doGetBookById(){
        Integer id = getIntent().getIntExtra("bookid", 0);
        Call<ReqBookId> call = userService.getBookById(id);
        call.enqueue(new Callback<ReqBookId>() {
            @Override
            public void onResponse(Call<ReqBookId> call, Response<ReqBookId> response) {
                ReqBookId reqBookId = response.body();
                Book book = reqBookId.getBook();
                bookTitle.setText(book.getOriginal_title());
                bookAuthor.setText(book.getAuthors());
                bookPublish.setText(book.getOriginal_publication_year());
                bookPrice.setText("Rp. " + book.getPrice());

                Picasso.with(context)
                        .load(book.getImage_url())
                        .resize(120, 160)
                        .centerCrop()
                        .into(bookImg);
            }

            @Override
            public void onFailure(Call<ReqBookId> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
