package io.lemonjuice.flandre_bot.console;

import io.lemonjuice.flan_sql_support.network.SQLCore;
import io.lemonjuice.flandre_bot.func.FunctionEnableManager;
import io.lemonjuice.flandre_bot.func.FunctionNameManager;
import io.lemonjuice.flandre_bot_framework.console.BotConsole;
import io.lemonjuice.flandre_bot_framework.console.ConsoleCommandRunner;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class FunctionConsoleCommand extends ConsoleCommandRunner {
    public FunctionConsoleCommand(String[] args) {
        super(args);
    }

    @Override
    public void apply() {
        try {
            String subCommand = this.args[0];

            switch(subCommand) {
                case "list", "lst", "l" -> this.showList();
                case "enable" -> this.modifyFunction(true);
                case "disable" -> this.modifyFunction(false);
            }
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            BotConsole.println("命令格式错误，格式: function <list|enable|disable> [群号] [功能名称]");
        }
    }

    private void modifyFunction(boolean enabled) {
        long groupId = Long.parseLong(this.args[1]);
        String funcName = this.args[2];
        if(enabled) {
            FunctionEnableManager.enableGroupFunc(groupId, funcName);
        } else {
            FunctionEnableManager.disableGroupFunc(groupId, funcName);
        }
    }

    private void showList() {
        long groupId;
        try {
            groupId = Long.parseLong(this.args[1]);
        } catch (Exception e) {
            groupId = -1L;
        }
        if(groupId == -1L) {
            BotConsole.println("群聊功能列表:");
            FunctionNameManager.GROUP_FUNCTIONS.forEach(BotConsole::println);
            return;
        }

        List<String> enabledFunctions = new ArrayList<>();
        try (Connection connection = SQLCore.getInstance().startConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT func_name FROM enabled_function WHERE group_id=?")) {
            ps.setLong(1, groupId);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    enabledFunctions.add(rs.getString("func_name"));
                }
            }
        } catch (SQLException e) {
            log.error("无法与数据库通信", e);
        }

        if(enabledFunctions.isEmpty()) {
            BotConsole.println(String.format("群聊%d未启用任何功能", groupId));
        } else {
            BotConsole.println(String.format("群聊%d启用的功能有:", groupId));
            enabledFunctions.forEach(BotConsole::println);
        }
    }

    @Override
    public List<String> getCommandBodies() {
        return List.of("function", "func");
    }

    @Override
    public String getUsingFormat() {
        return "function <list|enable|disable> [群号] [功能名称]";
    }

    @Override
    public String getDescription() {
        return "查看群功能列表|启用群功能|禁用群功能";
    }
}
