package io.lemonjuice.flandre_bot.command.group.maimai;

import io.lemonjuice.flan_mai_plugin.image_gen.SongInfoGenerator;
import io.lemonjuice.flan_mai_plugin.song.Song;
import io.lemonjuice.flan_mai_plugin.song.SongManager;
import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.func.FunctionCommand;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FunctionCommand("maimai_query")
public class GroupSongQueryCommand extends GroupCommandRunner {
    private static final String pattern1 = "^\\[CQ:at,qq=${selfId}][\\s+]?/mai\\s+song.+";
    private static final String pattern2 = "^\\[CQ:at,qq=${selfId}][\\s+]?.+是什么歌$";

    @Override
    public IPermissionLevel getPermissionLevel(Message command) {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean validate(Message command) {
        Pattern commandPattern1 = Pattern.compile(pattern1.replace("${selfId}", String.valueOf(command.selfId)));
        Pattern commandPattern2 = Pattern.compile(pattern2.replace("${selfId}", String.valueOf(command.selfId)));

        return commandPattern1.matcher(command.message).matches() ||
                commandPattern2.matcher(command.message).matches();
    }

    @Override
    public void apply(Message command) {
        Pattern commandPattern1 = Pattern.compile(pattern1.replace("${selfId}", String.valueOf(command.selfId)));
        Pattern commandPattern2 = Pattern.compile(pattern2.replace("${selfId}", String.valueOf(command.selfId)));

        String name = "";
        if(commandPattern1.matcher(command.message).matches()) {
            name = getName1(command);
        } else if(commandPattern2.matcher(command.message).matches()) {
            name = getName2(command);
        }

        List<Song> songs = new ArrayList<>();

        Pattern idQuery = Pattern.compile("^(id)?(\\d+)$");
        if(!name.isEmpty()) {
            Matcher idMatcher = idQuery.matcher(name);
            if(idMatcher.find()) {
                int songId = Integer.parseInt(idMatcher.group(2));
                if(SongManager.getSongById(songId) != null) {
                    songs.add(SongManager.getSongById(songId));
                }
            }
            if(songs.isEmpty()) {
                songs = SongManager.getSongByAlias(name);
            }
        }
        if(songs.isEmpty()) {
            SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) + "没有找到别名为\"" + name + "\"的歌曲诶...");
        } else if(songs.size() == 1) {
            int songId = songs.getFirst().id;
            if(SongInfoGenerator.generate(songId)) {
                File file = new File("./cache/mai_song_info/" + songId + ".png");
                SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) + "你要找的是不是：\n" + CQCodeUtils.image("file:///" + file.getAbsolutePath()));
            } else {
                SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) + "诶？！图片生成失败了...");
            }
        } else {
            StringBuilder reply = new StringBuilder("好像有不止一首歌叫这个名字呢\n芙兰帮你列出来了哦~\n\n");
            for(Song s : songs) {
                reply.append("id").append(s.id).append(" ").append(s.title).append("\n");
            }
            SendingUtils.sendGroupText(command.groupId, CQCodeUtils.reply(command.messageId) + reply.toString().trim());
        }
    }

    private static String getName1(Message command) {
        String message = command.message.replace(CQCodeUtils.at(command.selfId), "").trim();
        Pattern pattern = Pattern.compile("/mai\\s+song\\s+(.+)");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }

    private static String getName2(Message command) {
        String message = command.message.replace(CQCodeUtils.at(command.selfId), "").trim();
        Pattern pattern = Pattern.compile("^(.+)是什么歌$");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }
}
