package com.lib.picker.listpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lib.picker.R;
import com.lib.picker.bean.FifthBean;
import com.lib.picker.bean.FirstBean;
import com.lib.picker.bean.FourthBean;
import com.lib.picker.bean.SecondBean;
import com.lib.picker.bean.ThirdBean;

import java.util.Arrays;
import java.util.List;


/**
 * 一条筛选器的View视图（基于ListView实现）
 */
public class FirstPickerView extends LinearLayout implements AdapterView.OnItemClickListener {

    /**
     * 参数
     */
    private Context context;
    private int selectedIndex;//选中项的索引
    private int initPosition = -1;//初始化默认选中项
    private OnItemSelectListener onItemSelectListener;
    //数据
    List<FirstBean> list;
    //布局
    ListView lv_first;
    //适配器
    PickerListAdapter<FirstBean> firstAdapter;


    public FirstPickerView(Context context) {
        super(context);
        createView();
    }

    /**
     * 创建数据
     */
    private void createView() {
        firstAdapter = new PickerListAdapter<FirstBean>(list, context) {
            @Override
            public String provideText(FirstBean firstBeans) {
                return firstBeans.getName();
            }

            @Override
            protected void initCheckedTextView(FilterCheckedTextView checkedTextView) {
                checkedTextView.setPadding(10, 10, 10, 10);
            }
        };
        lv_first = new ListView(context);
        lv_first.setChoiceMode(ListView.CHOICE_MODE_SINGLE);//单选
        lv_first.setAdapter(firstAdapter);
        //设置监听
        lv_first.setOnItemClickListener(this);
    }

    //设置默认选中
    public void setFirstList(List<FirstBean> list, int checkedPosition) {
        this.list = list;
        firstAdapter.setList(list);
        //是否默认选中
        if (checkedPosition != -1) {
            lv_first.setItemChecked(checkedPosition, true);
        }
        setSelectedIndex(checkedPosition);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == lv_first) {
            if (onItemSelectListener != null) {
                onItemSelectListener.onSelected(position);
            }
        }
    }

    /**
     * 外部监听
     *
     * @param onItemSelectListener
     */
    public final void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }


    public final void setSelectedIndex(int index) {
        if (list == null || list.isEmpty()) {
            return;
        }
        int size = list.size();
        if (index == 0 || (index > 0 && index < size && index != selectedIndex)) {
            initPosition = index;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {


    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//    }


    /**
     * 获取选项个数
     */
    protected int getItemCount() {
        return list != null ? list.size() : 0;
    }


    /**
     * 用于兼容旧版本的纯字符串条目
     */
    private static class StringItem implements PickerItem {
        private String name;

        private StringItem(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

    }

    public interface OnItemSelectListener {
        /**
         * 滑动选择回调
         *
         * @param index 当前选择项的索引
         */
        void onSelected(int index);

    }


    /**
     * dp转换为px
     */
    private int toPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int pxValue = (int) (dpValue * scale + 0.5f);
        return pxValue;
    }

    /**
     * px转换为dp
     */
    private int toDp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int dpValue = (int) (pxValue / scale + 0.5f);
        return dpValue;
    }

}
