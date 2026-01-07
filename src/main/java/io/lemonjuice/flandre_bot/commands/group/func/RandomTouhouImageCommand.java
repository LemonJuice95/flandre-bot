package io.lemonjuice.flandre_bot.commands.group.func;

import io.lemonjuice.flandre_bot.api.RandomTouhouImage;
import io.lemonjuice.flandre_bot.func.FunctionCommand;
import io.lemonjuice.flandre_bot.resources.ResourceInit;
import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.message.pattern.MessagePattern;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.AtNode;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.RegexNode;
import io.lemonjuice.flandre_bot_framework.message.segment.ImageMessageSegment;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FunctionCommand("random_touhou_image")
public class RandomTouhouImageCommand extends GroupCommandRunner {
    private static final JSONObject NAME_TAGS = ResourceInit.TOUHOU_CHARACTER_NAMES.get();

    private static final Pattern commandPattern = Pattern.compile("/随机东方图(\\s+\\S+)?");
    private static final MessagePattern messagePattern = new MessagePattern.Builder()
            .nextNode(AtNode.atBot())
            .nextNode(new RegexNode(commandPattern))
            .build();

    public RandomTouhouImageCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean matches() {
        return messagePattern.matcher(this.command.message).matches();
    }

    @Override
    public void apply() {
        String tag = this.getTag();
        tag = tryMappingTag(tag);
        String image = RandomTouhouImage.get(tag);
        if(image.isEmpty()) {
            this.command.getContext().replyWithText("获取失败……芙兰明明已经很努力了……");
            return;
        }
        this.command.getContext().sendMessage(List.of(new ImageMessageSegment("file:///" + image)));
    }

    private String tryMappingTag(String rawTag) {
        if(NAME_TAGS.has(rawTag))
            return NAME_TAGS.getString(rawTag);
        return rawTag;
    }

    private String getTag() {
        String message = this.command.message
                .getSegments()
                .get(1)
                .toString()
                .trim();
        Matcher matcher = commandPattern.matcher(message);
        return matcher.find() && matcher.group(1) != null ? matcher.group(1).trim() : "";
    }
}
