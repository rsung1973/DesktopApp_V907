package com.dnake.desktop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dnake.desktop.R;
import com.dnake.desktop.model.ContactLogger;

import java.util.List;

public class PhonebookListAdapter extends RecyclerView.Adapter<PhonebookListAdapter.ViewHolder> {

    private Context mContext;

    public List<ContactLogger.data> getmDatas() {
        return mDatas;
    }

    public void setmDatas(List<ContactLogger.data> mDatas) {
        this.mDatas = mDatas;
    }

    private List<ContactLogger.data> mDatas;

    public PhonebookListAdapter(Context context, List<ContactLogger.data> datas) {
        mContext = context;
        mDatas = datas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_phonebook_rv, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContactLogger.data item = mDatas.get(position);
        holder.tvName.setText(item.name);
        holder.tvName.setTextColor(mContext.getResources().getColor(R.color.white));
        holder.layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.tvName.setTextColor(mContext.getResources().getColor(R.color.white));
                if (mCallback != null) {
                    mCallback.onClickSelect(v, position, item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutMain;
        ImageView ivType;
        TextView tvName;

        public ViewHolder(View view) {
            super(view);
            layoutMain = (LinearLayout) view.findViewById(R.id.layout_main);
            ivType = (ImageView) view.findViewById(R.id.iv_type);
            tvName = (TextView) view.findViewById(R.id.tv_name);
        }
    }

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onClickSelect(View view, int index, ContactLogger.data bean);

        void onEnterDetail(int index, ContactLogger.data bean);
    }
}
