package io.lemonjuice.flandre_bot.command.group.interaction;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupNicknameCommand extends GroupCommandRunner {
    public GroupNicknameCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate() {
        String message = this.command.message.replace(" ", "");
        return message.startsWith(CQCodeUtils.at(this.command.selfId) + "/称呼");
    }

    @Override
    public void apply() {
        String nickname = this.getNickname();
        if(!nickname.isEmpty()) {
            NicknameManager.updateNickname(this.command.userId, nickname);
            SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + "唔……记住啦！\n芙兰以后就叫你" + nickname + "啦！");
        } else {
            SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + "诶？要芙兰叫你什么？没有听清呢……");
        }
    }

    private String getNickname() {
        if(Pattern.compile("\\[CQ:\\S+]").matcher(this.command.message).results().count() > 1) {
            return "";
        }
        String message = this.command.message
                .replace(CQCodeUtils.at(this.command.selfId), "")
                .replace("[]", "")
                .replace("&#91;", "[")
                .replace("&#93;", "]")
                .trim();
        Pattern pattern = Pattern.compile("/称呼\\s+(\\S+)");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }
}
