package io.lemonjuice.flandre_bot.command.group.func;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.func.FunctionNameManager;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.network.SQLCore;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class DisableFunctionCommand extends GroupCommandRunner {
    @Override
    public IPermissionLevel getPermissionLevel(Message command) {
        return PermissionLevel.ADMIN;
    }

    @Override
    public boolean validate(Message command) {
        String message = command.message.replaceAll(" ", "");
        return message.startsWith(CQCodeUtils.at(command.selfId) + "/禁用功能");
    }

    @Override
    public void apply(Message command) {
        String funcMessage = this.getFuncMessage(command);
        if(funcMessage.isEmpty()) {
            SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) + "格式错误\n正确格式：/启用功能 <功能名称>\n使用 \"/可配置功能列表\" 来查询所有可以手动启用/禁用的功能");
            return;
        }

        if(FunctionNameManager.GROUP_FUNCTIONS.containsKey(funcMessage)) {
            String funcName = FunctionNameManager.GROUP_FUNCTIONS.get(funcMessage);
            try (Connection co = SQLCore.getInstance().startConnection();
                 PreparedStatement ps = co.prepareStatement("DELETE FROM enabled_function WHERE func_name=? AND group_id=?")) {
                ps.setString(1, funcName);
                ps.setLong(2, command.groupId);
                ps.execute();
            } catch (SQLException e) {
                log.error(e);
            }
            SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) + "已禁用功能 " + funcMessage);
        } else {
            SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) + "未找到该功能\n使用 \"/可配置功能列表\" 来查询所有可以手动启用/禁用的功能");
        }
    }

    private String getFuncMessage(Message command) {
        String message = command.message
                .replace(CQCodeUtils.at(command.selfId), "")
                .replace("\\[]", "")
                .trim();
        Matcher matcher = Pattern.compile("/禁用功能\\s+(\\S+)").matcher(message);

        return matcher.find() ? matcher.group(1) : "";
    }
}
