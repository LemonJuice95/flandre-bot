package io.lemonjuice.flandre_bot.commands.group.special;

import io.lemonjuice.flan_sql_support.network.SQLCore;
import io.lemonjuice.flandre_bot.func.FunctionCommand;
import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;
import lombok.extern.log4j.Log4j2;

import java.sql.*;
import java.time.LocalTime;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@FunctionCommand(value = "k11_number", report = false)
public class K11PeopleNumberCommand extends GroupCommandRunner {
    private static final Object dbLock = new Object();
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private static final Pattern commandPattern = Pattern.compile("k([+\\-]?)(\\d+|j)");

    private volatile static UpdateInfo lastInfo;

    private final Matcher matcher;

    public K11PeopleNumberCommand(Message command) {
        super(command);
        this.matcher = commandPattern.matcher(command.message.toString().trim());
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean matches() {
        return this.matcher.matches();
    }

    @Override
    public void apply() {
        String arg = this.matcher.group(2);

        if(arg.equals("j")) {
            try {
                rwLock.readLock().lock();
                if (lastInfo != null) {
                    StringBuilder reply = new StringBuilder("芙兰看看...\n当前 k11 共有 ");
                    reply.append(lastInfo.number);
                    reply.append(" 人\n由 ");
                    reply.append(lastInfo.userName);
                    reply.append("(");
                    reply.append(lastInfo.qq);
                    reply.append(") 上报于 ");
                    reply.append(lastInfo.time);
                    this.command.getContext().replyWithText(reply.toString().trim());
                } else {
                    this.command.getContext().replyWithText("暂时还没收到汇报呢……");
                }
            } finally {
                rwLock.readLock().unlock();
            }
        } else {
            String operator = this.matcher.group(1);
            int number = Integer.parseInt(arg);
            try {
                rwLock.writeLock().lock();
                int lastNumber = lastInfo != null ? lastInfo.number : 0;
                switch (operator) {
                    case "+" -> number += lastNumber;
                    case "-" -> number = lastNumber - number;
                    default -> {
                    }
                }
                if (number > 100) {
                    this.command.getContext().replyWithText("诶？！这么多人真的装得下吗？");
                } else if (number < 0) {
                    this.command.getContext().replyWithText("诶？！人数是负数吗？这个要怎么做到呢……");
                } else {
                    lastInfo = new UpdateInfo(
                            this.command.sender.nickName,
                            this.command.userId,
                            LocalTime.now().toString().substring(0, 8),
                            number
                    );
                    this.command.getContext().replyWithText("收到~k11现在一共有 " + number + " 人");
                }
            } finally {
                rwLock.writeLock().unlock();
            }
        }
    }

    public static void refresh() {
        synchronized (dbLock) {
            try {
                rwLock.writeLock().lock();
                lastInfo = null;
                try (Connection connection = SQLCore.getInstance().startConnection();
                     Statement statement = connection.createStatement()) {
                    statement.execute("DELETE FROM k11_number");
                } catch (SQLException e) {
                    log.error("无法刷新数据库中的k11人数数据", e);
                }
            } finally {
                rwLock.writeLock().unlock();
            }
        }
    }

    public static void init() {
        synchronized (dbLock) {
            try (Connection connection = SQLCore.getInstance().startConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT * FROM k11_number");
                 ResultSet rs = ps.executeQuery()) {
                rwLock.writeLock().lock();
                if(rs.next()) {
                    lastInfo = new UpdateInfo(
                            rs.getString("username"),
                            rs.getLong("qq"),
                            rs.getString("at"),
                            rs.getInt("number")
                    );
                }
            } catch (SQLException e) {
                log.error("k11人数数据初始化失败！", e);
            } finally {
                rwLock.writeLock().unlock();
            }
        }
    }

    public static void save() {
        synchronized (dbLock) {
            try (Connection connection = SQLCore.getInstance().startConnection();
                 Statement statement = connection.createStatement();
                 PreparedStatement ps = connection.prepareStatement("INSERT INTO k11_number(number,qq,username,at) VALUES(?,?,?,?)")) {
                rwLock.readLock().lock();
                statement.execute("DELETE FROM k11_number");
                if(lastInfo != null) {
                    ps.setInt(1, lastInfo.number);
                    ps.setLong(2, lastInfo.qq);
                    ps.setString(3, lastInfo.userName);
                    ps.setString(4, lastInfo.time);
                    ps.execute();
                }
            } catch (SQLException e) {
                log.error("k11人数数据保存失败！", e);
            } finally {
                rwLock.readLock().unlock();
            }
        }
    }

    public record UpdateInfo(String userName, long qq, String time, int number) {
    }
}
