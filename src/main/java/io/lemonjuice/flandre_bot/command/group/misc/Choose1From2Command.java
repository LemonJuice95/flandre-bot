package io.lemonjuice.flandre_bot.command.group.misc;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

import java.util.concurrent.ThreadLocalRandom;

public class Choose1From2Command extends GroupCommandRunner {

    public Choose1From2Command(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate() {
        if(!this.command.message.startsWith(CQCodeUtils.at(this.command.selfId)))
            return false;
        String[] options = this.getOptions();
        return options.length == 2;
    }

    @Override
    public void apply() {
        int index = ThreadLocalRandom.current().nextInt(2);
        String[] options = this.getOptions();
        SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + "芙兰建议你" + options[index] +"哦！");
    }

    private String[] getOptions() {
        return this.command.message
                .replace(CQCodeUtils.at(this.command.selfId), "")
                .trim()
                .split("还是");
    }
}