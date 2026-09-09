package io.lemonjuice.flandre_bot.commands.group.misc;

import io.lemonjuice.flandre_bot.utils.BirthdayManager;
import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.message.pattern.MessageMatcher;
import io.lemonjuice.flandre_bot_framework.message.pattern.MessagePattern;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.AtNode;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.RegexNode;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupBirthdayCommand extends GroupCommandRunner {
    private static final Pattern pattern = Pattern.compile("/生日 (\\d+)/(\\d+)");
    private static final MessagePattern messagePattern = MessagePattern.builder()
            .nextNode(AtNode.atBot())
            .startGroup()
            .nextNode(new RegexNode(pattern))
            .endGroup()
            .build();

    private final MessageMatcher matcher;
    public GroupBirthdayCommand(Message command) {
        super(command);
        this.matcher = messagePattern.matcher(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean matches() {
        return this.matcher.matches();
    }

    @Override
    public void apply() {
        Matcher matcher = pattern.matcher(this.matcher.group(1).toString());
        if (matcher.find()) {
            try {
                int month = Integer.parseInt(matcher.group(1));
                int day = Integer.parseInt(matcher.group(2));
                LocalDate localDate = LocalDate.of(2000, month, day);
                BirthdayManager.updateBirthDay(this.command.userId, month, day);
                this.command.getContext().replyWithText("芙兰记住你的生日了哦~");
            } catch (NumberFormatException | DateTimeException e) {
                this.command.getContext().replyWithText("日期的格式好像不太对呢……检查一下吧！");
            }
        }
    }
}
