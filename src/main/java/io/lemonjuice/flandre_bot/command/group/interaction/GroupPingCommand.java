package io.lemonjuice.flandre_bot.command.group.interaction;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GroupPingCommand extends GroupCommandRunner {
    public static List<String> REPLIES = Collections.unmodifiableList(new ArrayList<>());

    public GroupPingCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate() {
        String message = this.command.message;
        return message.equals(CQCodeUtils.at(this.command.selfId)) ||
                message.trim().equals("芙兰") ||
                message.replace(" ", "").equals(CQCodeUtils.at(this.command.selfId) + "芙兰");
    }

    @Override
    public void apply() {
        String reply = REPLIES.get(new Random().nextInt(REPLIES.size()));
        SendingUtils.sendGroupText(this.command.groupId, reply);
    }
}
