package io.lemonjuice.flandre_bot.commands.group.interaction;

import io.lemonjuice.flandre_bot.resources.ResourceInit;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import io.lemonjuice.flandre_bot_framework.account.AccountInfo;
import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.message.segment.AtMessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.MessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.ReplyMessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.TextMessageSegment;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class GroupSimpleInteractionCommand extends GroupCommandRunner {
    private static final Map<String, List<List<MessageSegment>>> INTERACTION_MAP = ResourceInit.SIMPLE_INTERACTION_REPLIES.get();

    public GroupSimpleInteractionCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean matches() {
        StringBuilder key = new StringBuilder();
        for(MessageSegment seg : this.command.message.getSegments()) {
            if(seg instanceof AtMessageSegment atSeg) {
                if(atSeg.getQQ() == AccountInfo.getBotId()) {
                    key.append("<selfAt>");
                }
            } else {
                key.append(seg.toString().replace("<selfAt>", "##PLACEHOLDER_\uE000\uE001\uE002##").trim());
            }
        }
        return INTERACTION_MAP.containsKey(key.toString());
    }

    @Override
    public void apply() {
        StringBuilder key = new StringBuilder();
        for(MessageSegment seg : this.command.message.getSegments()) {
            if(seg instanceof AtMessageSegment atSeg) {
                if(atSeg.getQQ() == AccountInfo.getBotId()) {
                    key.append("<selfAt>");
                }
            } else {
                key.append(seg.toString().replace("<selfAt>", "").trim());
            }
        }
        List<List<MessageSegment>> repliesPool = INTERACTION_MAP.get(key.toString());
        List<MessageSegment> replyRaw = repliesPool.get(ThreadLocalRandom.current().nextInt(repliesPool.size()));
        List<MessageSegment> reply = new ArrayList<>();
        for(MessageSegment seg : replyRaw) {
            if(seg instanceof TextMessageSegment textSeg) {
                String text = textSeg.getContent();
                if(text.startsWith("<reply>")) {
                    text = text.replace("<reply>", "");
                    reply.add(new ReplyMessageSegment(this.command.messageId));
                }
                if(NicknameManager.getNickname(this.command.userId) != null) {
                    reply.add(new TextMessageSegment(text.replace("<user>", NicknameManager.getNickname(this.command.userId))));
                } else {
                    String[] spReply = text.split("<user>", -1);
                    for(int i = 0; i < spReply.length - 1; i++) {
                        reply.add(new TextMessageSegment(spReply[i]));
                        reply.add(new AtMessageSegment(this.command.userId));
                    }
                    reply.add(new TextMessageSegment(spReply[spReply.length - 1]));
                }
            } else {
                reply.add(seg);
            }
        }
        this.command.getContext().sendMessage(reply);
    }
}
