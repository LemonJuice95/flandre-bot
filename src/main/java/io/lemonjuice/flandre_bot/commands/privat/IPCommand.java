package io.lemonjuice.flandre_bot.commands.privat;

import io.lemonjuice.flandre_bot_framework.command.privat.SimplePrivateCommandRunner;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

@Log4j2
public class IPCommand extends SimplePrivateCommandRunner {
    public IPCommand(Message command) {
        super(command);
    }

    @Override
    protected String getCommandBody() {
        return "ip";
    }

    @Override
    public boolean needBeFriends() {
        return true;
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.DEBUG;
    }

    @Override
    public void apply() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet get = new HttpGet("http://icanhazip.com");
            HttpResponse response = httpClient.execute(get);
            if(response.getStatusLine().getStatusCode() != 200) {
                this.command.getContext().sendText(String.format("抱歉呢……ip获取失败了（HTTP ERROR %d）", response.getStatusLine().getStatusCode()));
                return;
            } else {
                String ip = EntityUtils.toString(response.getEntity());
                this.command.getContext().sendText("芙兰目前的ip为：\n" + ip);
            }
        } catch (IOException e) {
            log.error("无法获取ip！", e);
            this.command.getContext().sendText("抱歉呢……ip获取失败了");
        }
    }
}
