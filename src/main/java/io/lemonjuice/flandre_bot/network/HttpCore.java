package io.lemonjuice.flandre_bot.network;

import lombok.Getter;
import org.springframework.web.client.RestTemplate;

public class HttpCore {
    @Getter
    private static final HttpCore instance = new HttpCore();

    @Getter
    private final RestTemplate restTemplate = new RestTemplate();

    private HttpCore() {
    }
}
