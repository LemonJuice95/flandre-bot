package io.lemonjuice.flandre_bot.commands.group.maimai.query;

import io.lemonjuice.flan_mai_plugin.api.DivingFishB50Generator;
import io.lemonjuice.flan_mai_plugin.exception.NotInitializedException;
import io.lemonjuice.flandre_bot.func.FunctionCommand;
import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.message.pattern.MessagePattern;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.AtNode;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.RegexNode;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.TypedSegmentNode;
import io.lemonjuice.flandre_bot_framework.message.segment.AtMessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.ImageMessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.MessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.ReplyMessageSegment;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FunctionCommand("maimai_query")
public class GroupB50Command extends GroupCommandRunner {
    private static final Pattern commandPattern = Pattern.compile("/?(mai\\s+)?b50(\\s+(\\d+))?");
    private static final MessagePattern messagePattern = new MessagePattern.Builder()
            .nextNode(AtNode.atBot())
            .nextNode(new RegexNode(commandPattern))
            .nextOptNode(new TypedSegmentNode(AtMessageSegment.class))
            .build();
    
    public GroupB50Command(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean matches() {
        return messagePattern.matcher(this.command.message.trim()).matches();
    }

    @Override
    public void apply() {
        this.command.getContext().replyWithText("b50吗...芙兰查查看...");
        try {
            long qq = getQQIdParam();
            qq = qq == -1 ? this.command.userId : qq;
            BufferedImage image = DivingFishB50Generator.generate(qq);
            if (image != null) {
                List<MessageSegment> reply = new ArrayList<>();
                reply.add(new ReplyMessageSegment(this.command.messageId));
                reply.add(new ImageMessageSegment(image, "PNG"));
                this.command.getContext().sendMessage(reply);
            } else {
                this.command.getContext().replyWithText("抱歉...获取失败了...\n你的水鱼绑定qq号了吗？\n没绑定的话请前往https://www.diving-fish.com/maimaidx/prober/进行绑定\n如果绑定了还是失败的话就联系一下bot管理员吧");
            }
        } catch (NotInitializedException e) {
            this.command.getContext().replyWithText("曲目信息还没加载完呢，稍等一会吧~");
        }
    }

    private long getQQIdParam() {
        if(this.command.message.getSegments().size() == 3 &&
                this.command.message.getSegments().get(2) instanceof AtMessageSegment atSeg) {
            return atSeg.getQQ();
        }
        Matcher matcher = commandPattern.matcher(this.command.message.getSegments().get(1).toString());
        if(matcher.find()) {
            if(matcher.group(3) != null) {
                return Long.parseLong(matcher.group(3).trim());
            }
        }
        return -1;
    }
}