package io.lemonjuice.flandre_bot.utils;

import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.network.WSClientCore;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class SendingUtils {
    public static void sendGroupText(long groupId, String message) {
        sendGroupText(groupId, message, false);
    }

    public static void sendGroupText(long groupId, String message, boolean sendAsRawText) {
        JSONObject json = new JSONObject();
        json.put("action", "send_group_msg");
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("message", message);
        params.put("auto_escape", sendAsRawText);
        json.put("params", params);
        WSClientCore.getInstance().sendText(json.toString());
    }

    public static void replyPrivateText(Message source, String message) {
        replyPrivateText(source, message, false);
    }

    public static void replyPrivateText(Message source, String message, boolean sendAsRawText) {
        if(source.type.equals("private") && source.subType.equals("friend")) {
            sendPrivateText(source.userId, message, sendAsRawText);
        }
        if(source.type.equals("private") && source.subType.equals("group")) {
            sendTmpText(source.userId, source.groupId, message, sendAsRawText);
        }
    }

    public static void sendTmpText(long userId, long groupId, String message) {
        sendTmpText(userId, groupId, message, false);
    }


    public static void sendTmpText(long userId, long groupId, String message, boolean sendAsRawText) {
        JSONObject json = new JSONObject();
        json.put("action", "send_private_msg");
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        params.put("message", message);
        params.put("auto_escape", sendAsRawText);
        json.put("params", params);
        WSClientCore.getInstance().sendText(json.toString());
    }

    public static void sendPrivateText(long userId, String message) {
        sendPrivateText(userId, message, false);
    }

    public static void sendPrivateText(long userId, String message, boolean sendAsRawText) {
        JSONObject json = new JSONObject();
        json.put("action", "send_private_msg");
        JSONObject params = new JSONObject();
        params.put("user_id", userId);
        params.put("message", message);
        params.put("auto_escape", sendAsRawText);
        json.put("params", params);
        WSClientCore.getInstance().sendText(json.toString());
    }

    public static void sendGroupForwardText(long selfId, long groupId, List<String> messages) {
        JSONObject json = new JSONObject();
        json.put("action", "send_group_forward_msg");
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        JSONArray jsonArray = new JSONArray();
        JSONObject node = new JSONObject();
        JSONObject data = new JSONObject();
        for(String m : messages) {
            node.put("type", "node");
            data.put("user_id", selfId);
            data.put("nickname", "芙兰朵露·斯卡雷特");
            data.put("content", m);
            node.put("data", data);
            jsonArray.put(node);
            node = new JSONObject();
            data = new JSONObject();
        }
        params.put("messages", jsonArray);
        json.put("params", params);
        WSClientCore.getInstance().sendText(json.toString());
    }
}
