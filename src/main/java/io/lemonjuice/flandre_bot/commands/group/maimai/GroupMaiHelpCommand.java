package io.lemonjuice.flandre_bot.commands.group.maimai;

import io.lemonjuice.flandre_bot.resources.ResourceInit;
import io.lemonjuice.flandre_bot_framework.command.group.SimpleGroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;

import java.util.List;

public class GroupMaiHelpCommand extends SimpleGroupCommandRunner {
    private static final List<String> DOC = ResourceInit.HELP_DOC_MAI.get();

    public GroupMaiHelpCommand(Message command) {
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
        return "/mai help";
    }

    @Override
    public void apply() {
        this.command.getContext().sendForwardText(DOC);
    }
}
