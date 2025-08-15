package io.lemonjuice.flandre_bot.command.group.daily;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.network.SQLCore;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.NickNameManager;
import io.lemonjuice.flandre_bot.utils.SendingUtils;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Log4j2
public class GroupSignInCommand extends GroupCommandRunner {
    public static List<String> FORTUNES = new ArrayList<>();
    public static List<String> THINGS = new ArrayList<>();

    @Override
    public IPermissionLevel getPermissionLevel(Message command) {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate(Message command) {
        String message = command.message.replaceAll(" ", "");
        return message.equals(CQCodeUtils.at(command.selfId) + "/签到");
    }

    @Override
    public void apply(Message command) {
        if(this.checkSignIn(command.userId, command.groupId)) {
            if(THINGS.size() < 4) {
                return;
            }
            Random random = ThreadLocalRandom.current();
            int fortune = random.nextInt(FORTUNES.size());
            List<Integer> things = random.ints(1, THINGS.size())
                    .distinct()
                    .limit(4)
                    .boxed()
                    .toList();
            String thingsStr = things.stream().map(String::valueOf).collect(Collectors.joining(","));

            try (Connection co = SQLCore.getInstance().startConnection();
                 PreparedStatement ps = co.prepareStatement("INSERT INTO sign_in VALUES(?,?,?,?,?)")) {
                ps.setLong(1, command.userId);
                ps.setLong(2, command.groupId);
                ps.setLong(3, (long) command.time);
                ps.setInt(4, fortune);
                ps.setString(5, thingsStr);
                ps.execute();
            } catch (SQLException e) {
                log.error(e);
            }
            String tmp = "宜 ";
            StringBuilder reply = new StringBuilder("签到成功，今日运势: ").append(FORTUNES.get(fortune)).append("\n");
            for(int i = 0; i < things.size(); i++) {
                reply.append(tmp).append(THINGS.get(things.get(i))).append("\n");
                if(i + 1 == things.size() / 2) {
                    tmp = "忌 ";
                }
            }
            SendingUtils.sendGroupText(command.groupId,
                    CQCodeUtils.reply(command.messageId) +
                            NickNameManager.getNickname(command.userId) +
                            reply.toString().trim());
        } else {
            try (Connection co_ = SQLCore.getInstance().startConnection();
                 PreparedStatement ps_ = co_.prepareStatement("SELECT * FROM sign_in WHERE uid=? AND group_id=?")) {
                ps_.setLong(1, command.userId);
                ps_.setLong(2, command.groupId);
                try (ResultSet record = ps_.executeQuery()) {
                    record.next();
                    int fortune = record.getInt("fortune");
                    String thingsStr = record.getString("things");
                    List<Integer> things = Arrays.stream(thingsStr.split(",")).map(Integer::parseInt).toList();
                    String tmp = "宜 ";
                    StringBuilder reply = new StringBuilder("你今天已经签到过啦！不能重复签到！\n今日运势: ").append(FORTUNES.get(fortune)).append("\n");
                    for(int i = 0; i < things.size(); i++) {
                        reply.append(tmp).append(THINGS.get(things.get(i))).append("\n");
                        if(i + 1 == things.size() / 2) {
                            tmp = "忌 ";
                        }
                    }
                    SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) + reply.toString().trim());
                }
            } catch (SQLException e) {
                log.error(e);
            }
        }
    }

    private boolean checkSignIn(long uid, long groupId) {
        try (Connection co = SQLCore.getInstance().startConnection();
             PreparedStatement ps = co.prepareStatement("SELECT * FROM sign_in WHERE uid=? AND group_id=?")){
            ps.setLong(1, uid);
            ps.setLong(2, groupId);
            try (ResultSet record = ps.executeQuery()) {
                if (record.next()) {
                    return false;
                }
            }
        } catch (SQLException e) {
            log.error(e);
        }
        return true;
    }
}
