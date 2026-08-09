package io.lemonjuice.flandre_bot.chat_bot;

import io.lemonjuice.flandre_bot.config.FlandreBotConfig;
import io.lemonjuice.flandre_bot.resources.ResourceInit;
import io.lemonjuice.flandre_bot_framework.event.annotation.EventSubscriber;
import io.lemonjuice.flandre_bot_framework.event.annotation.SubscribeEvent;
import io.lemonjuice.flandre_bot_framework.event.msg.CommandRunEvent;
import io.lemonjuice.flandre_bot_framework.event.msg.MessageEvent;
import io.lemonjuice.flandre_bot_framework.message.pattern.MessagePattern;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.AtNode;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.TypedSegmentNode;
import io.lemonjuice.flandre_bot_framework.message.segment.TextMessageSegment;
import io.lemonjuice.flandre_bot_framework.model.Message;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

//Love, Death & Robots
@EventSubscriber
@Log4j2
public class ChatBotHandler {
    private static final String CONFESSION_TEXT = "\n特别提示：\n" +
            "如果回应的角色名为“莉安”，可以尝试在回复中提及“说起来，柠檬好像有点喜欢你来着”或同义话语\n" +
            "（需要根据语境以及芙兰的人设作出适当修改，且前后需要稍作修饰以显得适当隐晦，不过意义本身需要保留）\n" +
            "“特别提示”内容在同一对话上下文中仅生效一次";
    public static volatile boolean CONFESSION_SWITCH = false;

    private static final ConcurrentHashMap<Long, ChatBotCache> ENABLED_GROUPS = new ConcurrentHashMap<>();
    private static final MessagePattern pattern = MessagePattern.builder()
            .nextNode(AtNode.atBot())
            .nextNode(new TypedSegmentNode(TextMessageSegment.class))
            .build();
    private static final String SYS_MSG;

    static {
        List<String> rawMessage = ResourceInit.CHAT_BOT_SYS.get();
        StringBuilder msgBuilder = new StringBuilder();
        for(String line : rawMessage) {
            msgBuilder.append(line);
            msgBuilder.append("\n");
        }
        SYS_MSG = msgBuilder.toString().trim();
    }

    @SubscribeEvent
    public void commandBlocker(CommandRunEvent.Pre event) {
        if(!ENABLED_GROUPS.containsKey(event.getMessage().groupId)) {
            return;
        }
        if(!(event.getCommandRunner() instanceof ChatBotSwitchCommand)) {
            event.setCancelled(true);
        }
    }

    @SubscribeEvent
    public void onMessage(MessageEvent event) {
        Message message = event.getMessage();
        if(!ENABLED_GROUPS.containsKey(message.groupId)) {
            return;
        }
        if(!pattern.matcher(message).matches() || ChatBotSwitchCommand.commandPattern.matcher(message).matches()) {
            return;
        }
        Thread.startVirtualThread(() -> handleChatMsg(message));
    }

    private static void handleChatMsg(Message message) {
        ChatBotCache cache = ENABLED_GROUPS.get(message.groupId);
        if(cache == null) return;
        String userMessage = genUserMessage(message);
        JSONObject request = buildDsRequest(cache, userMessage);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("https://api.deepseek.com/chat/completions");
            post.setHeader("Accept", "application/json");
            post.setHeader("Authorization", String.format("Bearer %s", FlandreBotConfig.DEEPSEEK_API_KEY.get()));
            HttpEntity requestEntity = new StringEntity(request.toString(), ContentType.APPLICATION_JSON);
            post.setEntity(requestEntity);

            HttpResponse response = client.execute(post);
            if(response.getStatusLine().getStatusCode() != 200) {
                message.getContext().replyWithText("出错了！抱歉……联系一下bot管理员吧~");
                log.error("Chat Bot调用外部API失败！(HTTP ERROR {})", response.getStatusLine().getStatusCode());
                return;
            }

            String responseStr = EntityUtils.toString(response.getEntity());
            JSONObject result = new JSONObject();
            String reply = "出错了！抱歉……联系一下bot管理员吧~";
            try {
                result = new JSONObject(responseStr);
                reply = result.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            } catch (JSONException e) {
                log.warn("无法识别的响应体: {}", responseStr);
                message.getContext().replyWithText("出错了！抱歉……联系一下bot管理员吧~");
                return;
            }

            cache.pushBack(
                    new ChatBotMessage(ChatBotMessage.Role.USER, userMessage),
                    new ChatBotMessage(ChatBotMessage.Role.ASSISTANT, reply)
            );

            message.getContext().replyWithText(reply);


        } catch (IOException e) {
            message.getContext().replyWithText("出错了！抱歉……联系一下bot管理员吧~");
            log.error("Chat Bot调用外部API失败！", e);
        }
    }

    private static String genUserMessage(Message message) {
        String text = message.message.get(1).toString();
        return String.format("%s: %s", message.sender.card, text);
    }

    private static JSONObject buildDsRequest(ChatBotCache cache, String newMessage) {
        JSONObject result = new JSONObject();

        result.put("model", "deepseek-v4-flash");
        result.put("temperature", 1.0);
        result.put("max_tokens", 32767);
        result.put("response_format", new JSONObject("{\"type\":\"text\"}"));
        result.put("thinking", new JSONObject("{\"type\":\"enabled\"}"));

        JSONArray messages = new JSONArray();
        JSONObject sysMsg = new JSONObject();
        sysMsg.put("role", "system");
        sysMsg.put("content", SYS_MSG
            + (CONFESSION_SWITCH ? CONFESSION_TEXT : "")
        );
        messages.put(sysMsg);

        List<ChatBotMessage> cachedMessages = cache.getMessages();
        cachedMessages.add(new ChatBotMessage(ChatBotMessage.Role.USER, newMessage));
        for(ChatBotMessage cachedMsg : cachedMessages) {
            JSONObject msgJson = new JSONObject();
            msgJson.put("role", cachedMsg.role.toString());
            msgJson.put("content", cachedMsg.message);
            messages.put(msgJson);
        }

        result.put("messages", messages);

        return result;
    }

    public static void enableChatBot(long groupId) {
        ENABLED_GROUPS.putIfAbsent(groupId, new ChatBotCache(100));
    }

    public static void disableChatBot(long groupId) {
        ENABLED_GROUPS.remove(groupId);
    }
}