package com.dnake.desktop.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.dnake.desktop.BaseDialogFragment;
import com.dnake.desktop.R;
import com.dnake.desktop.utils.DensityUtil;
import com.dnake.widget.Button2;

public class LiftCtrlDialogFragment extends BaseDialogFragment {
    private View inflaterView;
    private ImageView btnClose;
    private Button2 smart_elev1_up, smart_elev1_down;
    private TextView btnPermit;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.NoticeDialogStyle);
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(inflaterView==null) {
            inflaterView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_lift_ctrl_confirm, null);
        }
        btnClose = (ImageView) inflaterView.findViewById(R.id.btn_close);
        btnPermit = (TextView) inflaterView.findViewById(R.id.btn_permit);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnPermit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return inflaterView;
    }

    @Override
    public void onStart() {
        super.onStart();
        final WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.width = DensityUtil.dip2px(getActivity(), 500);
        layoutParams.height = DensityUtil.dip2px(getActivity(), 360);
        getDialog().getWindow().setAttributes(layoutParams);
    }
}