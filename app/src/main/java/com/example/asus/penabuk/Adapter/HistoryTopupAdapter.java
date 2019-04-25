package com.example.asus.penabuk.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asus.penabuk.Model.Topup;
import com.example.asus.penabuk.R;

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
        holder.textBalance.setText(topup.getBalance().toString());
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
        CardView cv;

        public ViewHolder(View itemView){
            super(itemView);
            textStatus = (TextView)itemView.findViewById(R.id.textStatus);
            textBalance = (TextView)itemView.findViewById(R.id.textBalance);
            textDate = (TextView)itemView.findViewById(R.id.textDate);
            cv = (CardView)itemView.findViewById(R.id.cvHistoryTopup);
        }
    }
}
