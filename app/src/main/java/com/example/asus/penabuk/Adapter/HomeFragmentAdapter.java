package com.example.asus.penabuk.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.asus.penabuk.Activity.ViewDetailActivity;
import com.example.asus.penabuk.Model.Book;
import com.example.asus.penabuk.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeFragmentAdapter extends RecyclerView.Adapter<HomeFragmentAdapter.ViewHolder> {

    List<Book> books;
    Context context;

    public HomeFragmentAdapter(List<Book> bookList){ this.books = bookList;}

    @Override
    public HomeFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_home_fragment, parent, false);
        HomeFragmentAdapter.ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HomeFragmentAdapter.ViewHolder holder, int position) {
        final Book book = books.get(holder.getAdapterPosition());
        holder.bookTitle.setText(book.getOriginal_title());
        holder.bookPrice.setText("Rp. " + book.getPrice());

        Picasso.with(context)
                .load(book.getImage_url())
                .resize(80, 120)
                .centerCrop()
                .into(holder.bookImg);

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ViewDetailActivity.class);
                intent.putExtra("bookid", book.getId());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView bookImg;
        TextView bookTitle;
        TextView bookPrice;
        CardView cv;

        public ViewHolder(View itemView){
            super(itemView);
            bookImg = (ImageView)itemView.findViewById(R.id.bookImg);
            bookTitle = (TextView)itemView.findViewById(R.id.bookTitle);
            bookPrice = (TextView)itemView.findViewById(R.id.bookPrice);
            cv = (CardView)itemView.findViewById(R.id.cvHomeFragment);
        }
    }
}
