package io.lemonjuice.flandre_bot.command;

import io.lemonjuice.flandre_bot.DebugModeToolkit;
import io.lemonjuice.flandre_bot.command.group.*;
import io.lemonjuice.flandre_bot.command.group.debug.*;
import io.lemonjuice.flandre_bot.command.group.func.*;
import io.lemonjuice.flandre_bot.command.group.interaction.*;
import io.lemonjuice.flandre_bot.command.group.interest.GroupFortuneCookieCommand;
import io.lemonjuice.flandre_bot.command.group.maimai.*;
import io.lemonjuice.flandre_bot.command.group.misc.Choose1From2Command;
import io.lemonjuice.flandre_bot.command.group.misc.GroupHelpCommand;
import io.lemonjuice.flandre_bot.command.group.daily.GroupSignInCommand;
import io.lemonjuice.flandre_bot.command.privat.PrivateCommandRunner;
import io.lemonjuice.flandre_bot.command.privat.PrivateHelloCommand;
import io.lemonjuice.flandre_bot.message.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CommandRegister {
    public static List<Function<Message, GroupCommandRunner>> GROUP_COMMANDS = new ArrayList<>();
    public static List<Function<Message, PrivateCommandRunner>> PRIVATE_COMMANDS = new ArrayList<>();

    static {
        registerGroupCommand(GroupHelloCommand::new);
        registerGroupCommand(GroupSignInCommand::new);
        registerGroupCommand(Choose1From2Command::new);
        registerGroupCommand(EnableFunctionCommand::new);
        registerGroupCommand(DisableFunctionCommand::new);
        registerGroupCommand(ShowFunctionsCommand::new);
        registerGroupCommand(GroupDiceCommand::new);
        registerGroupCommand(GroupPingCommand::new);
        registerGroupCommand(RandomTouhouImageCommand::new);
        registerGroupCommand(GroupHelpCommand::new);
        registerGroupCommand(GroupFortuneCookieCommand::new);
        registerGroupCommand(GroupNicknameCommand::new);
        registerGroupCommand(GroupClearNicknameCommand::new);
        registerGroupCommand(GroupHitCommand::new);

        registerGroupCommand(GroupMaiHelpCommand::new);
        registerGroupCommand(GroupB50Command::new);
        registerGroupCommand(GroupSongQueryCommand::new);
        registerGroupCommand(GroupSongAliasListCommand::new);
        registerGroupCommand(GroupSongInfoCommand::new);

        registerGroupCommand(ManualDailyRefreshCommand::new);



        if(DebugModeToolkit.isDebugMode()) {
            registerGroupCommand(GroupImageTestCommand::new);
            registerGroupCommand(GroupRefreshSignInStatusCommand::new);
            registerGroupCommand(GroupCurrentTimeCommand::new);
        }


        registerPrivateCommand(PrivateHelloCommand::new);
    }

    public static void registerGroupCommand(Function<Message, GroupCommandRunner> constructor) {
        GROUP_COMMANDS.add(constructor);
    }

    public static void registerPrivateCommand(Function<Message, PrivateCommandRunner> constructor) {
        PRIVATE_COMMANDS.add(constructor);
    }
}
