package io.lemonjuice.flandre_bot.utils;

import io.lemonjuice.flan_sql_support.network.SQLCore;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class BirthdayManager {
    public static void updateBirthDay(long userId, int month, int day) {
        try (Connection connection = SQLCore.getInstance().startConnection();
             PreparedStatement ps = connection.prepareStatement("INSERT INTO birthday(user_id, month, day) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE month=?, day=?")) {
            ps.setLong(1, userId);
            ps.setInt(2, month);
            ps.setInt(3, day);
            ps.setInt(4, month);
            ps.setInt(5, day);
            ps.execute();
        } catch (SQLException e) {
            log.error("插入用户生日条目失败！", e);
        }
    }

    public static List<Long> getCelebrantWithDate(int month, int day) {
        List<Long> result = new ArrayList<>();
        try (Connection connection = SQLCore.getInstance().startConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT user_id FROM birthday WHERE month=? AND day=?")) {
            ps.setInt(1, month);
            ps.setInt(2, day);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    result.add(rs.getLong("user_id"));
                }
            }
        } catch (SQLException e) {
            log.error("获取用户生日条目失败！", e);
        }
        return result;
    }
}
