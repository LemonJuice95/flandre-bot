package io.lemonjuice.flandre_bot.api;

import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.UUID;

@Log4j2
public class RandomTouhouImage {
    public static String get(String tag) {
        UUID uuid = UUID.randomUUID();
        File file = new File("./cache/random_touhou/" + uuid + ".png");
        if(file.exists()) {
            file.delete();
        }
        if(!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url1 = "https://img.paulzzh.com/touhou/random?type=json&size=all" + (tag.isEmpty() ? "" : "&tag=" + tag);
            StringBuilder url2 = new StringBuilder("https://static.paulzzh.com/touhou/webp/");

            HttpGet get1 = new HttpGet(url1);
            HttpResponse response1 = httpClient.execute(get1);
            JSONObject jsonObject = new JSONObject(EntityUtils.toString(response1.getEntity()));

            String md5 = jsonObject.getString("md5");
            String head = md5.substring(0, 2);
            url2.append(head).append("/").append(md5).append(".webp");

            HttpGet get2 = new HttpGet(url2.toString());
            HttpResponse response2 = httpClient.execute(get2);
            byte[] data = EntityUtils.toByteArray(response2.getEntity());

            try (ByteArrayInputStream input = new ByteArrayInputStream(data)) {
                BufferedImage bufferedImage = ImageIO.read(input);
                ImageIO.write(bufferedImage, "PNG", file);
            }
        } catch (Exception e) {
            log.error("随机东方图获取失败！", e);
            return "";
        }
        return file.getAbsolutePath();
    }
}
