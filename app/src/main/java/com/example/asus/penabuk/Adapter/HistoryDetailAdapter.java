package com.example.asus.penabuk.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asus.penabuk.Model.BookHistory;
import com.example.asus.penabuk.R;

import java.util.List;

public class HistoryDetailAdapter extends RecyclerView.Adapter<HistoryDetailAdapter.ViewHolder> {

    List<BookHistory> bookHistories;
    Context context;

    public HistoryDetailAdapter(List<BookHistory> bookHistoryList){this.bookHistories = bookHistoryList;}

    @Override
    public HistoryDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_history_detail_activity, parent, false);
        HistoryDetailAdapter.ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryDetailAdapter.ViewHolder holder, int position) {
        final BookHistory bookHistory = bookHistories.get(holder.getAdapterPosition());

        holder.textTitle.setText(bookHistory.getName());
        holder.textPrice.setText("Rp. " + bookHistory.getValue());
        holder.textQty.setText("Qty : " + bookHistory.getTotal());
    }

    @Override
    public int getItemCount() {
        return bookHistories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textTitle;
        TextView textPrice;
        TextView textQty;
        CardView cv;

        public ViewHolder(View itemView){
            super(itemView);
            textTitle = (TextView)itemView.findViewById(R.id.textTitle);
            textPrice = (TextView)itemView.findViewById(R.id.textPrice);
            textQty = (TextView)itemView.findViewById(R.id.textQty);
            cv = (CardView)itemView.findViewById(R.id.cvHistoryDetail);
        }
    }
}
