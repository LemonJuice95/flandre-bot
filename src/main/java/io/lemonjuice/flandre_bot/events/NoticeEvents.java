package io.lemonjuice.flandre_bot.events;

import io.lemonjuice.flan_sql_support.network.SQLCore;
import io.lemonjuice.flandre_bot.commands.CommandRunningStatistics;
import io.lemonjuice.flandre_bot_framework.event.annotation.EventSubscriber;
import io.lemonjuice.flandre_bot_framework.event.annotation.SubscribeEvent;
import io.lemonjuice.flandre_bot_framework.event.msg.NetworkMessageEvent;
import io.lemonjuice.flandre_bot_framework.message.FriendContext;
import io.lemonjuice.flandre_bot_framework.message.GroupContext;
import lombok.extern.log4j.Log4j2;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//TODO 框架完善
@EventSubscriber
@Log4j2
public class NoticeEvents {
    @SubscribeEvent
    public void onWSMessage(NetworkMessageEvent event) {
        JSONObject json = event.getMessage();
        String postType = json.optString("post_type", "");
        if(postType.equals("notice")) {
            if (json.has("notice_type")) {
                String noticeType = json.getString("notice_type");
                switch (noticeType) {
                    case "group_decrease" -> onGroupDecrease(json);
                    case "notify" -> onNotify(json);
                }
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
                    long groupId = message.getLong("group_id");
                    new GroupContext(groupId).sendText("戳芙兰干嘛owo");
                }
                if(message.has("sender_id")) {
                    long senderId = message.getLong("sender_id");
                    new FriendContext(senderId).sendText("戳芙兰干嘛owo");
                }
                CommandRunningStatistics.addUsingCount("Poke");
            }
        } catch (JSONException e) {
            log.error(e);
        }
    }
}
