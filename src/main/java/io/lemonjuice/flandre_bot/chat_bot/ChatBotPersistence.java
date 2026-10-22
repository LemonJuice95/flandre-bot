package io.lemonjuice.flandre_bot.chat_bot;

import io.lemonjuice.flandre_bot.config.FlandreBotConfig;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ChatBotPersistence {
    public static void save(long groupId, ChatBotContext ctx) {
        File file = new File(String.format("%s%d.json", FlandreBotConfig.CHATBOT_PERSISTENCE_ROOT.get(), groupId));
        JSONObject serialized = serialize(ctx);
        try (OutputStream output = new FileOutputStream(file)) {
            output.write(serialized.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("持久化ChatBot聊天记录失败！", e);
        }
    }

    public static ChatBotContext read(long groupId) {
        File file = new File(String.format("%s%d.json", FlandreBotConfig.CHATBOT_PERSISTENCE_ROOT.get(), groupId));
        if(!file.exists()) return null;
        try (InputStream input = new FileInputStream(file)) {
            JSONTokener tokener = new JSONTokener(input);
            JSONObject json = new JSONObject(tokener);
            return deserialize(json);
        } catch (IOException | JSONException e) {
            log.error("从文件读取ChatBot聊天记录失败！", e);
        }
        return null;
    }

    public static JSONObject serialize(ChatBotContext ctx) {
        JSONObject result = new JSONObject();
        result.put("size", ctx.getMaxSize());
        result.put("prompt", ctx.getPrompt());

        JSONArray contents = new JSONArray();
        ctx.getMessages().forEach((msg) -> {
            JSONObject sMsg = new JSONObject();
            sMsg.put("role", msg.role.toString());
            sMsg.put("content", ChatBotHandler.serializeBody(msg.message));
            contents.put(sMsg);
        });

        result.put("contents", contents);

        return result;
    }

    public static ChatBotContext deserialize(JSONObject json) {
        ChatBotContext ctx = new ChatBotContext(
                json.optInt("size", FlandreBotConfig.CHATBOT_CACHE_SIZE.get()),
                json.optString("prompt", ChatBotHandler.SYS_MSG)
        );

        ArrayList<ChatBotMessage> messages = new ArrayList<>();
        JSONArray contents = json.optJSONArray("contents", new JSONArray());
        for(int i = 0; i < contents.length(); i++) {
            JSONObject sMsgI = contents.getJSONObject(i);
            ChatBotMessage msgI = new ChatBotMessage(
                    ChatBotMessage.Role.valueOf(sMsgI.getString("role").toUpperCase()),
                    deserializeBody(sMsgI.get("content"))
            );
            messages.add(msgI);
        }

        ctx.pushBack(messages);

        return ctx;
    }

    public static ChatBotMessage.Body deserializeBody(Object body) {
        if(body instanceof String str) {
            return new ChatBotMessage.Body(str);
        } else if(body instanceof JSONArray jsonArray) {
            String text = "";
            List<String> fileIds = new ArrayList<>();
            for(int i = 0 ; i < jsonArray.length(); i++) {
                JSONObject eleI = jsonArray.getJSONObject(i);
                String type = eleI.getString("type");
                if(type.equals("text")) {
                    text = eleI.getString("text");
                } else if(type.equals("file")) {
                    fileIds.add(eleI.getString("file_id"));
                }
            }

            return new ChatBotMessage.Body(text, fileIds);
        }
        return null;
    }
}
