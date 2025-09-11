package io.lemonjuice.flandre_bot.commands.group.maimai.open_chars;

import io.lemonjuice.flan_mai_plugin.games.open_chars.OpenCharsProcess;
import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;

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
    public boolean matches() {
        return OpenCharsManager.hasProcess(this.command.groupId) &&
                commandPattern.matcher(this.command.message).matches();
    }

    @Override
    public void apply() {
        Matcher matcher = commandPattern.matcher(this.command.message.trim());
        if(matcher.find()) {
            String str = matcher.group(1);
            if(str.length() != 1) {
                this.command.getContext().replyWithText("一次只能开一个字符哦~");
            } else {
                OpenCharsProcess process = OpenCharsManager.getProcess(this.command.groupId);
                if(process.openChar(str.charAt(0))) {
                    this.command.getContext().sendText(OpenCharsManager.makeMessage(this.command.groupId));
                    if(process.checkAccomplished()) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {
                        }
                        OpenCharsManager.stopProcess(this.command.groupId);
                        this.command.getContext().sendText("看样子游戏结束了呢~");
                    }
                } else {
                    this.command.getContext().replyWithText("这个字符已经被开过了哦~");
                }
            }
        }
    }
}
