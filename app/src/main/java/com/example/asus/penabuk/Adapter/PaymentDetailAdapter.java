package com.example.asus.penabuk.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.penabuk.Model.Book;
import com.example.asus.penabuk.Model.BookPayment;
import com.example.asus.penabuk.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PaymentDetailAdapter extends RecyclerView.Adapter<PaymentDetailAdapter.ViewHolder> {

    List<BookPayment> bookPayments;
    Context context;
    Integer count=1;

    public PaymentDetailAdapter(List<BookPayment> bookPaymentList){
        this.bookPayments = bookPaymentList;
    }

    @Override
    public PaymentDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_payment_detail_activity, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PaymentDetailAdapter.ViewHolder holder, int position) {
        final BookPayment bookPayment = bookPayments.get(holder.getAdapterPosition());
        holder.bookTitle.setText(bookPayment.getBook().getOriginal_title());
        holder.bookPrice.setText("Rp. " + bookPayment.getBook().getPrice());

        Picasso.with(context)
                .load(bookPayment.getBook().getImage_url())
                .resize(80, 120)
                .centerCrop()
                .into(holder.bookImg);


        holder.bookQty.setText(String.valueOf(count));
        holder.imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer count = bookPayment.getCount();
                count++;
                holder.bookQty.setText(String.valueOf(count));
                bookPayment.setCount(count);
            }
        });

        holder.imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer count = bookPayment.getCount();
                if(count>1) {
                    count--;
                    holder.bookQty.setText(String.valueOf(count));
                    bookPayment.setCount(count);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookPayments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView bookImg;
        TextView bookTitle;
        TextView bookPrice;
        ImageView imgMinus;
        TextView bookQty;
        ImageView imgPlus;
        public ViewHolder(View itemView){
            super(itemView);
            bookImg = (ImageView)itemView.findViewById(R.id.bookImg);
            bookTitle = (TextView)itemView.findViewById(R.id.bookTitle);
            bookPrice = (TextView)itemView.findViewById(R.id.bookPrice);
            imgMinus = (ImageView)itemView.findViewById(R.id.imgMinus);
            bookQty = (TextView)itemView.findViewById(R.id.bookQty);
            imgPlus = (ImageView)itemView.findViewById(R.id.imgPlus);
        }
    }
}
