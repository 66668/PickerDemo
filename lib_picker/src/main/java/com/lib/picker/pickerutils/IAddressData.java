package com.lib.picker.pickerutils;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * 五级联动的数据接口
 */
public interface IAddressData<Fst extends LinkedFirstItem<Snd>//1
        , Snd extends LinkedSecondItem<Trd>//2
        , Trd extends LinkedThirdItem<Fur>//3
        , Fur extends LinkedFourItem<Fiv>//4
        , Fiv> {//5
    
    //初始化1级数据
    @NonNull
    List<Fst> initFirstData();

    //关联2级数据
    @NonNull
    List<Snd> initSecondData(int firstPosition);

    /**
     * 根据第一二级数据联动第三级数据
     */
    @NonNull
    List<Trd> initThirdData(int firstPosition, int secondPosition);

    /**
     * 根据第一二三级数据联动第4级数据
     */
    @NonNull
    List<Fur> initFourthData(int firstPosition, int secondPosition, int thirdPosition);

    /**
     * 根据第一二三四级数据联动第5级数据
     */
    @NonNull
    List<Fiv> initFifthData(int firstPosition, int secondPosition, int thirdPosition, int fourthPosition);

}
