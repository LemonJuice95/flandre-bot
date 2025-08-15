package io.lemonjuice.flandre_bot.utils;

import io.lemonjuice.flandre_bot.FlandreBot;

public class CQCodeUtils {
    public static String reply(long id) {
        return "[CQ:reply,id=" + id + "]";
    }

    public static String at(long id) {
        return "[CQ:at,qq=" + id + "]";
    }

    public static String localImage(String name) {
        String imagePath = FlandreBot.getJarPath() + "images/" + name;
        return "[CQ:image,file=file:///" + imagePath + "]";
    }

    public static String image(String url) {
        return "[CQ:image,file=" + url + "]";
    }

    public static String localExpressionImage(String name) {
        String imagePath = FlandreBot.getJarPath() + "images/" + name;
        return "[CQ:image,file=file:///" + imagePath + ",sub_type=1]";
    }
}

