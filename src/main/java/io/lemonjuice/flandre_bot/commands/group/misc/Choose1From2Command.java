package io.lemonjuice.flandre_bot.commands.group.misc;

import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;
import io.lemonjuice.flandre_bot_framework.utils.CQCode;

import java.util.concurrent.ThreadLocalRandom;

public class Choose1From2Command extends GroupCommandRunner {
    private final String[] options;

    public Choose1From2Command(Message command) {
        super(command);
        this.options = this.getOptions();
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean matches() {
        if(!this.command.message.startsWith(CQCode.at(this.command.selfId)))
            return false;
        return this.options.length == 2;
    }

    @Override
    public void apply() {
        int index = ThreadLocalRandom.current().nextInt(2);
        this.command.getContext().replyWithText("芙兰建议你" + this.options[index] +"哦！");
    }

    private String[] getOptions() {
        return this.command.message
                .replace(CQCode.at(this.command.selfId), "")
                .trim()
                .split("还是");
    }

    @Override
    public boolean blockAfterCommands() {
        return false;
    }
}