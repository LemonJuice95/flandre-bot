package io.lemonjuice.flandre_bot.commands.group.daily;

import io.lemonjuice.flan_sql_support.network.SQLCore;
import io.lemonjuice.flandre_bot.resources.ResourceInit;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import io.lemonjuice.flandre_bot_framework.command.group.SimpleGroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Log4j2
public class GroupSignInCommand extends SimpleGroupCommandRunner {
    private static final List<String> FORTUNES = ResourceInit.SIGN_IN_FORTUNES.get();
    private static final List<String> THINGS = ResourceInit.SIGN_IN_THINGS.get();

    public GroupSignInCommand(Message command) {
        super(command);
    }

    @Override
    protected boolean needAtFirst() {
        return true;
    }

    @Override
    protected String getCommandBody() {
        return "/签到";
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public void apply() {
        if(this.checkCanSignIn(this.command.userId, this.command.groupId)) {
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
                ps.setLong(1, this.command.userId);
                ps.setLong(2, this.command.groupId);
                ps.setLong(3, (long) this.command.time);
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
            this.command.getContext().replyWithText(NicknameManager.getNickname(this.command.userId) +
                            reply.toString().trim());
        } else {
            try (Connection co_ = SQLCore.getInstance().startConnection();
                 PreparedStatement ps_ = co_.prepareStatement("SELECT * FROM sign_in WHERE uid=? AND group_id=?")) {
                ps_.setLong(1, this.command.userId);
                ps_.setLong(2, this.command.groupId);
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
                    this.command.getContext().replyWithText(reply.toString().trim());
                }
            } catch (SQLException e) {
                log.error(e);
            }
        }
    }

    private boolean checkCanSignIn(long uid, long groupId) {
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
