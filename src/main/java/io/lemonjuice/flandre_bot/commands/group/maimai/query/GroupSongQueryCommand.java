package io.lemonjuice.flandre_bot.commands.group.maimai.query;

import io.lemonjuice.flan_mai_plugin.api.SongInfoGenerator;
import io.lemonjuice.flan_mai_plugin.exception.NotInitializedException;
import io.lemonjuice.flan_mai_plugin.model.Song;
import io.lemonjuice.flan_mai_plugin.utils.SongManager;
import io.lemonjuice.flandre_bot.func.FunctionCommand;
import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;
import io.lemonjuice.flandre_bot_framework.utils.CQCode;

import java.awt.image.BufferedImage;
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
    public boolean matches() {
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
                this.command.getContext().replyWithText("没有找到叫做\"" + name + "\"的歌曲诶...");
            } else if (songs.size() == 1) {
                int songId = songs.getFirst().id;
                BufferedImage image = SongInfoGenerator.generate(songId);
                if (image != null) {
                    this.command.getContext().replyWithText("你要找的是不是：\n" + CQCode.image(image, "PNG"));
                } else {
                    this.command.getContext().replyWithText("诶？！图片生成失败了...");
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
                this.command.getContext().replyWithText(reply.toString().trim());
            }
        } catch (NotInitializedException e) {
            this.command.getContext().replyWithText("曲目信息还没加载完呢，稍等一会吧~");
        }
    }

    private String getName1() {
        String message = this.command.message.replace(CQCode.at(this.command.selfId), "").trim();
        Pattern pattern = Pattern.compile("/mai\\s+song\\s+(.+)");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }

    private String getName2() {
        String message = this.command.message.replace(CQCode.at(this.command.selfId), "").trim();
        Pattern pattern = Pattern.compile("^(.+)是什么歌$");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1).replace("&#91;", "[").replace("&#93;", "]") : "";
    }
}
