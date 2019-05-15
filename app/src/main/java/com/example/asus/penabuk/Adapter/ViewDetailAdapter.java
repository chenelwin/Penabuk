package com.example.asus.penabuk.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.asus.penabuk.Model.Book;
import com.example.asus.penabuk.Model.Review;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

import java.util.List;

public class ViewDetailAdapter extends RecyclerView.Adapter<ViewDetailAdapter.ViewHolder> {

    public interface PassingBtnEditReview{
        void passEditData(Integer rating, String comment, int position);
    }

    public interface PassingBtnRemoveReview{
        void passData(int position);
    }

    public static PassingBtnEditReview passingBtnEditReview;
    public static PassingBtnRemoveReview passingBtnRemoveReview;

    Context context;
    List<Review> reviews;
    SharedPrefManager sharedPrefManager;

    public ViewDetailAdapter(List<Review> reviewList){ this.reviews = reviewList; }


    @Override
    public ViewDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_view_detail_activity, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewDetailAdapter.ViewHolder holder, final int position) {
        final Review review = reviews.get(holder.getAdapterPosition());
        sharedPrefManager = new SharedPrefManager(context);
        holder.reviewName.setText(review.getReview_author());
        holder.reviewComment.setText(review.getReview());
        holder.reviewRate.setRating(review.getRating());
        holder.reviewDate.setText(review.getCreatedAt());

        if(position==0 && sharedPrefManager.getSPNama().equals(review.getReview_author())){
            holder.btnEditReview.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        }

        holder.btnEditReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passingBtnEditReview.passEditData(review.getRating(), review.getReview(), position);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passingBtnRemoveReview.passData(position);
                reviews.remove(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView reviewName;
        TextView reviewComment;
        TextView reviewDate;
        RatingBar reviewRate;
        ImageView btnEditReview;
        ImageView btnDelete;
        CardView cv;

        public ViewHolder(View itemView){
            super(itemView);
            reviewName = (TextView)itemView.findViewById(R.id.reviewName);
            reviewComment = (TextView)itemView.findViewById(R.id.reviewComment);
            reviewDate = (TextView)itemView.findViewById(R.id.reviewDate);
            reviewRate = (RatingBar) itemView.findViewById(R.id.reviewRate);
            btnEditReview = (ImageView)itemView.findViewById(R.id.btnEditReview);
            btnDelete = (ImageView)itemView.findViewById(R.id.btnDelete);
            cv = (CardView)itemView.findViewById(R.id.cvBookDetail);
        }
    }
}
