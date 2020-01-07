package com.sjy.picker.utils;

import android.util.Log;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lib.picker.wheelpicker.bean.FifthBean;
import com.lib.picker.wheelpicker.bean.FirstBean;
import com.lib.picker.wheelpicker.bean.FourthBean;
import com.lib.picker.wheelpicker.bean.SecondBean;
import com.lib.picker.wheelpicker.bean.ThirdBean;
import com.lib.picker.wheelpicker.bean.AddressData;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 实战项目的json处理，
 * <p>
 * 贴出来让你们瞅瞅
 *
 * <p>
 */
public class JsonUtils {
    public static final String TAG = "SJY";
    private static AddressData mprovider = null;

    public static AddressData getProvider() {
        return mprovider;
    }

    public static void setProvider(AddressData provider) {
        JsonUtils.mprovider = provider;
    }

    /**
     * @param orgObj    {"tree":{完整json小区数据，从level=1开始}，"current_node_id":"挂载的节点"}
     * @param currentId 该id一般为单元id
     * @return
     */
    public static AddressData loadRoomData(JSONObject orgObj, int currentId) {

        //变量
        AddressData provider = null;
        String[] labels = null;// 标签数组，由labelList转换
        int showNum = 2;// 默认为单元门下，showNum=labelList.size()
        List<String> labelList = new ArrayList<String>();// 用于保存标签和获取筛选器的个数
        List<FirstBean> firstBeans = new ArrayList<FirstBean>();//
        int base_Node_id = orgObj.getIntValue("current_node_id");//该值和currentId是一样的
        //

        Log.d(TAG, "原始json数据：" + orgObj.toJSONString());//
        //完整节点jsonobj数据， 从level=1开始
        JSONObject baseObj = orgObj.getJSONObject("tree");//tree下小区层数据 从level=1开始
        Log.d(TAG, "完整小区层级数据：" + baseObj.toJSONString());

        //
        if (baseObj == null || !baseObj.containsKey("child")) {
            Log.e(TAG, "请重新配置，获取接口信息失败");
            return null;
        }

        //获取currentId下的数据list
        JSONArray targetArray = null;

        //level=1的判断（小区不是list,需要单独判断 ），该处获取的数据，是挂在小区节点上（是level=1还是level=2需要进一步判断）
        if (currentId == baseObj.getIntValue("node_id")) {
            Logg.d("SJY", "目标数据从level=1处获取到");
            targetArray = baseObj.getJSONArray("child");
        } else {//level=2/3的判断
            Logg.d("SJY", "目标数据从level=2/3处获取到");
            targetArray = getPickerOrgData(currentId, baseObj.getJSONArray("child"));
        }

        // 原始数据转换成筛选器需要的数据
        if (targetArray == null) {
            Log.e(TAG, "原始json数据没有currentId对应的数据");
            return null;
        }

        //添加标签
        if (targetArray.getJSONObject(0) == null) {
            Log.e(TAG, "挂载信息全为空，无法添加标签，请房间节点务必添加一个非空信息");
            return null;
        }
        //从数组中遍历，添加标签
        for (int i = 0; i < targetArray.size(); i++) {
            String level = targetArray.getJSONObject(i).getString("level");
            //屏蔽影响因素
            if (level.equals("999")) {
                continue;
            } else {
                if (targetArray.getJSONObject(i).getString("level").equals("2")) {
                    labelList.add("小区");//二道门的小区
                    labelList.add("楼号");
                    labelList.add("单元");
                    labelList.add("层");
                    labelList.add("房间号");
                    Logg.d(TAG, "labelList--2");
                } else if (targetArray.getJSONObject(i).getString("level").equals("3")) {
                    labelList.add("楼号");
                    labelList.add("单元");
                    labelList.add("层");
                    labelList.add("房间号");
                    Logg.d(TAG, "labelList--3");
                } else if (targetArray.getJSONObject(i).getString("level").equals("4")) {
                    labelList.add("单元");
                    labelList.add("层");
                    labelList.add("房间号");
                    Logg.d(TAG, "labelList--4");
                } else if (targetArray.getJSONObject(i).getString("level").equals("5")) {
                    labelList.add("层");
                    labelList.add("户");
                    Logg.d(TAG, "labelList--5");
                } else if (targetArray.getJSONObject(i).getString("level").equals("6")) {
                    //不会出现，最少1个筛选：层+房间号
                    labelList.add("户");
                    Logg.d(TAG, "labelList--6");
                } else {
                    Logg.e(TAG, "labelList--获取异常");
                }
                //拿到标签，跳出for
                if(labelList.size()>0){
                    break;
                }
            }
        }

        //////////////////////////////////////第1层数据--开始////////////////////////////////////////////
        // 第一层数据 第一层没有空的情况
        for (int i = 0; i < targetArray.size(); i++) {
            //第一层的item数据
            JSONObject firstOrg = targetArray.getJSONObject(i);//最大-二道门
            String level_1 = firstOrg.getString("level");
            if (level_1.equals("999")) {//屏蔽物业
                continue;
            }
            String name_1 = filterHanzi(firstOrg.getString("node_name"), level_1);//xx小区/xx楼/...

            //创建筛选器第一列的item数据
            FirstBean bean1 = new FirstBean(firstOrg.getIntValue("node_id")
                    , firstOrg.getInteger("parent_id"),
                    name_1,
                    level_1);
            //////////////////////////////////////第2层数据--开始////////////////////////////////////////////
            // 第二层数据
            JSONArray secondArray = firstOrg.getJSONArray("child");
            if (secondArray != null && secondArray.size() > 0) {
                //非空 seconds
                List<SecondBean> seconds = new ArrayList<SecondBean>();
                for (int j = 0; j < secondArray.size(); j++) {
                    JSONObject secondOrg = secondArray.getJSONObject(j);//最大-楼
                    String level_2 = secondOrg.getString("level");//最大-楼
                    if (level_2.equals("999")) {//屏蔽物业
                        continue;
                    }
                    String name_2 = filterHanzi(secondOrg.getString("node_name"), level_2);//xx小区/xx楼/...

                    //创建筛选器第2列的item数据
                    SecondBean bean2 = new SecondBean(secondOrg.getIntValue("node_id"),
                            secondOrg.getInteger("parent_id"),
                            name_2,
                            level_2);
                    //////////////////////////////////////第3层数据--开始////////////////////////////////////////////
                    // 第3层数据
                    JSONArray thirdArray = secondOrg.getJSONArray("child");
                    if (thirdArray != null && thirdArray.size() > 0) {
                        //非空 thirds
                        List<ThirdBean> thirds = new ArrayList<ThirdBean>();
                        for (int k = 0; k < thirdArray.size(); k++) {
                            JSONObject thirdOrg = thirdArray.getJSONObject(k);
                            String level_3 = thirdOrg.getString("level");//最大-单元
                            if (level_3.equals("999")) {//屏蔽物业
                                continue;
                            }
                            String name_3 = filterHanzi(thirdOrg.getString("node_name"), level_3);//xx小区/xx楼/...

                            //创建筛选器第3列的item数据
                            ThirdBean bean3 = new ThirdBean(thirdOrg.getIntValue("node_id"),
                                    thirdOrg.getInteger("parent_id"),
                                    name_3,
                                    level_3);
                            //////////////////////////////////////第4层数据--开始////////////////////////////////////////////
                            // 第4层数据
                            JSONArray fourthArray = thirdOrg.getJSONArray("child");
                            if (fourthArray != null && fourthArray.size() > 0) {
                                //非空 fourths
                                List<FourthBean> fourths = new ArrayList<FourthBean>();
                                for (int m = 0; m < fourthArray.size(); m++) {
                                    JSONObject fourthOrg = fourthArray.getJSONObject(m);
                                    String level_4 = fourthOrg.getString("level");//最大-层
                                    if (level_4.equals("999")) {//屏蔽物业
                                        continue;
                                    }
                                    String name_4 = filterHanzi(fourthOrg.getString("node_name"), level_4);//xx小区/xx楼/...
                                    //创建筛选器第4列的item数据
                                    FourthBean bean4 = new FourthBean(fourthOrg.getIntValue("node_id"),
                                            fourthOrg.getInteger("parent_id"),
                                            name_4,
                                            level_4);
                                    //////////////////////////////////////第5层数据--开始////////////////////////////////////////////
                                    // 第五层数据
                                    JSONArray fifthArray = fourthOrg.getJSONArray("child");
                                    if (fifthArray != null && fifthArray.size() > 0) {
                                        // 非空 fifths
                                        List<FifthBean> fifths = new ArrayList<FifthBean>();
                                        for (int n = 0; n < fifthArray.size(); n++) {
                                            JSONObject fifthOrg = fifthArray.getJSONObject(n);
                                            String level_5 = fourthOrg.getString("level");
                                            if (level_4.equals("999")) {//屏蔽物业
                                                continue;
                                            }
                                            String name_5 = filterHanzi(fifthOrg.getString("node_name"), level_5);//xx小区/xx楼/...

                                            FifthBean bean5 = new FifthBean(fifthOrg.getIntValue("node_id"),
                                                    fifthOrg.getInteger("parent_id"),
                                                    name_5,
                                                    level_5);
                                            // 添加第5层级数据
                                            fifths.add(bean5);
                                        }
                                        // 第4列关联第5列数据
                                        bean4.setLists(fifths);
                                    } else {//最大-空层
                                        // 空 fifths
                                        List<FifthBean> fifths_n = new ArrayList<FifthBean>();
                                        if (level_4.equals("5")) {//最大-层
                                            //添加第5层级空数据
                                            FifthBean bean5 = new FifthBean(-1,
                                                    -1, "空",
                                                    "6");
                                            fifths_n.add(bean5);

                                        } else {
                                            Log.e(TAG, "绑定空数据bug level=5");
                                        }
                                        //空绑定
                                        if (fifths_n != null && fifths_n.size() > 0) {
                                            bean4.setLists(fifths_n);//绑定第5层list
                                        } else {
                                            Log.e(TAG, "异常- bean4.setLists(fifths_n)为空");
                                        }
                                    }
                                    ///////////////////////////////////////第5层数据--结束///////////////////////////////////////////
                                    fourths.add(bean4);
                                }
                                // 第3列关联第4列数据
                                bean3.setLists(fourths);

                            } else {//最大-空单元（创建空层）
                                //空 fourths
                                List<FourthBean> fourths_n = new ArrayList<FourthBean>();
                                if (level_3.equals("4")) {//最大-单元

                                    //添加第4层级空数据
                                    FourthBean bean4 = new FourthBean(-1,
                                            -1, "空",
                                            "5");
                                    fourths_n.add(bean4);

                                    //添加第5层级空数据
                                    List<FifthBean> fifths = new ArrayList<FifthBean>();
                                    FifthBean bean5 = new FifthBean(-1,
                                            -1, "空",
                                            "6");
                                    fifths.add(bean5);

                                    //list绑定
                                    bean4.setLists(fifths);//绑定第5层list

                                } else if (level_3.equals("5")) {//层

                                    //添加第5层级空数据
                                    FourthBean bean4 = new FourthBean(-1,
                                            -1, "空",
                                            "6");
                                    fourths_n.add(bean4);

                                } else {
                                    Log.e(TAG, "绑定空数据bug level=4");
                                }

                                //空绑定
                                if (fourths_n != null && fourths_n.size() > 0) {
                                    bean3.setLists(fourths_n);//绑定第4层list
                                } else {
                                    Log.e(TAG, "异常-bean3.setLists(fourths_n)为空");
                                }
                            }
                            ///////////////////////////////////////第4层数据--结束///////////////////////////////////////////
                            thirds.add(bean3);
                        }
                        // 非空绑定
                        bean2.setLists(thirds);

                    } else {//最大-空楼（创建空单元）
                        //空 thirds
                        List<ThirdBean> thirds_n = new ArrayList<ThirdBean>();
                        if (level_2.equals("3")) {//最大-楼

                            //添加第3层级空数据
                            ThirdBean bean3 = new ThirdBean(-1,
                                    -1, "空",
                                    "4");
                            thirds_n.add(bean3);

                            //添加第4层级空数据
                            List<FourthBean> fourths = new ArrayList<FourthBean>();
                            FourthBean bean4 = new FourthBean(-1,
                                    -1, "空",
                                    "5");
                            fourths.add(bean4);

                            //添加第5层级空数据
                            List<FifthBean> fifths = new ArrayList<FifthBean>();
                            FifthBean bean5 = new FifthBean(-1,
                                    -1, "空",
                                    "6");
                            fifths.add(bean5);

                            //list绑定
                            bean4.setLists(fifths);//绑定第5层list
                            bean3.setLists(fourths);

                        } else if (level_2.equals("4")) {//单元

                            //添加第3层级空数据
                            ThirdBean bean3 = new ThirdBean(-1,
                                    -1, "空",
                                    "5");
                            thirds_n.add(bean3);

                            //添加第4层级空数据
                            List<FourthBean> fourths = new ArrayList<FourthBean>();
                            FourthBean bean4 = new FourthBean(-1,
                                    -1, "空",
                                    "6");
                            fourths.add(bean4);

                            //list绑定
                            bean3.setLists(fourths);//绑定第4层list

                        } else if (level_2.equals("5")) {//层

                            //添加第3层级空数据
                            ThirdBean bean3 = new ThirdBean(-1,
                                    -1, "空",
                                    "6");
                            thirds_n.add(bean3);

                        } else {
                            Log.e(TAG, "绑定空数据bug level=3");
                        }

                        //空绑定
                        if (thirds_n != null && thirds_n.size() > 0) {
                            bean2.setLists(thirds_n);//绑定第三层list
                        } else {
                            Log.e(TAG, "异常-bean2.setLists(thirds_n)为空");
                        }

                    }
                    //////////////////////////////////////第3层数据--结束////////////////////////////////////////////
                    seconds.add(bean2);
                }
                // 非空绑定
                bean1.setLists(seconds);

            } else {//最大-空二道门（创建空楼）
                //空 seconds
                List<SecondBean> seconds_n = new ArrayList<SecondBean>();
                if (level_1.equals("2")) {//最大-二道门（五级）
                    //添加第2层级空数据

                    SecondBean bean2 = new SecondBean(-1,
                            -1,
                            "空",
                            "3");
                    seconds_n.add(bean2);

                    //添加第3层级空数据
                    List<ThirdBean> thirds = new ArrayList<ThirdBean>();
                    ThirdBean bean3 = new ThirdBean(-1,
                            -1, "空",
                            "4");
                    thirds.add(bean3);

                    //添加第4层级空数据
                    List<FourthBean> fourths = new ArrayList<FourthBean>();
                    FourthBean bean4 = new FourthBean(-1,
                            -1, "空",
                            "5");
                    fourths.add(bean4);

                    //添加第5层级空数据
                    List<FifthBean> fifths = new ArrayList<FifthBean>();
                    FifthBean bean5 = new FifthBean(-1,
                            -1, "空",
                            "6");
                    fifths.add(bean5);

                    //list绑定
                    bean4.setLists(fifths);//绑定第5层list
                    bean3.setLists(fourths);
                    bean2.setLists(thirds);

                } else if (level_1.equals("3")) {//楼

                    //添加第2层级空数据
                    SecondBean bean2 = new SecondBean(-1,
                            -1,
                            "空",
                            "4");
                    seconds_n.add(bean2);

                    //添加第3层级空数据
                    List<ThirdBean> thirds = new ArrayList<ThirdBean>();
                    ThirdBean bean3 = new ThirdBean(-1,
                            -1, "空",
                            "5");
                    thirds.add(bean3);

                    //添加第4层级空数据
                    List<FourthBean> fourths = new ArrayList<FourthBean>();
                    FourthBean bean4 = new FourthBean(-1,
                            -1, "空",
                            "6");
                    fourths.add(bean4);

                    //list绑定
                    bean3.setLists(fourths);//绑定第4层list
                    bean2.setLists(thirds);//

                } else if (level_1.equals("4")) {//单元

                    //添加第2层级空数据
                    SecondBean bean2 = new SecondBean(-1,
                            -1,
                            "空",
                            "5");
                    seconds_n.add(bean2);

                    //添加第3层级空数据
                    List<ThirdBean> thirds = new ArrayList<ThirdBean>();
                    ThirdBean bean3 = new ThirdBean(-1,
                            -1, "空",
                            "6");
                    thirds.add(bean3);

                    //list绑定
                    bean2.setLists(thirds);//绑定第三层list
                } else if (level_1.equals("5")) {//层

                    //添加第2层级空数据
                    SecondBean bean2 = new SecondBean(-1,
                            -1,
                            "空",
                            "6");
                    seconds_n.add(bean2);

                } else {
                    //level_1不可能是 6，最低是 5
                    Log.e(TAG, "绑定空数据bug level=2");
                }

                //空绑定
                if (seconds_n != null && seconds_n.size() > 0) {
                    bean1.setLists(seconds_n);
                } else {
                    Log.e(TAG, "异常- bean1.setLists(seconds_n)为空");
                }

            }
            //////////////////////////////////////第2层数据--结束////////////////////////////////////////////
            firstBeans.add(bean1);
        }
        //////////////////////////////////////第1层数据（第一层没有空的情况）--for循环结束////////////////////////////////////////////

        // 上边获取到labelList数据，转换：
        showNum = labelList.size();// 获取到showNum
        StringBuilder logStr = new StringBuilder();
        for (String name : labelList) {
            logStr.append(name).append("--");
        }

        // 倒序置换
//            Collections.reverse(firstBeans);
//        Collections.reverse(labelList);
        labels = new String[labelList.size()];
        labelList.toArray(labels);// 获取到labels
        Log.i(TAG, "筛选器使用的数据------showNum=" + showNum + "--- labelList.size()" + labelList.size() + "="
                + logStr.toString());


        if (firstBeans != null && labels != null && labelList != null && firstBeans.size() > 0
                && labelList.size() > 0) {
            // 合成筛选器数据
            provider = new AddressData(firstBeans, showNum, labels);
        } else {
            provider = null;
        }
        // 保存
        if (provider != null) {
            Log.i(TAG, "缓存数据");
//            CacheUtils.getInstance().put(TouchConstants.ROOM_DATAS, provider);//项目代码，demo不需要
            mprovider = provider;
        }
        return provider;
    }

    /**
     * 楼 单元 层 去除汉字
     */
    private static String filterHanzi(String str, String level) {
        String newStr;
        if (level.contains("3") || level.contains("4") || level.contains("5")) {
            String reg = "[\u4e00-\u9fa5]";

            Pattern pat = Pattern.compile(reg);

            Matcher mat = pat.matcher(str);

            newStr = mat.replaceAll("");
            return newStr;
        } else {
            return str;
        }
    }

    /**
     * 线程中的方法 筛选器要使用的原始数据
     * <p>
     * 说明：这个方法“小区层级（level=1）”不会是当前节点，因为返回json
     * 小区不是list，从二道门开始才是list，所以如果走该方法，筛选器最大有4个筛选滑动块
     *
     * @return
     */
    private static JSONArray getPickerOrgData(int currentId, JSONArray baseArray) {
        Log.i(TAG, "原始数据baseArray=" + baseArray.size());
        JSONArray array = null;
        if (baseArray != null && baseArray.size() > 0) {
            for (int i = 0; i < baseArray.size(); i++) {// 二道门
                // 解析array下的JSONObject
                JSONObject obj = baseArray.getJSONObject(i);
                //
                if (currentId == obj.getIntValue("node_id")) {
                    array = obj.getJSONArray("child");// 返回该节点下的数据供筛选器使用
                    return array;
                } else {
                    // 递归，寻找下一层是否满足
                    array = getPickerOrgData(currentId, obj.getJSONArray("child"));
                    if (array != null) {
                        Log.e(TAG, "递归拿到数据");
                        return array;
                    }
                }
            }
        } else {
            Log.e(TAG, "原始json数据不能使用，无法创建小区筛选器");
            return null;
        }
        return null;
    }

    /**
     * //项目代码，demo不需要
     * <p>
     * 数据提前加载使用
     */
    public static void loadData() {
        //清除旧缓存
//        CacheUtils.getInstance().remove(TouchConstants.ROOM_DATAS);
//        //
//        String current_note_id = "" + NodeModel.getInstance().currentNodeId;//获取当前节点
//        final int currentId = Integer.valueOf(current_note_id);
//        String str = (String) CacheUtils.getInstance().getString(CacheKeys.NODE_LEVEL_INFO);
//        final JSONObject orgObj = JSONObject.parseObject(str);
//        Log.d(TAG, "PhoneAndRoomAct--拿到原始JSONObject");
//        if (orgObj == null || !orgObj.containsKey("tree")) {
//            Log.e(TAG, "层级信息转换失败");
//            return;
//        } else {
//            new AsyncTask<Void, Void, AddressData>() {
//                @Override
//                protected AddressData doInBackground(Void... voids) {
//                    AddressData provider = loadRoomData(orgObj, currentId);
//                    return provider;
//                }
//
//                @Override
//                protected void onPostExecute(AddressData mProvider) {
//                    super.onPostExecute(mProvider);
//                    if (mProvider != null) {
//                        mprovider = mProvider;
//                    } else {
//                        mprovider = null;
//                    }
//                }
//            }.execute();
//        }
    }

}
