package com.dnake.desktop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.dnake.desktop.BaseDialogFragment;
import com.dnake.desktop.R;
import com.dnake.desktop.utils.DensityUtil;
import com.dnake.desktop.utils.NavigationBarUtil;
import com.dnake.v700.dmsg;
import com.dnake.v700.dxml;
import com.dnake.v700.utils;
import com.dnake.widget.ZXing;

public class OneKeySecurityDialogFragment extends BaseDialogFragment {
    private ImageView iv_out, iv_home, iv_sleep, iv_withdraw;
    private LinearLayout btn_out, btn_home, btn_sleep, btn_withdraw;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.NoticeDialogStyle);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (inflaterView == null) {
            inflaterView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_one_key_security, null);
        }
        iv_out = (ImageView) inflaterView.findViewById(R.id.iv_out);
        iv_home = (ImageView) inflaterView.findViewById(R.id.iv_home);
        iv_sleep = (ImageView) inflaterView.findViewById(R.id.iv_sleep);
        iv_withdraw = (ImageView) inflaterView.findViewById(R.id.iv_withdraw);
        btn_out = (LinearLayout) inflaterView.findViewById(R.id.defence_btn_out);
        btn_out.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setSecurityDefence(1);
                slaveSetMark();
                load_st();
            }
        });
        btn_home = (LinearLayout) inflaterView.findViewById(R.id.defence_btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setSecurityDefence(2);
                slaveSetMark();
                load_st();
            }
        });
        btn_sleep = (LinearLayout) inflaterView.findViewById(R.id.defence_btn_sleep);
        btn_sleep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setSecurityDefence(3);
                slaveSetMark();
                load_st();
            }
        });
        btn_withdraw = (LinearLayout) inflaterView.findViewById(R.id.defence_btn_withdraw);
        btn_withdraw.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setSecurityDefence(0);
                slaveSetMark();
                load_st();
            }
        });
        load_st();
        return inflaterView;
    }

    public void load_st() {
        iv_out.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ic_fq_out_off));
        iv_home.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ic_fq_home_off));
        iv_sleep.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ic_fq_sleep_off));
        iv_withdraw.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ic_fq_off_off));

        int defenceType = getSecurityDefence();
        switch (defenceType) {
            case 0:
                iv_withdraw.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ic_fq_off));
                break;
            case 1:
                iv_out.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ic_fq_out));
                break;
            case 2:
                iv_home.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ic_fq_home));
                break;
            case 3:
                iv_sleep.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ic_fq_sleep));
                break;
        }
    }

    private int getSecurityDefence() {
        dmsg req = new dmsg();
        dxml p = new dxml();
        req.to("/security/defence_read", null);
        p.parse(req.mBody);
        int defence = p.getInt("/params/defence", 0);
        return defence;
    }

    private void setSecurityDefence(int defence) {
        dmsg req = new dmsg();
        dxml p = new dxml();
        p.setInt("/params/defence", defence);
        req.to("/security/defence", p.toString());
    }

    private void slaveSetMark() {
        dmsg req = new dmsg();
        req.to("/security/slaves_setMarks_def", null);
    }

    @Override
    public void onStart() {
        super.onStart();
        final WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.width = DensityUtil.dip2px(getActivity(), 850);
        layoutParams.height = DensityUtil.dip2px(getActivity(), 450);
//        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getDialog().getWindow().setAttributes(layoutParams);
        NavigationBarUtil.hideNavigationBar(getDialog().getWindow());
        NavigationBarUtil.clearFocusNotAle(getDialog().getWindow());
    }
}
