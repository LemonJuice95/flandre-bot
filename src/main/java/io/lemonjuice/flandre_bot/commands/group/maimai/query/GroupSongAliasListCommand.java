package io.lemonjuice.flandre_bot.commands.group.maimai.query;

import io.lemonjuice.flan_mai_plugin.exception.NotInitializedException;
import io.lemonjuice.flan_mai_plugin.model.Song;
import io.lemonjuice.flan_mai_plugin.utils.SongManager;
import io.lemonjuice.flandre_bot.func.FunctionCommand;
import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.message.pattern.MessagePattern;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.AtNode;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.RegexNode;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FunctionCommand("maimai_query")
public class GroupSongAliasListCommand extends GroupCommandRunner {
    private static final Pattern commandPattern = Pattern.compile(".+有什么别[名称]");
    private static final MessagePattern messagePattern = new MessagePattern.Builder()
            .nextNode(AtNode.atBot())
            .nextNode(new RegexNode(commandPattern))
            .build();

    public GroupSongAliasListCommand(Message command) {
        super(command);
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean matches() {
        return messagePattern.matcher(this.command.message).matches();
    }

    @Override
    public void apply() {
        String name = getSongName();
        try {
            List<Song> songList = SongManager.searchSong(name);

            if (songList.isEmpty()) {
                this.command.getContext().replyWithText("没有找到叫做\"" + name + "\"的歌曲诶...");
            } else if (songList.size() == 1) {
                Song song = songList.getFirst();
                StringBuilder reply = new StringBuilder("找到啦！\nid");
                reply.append(song.id);
                reply.append(":  ");
                reply.append(song.title);
                reply.append("\n有以下别名：\n\n");
                for (String alias : song.alias) {
                    reply.append(alias).append("\n");
                }
                this.command.getContext().replyWithText(reply.toString().trim());
            } else {
                StringBuilder reply = new StringBuilder("好像有不止一首歌叫这个名字呢\n芙兰帮你列出来了哦~\n\n");
                for (Song s : songList) {
                    reply.append("id");
                    reply.append(s.id);
                    reply.append(":  ");
                    reply.append(s.title);
                    reply.append("\n");
                }
                this.command.getContext().replyWithText(reply.toString().trim());
            }
        } catch (NotInitializedException e) {
            this.command.getContext().replyWithText("曲目信息还没加载完呢，稍等一会吧~");
        }
    }

    private String getSongName() {
        String message = this.command.message.getSegments().get(1).toString().trim();
        Pattern pattern = Pattern.compile("^(.+)有什么别[名称]$");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }
}
