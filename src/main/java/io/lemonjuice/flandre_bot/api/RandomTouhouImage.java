package io.lemonjuice.flandre_bot.api;

import io.lemonjuice.flandre_bot.FlandreBot;
import io.lemonjuice.flandre_bot.network.HttpCore;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;

public class RandomTouhouImage {
    public static String get(String tag, long groupId) {
        File file = new File(FlandreBot.getJarPath() + "cache/random_touhou/" + groupId + ".png");
        if(file.exists()) {
            file.delete();
        }
        if(!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            String url1 = "https://img.paulzzh.com/touhou/random?type=json&size=all" + (tag.isEmpty() ? "" : "&tag=" + tag);
            StringBuilder url2 = new StringBuilder("https://img.paulzzh.com/touhou/webp/");
            String responseRaw = HttpCore.getInstance().getRestTemplate().exchange(url1, HttpMethod.GET, null, String.class).getBody();
            JSONObject jsonObject = new JSONObject(responseRaw);
            String md5 = jsonObject.getString("md5");
            String head = md5.substring(0, 2);
            url2.append(head).append("/").append(md5).append(".webp");
            byte[] data = HttpCore.getInstance().getRestTemplate().exchange(url2.toString(), HttpMethod.GET, null, byte[].class).getBody();
            try (ByteArrayInputStream input = new ByteArrayInputStream(data)) {
                BufferedImage bufferedImage = ImageIO.read(input);
                ImageIO.write(bufferedImage, "PNG", file);
            }
        } catch (Exception e) {
            return "";
        }
        return file.getPath();
    }
}
