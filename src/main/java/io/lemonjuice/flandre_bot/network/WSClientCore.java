package io.lemonjuice.flandre_bot.network;

import io.lemonjuice.flandre_bot.FlandreBot;
import io.lemonjuice.flandre_bot.handler.HeartBeatHandler;
import io.lemonjuice.flandre_bot.handler.NoticeHandler;
import io.lemonjuice.flandre_bot.handler.ReceivingMessageHandler;
import io.lemonjuice.flandre_bot.message.Message;
import io.lemonjuice.flandre_bot.WatchDog;
import io.lemonjuice.flandre_bot.utils.MessageParser;
import jakarta.websocket.*;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ClientEndpoint(configurator = WSClientCore.Configurator.class)
@Log4j2
public class WSClientCore {
    private static final WatchDog watchDog = new WatchDog();

    @Getter
    private static WSClientCore instance;

    @Getter
    private final Session session;
    private static String TOKEN;

    private WSClientCore(String url) throws DeploymentException, IOException {
        this.session = ContainerProvider.getWebSocketContainer().connectToServer(this, URI.create(url));
    }

    public synchronized static void close() {
        try {
            instance.session.close();
        } catch (IOException e) {
            log.error("ws会话关闭异常！", e);
        }
    }

    public synchronized static boolean connect(String url, String token) {
        try {
            TOKEN = token;
            instance = new WSClientCore(url);
            return true;
        } catch (DeploymentException | IOException e) {
            log.error(e);
            log.error("Bot连接失败!");
            return false;
        }
    }

    public synchronized static void startWatchDog() {
        FlandreBot.getThreadPool().execute(watchDog);
    }

    public void sendText(String message) {
        this.session.getAsyncRemote().sendText(message);
    }

    @OnOpen
    public void onOpen(Session session) {
        log.info("Bot已连接!");
    }

    @OnMessage
    public void onMessage(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String postType = jsonObject.getString("post_type");

            logBody: {
                if (postType.equals("meta_event")) {
                    if (jsonObject.getString("meta_event_type").equals("heartbeat")) {
                        watchDog.onHeartBeat();
                        HeartBeatHandler.onHeartBeat();
                        break logBody;
                    }
                }
//                log.debug("Message Received: {}", json);
            }

            if (postType.equals("message")) {
                Message message = MessageParser.tryParse(jsonObject);

                ReceivingMessageHandler.handle(message);
            }

            if(postType.equals("notice")) {
                if(jsonObject.getString("notice_type").equals("bot_offline")) {
                    log.warn("qq账号下线! 原始json文本: {}", json);
                }
                NoticeHandler.handleNotice(jsonObject);
            }
        } catch (JSONException e) {

        }
    }

    @OnClose
    public void onClose(Session session) {
        log.info("Bot已断开连接!");
        Thread.startVirtualThread(new WSReconnect());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error(throwable);
    }

    public static class Configurator extends ClientEndpointConfig.Configurator {
        @Override
        public void beforeRequest(Map<String, List<String>> headers) {
            headers.put("Authorization", Collections.singletonList("Bearer " + TOKEN));
        }
    }
}
