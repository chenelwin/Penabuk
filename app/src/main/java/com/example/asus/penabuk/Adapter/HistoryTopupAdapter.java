package com.example.asus.penabuk.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asus.penabuk.Model.Topup;
import com.example.asus.penabuk.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class HistoryTopupAdapter extends RecyclerView.Adapter<HistoryTopupAdapter.ViewHolder>{

    Context context;
    List<Topup> topups;

    public HistoryTopupAdapter(List<Topup> topupList){ this.topups = topupList;}


    @Override
    public HistoryTopupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_history_topup, parent, false);
        HistoryTopupAdapter.ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryTopupAdapter.ViewHolder holder, int position) {
        final Topup topup = topups.get(holder.getAdapterPosition());
        holder.textDate.setText(topup.getCreatedAt());
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        String priceformat = formatter.format(topup.getBalance());
        holder.textBalance.setText("Rp "+priceformat);
        holder.textStatus.setText(topup.getStatus());
    }

    @Override
    public int getItemCount() {
        return topups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textStatus;
        TextView textBalance;
        TextView textDate;
        TextView textDetail;
        CardView cv;

        public ViewHolder(View itemView){
            super(itemView);
            textStatus = (TextView)itemView.findViewById(R.id.textStatus);
            textBalance = (TextView)itemView.findViewById(R.id.textBalance);
            textDate = (TextView)itemView.findViewById(R.id.textDate);
            textDetail = (TextView)itemView.findViewById(R.id.textDetail);
            textDetail.setPaintFlags(textDetail.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            cv = (CardView)itemView.findViewById(R.id.cvHistoryTopup);
        }
    }
}
