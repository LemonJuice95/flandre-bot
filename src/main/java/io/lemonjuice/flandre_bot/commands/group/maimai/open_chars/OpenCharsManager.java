package io.lemonjuice.flandre_bot.commands.group.maimai.open_chars;

import io.lemonjuice.flan_mai_plugin.games.condition.SongFilterCondition;
import io.lemonjuice.flan_mai_plugin.games.open_chars.OpenCharsProcess;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class OpenCharsManager {
    private static final ConcurrentHashMap<Long, OpenCharsProcess> PROCESSES = new ConcurrentHashMap<>();
    private static final int DEFAULT_SONG_NUM = 10;

    public static String makeMessage(long groupId) {
        if(hasProcess(groupId)) {
            return makeMessage(getProcess(groupId));
        }
        return "";
    }

    public static String makeMessage(OpenCharsProcess process) {
        StringBuilder result = new StringBuilder("舞萌开字母\n\n");
        result.append(process.getSongsMessage());
        result.append("\n\n已开的字符：");
        result.append(process.getOpenedCharsMessage());
        return result.toString();
    }

    public static boolean startNewProcess(long groupId) {
        return startNewProcess(groupId, new ArrayList<>());
    }

    public static boolean startNewProcess(long groupId, List<SongFilterCondition> conditions) {
        if(PROCESSES.containsKey(groupId)) {
            return false;
        }
        return Objects.equals(PROCESSES.putIfAbsent(groupId, new OpenCharsProcess(DEFAULT_SONG_NUM, conditions)), null);
    }

    public static boolean stopProcess(long groupId) {
        if(!PROCESSES.containsKey(groupId)) {
            return false;
        }
        return !Objects.equals(PROCESSES.remove(groupId), null);
    }

    public static boolean hasProcess(long groupId) {
        return PROCESSES.containsKey(groupId);
    }

    public static OpenCharsProcess getProcess(long groupId) {
        return PROCESSES.get(groupId);
    }
}
