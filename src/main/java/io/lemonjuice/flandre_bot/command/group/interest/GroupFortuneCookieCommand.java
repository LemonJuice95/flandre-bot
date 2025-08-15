package io.lemonjuice.flandre_bot.command.group.interest;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import io.lemonjuice.flandre_bot.utils.SendingUtils;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
@EnableScheduling
@EnableAsync
public class GroupFortuneCookieCommand extends GroupCommandRunner {
    public static List<String> COOKIES = new ArrayList<>();
    private static final Map<UUID, Integer> CACHE = new HashMap<>();

    public static void refresh() {
        CACHE.clear();
    }

    @Override
    public IPermissionLevel getPermissionLevel(Message command) {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate(Message command) {
        String message = command.message.replaceAll(" ", "");
        return message.equals(CQCodeUtils.at(command.selfId) + "/领取幸运饼干");
    }

    @Override
    public void apply(Message command) {
        UUID uuid = new UUID(command.groupId, command.userId);
        if(!CACHE.containsKey(uuid)) {
            int index = ThreadLocalRandom.current().nextInt(COOKIES.size());
            CACHE.put(uuid, index);
            SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) +
                    "送" + NicknameManager.getNickname(command.userId) + "一块可口的饼干！\n咦？饼干里面有一张纸条，上面写着: \n“" +
                    COOKIES.get(index) + "”");
        } else {
            SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) +
                    "你好像刚刚才领取过饼干诶……留下一些给别人吧~\n芙兰每个整点都会烤一次饼干！\n上一次你领到的饼干纸条是：\n“" +
                    COOKIES.get(CACHE.get(uuid)) + "”");
        }
    }
}
