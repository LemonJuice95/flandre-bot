package io.lemonjuice.flandre_bot.commands.group.interest;

import io.lemonjuice.flandre_bot.resources.ResourceInit;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import io.lemonjuice.flandre_bot_framework.command.group.SimpleGroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class GroupFortuneCookieCommand extends SimpleGroupCommandRunner {
    private static final List<String> COOKIES = ResourceInit.FORTUNE_COOKIES.get();

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
    protected boolean needAtFirst() {
        return true;
    }

    @Override
    protected String getCommandBody() {
        return "/领取幸运饼干";
    }

    @Override
    public void apply() {
        UUID uuid = new UUID(this.command.groupId, this.command.userId);
        int index = ThreadLocalRandom.current().nextInt(COOKIES.size());
        if(Objects.equals(CACHE.putIfAbsent(uuid, index), null)) {
            this.command.getContext().replyWithText("送" + NicknameManager.getNickname(this.command.userId) +
                    "一块可口的饼干！\n咦？饼干里面有一张纸条，上面写着: \n“" +
                    COOKIES.get(index) + "”");
        } else {
            this.command.getContext().replyWithText("你好像刚刚才领取过饼干诶……留下一些给别人吧~\n芙兰每个整点都会烤一次饼干！\n上一次你领到的饼干纸条是：\n“" +
                    COOKIES.get(CACHE.get(uuid)) + "”");
        }
    }
}