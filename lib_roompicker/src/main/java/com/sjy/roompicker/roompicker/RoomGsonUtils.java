package com.sjy.roompicker.roompicker;

import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sjy.roompicker.roompicker.utils.CacheUtils;
import com.sjy.roompicker.roompicker.utils.TouchConstants;

import java.util.logging.Logger;

/**
 * 房间json节点处理
 * 说明：
 * 1. 二道门节点可为空，单元节点可为空--20190513
 * <p>
 * sjy-2019-12-25
 */
public class RoomGsonUtils {
    public static final String TAG = "RJ_ROOM";

    /**
     * 获取当前节点下的数据，并缓存数据
     *
     * @param orgObj    {"tree":{完整json小区数据，从level=1开始}，"current_node_id":"挂载的节点"}
     * @param currentId 该id为挂载的节点id
     * @return
     */
    public static JSONArray getRoomJsonArray(JSONObject orgObj, int currentId) {
        JSONArray targetArray = null;//currentId对应的JSONArray
        //完整节点jsonobj数据， 从level=1开始
        JSONObject baseObj = orgObj.getJSONObject("tree");//tree下小区层数据 从level=1开始
        if (baseObj == null || !baseObj.containsKey("child")) {
            Log.e(TAG, "请重新配置，获取接口信息失败");
            return null;
        }
        //level=1的判断（level=1节点，是小区，数据不是list,需要单独判断 ），该处获取的数据，是挂在小区节点上（是level=1还是level=2需要进一步判断）
        if (currentId == baseObj.getIntValue("node_id")) {
            //小区节点（level=1）判断
            targetArray = baseObj.getJSONArray("child");
            String level = baseObj.getString("level");
            Log.d(TAG, "缓存当前节点level");
//            CacheUtils.getInstance().put(TouchConstants.ROOM_LEVEL, level);
        } else {//二道门一下节点（level=2+）的判断
            targetArray = getPickerOrgData(currentId, baseObj.getJSONArray("child"));
        }

        if (targetArray == null || targetArray.size() <= 0) {
            Log.e(TAG, "挂载信息全为空，原始json数据没有currentId对应的数据");
            return null;
        } else {
            Log.d(TAG, "缓存，当前节点下数据");
//            CacheUtils.getInstance().put(TouchConstants.ROOM_DATAS, targetArray);
        }
        return targetArray;
    }

    public static String getRoomLevel(JSONObject orgObj, int currentId) {
        JSONArray targetArray = null;//currentId对应的JSONArray
        String level = "3";
        //完整节点jsonobj数据， 从level=1开始
        JSONObject baseObj = orgObj.getJSONObject("tree");//tree下小区层数据 从level=1开始
        if (baseObj == null || !baseObj.containsKey("child")) {
            Log.e(TAG, "请重新配置，获取接口信息失败");
            return null;
        }
        //level=1的判断（level=1节点，是小区，数据不是list,需要单独判断 ），该处获取的数据，是挂在小区节点上（是level=1还是level=2需要进一步判断）
        if (currentId == baseObj.getIntValue("node_id")) {
            //小区节点（level=1）判断
            targetArray = baseObj.getJSONArray("child");
            level = baseObj.getString("level");

            Log.d(TAG, "缓存当前节点level");
//            CacheUtils.getInstance().put(TouchConstants.ROOM_LEVEL, level);
            return level;
        } else {//二道门一下节点（level=2+）的判断
            targetArray = getPickerOrgData(currentId, baseObj.getJSONArray("child"));
            //TODO
            level = "3";
        }

        return level;
    }

    /**
     * 获取设备当前节点Id
     *
     * @param orgObj
     * @return
     */
    public static int getCurrentNodeId(JSONObject orgObj) {
        return orgObj.getIntValue("current_node_id");
    }


    /**
     * 线程中的方法 筛选器要使用的目标json数据
     * <p>
     *
     * @return
     */
    private static JSONArray getPickerOrgData(int currentId, JSONArray baseArray) {
        JSONArray array = null;
        if (baseArray != null && baseArray.size() > 0) {
            for (int i = 0; i < baseArray.size(); i++) {// 二道门
                // 解析array下的JSONObject
                JSONObject obj = baseArray.getJSONObject(i);
                //
                if (currentId == obj.getIntValue("node_id")) {
                    array = obj.getJSONArray("child");// 返回该节点下的数据供筛选器使用
                    String level = obj.getString("level");
//                    CacheUtils.getInstance().put(TouchConstants.ROOM_LEVEL, level);
                    return array;
                } else {
                    // 递归，寻找下一层是否满足
                    array = getPickerOrgData(currentId, obj.getJSONArray("child"));
                    if (array != null) {
                        return array;//递归拿到数据
                    }
                }
            }
        } else {
            return null;//原始json数据不能使用，无法创建小区筛选器
        }
        return null;
    }


    /**
     * 判断当前targetArray是否有level=2的节点
     *
     * @param targetArray
     * @return
     */
    public static boolean hasNodeLevel2(JSONArray targetArray) {
        if (targetArray.toString().contains("\"level\":\"2\"")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断当前targetArray是否有level=4的节点
     *
     * @param targetArray
     * @return
     */
    public static boolean hasNodeLevel4(JSONArray targetArray) {
        if (targetArray.toString().contains("\"level\":\"4\"")) {
            return true;
        } else {
            return false;
        }
    }


}
