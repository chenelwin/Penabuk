package com.example.asus.penabuk.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.penabuk.Model.Address;
import com.example.asus.penabuk.R;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    public interface PassingBtnRemove{
        void passData(Integer address_id, int position);
    }

    public interface PassingPrimaryAddress{
        void passingPrimary(Integer address_id, int position);
    }

    public static PassingBtnRemove passingBtnRemove;
    public static PassingPrimaryAddress passingPrimaryAddress;

    Context context;
    List<Address> addresses;
    int lastChecked = -1;

    public AddressAdapter(List<Address> addressList){ this.addresses = addressList; }


    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_address_activity, parent, false);
        AddressAdapter.ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AddressAdapter.ViewHolder holder, final int position) {
        final Address address = addresses.get(holder.getAdapterPosition());

        String tmpaddress = address.getAddress_line()+", "+address.getCity()+", "+address.getDistrict()+", "+address.getProvince()+" "+address.getZip_code();
        holder.primaryAddress.setText(tmpaddress);
        holder.primaryAddress.setChecked(address.isIs_primary());
        if(address.isIs_primary()) {
            lastChecked = holder.getAdapterPosition();
        }
        if(lastChecked==holder.getAdapterPosition()){
            holder.primaryAddress.setChecked(true);
        }
        holder.primaryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lastChecked!=-1) {
                    addresses.get(lastChecked).setIs_primary(false);
                }
                lastChecked = holder.getAdapterPosition();
                notifyDataSetChanged();
                passingPrimaryAddress.passingPrimary(address.getId(), position);
            }
        });

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
            /*
            primaryAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lastChecked = getAdapterPosition();
                    notifyDataSetChanged();
                    Toast.makeText(view.getContext(), "set"+primaryAddress.getText(),Toast.LENGTH_LONG).show();
                }
            });*/
        }
    }
}
