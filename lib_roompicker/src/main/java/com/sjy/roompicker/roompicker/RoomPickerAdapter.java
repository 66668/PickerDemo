package com.sjy.roompicker.roompicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sjy.roompicker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 新版UI：房间筛选
 * <p>
 * 2019-12-25 sjy
 */
public class RoomPickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_TOUCH = 1;//触屏
    private final int TYPE_UNTOUCH = 2;//非触屏

    private Context mContext;
    public List<RoomItemBean> datas = new ArrayList<RoomItemBean>();
    private RoomPickerItemListener callback;
    private boolean isUntouch;
    private boolean isPort;

    public RoomPickerAdapter(Context context) {
        this.mContext = context;
    }

    public void setDatas(List<RoomItemBean> mMoreMenuItems) {
        this.datas = mMoreMenuItems;
    }

    /**
     * 该值设置早于菜单值
     *
     * @param untouch
     */
    public void setUntouch(boolean untouch) {
        isUntouch = untouch;
    }

    public boolean isPort() {
        return isPort;
    }

    public void setPort(boolean port) {
        isPort = port;
    }

    public void updateDatas(List<RoomItemBean> mMoreMenuItems) {
        this.datas = mMoreMenuItems;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(RoomPickerItemListener callback) {
        this.callback = callback;
    }

    //绑定布局
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_TOUCH) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_touch_roompicker, viewGroup, false);
            return new TouchViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_untouch_roompicker, viewGroup, false);
            return new UntouchViewHolder(view);
        }
    }

    //绑定数据
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final RoomItemBean item = datas.get(position);
        if (holder instanceof TouchViewHolder) {
            TouchViewHolder touchViewHolder = (TouchViewHolder) holder;
            touchViewHolder.tv_room_touch.setText(item.getNode_name());
            // 触摸监听
            touchViewHolder.tv_room_touch.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (callback != null) {
                        callback.onItemClickListener(item, position);
                    }
                }
            });
        } else if (holder instanceof UntouchViewHolder) {
            UntouchViewHolder unTouchViewHolder = (UntouchViewHolder) holder;
            unTouchViewHolder.tv_room_untouch.setText(item.getNode_name());
            unTouchViewHolder.tv_num.setText("" + (position + 1));
            // 触摸监听
            unTouchViewHolder.tv_room_untouch.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (callback != null) {
                        callback.onItemClickListener(item, position);
                    }
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return datas.size();
    }

    /**
     * 获取item的类型
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (isUntouch) {
            return TYPE_UNTOUCH;
        } else {
            return TYPE_TOUCH;
        }
    }

    public RoomItemBean getItem(int pos) {
        if (pos < getItemCount() && pos >= 0) {
            return datas.get(pos);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView tv_room_touch, tv_room_untouch, tv_num;
        RelativeLayout ly_touch, ly_untouch;

        public MyHolder(View itemView) {
            super(itemView);
            ly_touch = (RelativeLayout) itemView.findViewById(R.id.ly_touch);
            ly_untouch = (RelativeLayout) itemView.findViewById(R.id.ly_untouch);
            tv_room_touch = (TextView) itemView.findViewById(R.id.tv_room_touch);
            tv_room_untouch = (TextView) itemView.findViewById(R.id.tv_room_untouch);
            tv_num = (TextView) itemView.findViewById(R.id.tv_num);
        }
    }

    /**
     *
     */
    private class TouchViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout ly_touch;
        TextView tv_room_touch;

        public TouchViewHolder(@NonNull View itemView) {
            super(itemView);
            ly_touch = (RelativeLayout) itemView.findViewById(R.id.ly_touch);
            tv_room_touch = (TextView) itemView.findViewById(R.id.tv_room_touch);
        }
    }

    /**
     * 脚布局
     */
    private class UntouchViewHolder extends RecyclerView.ViewHolder {
        TextView tv_room_untouch, tv_num;
        RelativeLayout ly_untouch;

        public UntouchViewHolder(@NonNull View itemView) {
            super(itemView);
            ly_untouch = (RelativeLayout) itemView.findViewById(R.id.ly_untouch);
            tv_room_untouch = (TextView) itemView.findViewById(R.id.tv_room_untouch);
            tv_num = (TextView) itemView.findViewById(R.id.tv_num);
        }
    }

}
