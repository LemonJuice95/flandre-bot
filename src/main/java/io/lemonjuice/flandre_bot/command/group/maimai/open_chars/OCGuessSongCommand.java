package io.lemonjuice.flandre_bot.command.group.maimai.open_chars;

import io.lemonjuice.flan_mai_plugin.games.open_chars.OpenCharsProcess;
import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OCGuessSongCommand extends GroupCommandRunner {
    private static final Pattern commandPattern = Pattern.compile("^猜(\\d+)\\s+(.+)$");

    public OCGuessSongCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate() {
        return OpenCharsManager.hasProcess(this.command.groupId) &&
                commandPattern.matcher(this.command.message.trim()).matches();
    }

    @Override
    public void apply() {
        Matcher matcher = commandPattern.matcher(this.command.message.trim());
        if(matcher.find()) {
            int index = Integer.parseInt(matcher.group(1));
            String name = matcher.group(2).replace("&#91;", "[").replace("&#93;", "]");
            try {
                OpenCharsProcess process = OpenCharsManager.getProcess(this.command.groupId);
                if(!process.isSongUnknown(index)) {
                    SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + "这首歌曲的名字已经完全出来了\n不需要猜了呢~");
                } else if(process.guessSong(name, index)) {
                    StringBuilder reply = new StringBuilder(NicknameManager.getNickname(this.command.userId));
                    reply.append("猜对[");
                    reply.append(index);
                    reply.append("]号歌曲啦！\n");
                    reply.append(OpenCharsManager.makeMessage(this.command.groupId));
                    SendingUtils.sendGroupText(this.command.groupId, reply.toString());
                    if(process.checkAccomplished()) {
                        OpenCharsManager.stopProcess(this.command.groupId);
                        SendingUtils.sendGroupText(this.command.groupId, "看样子游戏结束了呢~");
                    }
                } else {
                    SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + "哼哼~猜错了哦~");
                }
            } catch (IndexOutOfBoundsException e) {
                SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + "编号超出范围了呢……");
            }
        }
    }
}
