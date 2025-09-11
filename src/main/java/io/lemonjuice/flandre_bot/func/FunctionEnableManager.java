package io.lemonjuice.flandre_bot.func;

import io.lemonjuice.flan_sql_support.network.SQLCore;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Log4j2
public class FunctionEnableManager {

    public static boolean isGroupFuncEnable(long groupId, String funcName) {
        try (Connection co = SQLCore.getInstance().startConnection();
             PreparedStatement ps = co.prepareStatement("SELECT * FROM enabled_function WHERE func_name=? AND group_id=?");) {
            ps.setString(1, funcName);
            ps.setLong(2, groupId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            log.error(e);
            return false;
        }
    }
}