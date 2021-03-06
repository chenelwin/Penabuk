package com.example.asus.penabuk.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.asus.penabuk.Activity.HistoryDetailActivity;
import com.example.asus.penabuk.Activity.HistoryTopupDetailActivity;
import com.example.asus.penabuk.Model.HistoryBalance;
import com.example.asus.penabuk.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class HistoryBalanceAdapter extends RecyclerView.Adapter<HistoryBalanceAdapter.ViewHolder> {

    Context context;
    List<HistoryBalance> historyBalances;

    public HistoryBalanceAdapter(List<HistoryBalance> historyBalanceList){ this.historyBalances = historyBalanceList; }

    @Override
    public HistoryBalanceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_history_balance, parent, false);
        HistoryBalanceAdapter.ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryBalanceAdapter.ViewHolder holder, int position) {
        final HistoryBalance historyBalance = historyBalances.get(holder.getAdapterPosition());
        holder.textOrderId.setText(historyBalance.getOrder_id());
        holder.textTransaction.setText(historyBalance.getTransactionType());
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        if(historyBalance.getBalance()<0){
            Integer tmpbalance = historyBalance.getBalance();
            tmpbalance*=-1;
            String priceformat = formatter.format(tmpbalance);
            holder.textBalance.setText("-Rp"+priceformat);
            holder.textBalance.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        }
        else{
            Integer tmpbalance = historyBalance.getBalance();
            String priceformat = formatter.format(tmpbalance);
            holder.textBalance.setText("+Rp"+priceformat);
            holder.textBalance.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        }
        holder.textDate.setText(historyBalance.getCreatedAt());

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(historyBalance.getOrder_id()!=null){
                    Intent intent = new Intent(view.getContext(), HistoryDetailActivity.class);
                    intent.putExtra("passingorderid", historyBalance.getOrder_id());
                    view.getContext().startActivity(intent);
                }
                else if(historyBalance.getTop_up_id()!=null) {
                        Intent intent = new Intent(view.getContext(), HistoryTopupDetailActivity.class);
                        intent.putExtra("passingtopupid", historyBalance.getTop_up_id());
                        view.getContext().startActivity(intent);
                    }
                }
        });
    }

    @Override
    public int getItemCount() {
        return historyBalances.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textOrderId;
        TextView textTransaction;
        TextView textBalance;
        TextView textDate;
        CardView cv;

        public ViewHolder(View itemView){
            super(itemView);
            textOrderId = (TextView)itemView.findViewById(R.id.textOrderid);
            textTransaction = (TextView)itemView.findViewById(R.id.textTransaction);
            textBalance = (TextView)itemView.findViewById(R.id.textBalance);
            textDate = (TextView)itemView.findViewById(R.id.textDate);
            cv = (CardView)itemView.findViewById(R.id.cvHistoryBalance);
        }
    }
}
