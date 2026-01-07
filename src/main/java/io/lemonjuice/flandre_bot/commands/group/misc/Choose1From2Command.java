package io.lemonjuice.flandre_bot.commands.group.misc;

import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.message.pattern.MessagePattern;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.AtNode;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.RegexNode;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Choose1From2Command extends GroupCommandRunner {
    private static final Pattern commandPattern = Pattern.compile("(.+)还是(.+)");
    private static final MessagePattern messagePattern = new MessagePattern.Builder()
            .nextNode(AtNode.atBot())
            .nextNode(new RegexNode(commandPattern))
            .build();

    public Choose1From2Command(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean matches() {
        return messagePattern.matcher(this.command).matches();
    }

    @Override
    public void apply() {
        int index = ThreadLocalRandom.current().nextInt(2);
        this.command.getContext().replyWithText("芙兰建议你" + this.getOptions()[index] +"哦！");
    }

    private String[] getOptions() {
        Matcher matcher = commandPattern.matcher(this.command.message.getSegments().get(1).toString().trim());
        return matcher.find() ? new String[]{matcher.group(1), matcher.group(2)} : new String[]{};
    }

    @Override
    public boolean blockAfterCommands() {
        return false;
    }
}