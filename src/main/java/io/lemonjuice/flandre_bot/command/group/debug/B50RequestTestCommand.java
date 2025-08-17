package io.lemonjuice.flandre_bot.command.group.debug;

import io.lemonjuice.flandre_bot.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot.command.group.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot.command.group.permission.PermissionLevel;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.network.HttpCore;
import io.lemonjuice.flandre_bot.utils.CQCodeUtils;
import io.lemonjuice.flandre_bot.utils.SendingUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class B50RequestTestCommand extends GroupCommandRunner {
    @Override
    public IPermissionLevel getPermissionLevel(Message command) {
        return PermissionLevel.DEBUG;
    }

    @Override
    public boolean validate(Message command) {
        String message = command.message.replaceAll(" ", "");
        return message.equals(CQCodeUtils.at(command.selfId) + "/b50_");
    }

    @Override
    public void apply(Message command) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>();
        body.put("qq", 1582017385L);
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);
        String url = "https://www.diving-fish.com/api/maimaidxprober/query/player";
        String resp = HttpCore.getInstance().getRestTemplate().exchange(url, HttpMethod.POST, requestEntity, String.class).getBody();
        SendingUtils.sendGroupText(command.groupId, resp);
    }
}
