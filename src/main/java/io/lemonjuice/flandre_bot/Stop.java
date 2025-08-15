package io.lemonjuice.flandre_bot;

import io.lemonjuice.flandre_bot.network.SQLCore;
import io.lemonjuice.flandre_bot.network.WSClientCore;
import io.lemonjuice.flandre_bot.utils.NicknameManager;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class Stop {
    @PreDestroy
    public void onStop() {
        NicknameManager.save();

        SQLCore.close();
        WSClientCore.close();
    }
}
