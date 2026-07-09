package io.lemonjuice.flandre_bot.commands.group.maimai.special;

import io.lemonjuice.flan_mai_plugin.model.Song;
import io.lemonjuice.flan_mai_plugin.service.MaiMaiProberService;
import io.lemonjuice.flan_mai_plugin.utils.SongManager;
import io.lemonjuice.flandre_bot.FlandreBotInit;
import io.lemonjuice.flandre_bot.config.FlandreBotConfig;
import io.lemonjuice.flandre_bot.func.FunctionCommand;
import io.lemonjuice.flandre_bot.resources.ResourceInit;
import io.lemonjuice.flandre_bot_framework.command.group.GroupCommandRunner;
import io.lemonjuice.flandre_bot_framework.message.pattern.MessagePattern;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.AtNode;
import io.lemonjuice.flandre_bot_framework.message.pattern.node.RegexNode;
import io.lemonjuice.flandre_bot_framework.message.segment.MessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.TextMessageSegment;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;
import io.lemonjuice.flandre_bot_framework.permission.PermissionLevel;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.text.html.parser.Entity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@FunctionCommand(value = "mai_deepseek", report = false)
public class SongFuzzySearchCommand extends GroupCommandRunner {
    private static String SYS_MSG = "";

    private static final DateTimeFormatter cacheDateFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Pattern textPattern = Pattern.compile("/?mai\\s+(模糊搜歌|模糊匹配)\\s+(.+)");
    private static final MessagePattern commandPattern = MessagePattern.builder()
            .nextNode(AtNode.atBot())
            .nextNode(new RegexNode(textPattern))
            .build();

    public SongFuzzySearchCommand(Message command) {
        super(command);
    }

    public static void init() {
        JSONArray songList = new JSONArray();
        for(Song song : SongManager.getSongs()) {
            JSONObject songData = new JSONObject();
            songData.put("id", song.id);
            songData.put("title", song.title);
            songData.put("version", song.info.from);
            songData.put("type", song.type);
            songData.put("category", song.info.category);
            songData.put("artist", song.info.artist);

            JSONArray aliases = new JSONArray();
            song.alias.forEach(aliases::put);
            songData.put("aliases", aliases);

            JSONArray levels = new JSONArray();
            song.level.forEach(levels::put);
            songData.put("levels", levels);

            songList.put(songData);
        }
        JSONObject songJson = new JSONObject();
        songJson.put("songs", songList);

        StringBuilder sysMsgTmp = new StringBuilder();
        for(String str : ResourceInit.MAI_FUZZY_SEARCH_SYS.get()) {
            sysMsgTmp.append(str);
            sysMsgTmp.append("\n");
        }

        SYS_MSG = sysMsgTmp.toString().trim().replace("${song_list}", songJson.toString());
    }

    @Override
    public IPermissionLevel getPermissionLevel() {
        return PermissionLevel.NORMAL;
    }

    @Override
    public boolean matches() {
        return commandPattern.matcher(command.message.trim()).matches();
    }

    @Override
    public void apply() {
        MessageSegment seg = this.command.message.get(1);
        if(seg instanceof TextMessageSegment textSeg) {
            Matcher matcher = textPattern.matcher(textSeg.getContent());
            if(!matcher.find()) {
                return;
            }
            String userInput = matcher.group(2);
            if(userInput.isBlank()) {
                return;
            }

            this.command.getContext().replyWithText("AI先生思考需要一些时间，耐心等一下吧~");

            JSONObject result = this.callDeepseekAPI(userInput);
            if(result.getInt("status_code") == -1) {
                this.command.getContext().replyWithText("抱歉……芙兰没能找到AI先生呢……\n联系一下bot管理员吧");
                return;
            }
            if(result.getInt("status_code") != 200) {
                this.command.getContext().replyWithText("抱歉……AI先生不帮芙兰诶……\n联系一下bot管理员吧");
                log.error("DeepSeek API调用失败！(HTTP ERROR {})", result.get("status_code"));
                log.error("Response Body: {}", result.toString());
                return;
            }

            try {
                JSONObject content = new JSONObject(result.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content"));
                JSONArray songs = content.getJSONArray("result");
                if (songs.isEmpty()) {
                    this.command.getContext().replyWithText("抱歉……没找到匹配的结果呢……");
                    return;
                }

                StringBuilder reply = new StringBuilder("搞定啦！有以下可能的结果呢：\n");
                boolean finded = false;
                for (int i = 0; i < songs.length(); i++) {
                    JSONObject songData = songs.getJSONObject(i);
                    int id = songData.optInt("song_id", -1);
                    if (id != -1) {
                        Song song = SongManager.getSongById(id);
                        if (song != null) {
                            finded = true;
                            reply.append(String.format("id%d %s(%s) (匹配度%.1f%%)",
                                    id,
                                    song.title,
                                    song.info.category.equals("宴会場") ? "宴会场" : song.type,
                                    songData.optFloat("matching_rate", 0.0F) * 100F)
                            );
                            reply.append("\n");
                        }
                    }
                }

                if (!finded) {
                    this.command.getContext().replyWithText("抱歉……没找到匹配的结果呢……");
                    return;
                } else {
                    this.command.getContext().replyWithText(reply.toString().trim());
                }
            } catch (JSONException e) {
                log.error("解析AI回复失败！", e);
                log.error("原始JSON文本: {}", result.toString());
                this.command.getContext().replyWithText("抱歉……芙兰看不懂AI先生的回复呢……");
            }
        }
    }

    private void outputResult(JSONObject result) {
        ZonedDateTime nowTime = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        String fileName = String.format("reply_%s.json", cacheDateFormatter.format(nowTime));
        File cacheFile = new File("./cache/ds_reply/mai_fuzzy_search/" + fileName);
        if(!cacheFile.getParentFile().exists()) {
            cacheFile.getParentFile().mkdirs();
        }
        try (OutputStream output = new FileOutputStream(cacheFile)) {
            output.write(result.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("输出AI回复失败！", e);
        }
    }

    private JSONObject callDeepseekAPI(String userInput) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("https://api.deepseek.com/chat/completions");
            post.setHeader("Accept", "application/json");
            post.setHeader("Authorization", String.format("Bearer %s", FlandreBotConfig.DEEPSEEK_API_KEY.get()));
            HttpEntity requestEntity = new StringEntity(this.genDSRequestData(userInput).toString(), ContentType.APPLICATION_JSON);
            post.setEntity(requestEntity);

            HttpResponse response = httpClient.execute(post);
            JSONObject result;
            try {
                result = new JSONObject(EntityUtils.toString(response.getEntity()));
            } catch (JSONException e) {
                result = new JSONObject();
                log.warn("无法识别的响应体: {}", EntityUtils.toString(response.getEntity()));
            }
            result.put("status_code", response.getStatusLine().getStatusCode());

            this.outputResult(result);

            return result;
        } catch (IOException e) {
            log.error("Deepseek API 调用失败！", e);
            JSONObject result = new JSONObject();
            result.put("status_code", -1);
            return result;
        }
    }

    private JSONObject genDSRequestData(String searchText) {
        JSONObject result = new JSONObject();

        result.put("model", "deepseek-v4-flash");
        result.put("temperature", 0.4);
        result.put("max_tokens", 32767);
        result.put("response_format", new JSONObject("{\"type\":\"json_object\"}"));
        result.put("thinking", new JSONObject("{\"type\":\"enabled\"}"));

        JSONArray messages = new JSONArray();

        JSONObject sysMessage = new JSONObject();
        sysMessage.put("role", "system");
        sysMessage.put("content", SYS_MSG);
        messages.put(sysMessage);

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", searchText);
        messages.put(userMessage);

        result.put("messages", messages);

        return result;
    }
}
