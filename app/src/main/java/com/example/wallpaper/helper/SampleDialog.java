package com.example.wallpaper.helper;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.wallpaper.R;

public class SampleDialog extends AppCompatDialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.preview_quality)
                .setMessage(R.string.caption_quality)
                .setPositiveButton((R.string.cancel), (dialogInterface, i) -> {

                });

        return builder.create();
    }
}