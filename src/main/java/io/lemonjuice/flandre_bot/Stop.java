package io.lemonjuice.flandre_bot;

import io.lemonjuice.flandre_bot.network.SQLCore;
import io.lemonjuice.flandre_bot.network.WSClientCore;
import io.lemonjuice.flandre_bot.utils.NickNameManager;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class Stop {
    @PreDestroy
    public void onStop() {
        NickNameManager.storage();

        SQLCore.close();
        WSClientCore.close();
    }
}
