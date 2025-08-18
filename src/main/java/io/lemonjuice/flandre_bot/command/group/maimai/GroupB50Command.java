package io.lemonjuice.flandre_bot.command.group.maimai;

import io.lemonjuice.flan_mai_plugin.b50.DivingFishB50Generator;
import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.func.FunctionCommand;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

import java.io.File;

@FunctionCommand("maimai_query")
public class GroupB50Command extends GroupCommandRunner {
    @Override
    public IPermissionLevel getPermissionLevel(Message command) {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate(Message command) {
        String message = command.message.replace(" ", "");
        return message.equals(CQCodeUtils.at(command.selfId) + "/b50") ||
                message.equals(CQCodeUtils.at(command.selfId) + "/maib50");
    }

    @Override
    public void apply(Message command) {
        SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) + "b50吗...芙兰查查看...");
        DivingFishB50Generator.generate(command.userId);
        File imageFile = new File("./cache/mai_b50/b50_" + command.userId + ".png");
        SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) + CQCodeUtils.image("file:///" + imageFile.getAbsolutePath()));
    }
}
