package io.lemonjuice.flandre_bot.command.group.maimai.open_chars;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StopOpenCharsCommand extends GroupCommandRunner {
    private static final String commandPatternRaw = "^\\[CQ:at,qq=%d]\\s*/结束开字母";

    private final Pattern commandPattern;

    public StopOpenCharsCommand(Message command) {
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
        if(OpenCharsManager.stopProcess(this.command.groupId)) {
            SendingUtils.sendGroupText(this.command.groupId, "诶？不玩了吗？好吧……");
        } else {
            SendingUtils.sendGroupText(this.command.groupId, "群里没有正在进行的开字母哦~");
        }
    }
}
