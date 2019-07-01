package com.example.asus.penabuk.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.penabuk.Activity.ViewDetailActivity;
import com.example.asus.penabuk.Model.Book;
import com.example.asus.penabuk.Model.Cart;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class CartFragmentAdapter extends RecyclerView.Adapter<CartFragmentAdapter.ViewHolder> {

    //passing data utk update harga
    public interface TotalHarga{
        void passTotalHarga(List<Cart> cartChecked);
    }
    public static TotalHarga totalHarga;

    //passing data utk dihapus
    public interface PassingBtnRemoveCart{
        void passData(Integer cart_id, int position);
    }

    public static PassingBtnRemoveCart passingBtnRemoveCart;

    //passing data utk save qty
    public interface PassingCountCart{
        void passCount(Integer book_id, Integer count, int position);
    }

    public static PassingCountCart passingCountCart;

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
    public void onBindViewHolder(final CartFragmentAdapter.ViewHolder holder, final int position) {
        final Cart cart = carts.get(holder.getAdapterPosition());
        holder.cartTitle.setText(cart.getBook().getOriginal_title());

        DecimalFormat formatter = new DecimalFormat("#,###,###");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        String priceformat = formatter.format(cart.getBook().getPrice());
        holder.cartPrice.setText("Rp. " + priceformat);

        if(cart.getBook().getImage_url().length()>0) {
            Picasso.with(context)
                    .load(cart.getBook().getImage_url())
                    .resize(80, 120)
                    .centerCrop()
                    .placeholder(R.drawable.emptyimage)
                    .error(R.drawable.emptyimage)
                    .into(holder.cartImg);
        }
        else {
            Picasso.with(context)
                    .load(ApiUtils.BASE_URL +"/image?id="+carts.get(position).getBook().getImage_local())
                    .resize(80, 120)
                    .centerCrop()
                    .placeholder(R.drawable.emptyimage)
                    .error(R.drawable.emptyimage)
                    .into(holder.cartImg);
        }

        holder.bookQty.setText(String.valueOf(cart.getCount()));

        holder.imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer count = cart.getCount();
                count++;
                holder.bookQty.setText(String.valueOf(count));
                cart.setCount(count);
                totalHarga.passTotalHarga(carts);
                passingCountCart.passCount(cart.getBook().getId(), count, position);
            }
        });

        holder.imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer count = cart.getCount();
                if (count > 1) {
                    count--;
                    holder.bookQty.setText(String.valueOf(count));
                    cart.setCount(count);
                    totalHarga.passTotalHarga(carts);
                    passingCountCart.passCount(cart.getBook().getId(), count, position);
                }
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Apakah Anda yakin akan menghapus barang dari keranjang?")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                passingBtnRemoveCart.passData(cart.getCart_id(), position);
                                carts.remove(position);
                                totalHarga.passTotalHarga(carts);
                            }
                        })
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        /*
        holder.cartImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ViewDetailActivity.class);
                intent.putExtra("bookid", cart.getBook().getId());
                view.getContext().startActivity(intent);
            }
        });
        holder.cartTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ViewDetailActivity.class);
                intent.putExtra("bookid", cart.getBook().getId());
                view.getContext().startActivity(intent);
            }
        });*/

        holder.checkCart.setOnCheckedChangeListener(null);
        holder.checkCart.setChecked(cart.isSelected());
        holder.checkCart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    cart.setSelected(true);
                    totalHarga.passTotalHarga(carts);
                } else if (!b) {
                    cart.setSelected(false);
                    totalHarga.passTotalHarga(carts);
                }
            }
        });
        //holder.checkCart.setChecked(cart.isSelected());


    }

    @Override
    public int getItemCount() {
        return carts.size();
    }


    public void selectAll(){
        for(int i=0; i<carts.size(); i++){
            carts.get(i).setSelected(true);
        }
        notifyDataSetChanged();
        totalHarga.passTotalHarga(carts);
    }

    public void deselectAll(){
        for(int i=0; i<carts.size(); i++){
            carts.get(i).setSelected(false);
        }
        notifyDataSetChanged();
        totalHarga.passTotalHarga(carts);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkCart;
        ImageView cartImg;
        TextView cartTitle;
        TextView cartPrice;
        ImageView imgMinus;
        TextView bookQty;
        ImageView imgPlus;
        ImageView btnDelete;
        CardView cv;

        public ViewHolder(View itemView){
            super(itemView);
            checkCart = (CheckBox)itemView.findViewById(R.id.checkCart);
            cartImg = (ImageView)itemView.findViewById(R.id.cartImg);
            cartTitle = (TextView)itemView.findViewById(R.id.cartTitle);
            cartPrice = (TextView)itemView.findViewById(R.id.cartPrice);
            imgMinus = (ImageView)itemView.findViewById(R.id.imgMinus);
            bookQty = (TextView)itemView.findViewById(R.id.bookQty);
            imgPlus = (ImageView)itemView.findViewById(R.id.imgPlus);
            btnDelete = (ImageView)itemView.findViewById(R.id.btnDelete);
            cv = (CardView)itemView.findViewById(R.id.cvCartFragment);
        }
    }
}
