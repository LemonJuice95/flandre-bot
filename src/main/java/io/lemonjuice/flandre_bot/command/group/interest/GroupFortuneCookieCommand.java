package io.lemonjuice.flandre_bot.command.group.interest;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.resources.ResourceRegister;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import io.lemonjuice.flandre_bot.utils.SendingUtils;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class GroupFortuneCookieCommand extends GroupCommandRunner {
    private static final List<String> COOKIES = ResourceRegister.FORTUNE_COOKIES.get();

    private static final ConcurrentHashMap<UUID, Integer> CACHE = new ConcurrentHashMap<>();

    public GroupFortuneCookieCommand(Message command) {
        super(command);
    }

    public static void refresh() {
        CACHE.clear();
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate() {
        String message = this.command.message.replaceAll(" ", "");
        return message.equals(CQCodeUtils.at(this.command.selfId) + "/领取幸运饼干");
    }

    @Override
    public void apply() {
        UUID uuid = new UUID(this.command.groupId, this.command.userId);
        int index = ThreadLocalRandom.current().nextInt(COOKIES.size());
        if(Objects.equals(CACHE.putIfAbsent(uuid, index), null)) {
            SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) +
                    "送" + NicknameManager.getNickname(this.command.userId) + "一块可口的饼干！\n咦？饼干里面有一张纸条，上面写着: \n“" +
                    COOKIES.get(index) + "”");
        } else {
            SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) +
                    "你好像刚刚才领取过饼干诶……留下一些给别人吧~\n芙兰每个整点都会烤一次饼干！\n上一次你领到的饼干纸条是：\n“" +
                    COOKIES.get(CACHE.get(uuid)) + "”");
        }
    }
}
