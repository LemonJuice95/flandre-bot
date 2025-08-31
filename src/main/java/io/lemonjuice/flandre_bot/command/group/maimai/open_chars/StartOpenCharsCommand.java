package io.lemonjuice.flandre_bot.command.group.maimai.open_chars;

import io.lemonjuice.flan_mai_plugin.exception.NotInitializedException;
import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.func.FunctionCommand;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FunctionCommand("maimai_games")
public class StartOpenCharsCommand extends GroupCommandRunner {
    private static final String commandPatternRaw = "^\\[CQ:at,qq=%d]\\s*/舞萌开字母$";

    private final Pattern commandPattern;

    public StartOpenCharsCommand(Message command) {
        super(command);
        this.commandPattern = Pattern.compile(String.format(commandPatternRaw, command.selfId));
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate() {
        Matcher matcher = this.commandPattern.matcher(this.command.message.trim());
        return matcher.matches();
    }

    @Override
    public void apply() {
        try {
            if (OpenCharsManager.startNewProcess(this.command.groupId)) {
                SendingUtils.sendGroupText(this.command.groupId, "要玩开字母吗？好嘞~\n" + OpenCharsManager.makeMessage(this.command.groupId));
            } else {
                SendingUtils.sendGroupText(this.command.groupId, "好像群里已经在玩开字母了诶？\n先把现在这一轮玩完吧！");
            }
        } catch (NotInitializedException e) {
            SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + "功能还没初始化完成呢，耐心等一会吧~");
        }
    }
}
