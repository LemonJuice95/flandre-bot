package io.lemonjuice.flandre_bot.command.group.maimai;

import io.lemonjuice.flan_mai_plugin.exception.NotInitializedException;
import io.lemonjuice.flan_mai_plugin.image_gen.DivingFishB50Generator;
import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.func.FunctionCommand;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FunctionCommand("maimai_query")
public class GroupB50Command extends GroupCommandRunner {
    private static final String commandPattern = "^\\[CQ:at,qq=%d]\\s*/(mai\\s+)?b50(\\s+)?(\\[CQ:at,qq=)?(\\d+\\s*)?(])?$";

    @Override
    public IPermissionLevel getPermissionLevel(Message command) {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate(Message command) {
        Pattern pattern = Pattern.compile(String.format(commandPattern, command.selfId));
        return pattern.matcher(command.message).matches();
    }

    @Override
    public void apply(Message command) {
        SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) + "b50吗...芙兰查查看...");
        try {
            long qq = getQQIdParam(command);
            qq = qq == -1 ? command.userId : qq;
            if (DivingFishB50Generator.generate(qq)) {
                File imageFile = new File("./cache/mai_b50/b50_" + qq + ".png");
                SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) + CQCodeUtils.image("file:///" + imageFile.getAbsolutePath()));
            } else {
                SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) + "抱歉...获取失败了...\n你的水鱼绑定qq号了吗？\n没绑定的话请前往https://www.diving-fish.com/maimaidx/prober/进行绑定\n如果绑定了还是失败的话就联系一下bot管理员吧");
            }
        } catch (NotInitializedException e) {
            SendingUtils.sendGroupText(command.groupId, "曲目信息还没加载完呢，稍等一会吧~");
        }
    }

    private long getQQIdParam(Message command) {
        Pattern pattern = Pattern.compile(String.format(commandPattern, command.selfId));
        Matcher matcher = pattern.matcher(command.message);
        if(matcher.find() && matcher.group(4) != null) {
            return Long.parseLong(matcher.group(4).trim());
        }
        return -1;
    }
}