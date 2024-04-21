package com.dnake.desktop.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dnake.desktop.DesktopActivity;
import com.dnake.desktop.R;
import com.dnake.desktop.model.AppModel;
import com.dnake.desktop.model.DesktopMainLogger;

import java.util.ArrayList;
import java.util.List;

public class MainAppMenuAdapter extends RecyclerView.Adapter<MainAppMenuAdapter.ViewHolder> {

    private Context mContext;

    public List<AppModel> getmDatas() {
        return mDatas;
    }

    public List<AppModel> getmTempDatas() {
        return mTempDatas;
    }

    public boolean containsAppById(int appId) {
        for (AppModel appModel : mDatas) {
            if (appModel.getId() == appId) {
                return true;
            }
        }
        return false;
    }

    public int getAppIndexById(int appId) {
        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i).getId() == appId) {
                return i;
            }
        }
        return -1;
    }

    private List<AppModel> mDatas;
    private List<AppModel> mTempDatas = new ArrayList<>();
    private int is_edit = 0;

    public int getIs_edit() {
        return is_edit;
    }

    public void setIs_edit(int is_edit) {
        this.is_edit = is_edit;
        notifyDataSetChanged();
    }

    public MainAppMenuAdapter(Context context, List<AppModel> datas) {
        mContext = context;
        mDatas = datas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_main_menu_app, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppModel item;
        holder.ivPic.setVisibility(View.VISIBLE);
        holder.ivPicThird.setVisibility(View.GONE);
        if (position == mDatas.size()) {
            item = new AppModel();
        } else {
            item = mDatas.get(position);
            holder.tvName.setText(item.getName());
        }
        if (is_edit == 0) {
            holder.btnDel.setVisibility(View.GONE);
            holder.layoutNormal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) mCallback.onItemClick(holder.layoutMain, item);
                }
            });
//            holder.layoutMain.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mCallback != null) mCallback.onItemClick(holder.layoutMain, item);
//                }
//            });
        } else {
            holder.tvNum.setVisibility(View.GONE);
            holder.btnDel.setVisibility(View.VISIBLE);
        }
        holder.layoutNormal.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mCallback != null) mCallback.onItemLongPress(holder);
                return false;
            }
        });
//        holder.layoutMain.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (mCallback != null) mCallback.onItemLongPress(holder);
//                return false;
//            }
//        });
        if (position == mDatas.size()) {
            holder.btnDel.setVisibility(View.GONE);
            holder.tvNum.setVisibility(View.GONE);
            holder.btnAdd.setVisibility(View.VISIBLE);
            holder.layoutNormal.setVisibility(View.GONE);
        } else {
            holder.btnAdd.setVisibility(View.GONE);
            holder.layoutNormal.setVisibility(View.VISIBLE);
        }
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
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) mCallback.onAddClick(holder.layoutNormal);
//                if (mCallback != null) mCallback.onAddClick(holder.layoutMain);
            }
        });
        holder.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatas.remove(position);
                notifyDataSetChanged();
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

    @Override
    public int getItemCount() {
        if (is_edit == 0) {
            return mDatas.size();
        } else {
            if (mDatas.size() <= 3) {
                return mDatas.size() + 1;
            } else {
                return mDatas.size();
            }
        }
    }

    public void setListData(List<AppModel> data) {
        mDatas = data;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
//        notifyDataSetChanged();
    }

    public void addItem(AppModel item) {
        mDatas.add(item);
    }

    public void tempData() {//缓存当前menu，取消可还原
        mTempDatas.clear();
        mTempDatas.addAll(mDatas);
    }

    public void cancelUpdate() {
        mDatas.clear();
        mDatas.addAll(mTempDatas);
        notifyDataSetChanged();
    }

    public void saveUpdate() {
        //do save
        DesktopMainLogger.save(mDatas);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public FrameLayout layoutMain;
        public LinearLayout layoutNormal;
        public ImageView ivPic;
        public TextView tvName;
        public LinearLayout btnAdd;
        public TextView tvNum;
        public ImageView btnDel;
        public ImageView ivPicThird;

        public ViewHolder(View view) {
            super(view);
            this.itemView = view;
            layoutMain = (FrameLayout) view.findViewById(R.id.layout_main);
            layoutNormal = (LinearLayout) view.findViewById(R.id.layout_normal);
            ivPic = (ImageView) view.findViewById(R.id.iv_pic);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            btnAdd = (LinearLayout) view.findViewById(R.id.btn_add);
            tvNum = (TextView) view.findViewById(R.id.tv_num);
            btnDel = (ImageView) view.findViewById(R.id.btn_del);
            ivPicThird = (ImageView) view.findViewById(R.id.iv_pic_third);
        }
    }

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onItemClick(View view, AppModel model);

        void onAddClick(View view);

        void onItemLongPress(RecyclerView.ViewHolder vh);
    }

    public int getIndexByItemID(int id) {
        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }
}