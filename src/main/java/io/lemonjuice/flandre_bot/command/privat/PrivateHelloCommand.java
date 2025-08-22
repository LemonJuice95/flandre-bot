package io.lemonjuice.flandre_bot.command.privat;

import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

public class PrivateHelloCommand extends PrivateCommandRunner{
    public PrivateHelloCommand(Message command) {
        super(command);
    }

    @Override
    public boolean validate() {
        return this.command.message.trim().equals("你好");
    }

    @Override
    public void apply() {
        SendingUtils.replyPrivateText(this.command, "你好~");
    }
}
