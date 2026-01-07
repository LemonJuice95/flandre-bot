package io.lemonjuice.flandre_bot.utils;

import io.lemonjuice.flan_sql_support.network.SQLCore;
import io.lemonjuice.flandre_bot_framework.message.MessageToSend;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
public class NicknameManager {
    private static final ConcurrentHashMap<Long, String> NICKNAMES = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, String> DIRTY = new ConcurrentHashMap<>();

    public static void removeNickname(long uid) {
        NICKNAMES.remove(uid);
        DIRTY.put(uid, "");
    }

    public static void updateNickname(long uid, String nickname) {
        NICKNAMES.put(uid, nickname);
        DIRTY.put(uid, nickname);
    }

    public static String getNickname(long uid) {
        return NICKNAMES.get(uid);
    }

    public static void appendNickname(long uid, MessageToSend message) {
        if(getNickname(uid) != null) {
            message.appendText(getNickname(uid));
        } else {
            message.appendAt(uid);
        }
    }

    public static synchronized void init() {
        try (Connection co = SQLCore.getInstance().startConnection();
             PreparedStatement ps = co.prepareStatement("SELECT * FROM nicknames");
             ResultSet rs = ps.executeQuery()) {
            while(rs.next()) {
                NICKNAMES.put(rs.getLong("user_id"), rs.getString("nickname"));
            }
        } catch (SQLException e) {
            log.error(e);
        }
    }

    public static synchronized void save() {
        try (Connection co = SQLCore.getInstance().startConnection();
             PreparedStatement insert = co.prepareStatement("INSERT INTO nicknames (user_id,nickname) VALUES(?,?) ON DUPLICATE KEY UPDATE nickname=?");
             PreparedStatement delete = co.prepareStatement("DELETE FROM nicknames WHERE user_id=?")) {
            DIRTY.forEach((uid, nickname) -> {
                if(nickname != null) {
                    if(!nickname.isEmpty()) {
                        try {
                            insert.setLong(1, uid);
                            insert.setString(2, nickname);
                            insert.setString(3, nickname);
                            insert.addBatch();
                        } catch (SQLException e) {
                            log.error(e);
                        }
                    } else {
                        try {
                            delete.setLong(1, uid);
                            delete.addBatch();
                        } catch (SQLException e) {
                            log.error(e);
                        }
                    }
                }
            });
            insert.executeBatch();
            delete.executeBatch();
            DIRTY.clear();
            log.info("昵称已存入数据库");
        } catch (SQLException e) {
            log.error(e);
        }
    }
}
