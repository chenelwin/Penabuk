package com.example.asus.penabuk.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.penabuk.Activity.HistoryDetailActivity;
import com.example.asus.penabuk.Model.History;
import com.example.asus.penabuk.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class HistoryFragmentAdapter extends RecyclerView.Adapter<HistoryFragmentAdapter.ViewHolder> {

    List<History> histories;
    Context context;

    public HistoryFragmentAdapter(List<History> historyList){this.histories = historyList;}

    @Override
    public HistoryFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_history_fragment, parent, false);
        HistoryFragmentAdapter.ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryFragmentAdapter.ViewHolder holder, int position) {
        final History history = histories.get(holder.getAdapterPosition());
        holder.textOrderid.setText(history.getOrder_id());
        holder.textDate.setText(history.getCreatedAt());
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        String priceformat = formatter.format(history.getTotal_price());
        holder.textPrice.setText("Rp. " + priceformat);
        holder.textStatus.setText(history.getStatus());

        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, HistoryDetailActivity.class);
                intent.putExtra("passingorderid", history.getOrder_id());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        Button btnView;
        TextView textOrderid;
        TextView textDate;
        TextView textPrice;
        TextView textStatus;
        CardView cv;

        public ViewHolder(View itemView){
            super(itemView);
            btnView = (Button)itemView.findViewById(R.id.btnView);
            textOrderid = (TextView)itemView.findViewById(R.id.textOrderid);
            textDate = (TextView)itemView.findViewById(R.id.textDate);
            textPrice = (TextView)itemView.findViewById(R.id.textPrice);
            textStatus = (TextView)itemView.findViewById(R.id.textStatus);
            cv = (CardView)itemView.findViewById(R.id.cvHistoryFragment);
        }
    }
}
