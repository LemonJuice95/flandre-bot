package io.lemonjuice.flandre_bot.command.group.maimai;

import io.lemonjuice.flan_mai_plugin.exception.NotInitializedException;
import io.lemonjuice.flan_mai_plugin.model.Song;
import io.lemonjuice.flan_mai_plugin.utils.SongManager;
import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.func.FunctionCommand;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FunctionCommand("maimai_query")
public class GroupSongAliasListCommand extends GroupCommandRunner {
    private static final String commandPattern = "^\\[CQ:at,qq=%d]\\s*.+有什么别[名称]$";

    @Override
    public IPermissionLevel getPermissionLevel(Message command) {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate(Message command) {
        Pattern pattern = Pattern.compile(String.format(commandPattern, command.selfId));
        return pattern.matcher(command.message).matches();
    }

    @Override
    public void apply(Message command) {
        String name = getSongName(command);
        try {
            List<Song> songList = SongManager.searchSong(name);

            if (songList.isEmpty()) {
                SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) + "没有找到叫做\"" + name + "\"的歌曲诶...");
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
                SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) + reply.toString().trim());
            } else {
                StringBuilder reply = new StringBuilder("好像有不止一首歌叫这个名字呢\n芙兰帮你列出来了哦~\n\n");
                for (Song s : songList) {
                    reply.append("id");
                    reply.append(s.id);
                    reply.append(":  ");
                    reply.append(s.title);
                    reply.append("\n");
                }
                SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) + reply.toString().trim());
            }
        } catch (NotInitializedException e) {
            SendingUtils.sendGroupText(command.groupId, "曲目信息还没加载完呢，稍等一会吧~");
        }
    }

    private static String getSongName(Message command) {
        String message = command.message.replace(CQCodeUtils.at(command.selfId), "").trim();
        Pattern pattern = Pattern.compile("^(.+)有什么别[名称]$");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }
}
