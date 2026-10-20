package io.lemonjuice.flandre_bot.commands.group.maimai.open_chars;

import io.lemonjuice.flan_mai_plugin.games.open_chars.OpenCharsProcess;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;

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
    public boolean matches() {
        return OpenCharsManager.hasProcess(this.getCommand().groupId) &&
                commandPattern.matcher(this.getCommand().message.toString().trim()).matches();
    }

    @Override
    public void apply() {
        Matcher matcher = commandPattern.matcher(this.getCommand().message.toString().trim());
        if(matcher.find()) {
            int index = Integer.parseInt(matcher.group(1));
            String name = matcher.group(2);
            try {
                OpenCharsProcess process = OpenCharsManager.getProcess(this.getCommand().groupId);
                if(!process.isSongUnknown(index)) {
                    this.getCommand().getContext().replyWithText("这首歌曲的名字已经完全出来了\n不需要猜了呢~");
                } else if(process.guessSong(name, index)) {
                    StringBuilder reply = new StringBuilder(NicknameManager.getNickname(this.getCommand().userId));
                    reply.append("猜对[");
                    reply.append(index);
                    reply.append("]号歌曲啦！\n");
                    reply.append(OpenCharsManager.makeMessage(this.getCommand().groupId));
                    this.getCommand().getContext().sendText(reply.toString());
                    if(process.checkAccomplished()) {
                        OpenCharsManager.stopProcess(this.getCommand().groupId);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {
                        }
                        this.getCommand().getContext().sendText("看样子游戏结束了呢~");
                    }
                } else {
                    this.getCommand().getContext().replyWithText("哼哼~猜错了哦~");
                }
            } catch (IndexOutOfBoundsException e) {
                this.getCommand().getContext().replyWithText("编号超出范围了呢……");
            }
        }
    }
}
