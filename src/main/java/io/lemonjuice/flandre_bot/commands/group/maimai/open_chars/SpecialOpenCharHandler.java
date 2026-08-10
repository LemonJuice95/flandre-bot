package io.lemonjuice.flandre_bot.commands.group.maimai.open_chars;

import io.lemonjuice.flan_mai_plugin.games.open_chars.OpenCharsProcess;
import io.lemonjuice.flan_mai_plugin.model.Song;
import io.lemonjuice.flan_mai_plugin.utils.SongManager;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Field;
import java.util.List;

@Log4j2
public class SpecialOpenCharHandler {
    private static final int[] SPECIAL_SONG_IDS = {
            18, 100018,
            508, 100508,
            524, 100524,
            11459, 11670,
            11663
    };

    @SuppressWarnings("unchecked")
    public static void handleSpecial(long groupId) {
        OpenCharsProcess process = OpenCharsManager.getProcess(groupId);
        try {
            Field songsField = OpenCharsProcess.class.getDeclaredField("songs");
            songsField.setAccessible(true);
            List<Song> songList = (List<Song>) songsField.get(process);
            songList.clear();
            for (int id : SPECIAL_SONG_IDS) {
                songList.add(SongManager.getSongById(id));
            }

            Field remainingField = OpenCharsProcess.class.getDeclaredField("remaining");
            remainingField.setAccessible(true);
            remainingField.set(process, SPECIAL_SONG_IDS.length);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("修饰开字母失败！", e);
        }
    }
}
