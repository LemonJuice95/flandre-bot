package io.lemonjuice.flandre_bot.chat_bot;

import io.lemonjuice.flandre_bot.config.FlandreBotConfig;
import io.lemonjuice.flandre_bot.resources.ResourceInit;
import io.lemonjuice.flandre_bot_framework.FlandreBot;
import io.lemonjuice.flandre_bot_framework.event.annotation.EventSubscriber;
import io.lemonjuice.flandre_bot_framework.event.annotation.SubscribeEvent;
import io.lemonjuice.flandre_bot_framework.event.msg.CommandRunEvent;
import io.lemonjuice.flandre_bot_framework.event.msg.MessageEvent;
import io.lemonjuice.flandre_bot_framework.message.MessageSegmentList;
import io.lemonjuice.flandre_bot_framework.message.pattern.MessageMatcher;
import io.lemonjuice.flandre_bot_framework.message.pattern.MessagePattern;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.AtNode;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.TypedSegmentNode;
import io.lemonjuice.flandre_bot_framework.message.segment.ImageMessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.MessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.ReplyMessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.TextMessageSegment;
import io.lemonjuice.flandre_bot_framework.model.Message;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//Love, Death & Robots
@EventSubscriber
@Log4j2
public class ChatBotHandler {
    private static final String CONFESSION_TEXT = "\n特别提示：\n" +
            "如果回应的角色名为“莉安”，可以尝试在回复中提及一个叫“柠檬”的人在喜欢她\n" +
            "（需要根据语境以及芙兰的人设作出适当适配，并且芙兰需表现得与柠檬熟识）\n" +
            "注：尚文忠提到的柠檬为男性，这一点无需在回复中刻意提及，但使用第三人称指代柠檬时请使用“他”\n" +
            "“特别提示”内容在同一对话上下文中仅生效一次";
    public static volatile boolean CONFESSION_SWITCH = false;

    private static final ConcurrentHashMap<Long, ChatBotCache> ENABLED_GROUPS = new ConcurrentHashMap<>();
    private static final MessagePattern pattern = MessagePattern.builder()
            .startGroup()
            .nextOptNode(new TypedSegmentNode(ReplyMessageSegment.class))
            .endGroup()
            .nextNode(AtNode.atBot())
            .startGroup()
            .nextOrNodes(new TypedSegmentNode(TextMessageSegment.class), new TypedSegmentNode(ImageMessageSegment.class))
            .endGroup(MessagePattern.GroupFlag.LOOP)
            .build();
    private static final String SYS_MSG;
    private static final ConcurrentHashMap<String, ChatBotCachedFile> CACHED_FILES = new ConcurrentHashMap<>();
    private static final int CACHED_FILES_EXPIRES_IN = 10800;
    private static final DateTimeFormatter cacheDateFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

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
        MessageMatcher matcher = pattern.matcher(message);
        if(!matcher.matches() || ChatBotSwitchCommand.commandPattern.matcher(message).simplyMatches()) {
            return;
        }
        Thread.startVirtualThread(() -> handleChatMsg(message, matcher));
    }

    private static void handleChatMsg(Message message, MessageMatcher matcher) {
        ChatBotCache cache = ENABLED_GROUPS.get(message.groupId);
        if(cache == null) return;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            ChatBotMessage.Body userMessage = genUserMessage(message, matcher, client);
            JSONObject request = buildDsRequest(cache, userMessage);

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
                outputDsResponse(result);
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
                    new ChatBotMessage(ChatBotMessage.Role.ASSISTANT, new ChatBotMessage.Body(reply))
            );

            message.getContext().replyWithText(reply);


        } catch (Exception e) {
            message.getContext().replyWithText("出错了！抱歉……联系一下bot管理员吧~");
            log.error("处理chat bot消息失败！", e);
        }
    }

    private static void outputDsResponse(JSONObject response) {
        ZonedDateTime nowTime = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        String fileName = String.format("reply_%s.json", cacheDateFormatter.format(nowTime));
        File cacheFile = new File("./cache/ds_reply/chat_bot/" + fileName);
        if(!cacheFile.getParentFile().exists()) {
            cacheFile.getParentFile().mkdirs();
        }
        try (OutputStream output = new FileOutputStream(cacheFile)) {
            output.write(response.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("输出AI回复失败！", e);
        }
    }

    private static ChatBotMessage.Body genUserMessage(Message message, MessageMatcher matcher, HttpClient client) throws IOException {
        StringBuilder referredTextRaw = new StringBuilder();
        String referredUsername = "";
        List<String> imageIds = new ArrayList<>();

        MessageSegmentList replySegments = matcher.group(1);
        if(replySegments != null) {
            MessageSegment seg = replySegments.getFirst();
            if(seg instanceof ReplyMessageSegment replySeg) {
                Message referredMsg = replySeg.getReferredMsg();
                referredUsername = referredMsg.sender.card.isEmpty() ? referredMsg.sender.nickName : referredMsg.sender.card;
                for(MessageSegment segI : referredMsg.message) {
                    if(segI instanceof ImageMessageSegment imgSeg) {
                        String imageId = handleImage(imgSeg, client);
                        imageIds.add(imageId);
                        referredTextRaw.append("${").append(imgSeg.getFile()).append("}\n");
                    } else {
                        referredTextRaw.append(segI.toString()).append("\n");
                    }
                }
            }
        }
        String referredText = referredTextRaw.toString();

        StringBuilder rawText = new StringBuilder();
        MessageSegmentList segments = matcher.group(2);
        for(MessageSegment seg : segments) {
            if(seg instanceof TextMessageSegment) {
                rawText.append(seg.toString()).append("\n");
            }
            if(seg instanceof ImageMessageSegment imgSeg) {
                String imageId = handleImage(imgSeg, client);
                imageIds.add(imageId);
                rawText.append("${").append(imgSeg.getFile()).append("}\n");
            }
        }

        String text = String.format("%s%s: %s",
                referredText.isBlank() ? "" : String.format("[reference]\n%s: %s\n[/reference]\n",
                            referredUsername,
                            referredText.trim()
                        ),
                message.sender.card.isEmpty() ? message.sender.nickName : message.sender.card,
                rawText.toString().trim()
        );

        if(imageIds.isEmpty()) {
            return new ChatBotMessage.Body(text);
        } else {
            return new ChatBotMessage.Body(text, imageIds);
        }
    }

    /**
     * @return ds file_id
     */
    private static String handleImage(ImageMessageSegment imgSeg, HttpClient client) throws IOException {
        File imgFile = FlandreBot.getFileHelper().getImageFile(imgSeg);

        ChatBotCachedFile cachedFile = CACHED_FILES.computeIfPresent(imgFile.getName(), (k, v) -> {
            if(v.isValidNow()) return v;
            return null;
        });

        if(cachedFile != null) {
            return cachedFile.fileId();
        }

        HttpPost post = new HttpPost("https://api.deepseek.com/files");

        post.addHeader("Authorization", String.format("Bearer %s", FlandreBotConfig.DEEPSEEK_API_KEY.get()));
        post.addHeader("Accept", "application/json");

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();

        entityBuilder.addTextBody("purpose", "user_data");
        entityBuilder.addTextBody("expires_after[anchor]", "created_at");
        entityBuilder.addTextBody("expires_after[seconds]", String.valueOf(CACHED_FILES_EXPIRES_IN));
        entityBuilder.addBinaryBody("file", imgFile);

        post.setEntity(entityBuilder.build());

        HttpResponse response = client.execute(post);
        if(response.getStatusLine().getStatusCode() != 200) {
            log.error("图片上传失败! (HTTP ERROR {})", response.getStatusLine().getStatusCode());
        }

        String responseStr = EntityUtils.toString(response.getEntity());
        try {
            JSONObject respJson = new JSONObject(responseStr);
            String fileId = respJson.getString("id");
            long expiresAt = respJson.optLong("expires_at", Long.MAX_VALUE);
            CACHED_FILES.put(imgFile.getName(), new ChatBotCachedFile(expiresAt, fileId));
            return fileId;
        } catch (JSONException e) {
            log.error("无法解析上传图片时的响应JSON", e);
            throw e;
        }
    }

    private static JSONObject buildDsRequest(ChatBotCache cache, ChatBotMessage.Body newMessage) {
        JSONObject result = new JSONObject();

        result.put("model", "deepseek-flash");
        result.put("temperature", 0.9);
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
            msgJson.put("content", serializeBody(cachedMsg.message));
            messages.put(msgJson);
        }

        result.put("messages", messages);

        return result;
    }

    private static Object serializeBody(ChatBotMessage.Body body) {
        if(body.fileIds() == null || body.fileIds().isEmpty()) {
            return body.text();
        }
        JSONArray result = new JSONArray();

        JSONObject textJson = new JSONObject();
        textJson.put("type", "text");
        textJson.put("text", body.text());
        result.put(textJson);

        for(String id : body.fileIds()) {
            JSONObject fileJson = new JSONObject();
            fileJson.put("type", "file");
            fileJson.put("file_id", id);
            result.put(fileJson);
        }

        return result;
    }

    public static void enableChatBot(long groupId) {
        ENABLED_GROUPS.putIfAbsent(groupId, new ChatBotCache(100));
    }

    public static void disableChatBot(long groupId) {
        ENABLED_GROUPS.remove(groupId);
    }
}