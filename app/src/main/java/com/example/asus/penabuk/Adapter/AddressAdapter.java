package com.example.asus.penabuk.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.asus.penabuk.Model.Address;
import com.example.asus.penabuk.R;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    public interface PassingBtnRemove{
        void passData(Integer address_id, int position);
    }

    public static PassingBtnRemove passingBtnRemove;

    Context context;
    List<Address> addresses;

    public AddressAdapter(List<Address> addressList){ this.addresses = addressList; }


    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_address_activity, parent, false);
        AddressAdapter.ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AddressAdapter.ViewHolder holder, final int position) {
        final Address address = addresses.get(holder.getAdapterPosition());

        String tmpaddress = address.getAddress_line()+", "+address.getCity()+", "+address.getDistrict()+", "+address.getProvince()+" "+address.getZip_code();
        //holder.textAddress.setText(tmpaddress);
        holder.primaryAddress.setText(tmpaddress);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passingBtnRemove.passData(address.getId(), position);
                addresses.remove(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textAddress;
        ImageView btnDelete;
        RadioButton primaryAddress;
        CardView cv;

        public ViewHolder(View itemView){
            super(itemView);
            textAddress = (TextView)itemView.findViewById(R.id.textAddress);
            btnDelete = (ImageView)itemView.findViewById(R.id.btnDelete);
            primaryAddress = (RadioButton)itemView.findViewById(R.id.primaryAddress);
            cv = (CardView)itemView.findViewById(R.id.cvAddress);
        }
    }
}
