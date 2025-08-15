package io.lemonjuice.flandre_bot.command.group.func;

import io.lemonjuice.flandre_bot.api.RandomTouhouImage;
import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.func.FunctionCommand;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FunctionCommand("random_touhou_image")
public class RandomTouhouImageCommand extends GroupCommandRunner {
    public static JSONObject NAME_TAGS = new JSONObject();

    @Override
    public IPermissionLevel getPermissionLevel(Message command) {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate(Message command) {
        String message = command.message.replaceAll(" ", "");
        return message.startsWith(CQCodeUtils.at(command.selfId) + "/随机东方图");
    }

    @Override
    public void apply(Message command) {
        String tag = this.getTag(command);
        tag = tryMappingTag(tag);
        String image = RandomTouhouImage.get(tag, command.groupId);
        if(image.isEmpty()) {
            SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) + "获取失败……芙兰明明已经很努力了……");
            return;
        }
        SendingUtils.sendGroupText(command.groupId, CQCodeUtils.image(image));
    }

    private String tryMappingTag(String rawTag) {
        if(NAME_TAGS.has(rawTag))
            return NAME_TAGS.getString(rawTag);
        return rawTag;
    }

    private String getTag(Message command) {
        String message = command.message
                .replace(CQCodeUtils.at(command.selfId), "")
                .replace("\\[]", "")
                .trim();
        Matcher matcher = Pattern.compile("/随机东方图\\s+(\\S+)").matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }
}
