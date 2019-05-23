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

import com.example.asus.penabuk.R;

public class CancelOrderDialog extends AppCompatDialogFragment {

    EditText cancelOrderInput;
    CancelOrderDialogListener cancelOrderDialogListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_order_cancel, null);

        cancelOrderInput = (EditText)view.findViewById(R.id.cancelInput);

        builder.setView(view)
                .setTitle("Batalkan Pesanan?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String input = cancelOrderInput.getText().toString();
                        if(input.trim().length()==0){
                            cancelOrderInput.setError("Alasan harus diisi");
                        }
                        else {
                            cancelOrderDialogListener.sendInput(input);
                        }
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            cancelOrderDialogListener = (CancelOrderDialogListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement CancelOrderDialogListener");
        }
    }

    public interface CancelOrderDialogListener{
        void sendInput(String cancelOrderInput);
    }
}
