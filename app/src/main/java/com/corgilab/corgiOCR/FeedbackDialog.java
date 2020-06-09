package com.corgilab.corgiOCR;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.fragment.app.DialogFragment;
import java.util.Objects;

/**
 * Classe per gestire una finestra di feedback
 */
public class FeedbackDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.feedback_dialog,null);

        builder
                .setView(view)
                .setPositiveButton("send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText tw = view.findViewById(R.id.feedback);
                        String txt = tw.getText().toString();
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/html");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"andrea.costalonga@outlook.com"});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                        intent.putExtra(Intent.EXTRA_TEXT, txt);
                        intent.setType("message/rfc822");
                        startActivity(intent);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Objects.requireNonNull(FeedbackDialog.this.getDialog()).cancel();
                    }
                });
        Dialog dialog = builder.create();
        // Create the AlertDialog object and return it
        return dialog;
    }
}
