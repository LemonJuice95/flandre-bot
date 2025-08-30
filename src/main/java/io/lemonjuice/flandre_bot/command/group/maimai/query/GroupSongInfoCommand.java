package io.lemonjuice.flandre_bot.command.group.maimai.query;

import io.lemonjuice.flan_mai_plugin.api.SongPlayDataGenerator;
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

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FunctionCommand("maimai_query")
public class GroupSongInfoCommand extends GroupCommandRunner {
    private static final String commandPattern = "^(\\[CQ:at,qq=%d]\\s*)?/m(ai\\s+)?info\\s+(.+)$";
    
    private final Pattern pattern;

    public GroupSongInfoCommand(Message command) {
        super(command);
        this.pattern = Pattern.compile(String.format(commandPattern, command.selfId));
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate() {
        return this.pattern.matcher(this.command.message).matches();
    }

    @Override
    public void apply() {
        String name = getSongName();
        try {
            List<Song> songs = SongManager.searchSong(name);

            if (songs.isEmpty()) {
                SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + "没有找到叫做\"" + name + "\"的歌曲诶...");
            } else if (songs.size() == 1) {
                int songId = songs.getFirst().id;
                String path = SongPlayDataGenerator.generate(this.command.userId, songId);
                if (!path.isEmpty()) {
                    File file = new File(path);
                    SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + CQCodeUtils.image("file:///" + file.getAbsolutePath()));
                } else {
                    SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + "诶？！图片生成失败了...");
                }
            } else {
                StringBuilder reply = new StringBuilder("好像有不止一首歌叫这个名字呢\n芙兰帮你列出来了哦~\n\n");
                for (Song s : songs) {
                    reply.append("id");
                    reply.append(s.id);
                    reply.append(":  ");
                    reply.append(s.title);
                    reply.append("\n");
                }
                SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + reply.toString().trim());
            }
        } catch (NotInitializedException e) {
            SendingUtils.sendGroupText(this.command.groupId, "曲目信息还没加载完呢，稍等一会吧~");
        }
    }

    private String getSongName() {
        Matcher matcher = this.pattern.matcher(this.command.message);
        return matcher.find() ? matcher.group(3).trim() : "";
    }
}
