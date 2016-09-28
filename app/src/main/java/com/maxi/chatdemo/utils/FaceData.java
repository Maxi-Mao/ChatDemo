package com.maxi.chatdemo.utils;

import com.maxi.chatdemo.R;

import java.util.HashMap;
import java.util.Map;

public class FaceData {
    public static Map<String, Integer> gifFaceInfo = new HashMap<String, Integer>();

    public static final String tb_1 = "[:困惑]";
    public static final String tb_2 = "[:流泪]";
    public static final String tb_3 = "[:鬼脸]";
    public static final String tb_4 = "[:郁闷]";
    public static final String tb_5 = "[:傲慢]";
    public static final String tb_6 = "[:闭嘴]";
    public static final String tb_7 = "[:惊讶]";
    public static final String tb_8 = "[:流汗]";
    public static final String tb_9 = "[:疑问]";
    public static final String tb_10 = "[:心动]";
    public static final String tb_11 = "[:耍酷]";
    public static final String tb_12 = "[:发怒]";
    public static final String tb_13 = "[:难过]";
    public static final String tb_14 = "[:高兴]";
    public static final String tb_15 = "[:委屈]";
    public static final String tb_16 = "[:鄙视]";
    public static final String tb_17 = "[:瞌睡]";
    public static final String tb_18 = "[:偷笑]";
    public static final String tb_19 = "[:胜利]";
    public static final String tb_20 = "[:再见]";
    public static final String tb_21 = "[:好样的]";
    public static final String tb_22 = "[:开心]";
    public static final String tb_23 = "[:鼓掌]";
    public static final String tb_24 = "[:我晕]";
    public static final String tb_25 = "[:握手]";
    public static final String tb_26 = "[:找打]";
    public static final String tb_27 = "[:吃饭]";
    public static final String tb_28 = "[:得意]";
    public static final String tb_29 = "[:ok]";
    public static final String tb_30 = "[:有礼]";
    public static final String tb_31 = "[:惊吓]";
    public static final String tb_32 = "[:发财]";
    public static final String tb_33 = "[:奋斗]";
    public static final String tb_34 = "[:蛋糕]";
    public static final String tb_35 = "[:鲜花]";
    public static final String tb_36 = "[:干杯]";
    public static final String tb_37 = "[:成交]";
    public static final String tb_38 = "[:欢迎]";
    public static final String tb_39 = "[:再会]";
    public static final String tb_40 = "[:通话中]";

    static {

        addString(gifFaceInfo, tb_1, R.mipmap.tb_1);
        addString(gifFaceInfo, tb_2, R.mipmap.tb_2);
        addString(gifFaceInfo, tb_3, R.mipmap.tb_3);
        addString(gifFaceInfo, tb_4, R.mipmap.tb_4);
        addString(gifFaceInfo, tb_5, R.mipmap.tb_5);
        addString(gifFaceInfo, tb_6, R.mipmap.tb_6);
        addString(gifFaceInfo, tb_7, R.mipmap.tb_7);
        addString(gifFaceInfo, tb_8, R.mipmap.tb_8);
        addString(gifFaceInfo, tb_9, R.mipmap.tb_9);
        addString(gifFaceInfo, tb_10, R.mipmap.tb_10);
        addString(gifFaceInfo, tb_11, R.mipmap.tb_11);
        addString(gifFaceInfo, tb_12, R.mipmap.tb_12);
        addString(gifFaceInfo, tb_13, R.mipmap.tb_13);
        addString(gifFaceInfo, tb_14, R.mipmap.tb_14);
        addString(gifFaceInfo, tb_15, R.mipmap.tb_15);
        addString(gifFaceInfo, tb_16, R.mipmap.tb_16);
        addString(gifFaceInfo, tb_17, R.mipmap.tb_17);
        addString(gifFaceInfo, tb_18, R.mipmap.tb_18);
        addString(gifFaceInfo, tb_19, R.mipmap.tb_19);
        addString(gifFaceInfo, tb_20, R.mipmap.tb_20);
        addString(gifFaceInfo, tb_21, R.mipmap.tb_21);
        addString(gifFaceInfo, tb_22, R.mipmap.tb_22);
        addString(gifFaceInfo, tb_23, R.mipmap.tb_23);
        addString(gifFaceInfo, tb_24, R.mipmap.tb_24);
        addString(gifFaceInfo, tb_25, R.mipmap.tb_25);
        addString(gifFaceInfo, tb_26, R.mipmap.tb_26);
        addString(gifFaceInfo, tb_27, R.mipmap.tb_27);
        addString(gifFaceInfo, tb_28, R.mipmap.tb_28);
        addString(gifFaceInfo, tb_29, R.mipmap.tb_29);
        addString(gifFaceInfo, tb_30, R.mipmap.tb_30);
        addString(gifFaceInfo, tb_31, R.mipmap.tb_31);
        addString(gifFaceInfo, tb_32, R.mipmap.tb_32);
        addString(gifFaceInfo, tb_33, R.mipmap.tb_33);
        addString(gifFaceInfo, tb_34, R.mipmap.tb_34);
        addString(gifFaceInfo, tb_35, R.mipmap.tb_35);
        addString(gifFaceInfo, tb_36, R.mipmap.tb_36);
        addString(gifFaceInfo, tb_37, R.mipmap.tb_37);
        addString(gifFaceInfo, tb_38, R.mipmap.tb_38);
        addString(gifFaceInfo, tb_39, R.mipmap.tb_39);
        addString(gifFaceInfo, tb_40, R.mipmap.tb_40);
    }

    private static void addString(Map<String, Integer> map, String smile,
                                  int resource) {
        map.put(smile, resource);
    }
}