package io.lemonjuice.flandre_bot.commands.group.func;

import io.lemonjuice.flan_sql_support.network.SQLCore;
import io.lemonjuice.flandre_bot.func.FunctionNameManager;
import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;
import io.lemonjuice.flandre_bot_framework.utils.CQCode;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class DisableFunctionCommand extends GroupCommandRunner {
    private static final Pattern commandPattern = Pattern.compile("/禁用功能\\s+(\\S+)");

    public DisableFunctionCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.ADMIN;
    }

    @Override
    public boolean matches() {
        String message = this.command.message.replace(CQCode.at(this.command.selfId), "");
        return this.command.message.startsWith(CQCode.at(this.command.selfId)) &&
                commandPattern.matcher(message.trim()).matches();
    }

    @Override
    public void apply() {
        String funcMessage = this.getFuncMessage();
        if(funcMessage.isEmpty()) {
            this.command.getContext().replyWithText("格式错误\n正确格式：/启用功能 <功能名称>\n使用 \"/可配置功能列表\" 来查询所有可以手动启用/禁用的功能");
            return;
        }

        if(FunctionNameManager.GROUP_FUNCTIONS.containsKey(funcMessage)) {
            String funcName = FunctionNameManager.GROUP_FUNCTIONS.get(funcMessage);
            try (Connection co = SQLCore.getInstance().startConnection();
                 PreparedStatement ps = co.prepareStatement("DELETE FROM enabled_function WHERE func_name=? AND group_id=?")) {
                ps.setString(1, funcName);
                ps.setLong(2, this.command.groupId);
                ps.execute();
            } catch (SQLException e) {
                log.error(e);
            }
            this.command.getContext().replyWithText("已禁用功能 " + funcMessage);
        } else {
            this.command.getContext().replyWithText("未找到该功能\n使用 \"/可配置功能列表\" 来查询所有可以手动启用/禁用的功能");
        }
    }

    private String getFuncMessage() {
        String message = this.command.message
                .replace(CQCode.at(this.command.selfId), "")
                .trim();
        Matcher matcher = commandPattern.matcher(message);

        return matcher.find() ? matcher.group(1) : "";
    }
}
