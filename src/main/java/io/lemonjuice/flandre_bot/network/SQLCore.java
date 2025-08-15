package io.lemonjuice.flandre_bot.network;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import javax.sql.DataSource;
import java.sql.*;
import java.util.function.Consumer;

@Log4j2
public class SQLCore {
    @Getter
    private static volatile SQLCore instance;

    private final HikariDataSource dataSource;

    private SQLCore(String url, String username, String password) throws SQLException {
        this.dataSource = this.createDataSource(url, username, password);
    }

    public synchronized static boolean connect(String url, String userName, String passWord) {
        try {
            instance = new SQLCore(url, userName, passWord);
            log.info("SQL已连接!");
            return true;
        } catch (SQLException e) {
            log.error("SQL连接失败!");
            log.error(e);
            return false;
        }
    }

    private HikariDataSource createDataSource(String url, String userName, String password) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setMaximumPoolSize(15);
        dataSource.setConnectionTimeout(3000);
        dataSource.setIdleTimeout(1800 * 1000);
        return dataSource;
    }

    @SneakyThrows
    public Connection startConnection() {
        return this.dataSource.getConnection();
    }

    @SneakyThrows
    public void safeQuery(PreparedStatement ps, Consumer<ResultSet> handler) {
        try (ResultSet rs = ps.executeQuery()) {
            handler.accept(rs);
        }
    }
}
