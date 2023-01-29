package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.tvbox.osc.R;

import org.jetbrains.annotations.NotNull;

public class TipDialog extends BaseDialog {

    public TipDialog(@NonNull @NotNull Context context, String tip, String left, String right, OnListener listener) {
        super(context);
        setContentView(R.layout.dialog_tip);
        setCanceledOnTouchOutside(false);
        TextView tipInfo = findViewById(R.id.tipInfo);
        TextView leftBtn = findViewById(R.id.leftBtn);
        TextView rightBtn = findViewById(R.id.rightBtn);
        tipInfo.setText(tip);
        leftBtn.setText(left);
        rightBtn.setText(right);
        leftBtn.setOnClickListener(v -> listener.left());
        rightBtn.setOnClickListener(v -> listener.right());
        setOnCancelListener(dialog -> listener.cancel());
    }

    public interface OnListener {
        void left();

        void right();

        void cancel();
    }
}
