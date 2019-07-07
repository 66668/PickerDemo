package com.sjy.picker.utils;

import com.lib.picker.bean.FifthBean;
import com.lib.picker.bean.FirstBean;
import com.lib.picker.bean.FourthBean;
import com.lib.picker.bean.SecondBean;
import com.lib.picker.bean.ThirdBean;
import com.lib.picker.bean.AddressData;

import java.util.ArrayList;
import java.util.List;

/**
 * 自己创建一个假数据，每一级数据都有的5级数据
 */
public class DataUtils {


    /**
     * 使用假数据测试
     * @return
     */
    public static AddressData getData() {

        String[] labels = new String[]{"小区", "楼", "单元", "层", "房间号"};
        int showNum = 5;
        /**
         * 五层for循环，小心脏扑腾的
         */
        //01小区信息
        List<FirstBean> firstBeans = new ArrayList<>();
        for (int i = 0; i < 2; i++) {//01小区
            FirstBean bean = new FirstBean(1, 0, i + "小区", "2");
            //02楼信息
            List<SecondBean> secondBeans = new ArrayList<>();

            for (int j = 0; j < 9; j++) {//02楼
                if (i == 0) {
                    SecondBean bean2 = new SecondBean(2, 1, "10-" + j, "3");
                    //03 单元信息
                    List<ThirdBean> thirdBeans = new ArrayList<>();

                    for (int k = 0; k < 3; k++) {//03单元
                        ThirdBean bean3 = new ThirdBean(3, 2, "0" + (k + 1), "4");
                        //04 层信息
                        List<FourthBean> fourthBeans = new ArrayList<>();
                        for (int a = 0; a < 6; a++) {
                            FourthBean bean4 = new FourthBean(4, 3, "0" + (a + 1), "5");

                            //05房间号信息
                            List<FifthBean> fifthBeans = new ArrayList<>();
                            fifthBeans.add(new FifthBean(5, 4, "0101", "6"));
                            fifthBeans.add(new FifthBean(5, 4, "0102", "6"));
                            fifthBeans.add(new FifthBean(5, 4, "0201", "6"));
                            fifthBeans.add(new FifthBean(5, 4, "0202", "6"));
                            fifthBeans.add(new FifthBean(5, 4, "0301", "6"));
                            fifthBeans.add(new FifthBean(5, 4, "0302", "6"));
                            fifthBeans.add(new FifthBean(5, 4, "0401", "6"));
                            fifthBeans.add(new FifthBean(5, 4, "0402", "6"));
                            fifthBeans.add(new FifthBean(5, 4, "0501", "6"));
                            fifthBeans.add(new FifthBean(5, 4, "0502", "6"));
                            //04添加楼号信息
                            bean4.setLists(fifthBeans);
                            fourthBeans.add(bean4);
                        }

                        //03添加层信息
                        bean3.setLists(fourthBeans);
                        thirdBeans.add(bean3);
                    }
                    //02 添加单元信息
                    bean2.setLists(thirdBeans);
                    secondBeans.add(bean2);
                } else {
                    SecondBean bean2 = new SecondBean(2, 1, "20-" + j, "3");
                    //03 单元信息
                    List<ThirdBean> thirdBeans = new ArrayList<>();

                    for (int k = 0; k < 3; k++) {//03单元
                        ThirdBean bean3 = new ThirdBean(3, 2, "00" + (k + 1), "4");
                        //04 层信息
                        List<FourthBean> fourthBeans = new ArrayList<>();
                        for (int a = 0; a < 5; a++) {
                            FourthBean bean4 = new FourthBean(4, 3, "0" + (a + 1), "5");

                            //05房间号信息
                            List<FifthBean> fifthBeans = new ArrayList<>();
                            fifthBeans.add(new FifthBean(5, 4, "101", "6"));
                            fifthBeans.add(new FifthBean(5, 4, "102", "6"));
                            fifthBeans.add(new FifthBean(5, 4, "201", "6"));
                            fifthBeans.add(new FifthBean(5, 4, "202", "6"));
                            fifthBeans.add(new FifthBean(5, 4, "301", "6"));
                            fifthBeans.add(new FifthBean(5, 4, "302", "6"));
                            fifthBeans.add(new FifthBean(5, 4, "401", "6"));
                            fifthBeans.add(new FifthBean(5, 4, "402", "6"));
                            fifthBeans.add(new FifthBean(5, 4, "501", "6"));
                            fifthBeans.add(new FifthBean(5, 4, "502", "6"));
                            fifthBeans.add(new FifthBean(5, 4, "601", "6"));
                            fifthBeans.add(new FifthBean(5, 4, "602", "6"));

                            //04添加楼号信息
                            bean4.setLists(fifthBeans);
                            fourthBeans.add(bean4);
                        }

                        //03添加层信息
                        bean3.setLists(fourthBeans);
                        thirdBeans.add(bean3);
                    }
                    //02 添加单元信息
                    bean2.setLists(thirdBeans);
                    secondBeans.add(bean2);
                }
            }

            //01添加楼信息
            bean.setLists(secondBeans);
            firstBeans.add(bean);
        }
        //构建数据源给选择器使用
        return new AddressData(firstBeans, showNum, labels);
    }
}
