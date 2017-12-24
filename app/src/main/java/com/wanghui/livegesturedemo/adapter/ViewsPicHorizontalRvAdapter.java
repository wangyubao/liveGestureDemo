package com.wanghui.livegesturedemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wanghui.livegesturedemo.R;
import com.wanghui.livegesturedemo.bean.LiveViewersPicBean;
import com.wanghui.livegesturedemo.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2017/1/6.
 */

public class ViewsPicHorizontalRvAdapter extends RecyclerView.Adapter<ViewsPicHorizontalRvAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private int[] mResIds;

    public ViewsPicHorizontalRvAdapter(Context context, int[] resIds) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mResIds = resIds;
        if (this.mResIds == null) {
            this.mResIds = new int[0];
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_live_viewers_pic,
                parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mImg.setImageResource(mResIds[position]);
    }


    @Override
    public int getItemCount() {
        return mResIds.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView mImg;

        public ViewHolder(View arg0) {
            super(arg0);
            mImg = (CircleImageView) arg0
                    .findViewById(R.id.iv_item_show_pic);
        }
    }


    public interface OnItemClickListening {
        void onItemClick(int position);
    }

    OnItemClickListening mOnItemClickListening = null;

    public void setOnItemClickListening(OnItemClickListening e) {
        mOnItemClickListening = e;
    }

}
