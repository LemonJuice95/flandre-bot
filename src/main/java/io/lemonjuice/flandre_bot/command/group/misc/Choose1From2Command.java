package io.lemonjuice.flandre_bot.command.group.misc;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Choose1From2Command extends GroupCommandRunner {

    @Override
    public IPermissionLevel getPermissionLevel(Message command) {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate(Message command) {
        if(!command.message.startsWith(CQCodeUtils.at(command.selfId)))
            return false;
        String[] options = this.getOptions(command);
        return options.length == 2;
    }

    @Override
    public void apply(Message command) {
        int index = ThreadLocalRandom.current().nextInt(2);
        String[] options = this.getOptions(command);
        SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) + "芙兰建议你" + options[index] +"哦！");
    }

    private String[] getOptions(Message command) {
        return command.message
                .replace(CQCodeUtils.at(command.selfId), "")
                .replace("\\[]", "")
                .trim()
                .split("还是");
    }
}