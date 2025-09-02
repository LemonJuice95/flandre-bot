package io.lemonjuice.flandre_bot.command.group.interaction;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.resources.ResourceRegister;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class GroupSimpleInteractionCommand extends GroupCommandRunner {
    private static final Map<String, List<String>> INTERACTION_MAP = ResourceRegister.SIMPLE_INTERACTION_REPLIES.get();

    public GroupSimpleInteractionCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate() {
        String message = this.command.message
                .replace("<selfAt>", "")
                .replace(CQCodeUtils.at(this.command.selfId), "<selfAt>")
                .replace(" ", "");
        return INTERACTION_MAP.containsKey(message);
    }

    @Override
    public void apply() {
        String message = this.command.message
                .replace(CQCodeUtils.at(this.command.selfId), "<selfAt>")
                .replace(" ", "");
        List<String> repliesPool = INTERACTION_MAP.get(message);
        String reply = repliesPool.get(ThreadLocalRandom.current().nextInt(repliesPool.size()))
                .replace("<reply>", CQCodeUtils.reply(this.command.messageId))
                .replace("<user>", NicknameManager.getNickname(this.command.userId));
        SendingUtils.sendGroupText(this.command.groupId, reply);
    }
}
