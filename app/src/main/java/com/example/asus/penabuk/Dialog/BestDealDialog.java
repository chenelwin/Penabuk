package com.example.asus.penabuk.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.penabuk.Model.BestDealPrice;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BestDealDialog extends AppCompatDialogFragment {

    UserService userService = ApiUtils.getUserService();
    EditText bestDealInput;
    TextView textMinimalBestDeal;
    BestDealDialogListener bestDealDialogListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_best_deal, null);
        textMinimalBestDeal = (TextView)view.findViewById(R.id.textMinimalBestDeal);
        doGetMinimalPrice();
        bestDealInput = (EditText)view.findViewById(R.id.bestDealInput);

        builder.setView(view)
                .setTitle("Best Deal")
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String input = bestDealInput.getText().toString();
                        bestDealDialogListener.sendInput(input);

                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            bestDealDialogListener = (BestDealDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement BestDealDialogListener");
        }
    }

    public interface BestDealDialogListener{
        void sendInput(String bestDealInput);
    }

    private void doGetMinimalPrice(){
        Call<BestDealPrice> call = userService.getMinimalPrice();
        call.enqueue(new Callback<BestDealPrice>() {
            @Override
            public void onResponse(Call<BestDealPrice> call, Response<BestDealPrice> response) {
                BestDealPrice bestDealPrice = response.body();
                textMinimalBestDeal.setText("NB: Minimal input adalah " + bestDealPrice.getPrice().toString());
            }

            @Override
            public void onFailure(Call<BestDealPrice> call, Throwable t) {
                Toast.makeText(getView().getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
