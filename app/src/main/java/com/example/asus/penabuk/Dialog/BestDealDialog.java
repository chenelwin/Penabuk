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

public class BestDealDialog extends AppCompatDialogFragment {

    EditText bestDealInput;
    BestDealDialogListener bestDealDialogListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_best_deal, null);

        bestDealInput = (EditText)view.findViewById(R.id.bestDealInput);

        builder.setView(view)
                .setTitle("Best Deal")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
}
