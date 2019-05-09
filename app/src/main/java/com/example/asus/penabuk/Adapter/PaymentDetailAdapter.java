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
import com.example.asus.penabuk.Remote.ApiUtils;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class PaymentDetailAdapter extends RecyclerView.Adapter<PaymentDetailAdapter.ViewHolder> {

    List<BookPayment> bookPayments;
    Context context;

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
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        String priceformat = formatter.format(bookPayment.getBook().getPrice());
        holder.bookPrice.setText("Rp. " + priceformat);

        if(bookPayment.getBook().getImage_url().length()>0) {
            Picasso.with(context)
                    .load(bookPayment.getBook().getImage_url())
                    .resize(80, 120)
                    .centerCrop()
                    .placeholder(R.drawable.emptyimage)
                    .error(R.drawable.emptyimage)
                    .into(holder.bookImg);
        }
        else {
            Picasso.with(context)
                    .load(ApiUtils.BASE_URL +"/image?id="+bookPayments.get(position).getBook().getImage_local())
                    .resize(80, 120)
                    .centerCrop()
                    .placeholder(R.drawable.emptyimage)
                    .error(R.drawable.emptyimage)
                    .into(holder.bookImg);
        }

        holder.bookQty.setText("Qty : " + bookPayment.getCount());

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
