package io.lemonjuice.flandre_bot.commands;

import io.lemonjuice.flan_sql_support.network.SQLCore;
import io.lemonjuice.flandre_bot_framework.command.BotCommandLookup;
import io.lemonjuice.flandre_bot_framework.command.CommandRunner;
import io.lemonjuice.flandre_bot_framework.model.Message;
import lombok.extern.log4j.Log4j2;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class CommandRunningStatistics {
    public static void addUsingCount(String commandName) {
        try (Connection connection = SQLCore.getInstance().startConnection();
             PreparedStatement ps = connection.prepareStatement("INSERT INTO command_statistics (command_name,using_count) VALUES(?,1) ON DUPLICATE KEY UPDATE using_count=using_count+1");
             PreparedStatement ps2 = connection.prepareStatement("INSERT INTO command_statistics_today (command_name,using_count) VALUES(?,1) ON DUPLICATE KEY UPDATE using_count=using_count+1")) {
            ps.setString(1, commandName);
            ps2.setString(1, commandName);
            ps.execute();
            ps2.execute();
        } catch (SQLException e) {
            log.error("更新命令使用次数失败！", e);
        }
    }

    public static int getSingleUsingCount(String commandName, boolean today) {
        int result = 0;

        String sql = "SELECT * FROM %s WHERE command_name=?";

        try (Connection connection = SQLCore.getInstance().startConnection();
             PreparedStatement ps = connection.prepareStatement(String.format(sql, today ? "command_statistics_today" : "command_statistics"));
             ResultSet rs = ps.executeQuery()) {
            if(rs.next()) {
                result = rs.getInt("using_count");
            }
        } catch (SQLException e) {
            log.error("获取命令使用次数失败！", e);
        }

        return result;
    }

    public static Map<String, Integer> getUsingCounts(boolean all, boolean today) {
        Map<String, Integer> result = new HashMap<>();
        if(all) {
            BotCommandLookup.GROUP_COMMANDS.forEach((f) -> {
                CommandRunner runner = f.apply(Message.DUMMY);
                result.put(runner.getClass().getSimpleName(), 0);
            });
            BotCommandLookup.PRIVATE_COMMANDS.forEach((f) -> {
                CommandRunner runner = f.apply(Message.DUMMY);
                result.put(runner.getClass().getSimpleName(), 0);
            });
            BotCommandLookup.FRIEND_COMMANDS.forEach((f) -> {
                CommandRunner runner = f.apply(Message.DUMMY);
                result.put(runner.getClass().getSimpleName(), 0);
            });
            result.put("Poke", 0);
        }

        String sql = "SELECT * FROM %s";

        try (Connection connection = SQLCore.getInstance().startConnection();
             PreparedStatement ps = connection.prepareStatement(String.format(sql, today ? "command_statistics_today" : "command_statistics"));
             ResultSet rs = ps.executeQuery()) {
            while(rs.next()) {
                String commandName = rs.getString("command_name");
                int usingCount = rs.getInt("using_count");
                result.put(commandName, usingCount);
            }
        } catch (SQLException e) {
            log.error("获取命令使用次数失败！", e);
        }

        return result;
    }

    public static void dailyClear() {
        try (Connection connection = SQLCore.getInstance().startConnection();
             Statement st = connection.createStatement()) {
            st.execute("TRUNCATE TABLE command_statistics_today");
        } catch (SQLException e) {
            log.error("清空当日命令使用记录失败！", e);
        }
    }
}
