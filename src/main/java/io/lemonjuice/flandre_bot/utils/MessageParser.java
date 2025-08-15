package io.lemonjuice.flandre_bot.utils;

import io.lemonjuice.flandre_bot.message.Message;
import jakarta.annotation.Nullable;
import lombok.extern.log4j.Log4j2;
import org.json.JSONException;
import org.json.JSONObject;

@Log4j2
public class MessageParser {

    @Nullable
    public static Message tryParse(JSONObject json) {
        try {
            Message message = new Message();

            message.selfId = json.getLong("self_id");
            message.userId = json.getLong("user_id");
            message.time = json.getInt("time");
            message.messageId = json.getInt("message_id");
            message.realId = json.getInt("real_id");
            message.realSeq = json.getString("real_seq");

            String type = json.getString("message_type");
            String subType = json.getString("sub_type");
            message.type = type;
            message.subType = subType;

            Message.Sender sender = new Message.Sender();
            JSONObject senderJson = json.getJSONObject("sender");
            sender.userId = senderJson.getLong("user_id");
            sender.nickName = senderJson.getString("nickname");
            sender.card = senderJson.getString("card");
            sender.role = senderJson.has("role") ? senderJson.getString("role") : "";
            message.sender = sender;

            message.rawMessage = json.getString("raw_message");
            message.message = json.getString("message");
            message.font = json.getInt("font");
            message.format = json.getString("message_format");

            message.targetId = json.has("target_id") ? json.getLong("target_id") : -1;
            message.groupId = json.has("group_id") ? json.getLong("group_id") : -1;

            return message;
        } catch (JSONException e) {
            log.error("消息解析异常! 原始json文本: \"{}\"", json.toString());
            log.error(e);
            return null;
        }
    }
}
