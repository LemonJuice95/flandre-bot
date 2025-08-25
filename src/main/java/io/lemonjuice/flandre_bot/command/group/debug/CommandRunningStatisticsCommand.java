package io.lemonjuice.flandre_bot.command.group.debug;

import io.lemonjuice.flandre_bot.command.CommandRunningStatistics;
import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandRunningStatisticsCommand extends GroupCommandRunner {
    private static final String commandPattern = "^[CQ:at,qq=%d]\\s*/命令统计";
    private static final Pattern argsPattern = Pattern.compile("\\s+-(a(ll)?|h(istory)?|s(orted)?)");

    private boolean all = false;
    private boolean history = false;
    private boolean sorted = false;

    public CommandRunningStatisticsCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.DEBUG;
    }

    @Override
    public boolean validate() {
        Pattern pattern = Pattern.compile(String.format(commandPattern, this.command.selfId));
        return pattern.matcher(this.command.message).matches();
    }

    @Override
    public void apply() {
        this.handleArgs();

        Map<String, Integer> statistics = CommandRunningStatistics.getUsingCounts(this.all, this.history);
        if(this.sorted) {
            statistics = statistics.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));
        }

        StringBuilder reply = new StringBuilder(CQCodeUtils.reply(this.command.messageId));

        for(Map.Entry<String, Integer> entry : statistics.entrySet()) {
            reply.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\n");
        }

        SendingUtils.sendGroupText(this.command.groupId, reply.toString().trim());

    }

    private void handleArgs() {
        Matcher matcher = argsPattern.matcher(this.command.message);
        while (matcher.find()) {
            String arg = matcher.group(1);
            switch (arg) {
                case "a", "all" -> this.all = true;
                case "h", "history" -> this.history = true;
                case "s", "sorted" -> this.sorted = true;
            }
        }
    }
}
