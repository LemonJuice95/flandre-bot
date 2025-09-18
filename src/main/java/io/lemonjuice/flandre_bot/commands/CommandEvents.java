package io.lemonjuice.flandre_bot.commands;

import io.lemonjuice.flandre_bot.func.FunctionCommand;
import io.lemonjuice.flandre_bot.func.FunctionEnableManager;
import io.lemonjuice.flandre_bot_framework.command.CommandRunner;
import io.lemonjuice.flandre_bot_framework.event.annotation.EventSubscriber;
import io.lemonjuice.flandre_bot_framework.event.annotation.SubscribeEvent;
import io.lemonjuice.flandre_bot_framework.event.msg.CommandRunEvent;
import io.lemonjuice.flandre_bot_framework.event.msg.PermissionDeniedEvent;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.utils.CQCode;

@EventSubscriber
public class CommandEvents {
    @SubscribeEvent
    public void onCommandRunPre(CommandRunEvent.Pre event) {
        checkFunctionEnabled(event);
    }

    @SubscribeEvent
    public void onCommandRunPost(CommandRunEvent.Post event) {
        CommandRunningStatistics.addUsingCount(event.getCommandRunner().getClass().getSimpleName());
    }

    @SubscribeEvent
    public void onPermissionDenied(PermissionDeniedEvent event) {
        Message message = event.getMessage();
        message.getContext().sendText(CQCode.reply(message.messageId) + "权限不足，芙兰办不到呢……");
    }

    private static void checkFunctionEnabled(CommandRunEvent.Pre event) {
        CommandRunner runner = event.getCommandRunner();
        Message message = event.getMessage();
        if(runner.getType() == CommandRunner.Type.GROUP && runner.getClass().isAnnotationPresent(FunctionCommand.class)) {
            FunctionCommand annotation = runner.getClass().getAnnotation(FunctionCommand.class);
            if(!FunctionEnableManager.isGroupFuncEnable(message.groupId, annotation.value())) {
                if(annotation.report()) {
                    message.getContext().sendText(CQCode.reply(message.messageId) + "本群没有启用该功能，芙兰办不到呢……");
                }
                event.setCancelled(true);
            }
        }
    }
}
