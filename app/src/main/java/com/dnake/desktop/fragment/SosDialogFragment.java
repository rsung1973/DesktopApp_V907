package com.dnake.desktop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.dnake.desktop.BaseDialogFragment;
import com.dnake.desktop.R;
import com.dnake.desktop.utils.DensityUtil;
import com.dnake.desktop.utils.NavigationBarUtil;
import com.dnake.v700.dmsg;

public class SosDialogFragment extends BaseDialogFragment {
    private View inflaterView;
    private TextView btnCancel, btnConfirm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.NoticeDialogStyle);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (inflaterView == null) {
            inflaterView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_sos_confirm, null);
        }
        btnCancel = (TextView) inflaterView.findViewById(R.id.btn_cancel);
        btnConfirm = (TextView) inflaterView.findViewById(R.id.btn_confirm);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dmsg req = new dmsg();
                req.to("/security/sos", null);
                //触发sos警报   cms   管理机
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
        NavigationBarUtil.hideNavigationBar(getDialog().getWindow());
        NavigationBarUtil.clearFocusNotAle(getDialog().getWindow());
    }
}