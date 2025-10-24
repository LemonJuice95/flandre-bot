package io.lemonjuice.flandre_bot.commands.group.maimai.query;

import io.lemonjuice.flan_mai_plugin.api.CompletionTableGenerator;
import io.lemonjuice.flan_mai_plugin.exception.NotInitializedException;
import io.lemonjuice.flan_mai_plugin.utils.enums.MaiVersion;
import io.lemonjuice.flandre_bot.func.FunctionCommand;
import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;
import io.lemonjuice.flandre_bot_framework.utils.CQCode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@FunctionCommand("maimai_query")
public class GroupPlateCompleteTableCommand extends GroupCommandRunner {
    private static final String versions = Arrays.stream(MaiVersion.values()).map(MaiVersion::getMatchingNames).collect(Collectors.joining()).replaceFirst("真", "");
    private static final String commandPatternRaw = "^\\[CQ:at,qq=%d]\\s*([%s霸](极|将|神|舞舞|者))完成表";

    private final Pattern commandPattern;

    public GroupPlateCompleteTableCommand(Message command) {
        super(command);
        this.commandPattern = Pattern.compile(String.format(commandPatternRaw, command.selfId, versions));
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean matches() {
        Matcher matcher = this.commandPattern.matcher(this.command.message);
        return matcher.matches();
    }

    @Override
    public void apply() {
        Matcher matcher = this.commandPattern.matcher(this.command.message);
        if(matcher.find()) {
            try {
                String plateName = matcher.group(1);
                if (plateName.charAt(0) == '舞' || plateName.equals("霸者")) {
                    this.command.getContext().replyWithText("芙兰暂时还不会画[舞]系牌子的表格呢……");
                    return;
                }
                try {
                    BufferedImage image = CompletionTableGenerator.generateWithPlates(this.command.userId, plateName);
                    if (image != null) {
                        this.command.getContext().replyWithText(CQCode.image(image, "PNG"));
                    } else {
                        this.command.getContext().replyWithText("诶？生成失败了……\n芙兰不是故意的……");
                    }
                } catch (IllegalArgumentException e) {
                    if(e.getMessage().equals("不存在的牌子")) {
                        this.command.getContext().replyWithText("没有这样的牌子哦~");
                    } else {
                        throw e;
                    }
                }
            } catch (NotInitializedException e) {
                this.command.getContext().replyWithText("曲目信息还没加载完呢，稍等一会吧~");
            }
        }
    }
}
