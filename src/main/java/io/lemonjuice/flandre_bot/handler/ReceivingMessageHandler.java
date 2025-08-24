package io.lemonjuice.flandre_bot.handler;

import io.lemonjuice.flandre_bot.DebugModeToolkit;
import io.lemonjuice.flandre_bot.command.CommandRegister;
import io.lemonjuice.flandre_bot.command.CommandRunningStatistics;
import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.privat.PrivateCommandRunner;
import io.lemonjuice.flandre_bot.func.FunctionCommand;
import io.lemonjuice.flandre_bot.func.FunctionEnableManager;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;
import lombok.extern.log4j.Log4j2;

import java.util.function.Function;

@Log4j2
public class ReceivingMessageHandler {
    public static void handle(Message message) {
        logMessage(message);

        if(message.type.equals("group")) {
            handleGroupCommand(message);
        }

        if(message.type.equals("private")) {
            handlePrivateCommand(message);
        }
    }

    private static void handleGroupCommand(Message message) {
        Thread.startVirtualThread(() -> {
            for (Function<Message, GroupCommandRunner> constructor : CommandRegister.GROUP_COMMANDS) {
                GroupCommandRunner runner = constructor.apply(message);
                if (runner.validate()) {
                    if (runner.getClass().isAnnotationPresent(FunctionCommand.class)) {
                        FunctionCommand annotation = runner.getClass().getAnnotation(FunctionCommand.class);
                        if (!FunctionEnableManager.isGroupFuncEnable(message.groupId, annotation.value())) {
                            if(annotation.report()) {
                                SendingUtils.sendGroupText(message.groupId, CQCodeUtils.reply(message.messageId) + "本群没有启用该功能，芙兰办不到呢……");
                            }
                            break;
                        }
                    }
                    if (!runner.getPermissionLevel().validatePermission(message)) {
                        SendingUtils.sendGroupText(message.groupId, CQCodeUtils.reply(message.messageId) + "权限不足，芙兰办不到呢……");
                        break;
                    }
                    runner.apply();
                    CommandRunningStatistics.addUsingCount(runner.getClass().getName());
                    break;
                }
            }
        });

    }

    private static void handlePrivateCommand(Message message) {
        Thread.startVirtualThread(() -> {
            for (Function<Message, PrivateCommandRunner> constructor : CommandRegister.PRIVATE_COMMANDS) {
                PrivateCommandRunner runner = constructor.apply(message);
                if (runner.validate()) {
                    if(runner.needsFriend() && !message.subType.equals("friend")) {
                        break;
                    }
                    if (runner.isDebugCommand() && !DebugModeToolkit.hasDebugPermission(message.userId)) {
                        break;
                    }
                    runner.apply();
                    CommandRunningStatistics.addUsingCount(runner.getClass().getName());
                }
            }
        });
    }

    private static void logMessage(Message message) {
        String from = "私聊(";
        String nickName = !message.sender.card.isEmpty() ? nickName = message.sender.card : message.sender.nickName;
        String messageContent = message.message;

        if(message.type.equals("private")) {
            from += message.userId + ")";
        }
        if(message.type.equals("group")) {
            from = "群聊(" + message.groupId + ")";
        }

        log.info("消息接收 来自{}: [{}]: {}", from, nickName, messageContent);
    }
}
