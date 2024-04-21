package com.dnake.desktop.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dnake.desktop.R;
import com.dnake.desktop.model.AppModel;
import com.dnake.v700.sys;
import com.dnake.v700.utils;

import java.util.List;

public class AppsGridAdapter extends RecyclerView.Adapter<AppsGridAdapter.ViewHolder> {

    private Context mContext;
    private List<AppModel> mDatas;

    public AppsGridAdapter(Context context, List<AppModel> datas) {
        mContext = context;
        mDatas = datas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_apps_rv, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppModel item = mDatas.get(position);
        holder.tvName.setText(item.getName());
        holder.ivPic.setVisibility(View.VISIBLE);
        holder.ivPicThird.setVisibility(View.GONE);
        switch (item.getId()) {
            case 10001:
                holder.ivPic.setImageResource(R.mipmap.ic_apps_call);
                holder.tvName.setText(R.string.app_call);
                break;
            case 10002:
                holder.ivPic.setImageResource(R.mipmap.ic_apps_monitor);
                holder.tvName.setText(R.string.app_monitor);
                break;
            case 10003:
                holder.ivPic.setImageResource(R.mipmap.ic_apps_camera);
                holder.tvName.setText(R.string.app_camera);
                break;
            case 10004:
                holder.ivPic.setImageResource(R.mipmap.ic_apps_quad_splitter);
                holder.tvName.setText(R.string.app_quad_splitter);
                break;
            case 10005:
                holder.ivPic.setImageResource(R.mipmap.ic_apps_phonebook);
                holder.tvName.setText(R.string.app_phonebook);
                break;
            case 10006:
                holder.ivPic.setImageResource(R.mipmap.ic_apps_message);
                holder.tvName.setText(R.string.app_message);
                break;
            case 10007:
                holder.ivPic.setImageResource(R.mipmap.ic_apps_dnd_off);
                holder.tvName.setText(R.string.app_dnd);
                break;
            case 10008:
                holder.ivPic.setImageResource(R.mipmap.ic_apps_qr_code);
                holder.tvName.setText(R.string.app_qr_code);
                break;
            case 10009:
                holder.ivPic.setImageResource(R.mipmap.ic_apps_security_on);
                holder.tvName.setText(R.string.app_security);
                break;
            case 10010:
                holder.ivPic.setImageResource(R.mipmap.ic_apps_concierge);
                holder.tvName.setText(R.string.app_concierge);
                break;
            case 10011:
                holder.ivPic.setImageResource(R.mipmap.ic_apps_lift_ctrl);
                holder.tvName.setText(R.string.app_lift_ctrl);
                break;
            case 10012:
                holder.ivPic.setImageResource(R.mipmap.ic_apps_sos);
                holder.tvName.setText(R.string.app_sos);
                break;
            case 10013:
                holder.ivPic.setImageResource(R.mipmap.ic_apps_settings);
                holder.tvName.setText(R.string.app_setting);
                break;
            default:
                if (item.getIcon() != null) {
                    holder.ivPic.setVisibility(View.GONE);
                    holder.ivPicThird.setVisibility(View.VISIBLE);
                    holder.ivPicThird.setImageDrawable(item.getIcon());
                } else if (!TextUtils.isEmpty(item.getPackageName())) {
                    holder.ivPic.setVisibility(View.GONE);
                    holder.ivPicThird.setVisibility(View.VISIBLE);
                    holder.ivPicThird.setImageDrawable(getAppIcon(item.getPackageName()));
                }
                break;
        }
        if (utils.getVersionNameAll().contains("902")) {
            holder.layoutMain.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (item.getId() == 10012 && sys.sos.delay == 3) {
                        if (mCallback != null) mCallback.longClickSOS(v);
                        return true;
                    }
                    return false;
                }
            });
        } else {
            holder.layoutMain.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View arg0, MotionEvent arg1) {
                    if (item.getId() == 10012 && sys.sos.delay == 3) {
                        mLastEvent = arg1.getAction();
                        switch (arg1.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                handler.postDelayed(runnable, 3000);
                                layout = holder.layoutMain;
                                break;
                            case MotionEvent.ACTION_MOVE:
                                break;
                            case MotionEvent.ACTION_UP:
                                handler.removeCallbacks(runnable);
                                layout = null;
                                break;

                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }
        holder.layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getId() != 10012 || sys.sos.delay != 3) {
                    if (mCallback != null) mCallback.onEnterBar(holder.layoutMain, item);
                }
            }
        });
    }

    private Drawable getAppIcon(String packageName) {
        Drawable icon = null;
        PackageManager pm = mContext.getPackageManager();
        try {
            icon = pm.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return icon;
    }

    View layout = null;
    public static int mLastEvent = MotionEvent.ACTION_UP;
    final Handler handler = new Handler();
    final Runnable runnable = new Runnable() {

        @Override
        public void run() {
//            if (mLastEvent == MotionEvent.ACTION_MOVE) {
            if (mCallback != null) mCallback.longClickSOS(layout);
//            }
        }
    };

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void setListData(List<AppModel> data) {
        mDatas = data;
        notifyDataSetChanged();
    }

    public void addItem(AppModel item) {
        mDatas.add(item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public LinearLayout layoutMain;
        public ImageView ivPic;
        public TextView tvName;
        public ImageView ivPicThird;

        public ViewHolder(View view) {
            super(view);
            this.itemView = view;
            layoutMain = (LinearLayout) view.findViewById(R.id.layout_main);
            ivPic = (ImageView) view.findViewById(R.id.iv_pic);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            ivPicThird = (ImageView) view.findViewById(R.id.iv_pic_third);
        }
    }

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onEnterBar(View view, Object bean);

        void longClickSOS(View view);
    }
}