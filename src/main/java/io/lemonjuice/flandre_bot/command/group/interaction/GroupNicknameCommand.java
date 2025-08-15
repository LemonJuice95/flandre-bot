package io.lemonjuice.flandre_bot.command.group.interaction;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.NickNameManager;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupNicknameCommand extends GroupCommandRunner {
    @Override
    public IPermissionLevel getPermissionLevel(Message command) {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate(Message command) {
        String message = command.message.replaceAll(" ", "");
        return message.startsWith(CQCodeUtils.at(command.selfId) + "/称呼");
    }

    @Override
    public void apply(Message command) {
        String nickname = this.getNickname(command);
        if(!nickname.isEmpty()) {
            NickNameManager.updateNickname(command.userId, nickname);
            SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) + "唔……记住啦！\n芙兰以后就叫你" + nickname + "啦！");
        } else {
            SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) + "诶？要芙兰叫你什么？没有听清呢……");
        }
    }

    private String getNickname(Message command) {
        if(Pattern.compile("\\[CQ:\\S+]").matcher(command.message).results().count() > 1) {
            return "";
        }
        String message = command.message
                .replace(CQCodeUtils.at(command.selfId), "")
                .replace("[]", "")
                .trim();
        Pattern pattern = Pattern.compile("/称呼\\s+(\\S+)");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }
}
