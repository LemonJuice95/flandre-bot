package io.lemonjuice.flandre_bot.commands.group.interaction;

import io.lemonjuice.flandre_bot.resources.ResourceInit;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;
import io.lemonjuice.flandre_bot_framework.utils.CQCode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class GroupSimpleInteractionCommand extends GroupCommandRunner {
    private static final Map<String, List<String>> INTERACTION_MAP = ResourceInit.SIMPLE_INTERACTION_REPLIES.get();

    public GroupSimpleInteractionCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean matches() {
        String message = this.command.message
                .replace("<selfAt>", "")
                .replaceAll( String.format("\\s+%s\\s+", Pattern.quote(CQCode.at(this.command.selfId))), "<selfAt>");
        return INTERACTION_MAP.containsKey(message);
    }

    @Override
    public void apply() {
        String message = this.command.message
                .replaceAll( String.format("\\s+%s\\s+", Pattern.quote(CQCode.at(this.command.selfId))), "<selfAt>");
        List<String> repliesPool = INTERACTION_MAP.get(message);
        String reply = repliesPool.get(ThreadLocalRandom.current().nextInt(repliesPool.size()))
                .replace("<reply>", CQCode.reply(this.command.messageId))
                .replace("<user>", NicknameManager.getNickname(this.command.userId));
        this.command.getContext().sendText(reply);
    }
}
