package com.example.asus.penabuk.Adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.penabuk.Model.Book;
import com.example.asus.penabuk.Model.Cart;
import com.example.asus.penabuk.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartFragmentAdapter extends RecyclerView.Adapter<CartFragmentAdapter.ViewHolder> {

    List<Cart> carts;
    Context context;

    public CartFragmentAdapter(List<Cart> cartList){this.carts = cartList;}

    @Override
    public CartFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_cart_fragment, parent, false);
        CartFragmentAdapter.ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CartFragmentAdapter.ViewHolder holder, int position) {
        final Cart cart = carts.get(holder.getAdapterPosition());
        holder.cartTitle.setText(cart.getBook().getOriginal_title());
        holder.cartPrice.setText("Rp. " + cart.getBook().getPrice());
        /*
        cart.setSelected(true);
        if(cart.isSelected()){
            holder.checkCart.setChecked(true);
        }
        else {
            holder.checkCart.setChecked(false);
        }*/

        Picasso.with(context)
                .load(cart.getBook().getImage_url())
                .resize(80, 120)
                .centerCrop()
                .into(holder.cartImg);
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkCart;
        ImageView cartImg;
        TextView cartTitle;
        TextView cartPrice;
        CardView cv;

        public ViewHolder(View itemView){
            super(itemView);
            checkCart = (CheckBox)itemView.findViewById(R.id.checkCart);
            cartImg = (ImageView)itemView.findViewById(R.id.cartImg);
            cartTitle = (TextView)itemView.findViewById(R.id.cartTitle);
            cartPrice = (TextView)itemView.findViewById(R.id.cartPrice);
            cv = (CardView)itemView.findViewById(R.id.cvCartFragment);
        }
    }
}
