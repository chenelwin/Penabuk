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
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.penabuk.Activity.ViewDetailActivity;
import com.example.asus.penabuk.Model.Book;
import com.example.asus.penabuk.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class ViewAllAdapter extends RecyclerView.Adapter<ViewAllAdapter.ViewHolder> {

    public interface PassingBtnAdd{
        void passData(Integer book_id, int position);
    }

    public static PassingBtnAdd passingBtnAdd;

    List<Book> books;
    Context context;

    public ViewAllAdapter (List<Book> bookList){this.books = bookList;}

    @Override
    public ViewAllAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_view_all_activity, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewAllAdapter.ViewHolder holder, final int position) {
        final Book book = books.get(holder.getAdapterPosition());
        holder.bookTitle.setText(book.getOriginal_title());

        DecimalFormat formatter = new DecimalFormat("#,###,###");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        String priceformat = formatter.format(book.getPrice());
        holder.bookPrice.setText("Rp. " + priceformat);

        holder.bookRating.setRating(book.getAverage_rating());

        Picasso.with(context)
                .load(book.getImage_url())
                .resize(80, 120)
                .centerCrop()
                .into(holder.bookImg);

        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passingBtnAdd.passData(book.getId(), position);
            }
        });

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
        RatingBar bookRating;
        Button btnAdd;
        CardView cv;

        public ViewHolder(View itemView){
            super(itemView);
            bookImg = (ImageView)itemView.findViewById(R.id.bookImg);
            bookTitle = (TextView)itemView.findViewById(R.id.bookTitle);
            bookPrice = (TextView)itemView.findViewById(R.id.bookPrice);
            bookRating = (RatingBar)itemView.findViewById(R.id.bookRating);
            btnAdd = (Button)itemView.findViewById(R.id.btnAdd);
            cv = (CardView)itemView.findViewById(R.id.cvViewAllActivity);
        }
    }

}
