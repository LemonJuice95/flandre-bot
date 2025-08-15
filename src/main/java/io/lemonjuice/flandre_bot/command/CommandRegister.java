package io.lemonjuice.flandre_bot.command;

import io.lemonjuice.flandre_bot.DebugModeToolkit;
import io.lemonjuice.flandre_bot.command.group.*;
import io.lemonjuice.flandre_bot.command.group.debug.GroupCurrentTimeCommand;
import io.lemonjuice.flandre_bot.command.group.debug.GroupImageTestCommand;
import io.lemonjuice.flandre_bot.command.group.debug.GroupRefreshSignInStatusCommand;
import io.lemonjuice.flandre_bot.command.group.debug.ManualDailyRefreshCommand;
import io.lemonjuice.flandre_bot.command.group.func.*;
import io.lemonjuice.flandre_bot.command.group.interaction.*;
import io.lemonjuice.flandre_bot.command.group.interest.GroupFortuneCookieCommand;
import io.lemonjuice.flandre_bot.command.group.misc.Choose1From2Command;
import io.lemonjuice.flandre_bot.command.group.misc.GroupHelpCommand;
import io.lemonjuice.flandre_bot.command.group.daily.GroupSignInCommand;
import io.lemonjuice.flandre_bot.command.privat.PrivateCommandRunner;
import io.lemonjuice.flandre_bot.command.privat.PrivateHelloCommand;

import java.util.ArrayList;
import java.util.List;

public class CommandRegister {
    public static List<GroupCommandRunner> GROUP_COMMANDS = new ArrayList<>();
    public static List<PrivateCommandRunner> PRIVATE_COMMANDS = new ArrayList<>();

    static {
        registerGroupCommand(new GroupHelloCommand());
        registerGroupCommand(new GroupSignInCommand());
        registerGroupCommand(new Choose1From2Command());
        registerGroupCommand(new EnableFunctionCommand());
        registerGroupCommand(new DisableFunctionCommand());
        registerGroupCommand(new ShowFunctionsCommand());
        registerGroupCommand(new GroupDiceCommand());
        registerGroupCommand(new GroupPingCommand());
        registerGroupCommand(new RandomTouhouImageCommand());
        registerGroupCommand(new GroupHelpCommand());
        registerGroupCommand(new GroupFortuneCookieCommand());
        registerGroupCommand(new GroupNicknameCommand());
        registerGroupCommand(new GroupClearNicknameCommand());
        registerGroupCommand(new GroupHitCommand());

        registerGroupCommand(new ManualDailyRefreshCommand());

        if(DebugModeToolkit.isDebugMode()) {
            registerGroupCommand(new GroupImageTestCommand());
            registerGroupCommand(new GroupRefreshSignInStatusCommand());
            registerGroupCommand(new GroupCurrentTimeCommand());
        }


        registerPrivateCommand(new PrivateHelloCommand());
    }

    public static void registerGroupCommand(GroupCommandRunner runner) {
        GROUP_COMMANDS.add(runner);
    }

    public static void registerPrivateCommand(PrivateCommandRunner runner) {
        PRIVATE_COMMANDS.add(runner);
    }
}
