package io.lemonjuice.flandre_bot.command.group.maimai.query;

import io.lemonjuice.flan_mai_plugin.api.CompletionTableGenerator;
import io.lemonjuice.flan_mai_plugin.exception.NotInitializedException;
import io.lemonjuice.flan_mai_plugin.utils.enums.MaiVersion;
import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.func.FunctionCommand;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

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
    public boolean validate() {
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
                    SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + "芙兰暂时还不会画[舞]系牌子的表格呢……");
                    return;
                }
                String picPath = CompletionTableGenerator.generateWithPlates(this.command.userId, plateName);
                if(picPath.equals(CompletionTableGenerator.PLATE_NOT_FOUND)) {
                    SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + "没有这样的牌子哦~");
                } else if (!picPath.isEmpty()) {
                    File picFile = new File(picPath);
                    SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + CQCodeUtils.image("file:///" + picFile.getAbsolutePath()));
                } else {
                    SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + "诶？生成失败了……\n芙兰不是故意的……");
                }
            } catch (NotInitializedException e) {
                SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + "曲目信息还没加载完呢，稍等一会吧~");
            }
        }
    }
}
