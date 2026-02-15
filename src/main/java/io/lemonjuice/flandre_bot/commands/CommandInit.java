package io.lemonjuice.flandre_bot.commands;

import io.lemonjuice.flandre_bot.commands.group.daily.GroupSignInCommand;
import io.lemonjuice.flandre_bot.commands.group.func.*;
import io.lemonjuice.flandre_bot.commands.group.interaction.GroupClearNicknameCommand;
import io.lemonjuice.flandre_bot.commands.group.interaction.GroupHelloCommand;
import io.lemonjuice.flandre_bot.commands.group.interaction.GroupNicknameCommand;
import io.lemonjuice.flandre_bot.commands.group.interaction.GroupSimpleInteractionCommand;
import io.lemonjuice.flandre_bot.commands.group.interest.GroupFortuneCookieCommand;
import io.lemonjuice.flandre_bot.commands.group.maimai.GroupMaiHelpCommand;
import io.lemonjuice.flandre_bot.commands.group.maimai.open_chars.OCGuessSongCommand;
import io.lemonjuice.flandre_bot.commands.group.maimai.open_chars.OpenCharCommand;
import io.lemonjuice.flandre_bot.commands.group.maimai.open_chars.StartOpenCharsCommand;
import io.lemonjuice.flandre_bot.commands.group.maimai.open_chars.StopOpenCharsCommand;
import io.lemonjuice.flandre_bot.commands.group.maimai.query.*;
import io.lemonjuice.flandre_bot.commands.group.misc.Choose1From2Command;
import io.lemonjuice.flandre_bot.commands.group.misc.GroupHelpCommand;
import io.lemonjuice.flandre_bot.commands.group.special.K11PeopleNumberCommand;
import io.lemonjuice.flandre_bot.commands.privat.IPCommand;
import io.lemonjuice.flandre_bot_framework.command.CommandRegister;

public class CommandInit {
    public static final CommandRegister COMMANDS = new CommandRegister();

    static {
        //Group
        //常规
        COMMANDS.register(GroupHelpCommand::new);
        COMMANDS.register(GroupSignInCommand::new);

        COMMANDS.register(ShowFunctionsCommand::new);
        COMMANDS.register(DisableFunctionCommand::new);
        COMMANDS.register(EnableFunctionCommand::new);
        COMMANDS.register(GroupDiceCommand::new);
        COMMANDS.register(RandomTouhouImageCommand::new);

        COMMANDS.register(GroupClearNicknameCommand::new);
        COMMANDS.register(GroupNicknameCommand::new);
        COMMANDS.register(GroupHelloCommand::new);
        COMMANDS.register(GroupSimpleInteractionCommand::new);

        COMMANDS.register(GroupFortuneCookieCommand::new);
        COMMANDS.register(Choose1From2Command::new);

        //舞萌
        COMMANDS.register(K11PeopleNumberCommand::new);

        COMMANDS.register(GroupMaiHelpCommand::new);

        COMMANDS.register(GroupB50Command::new);
        COMMANDS.register(GroupPlateCompleteTableCommand::new);
        COMMANDS.register(GroupSongInfoCommand::new);
        COMMANDS.register(GroupSongAliasListCommand::new);
        COMMANDS.register(GroupSongQueryCommand::new);

        COMMANDS.register(StartOpenCharsCommand::new);
        COMMANDS.register(StopOpenCharsCommand::new);
        COMMANDS.register(OpenCharCommand::new);
        COMMANDS.register(OCGuessSongCommand::new);

        COMMANDS.register(IPCommand::new);
    }
}
