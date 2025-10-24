package io.lemonjuice.flandre_bot.commands.group.maimai.query;

import io.lemonjuice.flan_mai_plugin.api.DivingFishB50Generator;
import io.lemonjuice.flan_mai_plugin.exception.NotInitializedException;
import io.lemonjuice.flandre_bot.func.FunctionCommand;
import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;
import io.lemonjuice.flandre_bot_framework.utils.CQCode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FunctionCommand("maimai_query")
public class GroupB50Command extends GroupCommandRunner {
    private static final String commandPattern = "^\\[CQ:at,qq=%d]\\s*/?(mai\\s+)?b50(\\s+((\\[CQ:at,qq=(\\d+)])|(\\d+)))?\\s*$";

    private final Pattern pattern;
    
    public GroupB50Command(Message command) {
        super(command);
        this.pattern = Pattern.compile(String.format(commandPattern, this.command.selfId));
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean matches() {
        return this.pattern.matcher(this.command.message).matches();
    }

    @Override
    public void apply() {
        this.command.getContext().replyWithText("b50吗...芙兰查查看...");
        try {
            long qq = getQQIdParam();
            qq = qq == -1 ? this.command.userId : qq;
            BufferedImage image = DivingFishB50Generator.generate(qq);
            if (image != null) {
                this.command.getContext().replyWithText(CQCode.image(image));
            } else {
                this.command.getContext().replyWithText("抱歉...获取失败了...\n你的水鱼绑定qq号了吗？\n没绑定的话请前往https://www.diving-fish.com/maimaidx/prober/进行绑定\n如果绑定了还是失败的话就联系一下bot管理员吧");
            }
        } catch (NotInitializedException e) {
            this.command.getContext().replyWithText("曲目信息还没加载完呢，稍等一会吧~");
        }
    }

    private long getQQIdParam() {
        Matcher matcher = this.pattern.matcher(this.command.message);
        if(matcher.find()) {
            if(matcher.group(5) != null) {
                return Long.parseLong(matcher.group(5).trim());
            } else if(matcher.group(6) != null) {
                return Long.parseLong(matcher.group(6).trim());
            }
        }
        return -1;
    }
}