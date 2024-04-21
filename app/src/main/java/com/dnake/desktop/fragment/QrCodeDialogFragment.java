package com.dnake.desktop.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;

import com.dnake.desktop.BaseDialogFragment;
import com.dnake.desktop.R;
import com.dnake.desktop.utils.DensityUtil;
import com.dnake.desktop.utils.NavigationBarUtil;
import com.dnake.v700.dmsg;
import com.dnake.v700.dxml;
import com.dnake.widget.ZXing;
import com.hjq.toast.ToastUtils;

public class QrCodeDialogFragment extends BaseDialogFragment {
    private View inflaterView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.NoticeDialogStyle);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (inflaterView == null) {
            inflaterView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_qr_code, null);
        }
        ImageView btnClose = (ImageView) inflaterView.findViewById(R.id.btn_close);
        ImageView ivQrCode = (ImageView) inflaterView.findViewById(R.id.iv_qr_code);
        loadQRCode(ivQrCode);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return inflaterView;
    }

    private void loadQRCode(ImageView ivQRCode) {
//        dmsg req = new dmsg();
//        dxml p = new dxml();
//        req.to("/ui/web/qrcode/read", null);
//        p.parse(req.mBody);
//        String qrcodeStr = p.getText("/params/qrcode", "");
//        ivQRCode.setImageBitmap(ZXing.QR2D(qrcodeStr, 300));

        //tuya
        dmsg req = new dmsg();
        dxml p = new dxml();
        p.setInt("/params/is_show", 1);
        req.to("/apps/tuya/is_tuya_status", p.toString());
        p.parse(req.mBody);
        int isTuya = p.getInt("/params/is_tuya_status", 0);
        if (isTuya == 1) {
            req.to("/apps/tuya/read_qr", null);
            p.parse(req.mBody);
            String qr = p.getText("/params/qr", "");
            if (qr.isEmpty()) {
                ToastUtils.show(this.getString(R.string.get_qr_err));
            } else {
                ivQRCode.setImageBitmap(ZXing.QR2D(qr, 300));
            }
        }
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