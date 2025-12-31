package io.lemonjuice.flandre_bot.console;

import io.lemonjuice.flandre_bot_framework.console.ConsoleCommandRegister;

public class ConsoleCommandInit {
    public static final ConsoleCommandRegister CONSOLE_COMMANDS = new ConsoleCommandRegister();

    static {
        CONSOLE_COMMANDS.register(SendTextCommand::new);
        CONSOLE_COMMANDS.register(TempCommand::new);
    }
}
