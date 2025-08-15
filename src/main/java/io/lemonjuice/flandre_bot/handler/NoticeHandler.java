package io.lemonjuice.flandre_bot.handler;

import io.lemonjuice.flandre_bot.network.SQLCore;
import io.lemonjuice.flandre_bot.utils.SendingUtils;
import lombok.extern.log4j.Log4j2;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Log4j2
public class NoticeHandler {
    public static void handleNotice(JSONObject message) {
        if (message.has("notice_type")) {
            String noticeType = message.getString("notice_type");
            switch (noticeType) {
                case "group_decrease" -> onGroupDecrease(message);
                case "notify" -> onNotify(message);
            }
        }
    }

    private static void onGroupDecrease(JSONObject message) {
        if(message.has("sub_type") && message.has("group_id")) {
            long groupId = message.getLong("group_id");
            String subType = message.getString("sub_type");
            if(subType.equals("kick_me")) {
                Thread.startVirtualThread(() -> {
                    try (Connection co = SQLCore.getInstance().startConnection();
                         PreparedStatement ps = co.prepareStatement("DELETE FROM enabled_function WHERE group_id=?")) {
                        ps.setLong(1, groupId);
                        ps.execute();
                    } catch (SQLException e) {
                        log.error(e);
                    }
                });
            }
        }
    }

    private static void onNotify(JSONObject message) {
       try {
           if(message.getString("sub_type").equals("poke") &&
              message.getLong("self_id") == message.getLong("target_id")) {
               if(message.has("group_id")) {
                   SendingUtils.sendGroupText(message.getLong("group_id"), "戳芙兰干嘛owo");
               }
               if(message.has("sender_id")) {
                   SendingUtils.sendPrivateText(message.getLong("sender_id"), "戳芙兰干嘛owo");
               }
           }
       } catch (JSONException e) {
           log.error(e);
       }
    }
}
