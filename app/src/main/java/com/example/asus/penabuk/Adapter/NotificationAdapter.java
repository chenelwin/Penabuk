package com.example.asus.penabuk.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asus.penabuk.Activity.HistoryDetailActivity;
import com.example.asus.penabuk.Activity.HistoryTopupDetailActivity;
import com.example.asus.penabuk.Model.Notification;
import com.example.asus.penabuk.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    Context context;
    List<Notification> notifications;

    public NotificationAdapter(List<Notification> notificationList){ this.notifications = notificationList;}

    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_notification_activity, parent, false);
        NotificationAdapter.ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final NotificationAdapter.ViewHolder holder, int position) {
        final Notification notification = notifications.get(holder.getAdapterPosition());
        holder.textTypeName.setText(notification.getTypeName());
        holder.textMessage.setText(notification.getMessage());
        holder.textDate.setText(notification.getCreatedAt());

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notification.getType()==1){
                    Intent intent = new Intent(view.getContext(), HistoryTopupDetailActivity.class);
                    intent.putExtra("passingtopupid", Integer.parseInt(notification.getObject_id()));
                    view.getContext().startActivity(intent);
                }
                else{
                    Intent intent = new Intent(view.getContext(), HistoryDetailActivity.class);
                    intent.putExtra("passingorderid", notification.getObject_id());
                    view.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textTypeName;
        TextView textMessage;
        TextView textDate;
        CardView cv;

        public ViewHolder(View itemView){
            super(itemView);
            textTypeName = (TextView)itemView.findViewById(R.id.textTypeName);
            textMessage = (TextView)itemView.findViewById(R.id.textMessage);
            textDate = (TextView)itemView.findViewById(R.id.textDate);
            cv = (CardView)itemView.findViewById(R.id.cvNotification);
        }
    }
}
