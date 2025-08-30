package io.lemonjuice.flandre_bot.command.group.maimai.query;

import io.lemonjuice.flan_mai_plugin.exception.NotInitializedException;
import io.lemonjuice.flan_mai_plugin.api.SongInfoGenerator;
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
public class GroupSongQueryCommand extends GroupCommandRunner {
    private static final String pattern1 = "^\\[CQ:at,qq=%d]\\s*/mai\\s+song.+";
    private static final String pattern2 = "^\\[CQ:at,qq=%d]\\s*.+是什么歌$";
    
    private final Pattern commandPattern1;
    private final Pattern commandPattern2;

    public GroupSongQueryCommand(Message command) {
        super(command);
        this.commandPattern1 = Pattern.compile(String.format(pattern1, command.selfId));
        this.commandPattern2 = Pattern.compile(String.format(pattern2, command.selfId));
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate() {
        return this.commandPattern1.matcher(this.command.message).matches() ||
                this.commandPattern2.matcher(this.command.message).matches();
    }

    @Override
    public void apply() {
        String name = "";
        if(this.commandPattern1.matcher(this.command.message).matches()) {
            name = getName1();
        } else if(this.commandPattern2.matcher(this.command.message).matches()) {
            name = getName2();
        }

        try {
            List<Song> songs = SongManager.searchSong(name);

            if (songs.isEmpty()) {
                SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + "没有找到叫做\"" + name + "\"的歌曲诶...");
            } else if (songs.size() == 1) {
                int songId = songs.getFirst().id;
                String path = SongInfoGenerator.generate(songId);
                if (!path.isEmpty()) {
                    File file = new File("./cache/mai_song_info/" + songId + ".png");
                    SendingUtils.sendGroupText(this.command.groupId, CQCodeUtils.reply(this.command.messageId) + "你要找的是不是：\n" + CQCodeUtils.image("file:///" + file.getAbsolutePath()));
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

    private String getName1() {
        String message = this.command.message.replace(CQCodeUtils.at(this.command.selfId), "").trim();
        Pattern pattern = Pattern.compile("/mai\\s+song\\s+(.+)");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }

    private String getName2() {
        String message = this.command.message.replace(CQCodeUtils.at(this.command.selfId), "").trim();
        Pattern pattern = Pattern.compile("^(.+)是什么歌$");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }
}
