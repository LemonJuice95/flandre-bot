package io.lemonjuice.flandre_bot.command.privat;

import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

public class PrivateHelloCommand extends PrivateCommandRunner{
    @Override
    public boolean validate(Message command) {
        return command.message.trim().equals("你好");
    }

    @Override
    public void apply(Message command) {
        SendingUtils.replyPrivateText(command, "你好~");
    }
}
