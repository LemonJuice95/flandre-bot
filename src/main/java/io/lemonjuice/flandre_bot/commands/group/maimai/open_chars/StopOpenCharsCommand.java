package io.lemonjuice.flandre_bot.commands.group.maimai.open_chars;

import io.lemonjuice.flan_mai_plugin.model.Song;
import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.command.group.SimpleGroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StopOpenCharsCommand extends SimpleGroupCommandRunner {

    public StopOpenCharsCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    protected boolean needAtFirst() {
        return true;
    }

    @Override
    protected String getCommandBody() {
        return "/结束开字母";
    }

    @Override
    public void apply() {
        if(OpenCharsManager.hasProcess(this.command.groupId)) {
            this.command.getContext().sendText("诶？不玩了吗？好吧……");
            StringBuilder answer = new StringBuilder("下面是这次的答案：\n");
            List<Song> songs = OpenCharsManager.getProcess(this.command.groupId).getSongs();
            for(int i = 0; i < songs.size(); i++) {
                answer.append("[");
                answer.append(i);
                answer.append("]  ");
                answer.append(songs.get(i).title);
                answer.append("\n");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            this.command.getContext().sendText(answer.toString().trim());
            OpenCharsManager.stopProcess(this.command.groupId);
        } else {
            this.command.getContext().sendText("群里没有正在进行的开字母哦~");
        }
    }
}
