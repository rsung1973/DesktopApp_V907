package com.dnake.desktop.fragment;

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

import com.dnake.desktop.R;
import com.dnake.desktop.utils.DensityUtil;
import com.dnake.v700.dmsg;
import com.dnake.v700.dxml;
import com.dnake.widget.ZXing;

public class ConciergeLoadingDialogFragment extends DialogFragment {
    private View inflaterView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.NoticeDialogStyle);
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(inflaterView==null) {
            inflaterView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_concierge_loading, null);
        }
        TextView btnCancel = (TextView) inflaterView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        loadConcierge("xxxxx");
        return inflaterView;
    }

    private void loadConcierge(String deviceName) {

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
