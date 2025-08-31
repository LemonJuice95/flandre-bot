package io.lemonjuice.flandre_bot.command.group.maimai.open_chars;

import io.lemonjuice.flan_mai_plugin.games.open_chars.OpenCharsProcess;
import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenCharCommand extends GroupCommandRunner {
    private static final Pattern commandPattern = Pattern.compile("^开(\\S+)$");

    public OpenCharCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate() {
        return OpenCharsManager.hasProcess(this.command.groupId) &&
                commandPattern.matcher(this.command.message).matches();
    }

    @Override
    public void apply() {
        Matcher matcher = commandPattern.matcher(this.command.message.trim());
        if(matcher.find()) {
            String str = matcher.group(1).replace("&#91;", "[").replace("&#93;", "]");
            if(str.length() != 1) {
                SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + "一次只能开一个字符哦~");
            } else {
                OpenCharsProcess process = OpenCharsManager.getProcess(this.command.groupId);
                if(process.openChar(str.charAt(0))) {
                    SendingUtils.sendGroupText(this.command.groupId, OpenCharsManager.makeMessage(this.command.groupId));
                    if(process.checkAccomplished()) {
                        OpenCharsManager.stopProcess(this.command.groupId);
                        SendingUtils.sendGroupText(this.command.groupId, "看样子游戏结束了呢~");
                    }
                } else {
                    SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + "这个字符已经被开过了哦~");
                }
            }
        }
    }
}
