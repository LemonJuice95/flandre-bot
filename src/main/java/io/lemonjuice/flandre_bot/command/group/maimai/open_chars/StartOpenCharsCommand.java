package io.lemonjuice.flandre_bot.command.group.maimai.open_chars;

import io.lemonjuice.flan_mai_plugin.exception.NotInitializedException;
import io.lemonjuice.flan_mai_plugin.games.condition.LevelCondition;
import io.lemonjuice.flan_mai_plugin.games.condition.SongFilterCondition;
import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.maimai.GeneralMaiPatterns;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.func.FunctionCommand;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FunctionCommand("maimai_games")
public class StartOpenCharsCommand extends GroupCommandRunner {

    private static final String commandPatternRaw = "^\\[CQ:at,qq=%d]\\s*/舞萌开字母";

    private final Pattern commandPattern;
    private final List<SongFilterCondition> conditions = new ArrayList<>();

    public StartOpenCharsCommand(Message command) {
        super(command);
        this.commandPattern = Pattern.compile(String.format(commandPatternRaw, command.selfId));
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate() {
        Matcher matcher = this.commandPattern.matcher(this.command.message.trim());
        return matcher.find();
    }

    @Override
    public void apply() {
        try {
            this.parseConditions();
            if (OpenCharsManager.startNewProcess(this.command.groupId, this.conditions)) {
                SendingUtils.sendGroupText(this.command.groupId, "要玩开字母吗？好嘞~\n" + OpenCharsManager.makeMessage(this.command.groupId));
            } else {
                SendingUtils.sendGroupText(this.command.groupId, "好像群里已经在玩开字母了诶？\n先把现在这一轮玩完吧！");
            }
        } catch (NotInitializedException e) {
            SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + "功能还没初始化完成呢，耐心等一会吧~");
        }
    }

    private void parseConditions() {
        Matcher levelMatcher = GeneralMaiPatterns.levelCondition.matcher(this.command.message);
        if(levelMatcher.find()) {
            String modeStr = levelMatcher.group(1);
            modeStr = modeStr.isEmpty() ? "=" : modeStr;
            LevelCondition.Mode mode = GeneralMaiPatterns.LEVEL_MODE_MAP.get(modeStr);

            String level = levelMatcher.group(2);
            LevelCondition levelCondition = new LevelCondition(mode, level);
            this.conditions.add(levelCondition);
        }
    }
}
