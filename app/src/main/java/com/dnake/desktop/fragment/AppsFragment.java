package com.dnake.desktop.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dnake.desktop.BaseFragment;
import com.dnake.desktop.DesktopActivity;
import com.dnake.desktop.GridSpaceItemDecoration;
import com.dnake.desktop.R;
import com.dnake.desktop.SysReceiver;
import com.dnake.desktop.adapter.AppsGridAdapter;
import com.dnake.desktop.model.AppModel;
import com.dnake.desktop.utils.DensityUtil;
import com.dnake.v700.dmsg;
import com.dnake.v700.dxml;
import com.dnake.v700.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AppsFragment extends BaseFragment implements AppsGridAdapter.Callback {

    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    private String TAG;

    private RecyclerView rvApps;
    private AppsGridAdapter mAdapter;

    public List<AppModel> getAppsList() {
        return appsList;
    }

    public void setAppsList(List<AppModel> appsList) {
        this.appsList = appsList;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private List<AppModel> appsList;

    private Handler e_timer = null;
    private ScheduledThreadPoolExecutor timer = null;
//    private ScheduledThreadPoolExecutor sos_timer = null;
    public int mDnd = 0;

    public static AppsFragment newInstance() {
        return new AppsFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sub_screen;
    }

    @Override
    public void initView() {
        rvApps = (RecyclerView) rootView.findViewById(R.id.rv_apps);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 6);
        rvApps.setLayoutManager(gridLayoutManager);
        rvApps.addItemDecoration(new GridSpaceItemDecoration(2, DensityUtil.dip2px(mContext, 2), DensityUtil.dip2px(mContext, 2)));
        mAdapter = new AppsGridAdapter(this.getActivity(), appsList);
        mAdapter.setCallback(this);
        rvApps.setAdapter(mAdapter);
    }

    public void refresh() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void initData() {
        if (e_timer != null) {
            e_timer.removeCallbacksAndMessages(null);
            e_timer = null;
        }
//        if (sos_timer != null) {
//            sos_timer.shutdownNow();
//            sos_timer = null;
//        }
        if (timer != null) {
//            timer.cancel();
//            timer = null;
            timer.shutdownNow();
            timer = null;
        }
        e_timer = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (timer != null) doProcess();
            }
        };
        timer = new ScheduledThreadPoolExecutor(1);
        timer.scheduleWithFixedDelay(new tRun(), 1000, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void stopView() {
        if (e_timer != null) {
            e_timer.removeCallbacksAndMessages(null);
            e_timer = null;
        }
//        if (sos_timer != null) {
//            sos_timer.shutdownNow();
//            sos_timer = null;
//        }
        if (timer != null) {
//            timer.cancel();
//            timer = null;
            timer.shutdownNow();
            timer = null;
        }
    }

    @Override
    public void onEnterBar(View view, Object bean) {
//        onScaleAnimationBySpringWayOne(view);
        AppModel a = (AppModel) bean;
        if (a.getName().equals(getString(R.string.app_setting))) {
            Intent it = new Intent();
            ComponentName comp = new ComponentName("com.dnake.apps", "com.dnake.setting.activity.SettingsActivity");
            it.setComponent(comp);
            it.setAction("android.intent.action.VIEW");
            getActivity().startActivity(it);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (a.getName().equals(getString(R.string.app_call))) {
            Intent it = new Intent();
            ComponentName comp = new ComponentName("com.dnake.talk", "com.dnake.talk.activity.CallMainActivity");
            it.setComponent(comp);
            it.setAction("android.intent.action.VIEW");
            getActivity().startActivity(it);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (a.getName().equals(getString(R.string.app_security))) {
            Intent it = new Intent();
            ComponentName comp = new ComponentName("com.dnake.security", "com.dnake.security.MainActivity");
            it.setComponent(comp);
            it.setAction("android.intent.action.VIEW");
            getActivity().startActivity(it);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (a.getName().equals(getString(R.string.app_message))) {
            Intent it = new Intent();
            ComponentName comp = new ComponentName("com.dnake.apps", "com.dnake.message.activity.MessageActivity");
            it.setComponent(comp);
            it.setAction("android.intent.action.VIEW");
            startActivity(it);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (a.getName().equals(getString(R.string.app_qr_code))) {
            ((DesktopActivity) mContext).popupQRCode();
        } else if (a.getName().equals(getString(R.string.app_concierge))) {
            ((DesktopActivity) mContext).popupConcierge();
        } else if (a.getName().equals(getString(R.string.app_lift_ctrl))) {
//            ((DesktopActivity) mContext).popupLiftCtrlConfirm();
            Intent it = new Intent();
            ComponentName comp = new ComponentName("com.dnake.smart", "com.dnake.smart.ElevatorLabel");
            it.setComponent(comp);
            it.setAction("android.intent.action.VIEW");
            startActivity(it);
        } else if (a.getName().equals(getString(R.string.app_monitor))) {
            Intent it = new Intent();
            ComponentName comp = new ComponentName("com.dnake.talk", "com.dnake.talk.activity.MonitorActivity");
            it.setComponent(comp);
            it.setAction("android.intent.action.VIEW");
            startActivity(it);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (a.getName().equals(getString(R.string.app_camera))) {
            Intent it = new Intent();
            ComponentName comp = new ComponentName("com.dnake.security", "com.dnake.security.activity.IpcActivity");
            it.setComponent(comp);
            it.setAction("android.intent.action.VIEW");
            startActivity(it);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (a.getName().equals(getString(R.string.app_phonebook))) {
            Intent it = new Intent();
            ComponentName comp = new ComponentName("com.dnake.talk", "com.dnake.talk.activity.CallMainActivity");
            it.setComponent(comp);
            it.setAction("android.intent.action.VIEW");
            it.putExtra("tab_index", 2);
            startActivity(it);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (a.getName().equals(getString(R.string.app_sos))) {
            longClickSOS(view);
        } else if (a.getName().equals(getString(R.string.app_dnd))) {
            Intent it = new Intent("com.dnake.broadcast");
            it.putExtra("event", "com.dnake.talk.dnd");
            it.putExtra("data", mDnd == 0 ? 1 : 0);
            mContext.sendBroadcast(it);
//            dmsg req = new dmsg();
//            dxml p = new dxml();
//            p.setText("/params/path", "/storage/emulated/0/Download/mydemo.apk");
//            req.to("/apps/web/install_apk", p.toString());
        } else if (a.getName().equals(getString(R.string.app_home_screen))) {
            //跳转到主页
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        } else if (a.getName().equals(getString(R.string.app_quad_splitter))) {
            Intent it = new Intent();
            ComponentName comp = new ComponentName("com.dnake.talk", "com.dnake.talk.activity.MonitorQuadSplitterActivity");
            it.setComponent(comp);
            it.setAction("android.intent.action.VIEW");
            startActivity(it);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            if (a.getIntent() != null) {
                utils.openThirdApp(a.getPackageName(), a.getAction());
            }
        }
    }

    @Override
    public void longClickSOS(View view) {
        //sos长按3秒事件
        ((ImageView) view.findViewById(R.id.iv_pic)).setImageResource(R.mipmap.ic_apps_sos_red);
        ((TextView) view.findViewById(R.id.tv_name)).setTextColor(getResources().getColor(R.color.script_red));
        //发送警报给cms，管理机
        dmsg req = new dmsg();
        req.to("/security/sos", null);

//        sos_timer = new ScheduledThreadPoolExecutor(1);
//        sos_timer.schedule(new Runnable() {
//            @Override
//            public void run() {
//                ((ImageView) view.findViewById(R.id.iv_pic)).setImageResource(R.mipmap.ic_apps_sos);
//                ((TextView) view.findViewById(R.id.tv_name)).setTextColor(getResources().getColor(R.color.white));
//            }
//        }, 3000, TimeUnit.MILLISECONDS);

        //sos警报持续3秒
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((ImageView) view.findViewById(R.id.iv_pic)).setImageResource(R.mipmap.ic_apps_sos);
                ((TextView) view.findViewById(R.id.tv_name)).setTextColor(getResources().getColor(R.color.white));
            }
        }, 3000);

        //send alarm to line
        Intent it = new Intent("com.dnake.doorAlarm");
        it.addFlags((int)0x01000000 /*Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND*/);
//                it.putExtra("event", "com.dnake.boot");
        getActivity().sendBroadcast(it);

    }

    private void doProcess() {
        if (isVisible) {
            if (mDnd != SysReceiver.dnd && TAG.equals("0")) {
                mDnd = SysReceiver.dnd;
                View dndView = rvApps.getLayoutManager().findViewByPosition(5);
                if (mDnd == 0) {
                    ((ImageView) dndView.findViewById(R.id.iv_pic)).setImageResource(R.mipmap.ic_apps_dnd_off);
                } else {
                    ((ImageView) dndView.findViewById(R.id.iv_pic)).setImageResource(R.mipmap.ic_apps_dnd_on);
                }
            }
        }
    }

    private void onScaleAnimationBySpringWayOne(View view) {
        SpringAnimation animationX = new SpringAnimation(view, SpringAnimation.SCALE_X, 1.0f);
        SpringAnimation animationY = new SpringAnimation(view, SpringAnimation.SCALE_Y, 1.0f);
        animationX.getSpring().setStiffness(SpringForce.STIFFNESS_LOW);
        animationX.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
        animationX.setStartValue(0.8f);
        animationY.getSpring().setStiffness(SpringForce.STIFFNESS_LOW);
        animationY.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
        animationY.setStartValue(0.8f);
        animationX.start();
        animationY.start();
    }

    private class tRun implements Runnable {
        public void run() {
            if (e_timer != null) e_timer.sendMessage(e_timer.obtainMessage());
        }
    }
}