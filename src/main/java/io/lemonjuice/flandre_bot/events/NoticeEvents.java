package io.lemonjuice.flandre_bot.events;

import io.lemonjuice.flan_sql_support.network.SQLCore;
import io.lemonjuice.flandre_bot.commands.CommandRunningStatistics;
import io.lemonjuice.flandre_bot_framework.account.AccountInfo;
import io.lemonjuice.flandre_bot_framework.event.annotation.EventSubscriber;
import io.lemonjuice.flandre_bot_framework.event.annotation.SubscribeEvent;
import io.lemonjuice.flandre_bot_framework.event.msg.NetworkMessageEvent;
import io.lemonjuice.flandre_bot_framework.event.notice.GroupMemberChangeEvent;
import io.lemonjuice.flandre_bot_framework.event.notice.PokeEvent;
import io.lemonjuice.flandre_bot_framework.message.FriendContext;
import io.lemonjuice.flandre_bot_framework.message.GroupContext;
import lombok.extern.log4j.Log4j2;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@EventSubscriber
@Log4j2
public class NoticeEvents {
    @SubscribeEvent
    public void onPoke(PokeEvent event) {
        if(event.getTargetId() == event.getSelfId()) {
            event.getContext().sendText("戳芙兰干嘛owo");
            CommandRunningStatistics.addUsingCount("Poke");
        }
    }

    @SubscribeEvent
    public void onGroupDecrease(GroupMemberChangeEvent.Decrease event) {
        if(event.getSubType() == GroupMemberChangeEvent.Type.KICK_ME) {
            Thread.startVirtualThread(() -> {
                try (Connection co = SQLCore.getInstance().startConnection();
                     PreparedStatement ps = co.prepareStatement("DELETE FROM enabled_function WHERE group_id=?")) {
                    ps.setLong(1, event.getGroupId());
                    ps.execute();
                } catch (SQLException e) {
                    log.error(e);
                }
            });
        }
    }
}
