package io.lemonjuice.flandre_bot.command.group.func;

import io.lemonjuice.flandre_bot.api.RandomTouhouImage;
import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.func.FunctionCommand;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.resources.ResourceRegister;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FunctionCommand("random_touhou_image")
public class RandomTouhouImageCommand extends GroupCommandRunner {
    private static final JSONObject NAME_TAGS = ResourceRegister.TOUHOU_CHARACTER_NAMES.get();

    public RandomTouhouImageCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate() {
        String message = this.command.message.replaceAll(" ", "");
        return message.startsWith(CQCodeUtils.at(this.command.selfId) + "/随机东方图");
    }

    @Override
    public void apply() {
        String tag = this.getTag();
        tag = tryMappingTag(tag);
        String image = RandomTouhouImage.get(tag, this.command.groupId);
        if(image.isEmpty()) {
            SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + "获取失败……芙兰明明已经很努力了……");
            return;
        }
        SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.image(image));
    }

    private String tryMappingTag(String rawTag) {
        if(NAME_TAGS.has(rawTag))
            return NAME_TAGS.getString(rawTag);
        return rawTag;
    }

    private String getTag() {
        String message = this.command.message
                .replace(CQCodeUtils.at(this.command.selfId), "")
                .replace("\\[]", "")
                .trim();
        Matcher matcher = Pattern.compile("/随机东方图\\s+(\\S+)").matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }
}
