package com.sjy.picker.ui.pickerutils;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 地址选择器数据提供类（如果想添加省市区选择器等，创建该类型，不要放到AddressPicker中）
 * 根据参数，可以给选择器动态提供数据
 *
 */
public class AddressData implements IAddressData<FirstBean, SecondBean, ThirdBean, FourthBean, FifthBean> {
    /**
     * 显示选择器的个数，构造时，尽量 数据+showNum+titles个数相同,代码没有处理个数有差异导致的问题
     */
    public int showNum;

    public String[] lables;

    private List<FirstBean> firstBeans;

    public AddressData(List<FirstBean> firistBeans, int showNum, String[] lables) {
        this.showNum = showNum;
        this.lables = lables;
        this.firstBeans = firistBeans;
    }


    @NonNull
    @Override
    public List<FirstBean> initFirstData() {
        return firstBeans;
    }

    @NonNull
    @Override
    public List<SecondBean> initSecondData(int firstPosition) {
        return firstBeans.get(firstPosition).getSeconds();
    }

    @NonNull
    @Override
    public List<ThirdBean> initThirdData(int firstPosition, int secondPosition) {
        if (showNum < 3) {
            return new ArrayList<ThirdBean>();
        } else {
            return firstBeans.get(firstPosition).getSeconds()
                    .get(secondPosition).getThirds();
        }
    }

    @NonNull
    @Override
    public List<FourthBean> initFourthData(int firstPosition, int secondPosition, int thirdPosition) {
        if (showNum < 4) {
            return new ArrayList<FourthBean>();
        } else {
            return firstBeans.get(firstPosition).getSeconds()
                    .get(secondPosition).getThirds()
                    .get(thirdPosition).getFours();
        }
    }

    @NonNull
    @Override
    public List<FifthBean> initFifthData(int firstPosition, int secondPosition, int thirdPosition, int fourthPosition) {
        if (showNum < 5) {
            return new ArrayList<FifthBean>();
        } else {
            return firstBeans.get(firstPosition).getSeconds()
                    .get(secondPosition).getThirds()
                    .get(thirdPosition).getFours()
                    .get(fourthPosition).getFifths();
        }
    }
}
